
package com.routon.jme_droid;

import com.jme3.animation.Animation;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

import java.io.IOException;
import java.util.List;

public class Jme3DSMaxAnimDemo extends JStage {
    private final static String TAG = "Jme3DSMaxAnimDemo";
    
    private  JmeScene1Widget widget;
    
    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            try {
                switch(evt.getKeyCode()) {
                    case KeyInput.KEY_0:
                        break;
                    case KeyInput.KEY_1:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simpleInitApp() {
        /*
        //添加背景
        String bgTex = "Textures/diagonal-lines-abstract-wallpaper-1171.jpg";
        JActor background = new JActor("background");
        //actor.setupMesh(420, 250); scene-1
        background.setupMesh(1024, 960);
        background.setupTexture(assetManager.loadTexture(bgTex)); 
        rootNode.attachChild(background);
        */
        viewPort.setBackgroundColor(new ColorRGBA(29/255.0f, 22/255.0f, 38/255.0f, 1.0f));
        widget = new JmeScene1Widget("widget1", assetManager, rootNode, null);
        rootNode.attachChild(widget);
        widget.show();
    }
  
}
