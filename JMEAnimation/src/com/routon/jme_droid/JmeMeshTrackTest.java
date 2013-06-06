
package com.routon.jme_droid;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.jme3.animation.MeshAnimChannel;
import com.jme3.animation.MeshAnimControl;
import com.jme3.animation.MeshAnimTrack;
import com.jme3.animation.MeshAnimation;
import com.jme3.animation.SpatialTrack;
import com.jme3.asset.AssetKey;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.routon.jui.JActor;
import com.routon.jui.JDroidView;
import com.routon.jui.JStage;
import com.routon.jui.JTimer;
import com.routon.jui.JTimer.JTimerTask;

public class JmeMeshTrackTest extends JStage {
    private final static String TAG = "JmeMeshTrackTest";

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

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);

        try {

            actor = new JActor("actor");
            Mesh mesh = assetManager.loadAsset(new AssetKey("Anims/01-geom-1.j3o"));
            actor.setMesh(mesh);
            actor.setupTexture(assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
            actor.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

            actor.setLocalScale(0.0005f);

            Node node = new Node("");
            node.attachChild(actor);
            rootNode.attachChild(node);

            MeshAnimControl control = assetManager.loadAsset(new AssetKey("Anims/01Ctrl.j3o"));
            node.addControl(control);

            MeshAnimChannel channel = control.createChannle();
            channel.setAnim("01");

        } catch (Exception ex) {
            Log.d(TAG, "exception: " + ex);
        }
    }

}
