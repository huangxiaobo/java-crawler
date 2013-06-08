
package com.routon.jme_droid;

import android.util.Log;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SpatialTrack;
import com.jme3.animation.Track;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;

import java.util.ArrayList;
import java.util.List;

public class FlyInEffectDemo implements ShowHide{
    private static String TAG = "FlyInEffectDemo";
    private JFlyInGroup flyInGroup;
    private AssetManager assetManager;
    private Node parent;
    private Node selfRoot;
    private ShowHide nextToShow;

    FlyInEffectDemo(AssetManager am, final Node parent, final ShowHide toShow) {
        assetManager = am;
        this.parent = parent;
        nextToShow = toShow;
        
        selfRoot = new Node("selfRoot");
        parent.attachChild(selfRoot);
        flyInGroup = new JFlyInGroup("");
        selfRoot.attachChild(flyInGroup);
        // String trackFiles = {"Anims/Plane006_NA_1.j3o"};
        for (int i = 1; i <= 4; ++i) {
            SpatialTrack t = assetManager.loadAsset(new AssetKey("Anims/anim-" + i + ".j3o"));
            /*
            AnimationFactory af = new AnimationFactory(1, "anim", 10);
            af.addKeyFrameTranslation(0, new Vector3f(-10, 0, 0));
            af.addKeyFrameTranslation(10, new Vector3f(0, 0, 0));
            af.addKeyFrameColorGlass(0, new ColorRGBA(1, 1, 1, 0));
            af.addKeyFrameColorGlass(10, new ColorRGBA(1, 1, 1, 1));
            Animation anim = af.buildAnimation();
            flyInGroup.addTrack(anim.getTracks()[0]);
            */
            flyInGroup.addTrack(t);
        }

        for (int i = 0; i < 4; ++i) {
            JActor actor = new JActor("anonymous_" + i);
            actor.setupTexture(assetManager.loadTexture("Textures/Monkey.jpg"));
            actor.setupMesh(100, 100);
            actor.setEnableLighting(false);
            flyInGroup.attachChild(actor);
        }
        
        flyInGroup.setOnKeyEventListener(new JActorKeyEventListener () {

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
                    flyInGroup.flyIn();
                    return true;
                } else
                    return true;
            }
            
        });
        
        //当flyInGroup退出时mainStage显示
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
                }
            }
            
        });
    }

    public void show() {
        flyInGroup.flyIn();
        flyInGroup.requestKeyFocus();
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

class JFlyInGroup extends JActorGroup {
    private final static String TAG = "FlyInGroup";
    private List<Track> track;
    private List<AnimChannel> channels;

    public JFlyInGroup(String name) {
        super(name);
        // TODO Auto-generated constructor stub
        track = new ArrayList<Track>();
        channels = new ArrayList<AnimChannel>();
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
        channel.setLoopMode(LoopMode.DontLoop);
        channel.setTime(0);
        //channel.pause();
        channels.add(channel);
        //child.setVisibility(false);//先将子结点隐藏，直到做动画时才显示
    }

    public void flyIn() {
        try {
            for (AnimChannel ac : channels) {
                ac.play();
                ac.setTime(0);
                ac.getControl().getSpatial().setVisibility(true);
            }
        } catch (Exception ex) {
            Log.d(TAG, "ex: " + ex);
        }
    }
}
