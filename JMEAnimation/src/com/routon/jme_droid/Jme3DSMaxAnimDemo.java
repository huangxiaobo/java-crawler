
package com.routon.jme_droid;

import android.util.Log;

import com.jme3.animation.Animation;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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

    private JmeScene1Widget widget;

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            try {
                switch (evt.getKeyCode()) {
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
         * //添加背景 String bgTex =
         * "Textures/diagonal-lines-abstract-wallpaper-1171.jpg"; JActor
         * background = new JActor("background"); //actor.setupMesh(420, 250);
         * scene-1 background.setupMesh(1024, 960);
         * background.setupTexture(assetManager.loadTexture(bgTex));
         * rootNode.attachChild(background);
         */
        // 禁止全局显示的Viewport
        viewPort.detachScene(rootNode);
        viewPort.setEnabled(false);
        try {
            // 创建显示背景的Ｖiewport
            Camera camBg = cam.clone();
            camBg.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
            ViewPort bgView = renderManager.createMainView("Background", camBg);
            bgView.setClearFlags(true, true, true);
            JmeSceneBackground bgScene = new JmeSceneBackground("bg", assetManager, camBg);
            rootNode.attachChild(bgScene);
            bgView.attachScene(bgScene);
        } catch (Exception e) {
            Log.d(TAG, "JmeSceneBackground: " + e.getMessage());
        }
        try {
            Node mainNode = new Node("MaiScene");
            rootNode.attachChild(mainNode);
            Camera camMain = cam.clone();
            camMain.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
            ViewPort mainView = renderManager.createMainView("MainView", camMain);
            mainView.setClearFlags(false, false, false);
            widget = new JmeScene1Widget("widget1", assetManager, mainNode, null);
            mainNode.attachChild(widget);
            widget.show();
            
            mainView.attachScene(mainNode);
        } catch (Exception e) {
            Log.d(TAG, "error: " + e.getMessage());
        }
    }

}
