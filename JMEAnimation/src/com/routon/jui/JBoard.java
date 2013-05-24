
package com.routon.jui;

import android.util.Log;
import android.view.KeyEvent;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.cinematic.PlayState;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class JBoard extends JActorGroup {
    private static final String TAG = "JBoard";

    private int[] rotateKey = {
        KeyInput.KEY_RETURN
    };
    
    private int[] escapeKey = {
            KeyInput.KEY_ESCAPE, 111 // KeyInput的KEY_ESCAPE值和实际的按键值111不相同
    };

    private AnimChannel channel = null;
    
    public JBoard(String name) {
        super(name);
        
        addFlipAnimation();
    }

    @Override
    public boolean onEvent(String name, TouchEvent evt, float tpf) {
        Log.d(TAG, evt.getKeyCode() + " pressed" + "type: " + evt.getType());
        
        //只关注按下事件类型
        if (evt.getType() != TouchEvent.Type.KEY_DOWN)
            return false;
        
        // JBoard在动画的初始位置，回车键运行动画
        if (rotateKeyPressed(evt.getKeyCode()) && pauseAtBeginState()) {
            channel.play();//运行动画
            return true;
        }
        else if (escKeyPressed(evt.getKeyCode())) {
            if (isPlayAnimation())
                channel.reverse();//动画运行状态下，按ESC，动画反向
            
            // 不需要再调用reverse(),channel判断当前到达结束位置，自动将speed反向。
            // 不能采用LoopMode.DontLoop, LoopMode.Loop, 因为无法得到动画运动到开始或结束位置时的信息
            if (pauseAtEndState()){
                //channel.reverse();
                channel.play();
            }
            return true;
        }

        // 左右方向键按下
        // 只有当动画停在初始状态时，才允许向上传递它的父结点处理此事件 
        if (arrowKeyPressed(evt.getKeyCode())) {
            if (pauseAtBeginState())
                return false;
            else
                return true;
        }
        // 由于JBoard经常套在另一个JActorGroup里使用， Event由它传下来，
        // 如果再返回 super.onEvent，会造成死循环
        //return super.onEvent(name, evt, tpf);
        return false;
    }

    @Override
    protected void childAttachNotify(Spatial child, int index) {
        if (this.getChildren().size() == 2) {
            child.setLocalRotation(new Quaternion().fromAngleAxis(180 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));;
        } 
    }

    private void addFlipAnimation() {
        AnimationFactory factory = new AnimationFactory(3, "flip", 2);
        factory.addTimeRotation(0,
                new Quaternion().fromAngleAxis(0 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));
        factory.addTimeRotation(3,
                new Quaternion().fromAngleAxis(180 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));

        factory.addTimeTranslation(0, new Vector3f(0, 0, 0));
        factory.addTimeTranslation(1.5f, new Vector3f(0, 0, -2));
        factory.addTimeTranslation(3, new Vector3f(0, 0, 1));
        
        AnimControl control = new AnimControl();
        this.addControl(control);
        control.addAnim(factory.buildAnimation());

        channel = control.createChannel();
        channel.setAnim("flip", 0);
        channel.setLoopMode(LoopMode.Cycle);
        channel.setSpeed(2f);
        control.addListener(new AnimEventListener() {

            @Override
            public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                arg1.pause();
            }
            
        });
 
        return;
    }

    public void setRotateKey(int[] keys) {
        rotateKey = keys;
    }

    public int[] getRotateKey() {
        return rotateKey;
    }

    private boolean rotateKeyPressed(int key) {
        for (int i : rotateKey)
            if (key == i)
                return true;
        return false;
    }
    
    private boolean escKeyPressed(int key) {
        for (int i : escapeKey)
            if (key == i)
                return true;
        return false;
    }
    
    private boolean arrowKeyPressed(int key) {
        if (key == KeyInput.KEY_LEFT || key == KeyInput.KEY_RIGHT)
            return true;
        return false;
    }
    
    // 因为当动画暂停时，getTime的时间并不刚好等于{0, channel.getAnimMaxTime()},
    // 且由于此动画只会停在动画两端，所以只要动画处于暂停，且时间大于动画时长的一半
    // 即认为动画停在结束处，此时按ESC键，动画将反向动画到动画开始处
    private boolean pauseAtBeginState() {
        if (channel != null && channel.getPlayState() != PlayState.Playing &&
                channel.getTime() < channel.getAnimMaxTime()/2)
            return true;
        return false;
    }
    
    private boolean pauseAtEndState() {
        if (channel != null && channel.getPlayState() != PlayState.Playing &&
                channel.getTime() > channel.getAnimMaxTime()/2)
            return true;
        return false;     
    }
    
    private boolean isPlayAnimation() {
        if (channel != null && channel.getPlayState() == PlayState.Playing)
            return true;
        return false;
    }
}