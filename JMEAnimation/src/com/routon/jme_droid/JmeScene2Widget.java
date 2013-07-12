package com.routon.jme_droid;

import android.util.Log;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;

import java.io.IOException;
import java.util.List;

public class JmeScene2Widget extends JActorGroup implements ShowHide{
    private static String TAG = "JmeScene1Demo";
    
    private ShowHide prevShow;
    
    private ShowHide nextToShow;
    
    private AssetManager assetManager;

    private JRollerCoaster rollerCoaster;

    private JFocusStrategy rollerCoasterFocusStrategy;

    private static final int PANEL_NUM = 25;
    
    private int curFocus = PANEL_NUM / 2;
    
    public JmeScene2Widget(String name, AssetManager am, final Node parent, final ShowHide prev) {
        super(name);
        
        assetManager = am;
        
        prevShow = prev;
        
        nextToShow = null;
        
        try {
        createWidget();
        appendAnimation();
        } catch(Exception e) {
            Log.d(TAG, "error: " + e.toString());
        }
        this.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == 111) {
                    nextToShow = prevShow;
                    hide();
                    return true;
                } else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    return true;
                } else
                    return true;
            }
            
        });
    }
    
    @Override
    public void show() {
        // TODO Auto-generated method stub
        Log.d(TAG, "JmeScene1Widget show");
        try {
            this.setVisibility(true);
            AnimControl control = this.getControl(AnimControl.class);
            AnimChannel channel = control.createChannel();
            channel.setAnim("show");
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setTime(0);
            channel.play();
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        try {
        AnimControl control = this.getControl(AnimControl.class);
        AnimChannel channel = control.createChannel();
        channel.setAnim("hide");
        channel.setLoopMode(LoopMode.DontLoop);
        channel.setTime(0);
        channel.play();
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }
    }
    
   
    private void appendAnimation() {
        if (this.getControl(AnimControl.class) == null) {
            AnimControl control = new AnimControl();
            this.addControl(control);
        }
        final AnimControl control = this.getControl(AnimControl.class);
        this.addControl(control);
        
        control.addAnim(AnimationCollects.MoveIn());
        control.addAnim(AnimationCollects.MoveOut());
        
        control.addListener(new AnimEventListener() {
            @Override
            public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                arg1.stop();
                if (arg1.getAnimationName() == "show") {
                    rollerCoaster.requestKeyFocus();// Get key Focus.
                } else if (arg1.getAnimationName() == "hide") {
                    //control.getSpatial().getParent().detachChild(control.getSpatial());
                    control.getSpatial().setVisibility(false);
                    if (nextToShow != null)
                        nextToShow.show();
                }
            }
            
        });
    }
    
    private void updateFocus() {
        
    }
    
    private void createWidget() {
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        JAnimLoader loader = new JAnimLoader();

        List<Animation> anim = null;
        try {
            AssetInfo info = assetManager.locateAsset(new AssetKey("Anims/scene_2.anim"));
            anim = (List<Animation>) loader.load(info);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        int N = 10;
        float[] timePos = new float[N+1];
        float t = 0, delta = 20.0f / N;
        for (int i = 0; i <= N; i++)
            timePos[i] = i * delta;
        
        rollerCoasterFocusStrategy = new JFocusStrategy(5, timePos);
        rollerCoaster = new JRollerCoaster("roller coaster", anim.get(0),
                rollerCoasterFocusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);
        rollerCoaster.setSpeed(10, 4);
        rollerCoaster.setAdvanceKeys(new int[]{KeyInput.KEY_DOWN});
        rollerCoaster.setRetreatKeys(new int[]{KeyInput.KEY_UP});

        String images[] = {
          "zenvo-st1-11179-960x540.jpg",
          "wyndham_avengers_Robert_Downey-Jr_Iron_Man_960x540.jpg",
          "halo-4-17909-960x540.jpg",
          "Crystal_Airplane_Model_960x600.jpg",
          "bounty-hunter-solo-960x600.jpg"
        };
        
        for (int i = 0; i < PANEL_NUM; i++) {
            JActorGroup group = new JActorGroup("");
            JActor actor = new JActor("");
            //actor.setupMesh(420, 250); scene-1
            actor.setupMesh(600, 250);
            actor.setupTexture(assetManager.loadTexture("Textures/" + images[i % images.length]));
            //actor.setReflection(true, 0.3f, 0.5f, 0.7f);
            group.attachChild(actor);
            rollerCoaster.attachChild(group);
        }

        rollerCoaster.requestKeyFocus();
        rollerCoaster.setLocalTranslation(0, 0, 0);
        //rollerCoaster.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/* normal */, new Vector3f(0.0f,
        //        0.0f, -1.0f)/* up vector */);
        this.attachChild(rollerCoaster);     
        
        // 增加按键回调
        rollerCoaster.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    JmeScene3Widget widget = new JmeScene3Widget("widget3", 
                            assetManager,
                            rollerCoaster.getParent().getParent(), 
                            (ShowHide) rollerCoaster .getParent());
                    rollerCoaster.getParent().getParent().attachChild(widget);
                    nextToShow = widget;
                    widget.setVisibility(false);
                    hide();
                    return true;
                } else
                    return false;
            }
            
        });
    }
}
