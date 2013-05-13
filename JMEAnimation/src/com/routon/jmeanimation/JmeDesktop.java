package com.routon.jmeanimation;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.widget.TextView;

import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.android.JmeAndroidSystem;
import com.routon.jui.JDesktopLayout;
import com.routon.jui.JDroidView;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JQuad;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

import java.util.ArrayList;
import java.util.List;

public class JmeDesktop extends JStage {
    static private String TAG = "HelloJME";
    
    private TextView droidTxt = null;
    
    private JRollerCoaster recomPanel;
    
    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
//      Log.d(TAG, "TouchEvent = " + evt + " tpf = " + tpf);
        
        if (evt.getType() == TouchEvent.Type.KEY_UP) {
            try {
                droidTxt.setText("KEY UP : " + evt.getKeyCode());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } 
    }

    @Override
    public void simpleInitApp() {
        showRecommendation();
        showRollerCoaster();  
    }
        
    private void showRollerCoaster() {
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        List<Animation> anim = (List<Animation>) assetManager.loadAsset("Anims/plane-6.anim");
        JFocusStrategy focusStrategy = new JFocusStrategy(5, new float[] {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20});
        JRollerCoaster rollerCoaster = new JRollerCoaster("roller coaster", anim.get(0), focusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);
    
        String[] Jpgs = new String[] {
                "Textures/1.jpg",
                "Textures/2.jpg",
                "Textures/3.jpg",
                "Textures/4.jpg"
        };
        JQuad quad = new JQuad(2.0f, 3.0f);
      
        for (int i = 0; i < 10; i++) {
            Geometry rcChild = new Geometry("Rectangle", quad);
            Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat_stl.setTexture("ColorMap", assetManager.loadTexture(Jpgs[i % Jpgs.length]));
            rcChild.setMaterial(mat_stl);
            rollerCoaster.attachChild(rcChild);
        }
        
        rollerCoaster.requestKeyFocus();
        rollerCoaster.setLocalTranslation(0, -0.5f, 0);
        rollerCoaster.setProjectionCenterY(-0.5f);
        rollerCoaster.setLocalScale(0.85f);
        rollerCoaster.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/*normal*/, new Vector3f(0.0f, 0.0f, -1.0f)/*up vector*/);
        rootNode.attachChild(rollerCoaster);
    }
    
    private void showRecommendation () {
        AnimationFactory animationFactory = new AnimationFactory(12f, "anim", 30);
        
        animationFactory.addTimeTranslation(0, new Vector3f(-15f, 0f, 0.0f));
        animationFactory.addTimeTranslation(12, new Vector3f( 15f, 0f, 0.0f));
        Animation animation = animationFactory.buildAnimation();

        JFocusStrategy focusStrategy = new JFocusStrategy(2, new float[] {0f, 3f, 6f, 9f, 12f});
        recomPanel = new JRollerCoaster("Recommendation", animation, focusStrategy);
        recomPanel.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);
    
        JQuad quad = new JQuad(6, 3, 0, 2.5f);
        Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_stl.setTexture("ColorMap", assetManager.loadTexture("Textures/Wood.jpg"));

        
        for (int i = 0; i < 10; i++) {
            Geometry rcChild = new Geometry("Rectangle", quad);
            rcChild.setMaterial(mat_stl);
            recomPanel.attachChild(rcChild);
        }
        
        recomPanel.requestKeyFocus();
        rootNode.attachChild(recomPanel);
        recomPanel.setProjectionCenterY(1.5f);
        
        handler.postDelayed(myRunnable, 4000);
    }
    
    private Handler handler = new Handler();  
    private boolean run     = true;
    
    private Runnable myRunnable= new Runnable() {    
        public void run() {  
             
            if (run) {  
                //recomPanel.
                recomPanel.onAdvance();
                handler.postDelayed(this, 4000);  
            }  
        }  
    };
}
