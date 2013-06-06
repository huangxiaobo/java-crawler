
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
import com.jme3.export.binary.BinaryExporter;
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
import com.jme3.texture.Texture;
import com.routon.jui.JActor;
import com.routon.jui.JActorGroup;
import com.routon.jui.JQuad;
import com.routon.jui.JStage;

import java.util.ArrayList;
import java.util.List;

public class TestFlyInEffect extends JStage {
    private JFlyInGroup flyInGroup;
    private Track[] tracks;
    private Material mat;
    private Texture tex;

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
        tex = assetManager.loadTexture("Textures/Monkey.jpg");
        mat.setTexture("ColorMap", tex);
    }

    private void createFlyInGroup() {
        flyInGroup = new JFlyInGroup("");
        flyInGroup.setTracks(tracks);
        rootNode.attachChild(flyInGroup);

        for (int i = 0; i < 4; ++i) {
            JActor actor = new JActor("anonymous_" + i);
            actor.setupTexture(tex);
            actor.setupMesh(100, 100);
            actor.setEnableLighting(false);            
            flyInGroup.attachChild(actor); 
        }
    }

    private void createTracks() {
        tracks = new Track[4];

        AnimationFactory af = new AnimationFactory(5, "anim");
        af.addTimeTranslation(0, new Vector3f(-5, 0, 0));
        af.addTimeTranslation(5, new Vector3f(-1, 0, 0));
        af.addTimeScale(0, new Vector3f(2, 2, 2));
        af.addTimeScale(5, new Vector3f(1, 1, 1));
        af.addTimeColorGlass(0, new ColorRGBA(1, 1, 1, 0));
        af.addTimeColorGlass(5, new ColorRGBA(1, 1, 1, 1));
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

class JFlyInGroup extends JActorGroup {
    private final static String TAG = "FlyInGroup";
    private List<Track> track;

    public JFlyInGroup(String name) {
        super(name);
        // TODO Auto-generated constructor stub
        track = new ArrayList<Track>();
    }

    public void setTracks(Track[] t) {
        track.clear();
        for (int i = 0; i < t.length; ++i)
            track.add(t[i]);
    }
    
    public void addTrack(Track t) {
        track.add(t);
    }

    @Override
    protected void childAttachNotify(Spatial child, int index) {
        if (track == null)
            return;
        int trackIndex = index % track.size();
        Track t = track.get(trackIndex);
        AnimControl control = new AnimControl();
        Animation anim = new Animation("anim", t.getLength());
        anim.setTracks(new Track[] {
            t
        });
        control.addAnim(anim);
        child.addControl(control);
        AnimChannel channel = control.createChannel();
        channel.setAnim("anim");
        channel.setLoopMode(LoopMode.Loop);
    }

    public void flyIn() {
        try {
        AnimChannel ac;
        for (Spatial s : getChildren()) {
            ac = s.getControl(AnimControl.class).getChannel(0);
            ac.setSpeed(6);
            ac.play();
        }
        } catch (Exception ex) {
            Log.d(TAG, "ex: " + ex);
        }
    }
}
