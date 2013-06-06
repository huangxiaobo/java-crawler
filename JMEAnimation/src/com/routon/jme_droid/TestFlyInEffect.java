
package com.routon.jme_droid;

import android.util.Log;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SpatialTrack;
import com.jme3.animation.Track;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.routon.jui.JActorGroup;
import com.routon.jui.JStage;

import java.util.ArrayList;
import java.util.List;

public class TestFlyInEffect extends JStage {
    private FlyInGroup flyInGroup;
    private Track[] tracks;
    private Material mat;

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    public void simpleInitApp() {
        // TODO Auto-generated method stub

        createMat();
        createTracks();
        createFlyInGroup();
        flyInGroup.flyIn();
    }

    private void createMat() {
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Monkey.jpg"));
    }

    private void createFlyInGroup() {
        flyInGroup = new FlyInGroup("");
        flyInGroup.setTracks(tracks);
        rootNode.attachChild(flyInGroup);

        for (int i = 0; i < 4; ++i) {
            Box b = new Box(Vector3f.ZERO, 1, 1, 1); // create cube shape at the
                                                     // origin
            Geometry geom = new Geometry("Box", b); // create cube geometry from
                                                    // the shape

            geom.setMaterial(mat); // set the cube's material
            flyInGroup.attachChild(geom); // make the cube appear in the scene
        }
    }

    private void createTracks() {
        tracks = new Track[4];

        AnimationFactory af = new AnimationFactory(5, "anim");
        af.addTimeTranslation(0, new Vector3f(-5, 0, 0));
        af.addTimeTranslation(5, new Vector3f(-1, 0, 0));
        tracks[0] = af.buildAnimation().getTracks()[0];

        af.addTimeTranslation(0, new Vector3f(5, 0, 0));
        af.addTimeTranslation(5, new Vector3f(1, 0, 0));
        tracks[1] = af.buildAnimation().getTracks()[0];

        af.addTimeTranslation(0, new Vector3f(0, -5, 0));
        af.addTimeTranslation(5, new Vector3f(0, -1, 0));
        tracks[2] = af.buildAnimation().getTracks()[0];

        af.addTimeTranslation(0, new Vector3f(0, 5, 0));
        af.addTimeTranslation(5, new Vector3f(0, 1, 0));
        tracks[3] = af.buildAnimation().getTracks()[0];
    }
}

class FlyInGroup extends JActorGroup {
    private final static String TAG = "FlyInGroup";
    private Track[] track;

    public FlyInGroup(String name) {
        super(name);
        // TODO Auto-generated constructor stub
        track = null;
    }

    public void setTracks(Track[] t) {
        track = t;
    }

    @Override
    protected void childAttachNotify(Spatial child, int index) {
        if (track == null)
            return;
        int trackIndex = index % track.length;
        Track t = track[trackIndex];
        AnimControl control = new AnimControl();
        Animation anim = new Animation("anim", t.getLength());
        anim.setTracks(new Track[] {
            t
        });
        control.addAnim(anim);
        child.addControl(control);
        AnimChannel channel = control.createChannel();
        channel.setAnim("anim");
        channel.setLoopMode(LoopMode.Cycle);
    }

    public void flyIn() {
        try {
        AnimChannel ac;
        for (Spatial s : getChildren()) {
            ac = s.getControl(AnimControl.class).getChannel(0);
            ac.setSpeed(5);
            ac.play();
        }
        } catch (Exception ex) {
            Log.d(TAG, "ex: " + ex);
        }
    }
}
