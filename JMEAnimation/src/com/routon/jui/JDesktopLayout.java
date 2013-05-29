
package com.routon.jui;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.TimeCheckListener;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.PlayState;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 这次的修改是将头尾两个稳定点不用。
 * 只要某个结点的时间点小于正数第二个稳定点的时间或是大于倒数第二个稳定点的时间，就隐藏
 * 
 * 上面的注释已经失效，经过修改已经可以使用两个端点
 */
public class JDesktopLayout extends JActorGroup {
    enum PlayDirection {
        Forward,
        Backward,
    };

    private static final String TAG = "JmeDesktop********";
    List<Spatial> models;
    private List<Animation> animations = null;
    private List<ArrayList<AnimChannel>> animChannels = new ArrayList<ArrayList<AnimChannel>>();
    private final static Map<PlayDirection, Integer> AnimMap = new HashMap<PlayDirection, Integer>();

    List<Float> timePointsList = null;
    private int leftTimePointIndex, rightTimePointIndex;
    
    private List<Integer> currentNodePlace = new ArrayList<Integer>();
    private List<Integer> currentNodePlaceTmp = new ArrayList<Integer>();
    private List<Integer> nextNodePlace = new ArrayList<Integer>();
    private List<Integer> targetNodePlace = new ArrayList<Integer>();

    private final float speedFactor = 1.0f;
    private boolean forbidKey = false;// 当从最左边移到最右边或从最右边移到左边的动画时,禁止按方向键
    private PlayDirection oldplayDirect = PlayDirection.Forward;
    private PlayDirection curplayDirect = PlayDirection.Forward;
    private LoopMode loopMode           = LoopMode.Loop;
    private boolean atTargetPos         = false;
    private int focusTimePos            = -1;
    private boolean fillAllModel        = false;

    public JDesktopLayout(String name, List<Animation> anims, List<Float> ts, int midPointIdx) {
        super(name);
        
        timePointsList = ts;
        leftTimePointIndex = 0;
        rightTimePointIndex = timePointsList.size() - 1;
        
        animations = anims;
        
        focusTimePos = midPointIdx;//从０开始的索引号
    }

    public void initialize() {
        if (models != null)
            models = null;
        for (ArrayList<AnimChannel> la : animChannels) {
            la.clear();
        }
        //将与结点状态相关的变量清空
        animChannels.clear();
        currentNodePlace.clear();
        currentNodePlaceTmp.clear();
        nextNodePlace.clear();
        targetNodePlace.clear();
        oldplayDirect = curplayDirect = PlayDirection.Forward;

        models = children;// 取得当前group的所有子结点
        initModels();
        System.out.println(TAG + "models.size(): " + models.size());

        addListener();
        System.out.println(TAG + " setting finish.");
        


        for (int i = 0; i < models.size(); ++i) {
            currentNodePlace.add(i, i + 1 - models.size() + leftTimePointIndex);
            currentNodePlaceTmp.add(i, i + 1 - models.size() + leftTimePointIndex);
            nextNodePlace.add(i, i + 2 - models.size() + leftTimePointIndex);
            if (fillAllModel)
                targetNodePlace.add(i, i + 1 - models.size() + rightTimePointIndex);
            else
                targetNodePlace.add(i, focusTimePos + i);
        }
  
        setSpeed(animChannels.get(AnimMap.get(PlayDirection.Forward)), 10);
        pauseByIndex(curplayDirect);
        play(curplayDirect);
    }

    private void addListener() {
        for (final ArrayList<AnimChannel> channel : animChannels) {
            for (int i = 0; i < channel.size(); ++i) {
                final int index = i;
                channel.get(i).addListener(new TimeCheckListener() {
                    @Override
                    public void onTimePointReach(float time, AnimControl control, AnimChannel channel) {
                        System.out.println(TAG + " 挡板 " + index + " 到达时间: " + time);
                        if (curplayDirect == PlayDirection.Forward) {
                            currentNodePlaceTmp.set(index, currentNodePlace.get(index)+1);
                        } else {
                            currentNodePlaceTmp.set(index, currentNodePlace.get(index)-1);
                        }
                        showTarget();
                        if (currentNodePlaceTmp.get(index) == nextNodePlace.get(index)) {
                            animChannels.get(AnimMap.get(curplayDirect)).get(index).pause();
                            System.out.println(TAG + "第 " + index + " 暂停 ");
                        }
                        
                        checkAndSetvisible(index);
                        
                        //等待，直到所的结点都到达一下步,即currentNodePlaceTmp与nextNodePlace相同
                        if (reachNextPlace()) {
                            // 当前结点位置移到下一组位置
                            System.out.println(TAG + "到达下一组位置。");
                            currentNodePlace.clear();
                            currentNodePlace.addAll(nextNodePlace);
                            
                            checkAllAndSetvisible();   //检查所有
                            
                            if (reachTarget()) {// 到达目标
                                System.out.println(TAG + "到达目标位置。");
                                //从一端整体回退到另一端时，禁止按键，否则会出现问题
                                //原因是改变动画时结点和各个状态没有维护好
                                forbidKey = false;
                                atTargetPos = true;//所有结点处于目标位置
                                return;
                            }

                            calcuNextStep(curplayDirect);
                            pauseByIndex(curplayDirect);
                             
                            play(curplayDirect);
                        }
                    }
                });
            }
        }
    }

    private void initModels() {
        try {
            if (animations.size() == 1) {
                AnimMap.put(PlayDirection.Forward, 0);
                AnimMap.put(PlayDirection.Backward, 0);
            } else if (animations.size() == 2) {
                AnimMap.put(PlayDirection.Forward, 0);
                AnimMap.put(PlayDirection.Backward, 1);
            } else {
                // error
                System.out.println(TAG + "animations size error: " + animations.size());
            }

            for (int i = 0; i < animations.size(); ++i)
                animChannels.add(new ArrayList<AnimChannel>());
            for (int i = 0; i < models.size(); ++i) {
                Spatial n = models.get(i);
                AnimControl control = new AnimControl();

                for (int j = 0; j < animations.size(); ++j) {
                    control.addAnim(animations.get(j).clone());
                    n.addControl(control);
                    AnimChannel ac = control.createChannel();
                    ac.setAnim(animations.get(j).getName());
                    ac.setLoopMode(LoopMode.DontLoop);
                    animChannels.get(j).add(ac);
                }
            }

            // for test
            System.out.println(TAG + "animChannels size: " + animChannels.size());
        } catch (Exception ex) {
            System.out.println(TAG + "initModels exception: " + ex);
        }
    }

    public void setSpeed(List<AnimChannel> list, float speed) {
        System.out.println(TAG + "setSpeed: " + speed);
        for (AnimChannel ac : list)
            ac.setSpeed(speed * speedFactor);
    }

    private void forward() {
        System.out.println(TAG + " forward");
        for (AnimChannel ac : animChannels.get(AnimMap.get(PlayDirection.Forward))) {
            ac.forward();
        }
    }

    private void backward() {
        System.out.println(TAG + " backward");
        for (AnimChannel ac : animChannels.get(AnimMap.get(PlayDirection.Backward))) {
            ac.backward();
        }
    }

    private void play(PlayDirection direct) {
        System.out.println(TAG + " play");
        List<AnimChannel> list = animChannels.get(AnimMap.get(direct));
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).play();
            checkAndSetvisible(i);
        }
        atTargetPos = false;
    }
    
     private void pause(List<AnimChannel> list) {
        System.out.println(TAG + " pause");
        for (AnimChannel ac : list)
            ac.pause();
    }

     private void checkAndSetvisible(int index) {
         int curTimePos = currentNodePlace.get(index);
         int curTimePosTmp = currentNodePlaceTmp.get(index);
         int nextTimePos = nextNodePlace.get(index);
         
         List<AnimChannel> list = animChannels.get(AnimMap.get(curplayDirect));
         
         if (curTimePos < 0 || curTimePos >= timePointsList.size()) {
             models.get(index).setVisibility(false);
             if (index == 0) System.out.println(TAG + "第０个结点隐藏");
             return;
         }
         
         if (curTimePos == 0) {//处在时间轴最左端的点
             if (nextTimePos >= curTimePos) {
                 models.get(index).setVisibility(true);
                 if (index == 0) System.out.println(TAG + "第０个结点显示");
             }
             else {
                 models.get(index).setVisibility(false);
                 if (index == 0) System.out.println(TAG + "第０个结点隐藏");
             }
         }
         
         if (curTimePos == timePointsList.size()-1) {//处于时间轴最右端的点
             if (nextTimePos <= curTimePos)
                 models.get(index).setVisibility(true);
             else
                 models.get(index).setVisibility(false);
         }
     }
     
    private void checkAllAndSetvisible() {
        System.out.println(TAG + "checkAndSetvisible");
        List<AnimChannel> list = animChannels.get(AnimMap.get(curplayDirect));
        for (int i = 0; i < list.size(); ++i) {
            if (currentNodePlace.get(i) < 0 || currentNodePlace.get(i) >= timePointsList.size())
                models.get(i).setVisibility(false);
            else
                models.get(i).setVisibility(true);
        }
    }

    // 这个函数有问题，当模型个数多于稳定时间点时，就会有模型的稳定时间点长于时间轴，这部分暂没有考虑
    private void pauseByIndex(PlayDirection direct) {
        System.out.println(TAG + "pauseByIndex direct: " + direct );
        //依据nextNodePlace进行安排时间点

        List<AnimChannel> list = animChannels.get(AnimMap.get(direct));
        try {
            for (int i = 0; i < nextNodePlace.size(); ++i) {
                if (nextNodePlace.get(i) < leftTimePointIndex) {
                    //list.get(i).setTime(timePointsList.get(leftTimePointIndex-1));
                    list.get(i).setTimeCheckPoint(timePointsList.get(leftTimePointIndex));
                    System.out.println(TAG + "结点" + i + "检查点:" + timePointsList.get(leftTimePointIndex));
                } else if (nextNodePlace.get(i) > rightTimePointIndex) {
                    //list.get(i).setTime(timePointsList.get(rightTimePointIndex+1));
                    list.get(i).setTimeCheckPoint(timePointsList.get(rightTimePointIndex));
                    System.out.println(TAG + "结点" + i + "检查点:" + timePointsList.get(rightTimePointIndex));
                } else {
                    list.get(i).setTimeCheckPoint(timePointsList.get(nextNodePlace.get(i)));
                    System.out.println(TAG + "结点" + i + "检查点:" + timePointsList.get(nextNodePlace.get(i)));
                }
            }

        } catch (Exception e) {
            System.out.println(TAG + "pauseByIndex: " + e);
        }
    }

    private void changeAnim(PlayDirection toDirect) {
        if (animations.size() == 1)
            return;
        List<AnimChannel> toTarget, fromTarget;
        if (toDirect == PlayDirection.Forward) {
            toTarget = animChannels.get(AnimMap.get(PlayDirection.Forward));
            fromTarget = animChannels.get(AnimMap.get(PlayDirection.Backward));
        } else {
            toTarget = animChannels.get(AnimMap.get(PlayDirection.Backward));
            fromTarget = animChannels.get(AnimMap.get(PlayDirection.Forward));
        }
        pause(fromTarget);
        pause(toTarget);
        for (int i = 0; i < fromTarget.size(); ++i) {
            toTarget.get(i).setTime(fromTarget.get(i).getTime());
        }
    }
    
    private boolean reachNextPlace() {
        //currentNodePlaceTmp记录的是在移动过程中的中间状态
        for (int i = 0; i < currentNodePlace.size(); ++i) {
            if (currentNodePlaceTmp.get(i) != nextNodePlace.get(i))
                return false;
        }
        return true;
    }
    
    private boolean reachTarget() {
        //判断nextNodePlace 与 targetNodePlace是否相同
        for (int i = 0; i < nextNodePlace.size(); ++i) {
            if (nextNodePlace.get(i) != targetNodePlace.get(i))
                return false;
        }
        return true;
    }
    
    private void calcuNextStep(PlayDirection direct) {
        //计算下一步
        int delta = (direct == PlayDirection.Forward ? 1 : -1);
        for (int i = 0; i < currentNodePlace.size(); ++i) {
            nextNodePlace.set(i, currentNodePlace.get(i) + delta);
        }
        //以上方法有问题：当currentNodePlace 与 targetNodePlace相同时，导致动画停不下来
        //因为不管向前或是向后，总不能到达目标
        //以后会改为根据当前位置和目标位置计算下一步的位置
    }
    
    private void getFirstNodeMoveRange(int[] out) {
        if (fillAllModel) {
            out[0] = models.size() >= timePointsList.size() ? timePointsList.size()-models.size() : 0;
            out[1] = models.size() >= timePointsList.size() ? 0 : timePointsList.size()-models.size();
        } else {
            out[0] = focusTimePos + 1 - models.size();//因为focusTimePos是从０开始的索引号，所以要加１
            out[1] = focusTimePos;
        }
    }
    
    private boolean canMove(PlayDirection direct) {
        //保留时间轴左右两端的情况
        //showTarget();
        int[] range = new int[2];
        getFirstNodeMoveRange(range);

        System.out.println(TAG + "lower:" + range[0] + "upper: " + range[1]);
        if (targetNodePlace.get(0) == range[0] && direct == PlayDirection.Backward)
            return false;
        if (targetNodePlace.get(0) == range[1] && direct == PlayDirection.Forward)
            return false;
        return true;
    }
    
    private void showTarget() {
        System.out.print(TAG + " 当前：");
        for (int i = 0; i < currentNodePlace.size(); ++i) {
            System.out.print(currentNodePlace.get(i) + "\t");
        }
        System.out.println();
        System.out.print(TAG + " 临时：");
        for (int i = 0; i < currentNodePlaceTmp.size(); ++i) {
            System.out.print(currentNodePlaceTmp.get(i) + "\t");
        }
        System.out.println();
        System.out.print(TAG + " 下一：");
        for (int i = 0; i < nextNodePlace.size(); ++i) {
            System.out.print(nextNodePlace.get(i) + "\t");
        }
        System.out.println();
        System.out.print(TAG + " 目标：");
        for (int i = 0; i < targetNodePlace.size(); ++i) {
            System.out.print(targetNodePlace.get(i) + "\t");
        }
        System.out.println("");
    }
    
    private void moveTarget(PlayDirection direct) {
        System.out.print(TAG + " moveTarget  direct:" + direct);
        int delta = (direct == PlayDirection.Forward ? 1 : -1);
        for (int i = 0; i < targetNodePlace.size(); ++i)
            targetNodePlace.set(i, targetNodePlace.get(i) + delta);
    }
    
    private void setTarget(int startVal) {
        for (int i = 0; i < targetNodePlace.size(); ++i)
            targetNodePlace.set(i, startVal + i);
    }
    
    //参数，当前运动方向
    //根据方向，改变当前运动：从最左边到最右边或是从最右边到最左边
    private void moveReverse(PlayDirection cur_direct) {
        int     begin = 0;
        float   speed = 1;
        oldplayDirect = cur_direct;
        
        int[] range = new int[2];
        getFirstNodeMoveRange(range);
        
        if (cur_direct == PlayDirection.Forward) {
            curplayDirect = PlayDirection.Backward;
            begin = range[0];
            speed = -2.5f * speedFactor;
        } else {
            curplayDirect = PlayDirection.Forward;
            begin = range[1];
            speed = 2.5f * speedFactor; 
        }
        for (int i = 0; i < models.size(); ++i) {
            targetNodePlace.set(i, begin+i);
            System.out.println(TAG + " i: " + i + " taget:" + (begin + i));
        }
        calcuNextStep(curplayDirect);
        pauseByIndex(curplayDirect);
        setSpeed(animChannels.get(AnimMap.get(curplayDirect)), speed);
        play(curplayDirect);
        forbidKey = true;
    }
    
    private boolean compareList(List<Integer> l1, List<Integer> l2) {
        for (int i = 0; i < l1.size(); ++i) {
            if (l1.get(i) != l2.get(i))
                return false;
        }
        return true;
    }
    
    //速度
    private float speedAsc = 1;

    public void moveBackward() {
        System.out.println(TAG + "moveBackward. KEY_LEFT.");
        showTarget();
        try {
            if (forbidKey == true)
                return;
            boolean res = canMove(PlayDirection.Backward);
            if (res == false && atTargetPos == true && loopMode == LoopMode.Loop) {
                moveReverse(PlayDirection.Backward);
                return;
            }
            if (res == false) {
                System.out.println(TAG + "canMove == false.");
                return;
            }
            
            oldplayDirect = curplayDirect;
            curplayDirect = PlayDirection.Backward;

            if (oldplayDirect != curplayDirect) {// 如果原来是向右运动，需要切换动画
              //交换currentNodePlace和nextNodePlace,重新设置targetNodePlace
                System.out.println(TAG + "调换方向");
                showTarget();
                changeAnim(PlayDirection.Backward);
                currentNodePlace.clear();
                currentNodePlace.addAll(nextNodePlace);
                currentNodePlaceTmp.clear();
                currentNodePlaceTmp.addAll(currentNodePlace);
                setTarget(currentNodePlace.get(0) - 1);
                calcuNextStep(curplayDirect);
                showTarget();
                pauseByIndex(curplayDirect);
                
                speedAsc = 1;           //反向，速度恢复
            } else {
                if (compareList(currentNodePlace, targetNodePlace) == true) {//当前停止在目标位置
                    speedAsc = 1;       //从稳定点出发，速度初始值
                } else {
                    speedAsc += 0.5f;         //连续在同一方向按键，速度提高
                }
                moveTarget(curplayDirect);
                if (true || compareList(currentNodePlace, nextNodePlace)) {
                    System.out.println(TAG + " currentNodePlace == nextNodePlace");
                    calcuNextStep(curplayDirect);
                    pauseByIndex(curplayDirect);
                }
            }
            setSpeed(animChannels.get(AnimMap.get(PlayDirection.Backward)), speedAsc * speedFactor);
            backward();
            
            play(curplayDirect);
            
        } catch (Exception ex) {
            System.out.println(TAG + "moveBackward error: " + ex);
        }
    }

    public void moveForward() {
        System.out.println(TAG + "moveForward. KEY_RIGHT");
        if (forbidKey == true)
            return;
        boolean res = canMove(PlayDirection.Forward);
 
        if (res == false && atTargetPos == true && loopMode == LoopMode.Loop) {
            //已经移到最右端且还要向右移，结点整体移到最左端
            //此处有一个总是，没有判断结点是否真正移到最右端，可能还在中途，就开始整体向左移
            moveReverse(PlayDirection.Forward);
            return;
        }
        if (res == false) {
            return;
        }
        
        oldplayDirect = curplayDirect;
        curplayDirect = PlayDirection.Forward;


        if (oldplayDirect != curplayDirect) {//上次动画前进方向和此次不同，需要改变动画
            System.out.println(TAG + "调换方向");
            showTarget();
            changeAnim(PlayDirection.Forward);
            currentNodePlace.clear();
            currentNodePlace.addAll(nextNodePlace);
            currentNodePlaceTmp.clear();
            currentNodePlaceTmp.addAll(currentNodePlace);
            setTarget(currentNodePlace.get(0) + 1);
            calcuNextStep(curplayDirect);
            showTarget();
            pauseByIndex(curplayDirect);
            
            speedAsc = 1;       //反向，速度恢复
        } else {//
            if (compareList(currentNodePlace, targetNodePlace) == true) {//当前停止在目标位置,再按向前键时不应该增加速度
                speedAsc = 1;   //从稳定点出发，速度初始值
            } else {
                speedAsc += 0.5f;     //连续在同一方向按键，速度提高
            }
            moveTarget(curplayDirect);
            if (true ||compareList(currentNodePlace, nextNodePlace)) {
                System.out.println(TAG + " currentNodePlace == nextNodePlace");
                calcuNextStep(curplayDirect);
                pauseByIndex(curplayDirect);
            }
            //showTarget();
            System.out.println(TAG + " ......................");
        }
        setSpeed(animChannels.get(AnimMap.get(PlayDirection.Forward)), speedAsc * speedFactor);
        forward();
        play(curplayDirect);
    }
    
    @Override
    public boolean onEvent(String name, TouchEvent evt, boolean bubble, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            switch (evt.getKeyCode()) {
                case KeyInput.KEY_LEFT:
                    moveBackward();
                    return true;
                case KeyInput.KEY_RIGHT:
                    moveForward();
                    return true;
            }
        }
        return super.onEvent(name, evt, bubble, tpf);
    }
}
