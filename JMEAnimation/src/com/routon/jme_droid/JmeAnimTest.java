
package com.routon.jme_droid;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.android.JmeAndroidSystem;
import com.routon.jui.JActor;
import com.routon.jui.JDroidView;
import com.routon.jui.JStage;
import com.routon.jui.JTimer;
import com.routon.jui.JTimer.JTimerTask;

public class JmeAnimTest extends JStage {
    private final static String TAG = "JmeAnimTest";

    private TextView droidTxt = null;
    private JDroidView droid = null;

    private JActor actor = null;

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            try {
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simpleInitApp() {

        /**************** 显示帧率 *************************/
        droidTxt = new TextView((Context) JmeAndroidSystem.getActivity());
        droidTxt.setText("FPS");
        droidTxt.setTextSize(38);

        droid = new JDroidView("a droid view", droidTxt);
        droid.setFixOnLT(10, 620, true);
        droid.setReflection(true);
        droid.setColorGlass(ColorRGBA.Pink);
        rootNode.attachChild(droid);

        JTimer fpsTimer = new JTimer(1000, 0);
        fpsTimer.setLocalTask(new JTimerTask() {

            @Override
            public boolean task() {
                // TODO Auto-generated method stub
                droidTxt.setText("FPS: " + getTimer().getFrameRate());
                return false;
            }

        });
        fpsTimer.start();
        /************************************************/

        try {

           Geometry geom = new Geometry("geom", new Quad(1, 1));
           Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
           mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/beauty.jpg"));
           geom.setMaterial(mat_tt);
           rootNode.attachChild(geom);
           
           AnimationFactory af = new AnimationFactory(1f, "anim", 10);
           af.addKeyFrameTranslation(0, new Vector3f(-5, 0, 0));
           af.addKeyFrameScale(0, new Vector3f(1, 1, 1));
           af.addKeyFrameTranslation(3, new Vector3f(4, 0, 0));
           af.addKeyFrameScale(3, new Vector3f(3f, 3f, 3f));
           af.addKeyFrameTranslation(10, new Vector3f(5, 0, 0));
           af.addKeyFrameScale(10, new Vector3f(4, 4, 1));
          
           AnimControl control = new AnimControl();
           control.addAnim(af.buildAnimation());
           geom.addControl(control);
           AnimChannel channel = control.createChannel();
           channel.setAnim("anim");
           channel.setLoopMode(LoopMode.Cycle);
           channel.setSpeed(2);
           channel.play();
           
        } catch (Exception ex) {
            Log.d(TAG, "exception: " + ex);
        }
    }

}
