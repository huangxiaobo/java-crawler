
package com.routon.jmeanimation;

import android.content.Context;
import android.widget.TextView;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.TimeCheckListener;
import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.PlayState;
import com.jme3.input.KeyInput;
import com.jme3.input.android.AndroidInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Texture;
import com.routon.jui.JDesktopLayout;
import com.routon.jui.JDroidView;
import com.routon.jui.JStage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelloJME extends JStage {
    private static final String TAG = "JmeDesktop******** ";

    public static void main(String[] args) {
        HelloJME app = new HelloJME();
        app.start();
    }
    private TextView droidTxt = null;
    protected AndroidInput input;
    private final List<String> jpgs = new ArrayList<String>();
    List<Spatial> models = new ArrayList<Spatial>();
    JDesktopLayout panel;

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.White);
        //cam.setLocation(new Vector3f(0, 0, 10));
        // cam.setRotation(new Quaternion().fromAngleAxis(-0.05f * FastMath.PI,
        // Vector3f.UNIT_X));
        System.out.println(TAG + "direction: " + cam.getDirection());
        
        droidTxt = new TextView((Context)JmeAndroidSystem.getActivity());
        droidTxt.setText("ASK中文");
        droidTxt.setTextSize(38);
        
        JDroidView droid = new JDroidView("a droid view", droidTxt);        
        rootNode.attachChild(droid);

        try {
            //加载动画
            System.out.println(TAG + "开始加载动画。");
            assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
            List<Animation> anim = (List<Animation>) assetManager.loadAsset("Anims/plane-3.anim");
            System.out.println(TAG + "anim size: " + anim.size());
            for (Animation am : anim)
                System.out.println(TAG + "动画: " + am);
            
            
            //创建停止点
            List<Float> timeList = new ArrayList<Float>();
            Animation am = anim.get(0);
            float lenght = am.getLength();
            float t = 0, delta = 2.0f;
            while(t <= lenght) {
                timeList.add(t);
                t += delta;
            }
            for (Float f : timeList) {
                System.out.println(TAG + " : " + f);
            }
            
            panel = new JDesktopLayout(anim.subList(1, 2), timeList, timeList.size() / 2);
            panel.setLocalScale(0.5f, 1, 1);
            rootNode.attachChild(panel);
            //创健模型
            createModels();
            
            panel.initialize();
            
        } catch (Exception e) {
            System.out.println(TAG + e.getStackTrace());
        }
        
        

        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addListener(this, new String[] {
                "Touch"
        });

    }

    private void createModels() {
        jpgs.add("0.jpg");
        jpgs.add("1.jpg");
        jpgs.add("2.jpg");
        jpgs.add("3.jpg");
        jpgs.add("4.jpg");
        jpgs.add("5.jpg");
        jpgs.add("6.jpg");
        

        // viewPort.setBackgroundColor(ColorRGBA.Blue);
        // node.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f *
        // FastMath.PI, Vector3f.UNIT_X));

        System.out.println(TAG + "before load: " + System.currentTimeMillis());
        Geometry geom = new Geometry("x", new Box(new Vector3f(0f,0f,0f), 0.75f,0.1f,0.75f));
        models.add(0, geom);
        // model.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f *
        // FastMath.PI, Vector3f.UNIT_X));
        // model.setLocalScale(new Vector3f(0.15f, 0.15f, 0.15f));
        // model.center();
        System.out.println(TAG + "after load: " + System.currentTimeMillis());

        models.add(1,  models.get(0).clone());
        models.add(2,  models.get(0).clone());
        models.add(3,  models.get(0).clone());
        models.add(4,  models.get(0).clone());
        models.add(5,  models.get(0).clone());
        models.add(6,  models.get(0).clone());
        models.add(7,  models.get(0).clone());
        models.add(8,  models.get(0).clone());
        models.add(9,  models.get(0).clone());
        
        System.out.println(TAG + "models size: " + models.size());

        try {
            for (int i = 0; i < models.size(); ++i) {
                Spatial n = models.get(i);
                panel.attachChild(n);
                Spatial s = n;
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                Texture tex1_ml = assetManager.loadTexture("Textures/" + jpgs.get(i % jpgs.size()));
                mat.setTexture("ColorMap", tex1_ml);
                s.setMaterial(mat);
                System.out.println(TAG + "Plane01: " + s);
            }
        } catch (Exception ex) {
            System.out.println(TAG + "createModels" + ex);
        }
    }

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        // TODO Auto-generated method stub
        
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            System.out.println(TAG + "onTouch*******");
            switch (evt.getKeyCode()) {
                case KeyInput.KEY_LEFT:
                    panel.moveBackward();
                    break;
                case KeyInput.KEY_RIGHT:
                    panel.moveForward();
                    break;
            }
        }
        evt.setConsumed();
    }


}
