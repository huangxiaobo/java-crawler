package com.routon.jme_droid;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.android.JmeAndroidSystem;
import com.routon.T.Launcher.TgalleryBackPanelAdapter;
import com.routon.T.Launcher.TgalleryFrontPanelAdapter;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JBoard;
import com.routon.jui.JDroidView;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;

import java.io.IOException;
import java.util.List;

public class HorizontalTileDemo implements ShowHide{
    private final static String TAG = "HorizontalTileDemo";
    
    private JActorGroup selfRoot;
    
    private ShowHide nextToShow;
    
    HorizontalTileDemo(AssetManager am, final Node parent, final ShowHide toShow) {
        selfRoot = new HorizontalTileGroup("", am);
        parent.attachChild(selfRoot);
        nextToShow = toShow;
        
        selfRoot.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == 111) {
                    hide();
                    return true;
                } else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    return true;
                } else
                    return true;
            }
            
        });
        
      //当Group退出时mainStage显示
        AnimControl control = new AnimControl();
        selfRoot.addControl(control);
        control.addAnim(createHideAnim());
        control.addListener(new AnimEventListener() {

            @Override
            public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                if (arg1.getAnimationName() == "hide") {
                    arg1.stop();
                    nextToShow.show();
                    parent.detachChild(selfRoot);
                }
            }
            
        });
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        selfRoot.setVisibility(true);
        selfRoot.requestKeyFocus();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        try {
            AnimControl control = selfRoot.getControl(AnimControl.class);
            AnimChannel channel = control.createChannel();
            channel.setAnim("hide");
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setTime(0);
            channel.play();
            } catch(Exception e) {
                Log.d(TAG, e.toString());
            }
    }

    private Animation createHideAnim() {
        AnimationFactory af = new AnimationFactory(1, "hide", 10);
        af.addKeyFrameTranslation(0, new Vector3f(0, 0, 0));
        af.addKeyFrameTranslation(10, new Vector3f(10, 0, 0));
        return af.buildAnimation();
    }
}


class HorizontalTileGroup extends JActorGroup {
    
    private AssetManager assetManager;
        
    private int PANEL_NUM = 5;
    
    private int curFocus = PANEL_NUM / 2;
        
    public HorizontalTileGroup(String name, AssetManager as) {
        super(name);
        // TODO Auto-generated constructor stub
        assetManager = as;
        
        createTiles();
        
        this.setOnKeyEventListener(new JActorKeyEventListener() {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
                    curFocus--;
                    if (curFocus < 0) curFocus = 0;
                    updateFocus();
                } else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
                    curFocus++;
                    if (curFocus >= PANEL_NUM) curFocus = PANEL_NUM-1;
                    updateFocus();
                }
                return false;
            }
            
        });
    }
    
    private void updateFocus() {
        
    }
    
    public void createTiles() {
        
        for (int i = 0; i < PANEL_NUM; i++) {
            JActorGroup group = new JActorGroup("");
            JActor actor = new JActor("actor");
            actor.setupMesh(250, 700);
            actor.setLocalTranslation((i - PANEL_NUM / 2) * 2.50f, 0, 0);
            actor.setupTexture(assetManager.loadTexture("girl.jpg"));

            group.attachChild(actor);
            this.attachChild(group);
        }
    }
}