
package com.routon.jme_droid;

import android.util.Log;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SpatialTrack;
import com.jme3.animation.Track;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.PlayState;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JmeScene3Widget extends JActorGroup implements ShowHide {
    private static String TAG = "JmeScene3Widget";
        
    private AssetManager assetManager;
    
    private ShowHide prevShow = null;
    
    private ShowHide nextToShow = null;
        
    private List<Animation> animIn;
    
    private List<Animation> animOut;
    
    private List<Track> trackIn;
    
    private List<Track> trackOut;
        
    public JmeScene3Widget(String name, AssetManager am, final Node parent, final ShowHide toShow) {
        super(name);
        
        assetManager = am;
        
        prevShow = toShow;
        
        nextToShow = null;
        
        animIn = new ArrayList<Animation>();
        
        animOut = new ArrayList<Animation>();
        
        try {
            createWidget();
        } catch(Exception e) {
            Log.d(TAG, "error: " + e.toString());
        }
        this.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == 111) {
                    Log.d(TAG, "KEY_ESCAPE");
                    hide();
                    return true;
                } else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    Log.d(TAG, "evt.getKeyCode: " + evt.getKeyCode());
                    return true;
                } else
                    return true;
            }
            
        });
    }
  
    public void createWidget() {
        /////////////////////////////////////////////////
        /*
        List<Animation> ins = new ArrayList<Animation>();
        List<Animation> outs = new ArrayList<Animation>();
        
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        JAnimLoader loader = new JAnimLoader();

        List<Animation> anim = null;

        try {
            for (int i = 1; i <= 4; i++) {
                AssetInfo info = assetManager.locateAsset(new AssetKey("Anims/FlyAnim/n" + i + ".anim" ));
                anim = (List<Animation>) loader.load(info);
                ins.add(anim.get(0));
                outs.add(anim.get(1));
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "error: " + e.toString());
            e.printStackTrace();
            return;
        } */
        ///////////////////////////////////////////////////////////
        List<Track> ins = new ArrayList<Track>();
        List<Track> outs = new ArrayList<Track>();
        for (int i = 1; i <= 4; ++i) {
            ins.add((SpatialTrack)assetManager.loadAsset(new AssetKey("Anims/FlyAnim/in_" + i + ".j3o")));
            outs.add((SpatialTrack)assetManager.loadAsset(new AssetKey("Anims/FlyAnim/out_" + i + ".j3o")));
        }
        ////////////////////////////////////////////////////////
        
        try {
            //this.setFlyInAnims((Animation[])ins.toArray(new Animation[ins.size()]));
            //this.setFlyOutAnims((Animation[])outs.toArray(new Animation[outs.size()]));
            this.setFlyInTracks(ins);
            this.setFlyOutTracks(outs);
        String images[] = {
                "4836468698_d586e3df8f_o_icon.jpg",
                "4836468698_d586e3df8f_o_text.jpg",
                "diagonal-lines-abstract-wallpaper-1171.jpg",
                "windows-8-wallpaper_win8.jpg",
        };
        Texture texure1 = assetManager.loadTexture("Textures/" + images[0]);
        Texture texure2 = assetManager.loadTexture("Textures/" + images[1]);
        Texture texure3 = assetManager.loadTexture("Textures/" + images[2]);
        Texture texure4 = assetManager.loadTexture("Textures/" + images[3]);
        
        JActor actor1 = new JActor("1");
        actor1.setupMesh(450, 150);
        actor1.setupTexture(texure1);
        this.attachChild(actor1);
        
        JActor actor2 = new JActor("2");
        actor2.setupMesh(350, 25);
        actor2.setupTexture(texure2);
        this.attachChild(actor2);
        
        JActor actor3 = new JActor("3");
        actor3.setupMesh(450, 200);
        actor3.setupTexture(texure3);
        this.attachChild(actor3);
        
        JActor actor4 = new JActor("4");
        actor4.setupMesh(420, 55);
        actor4.setupTexture(texure4);
        this.attachChild(actor4);
              
        } catch (Exception e) {
            Log.d(TAG, "error: " + e.toString());
            return;
        }
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        Log.d(TAG, "JmeScene3Widget show");
        this.flyIn();
        this.setVisibility(true);
        this.requestKeyFocus();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        Log.d(TAG, "JmeScene3Widget hide");
        nextToShow = prevShow;
        this.flyOut();
    }
    
    public void setFlyInAnims(Animation[] t) {
        animIn.clear();
        for (int i = 0; i < t.length; ++i)
            animIn.add(t[i]);
    }

    public void setFlyOutAnims(Animation[] t) {
        animOut.clear();
        for (int i = 0; i < t.length; ++i)
            animOut.add(t[i]);
    }
    
    public void setFlyInTracks(List<Track> t) {
        trackIn = t;
    }
    
    public void setFlyOutTracks(List<Track> t) {
        trackOut = t;
    }
    
    @Override
    protected void childAttachNotify(Spatial child, int index) {
        ///////////////////////////////////////////////
        /*
        if (animIn.size() == 0 || animOut.size() == 0)
            Log.d(TAG, "没有添加进入退出动画!!!");
        
        AnimControl control = new AnimControl();
        control.addAnim(animIn.get(index % animIn.size()));
        control.addAnim(animOut.get(index % animOut.size()));
        Log.d(TAG, animIn.get(index % animIn.size()).getName());
        Log.d(TAG, animOut.get(index % animOut.size()).getName());
        child.addControl(control);
        */
        ////////////////////////////////////////////////
        Log.d(TAG, "trackIn.size: " + trackIn.size());
        int tIdx = index % trackIn.size();
        AnimControl control = new AnimControl();
        Animation animIn = new Animation("in", trackIn.get(tIdx).getLength());
        animIn.setTracks(new Track[] {
                trackIn.get(tIdx)
        });
        control.addAnim(animIn);
        Animation animOut = new Animation("out", trackOut.get(tIdx).getLength());
        animOut.setTracks(new Track[] {
                trackOut.get(tIdx)
        });
        control.addAnim(animOut);
        child.addControl(control);
        ////////////////////////////////////////////////////
        
        control.addListener(new AnimEventListener () {

            @Override
            public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
                // TODO Auto-generated method stub
                arg1.stop();
                Log.d(TAG, "Animation name: " + arg1.getAnimationName());
                Log.d(TAG, "nextToShow: " + nextToShow);
                Log.d(TAG, "arg0.getChannel(0) == arg1: " + (arg0.getChannel(0) == arg1));
                if (arg1.getPlayState() != PlayState.Playing) {
                    if (arg1.getAnimationName() == "in") {
                        ;
                    } else {
                        if (nextToShow != null)
                            nextToShow.show();
                    }
                }
            }
            
        });
  
        //child.setVisibility(false);//先将子结点隐藏，直到做动画时才显示
    }

    public void flyIn() {
        try {
            for (Spatial child : this.getChildren()) {
                AnimChannel channelIn = child.getControl(AnimControl.class).createChannel();
                channelIn.setAnim("in"); 
                channelIn.setLoopMode(LoopMode.DontLoop);
                channelIn.setTime(0);
                channelIn.play();
                channelIn.getControl().getSpatial().setVisibility(true);
                channelIn.setSpeed(25);
            }
        } catch (Exception ex) {
            Log.d(TAG, "flyIn error: " + ex);
        }
    }
    
    public void flyOut() {
        try {
            for (Spatial child : this.getChildren()) {
                AnimChannel channelOut = child.getControl(AnimControl.class).createChannel();
                channelOut.setAnim("out"); 
                channelOut.setLoopMode(LoopMode.DontLoop);
                channelOut.play();
                channelOut.setTime(0);
                channelOut.getControl().getSpatial().setVisibility(true);
                channelOut.setSpeed(25);
            }
        } catch (Exception ex) {
            Log.d(TAG, "flyOut error: " + ex);
        }
    }
}

