
package com.routon.jme_droid;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SpatialTrack;
import com.jme3.animation.Track;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.routon.T.Launcher.TgalleryBackPanelAdapter;
import com.routon.T.Launcher.TgalleryFrontPanelAdapter;
import com.routon.T.Launcher.TgallerypanelAdapter;
import com.routon.T.Launcher.TgalleryrecommendAdapter;
import com.routon.T.Launcher.TopImageView;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JBoard;
import com.routon.jui.JDroidView;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;
import com.routon.jui.JTimer;
import com.routon.jui.JTimer.JTimerTask;

import java.io.IOException;
import java.util.List;

public class JmeTrackLoad extends JStage {
    private final static String TAG = "JmeTrackLoad";
    private final static String TIME_DEBUG_TAG = "TimeDebug";

    private Vector3f lightTarget = new Vector3f(0, -1.5f, 0);

    private Vector3f lightPosition = new Vector3f(0, -1.5f, 5);

    private JRollerCoaster recomPanel;
    
    private JFocusStrategy recomPanelFocusStrategy;
    
    private Animation recomPanelAnim;
    
    private JTimer recomTimer;
    
    private Track scale_track;
    
    private JRollerCoaster rollerCoaster;
    
    private JFocusStrategy rollerCoasterFocusStrategy;
    
    private Animation rollerCoasterAnim;

    private static final int RECOMMAND_NUM = 10;

    private static final int PANEL_NUM = 10;

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
      showRollerCoaster();
    }
    
    private void showRollerCoaster() {
        SpatialTrack tracks = assetManager.loadAsset(new AssetKey("Anims/Plane006_NA_1.j3o"));
        Animation anim = new Animation("Plane006_NA_1", tracks.getLength());
        anim.addTrack(tracks);
        rollerCoasterFocusStrategy = new JFocusStrategy(5, new float[] {
                0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20
        });
        rollerCoaster = new JRollerCoaster("roller coaster", anim,
                rollerCoasterFocusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);

        TgalleryFrontPanelAdapter adpter_f = new TgalleryFrontPanelAdapter(
                (Context) JmeAndroidSystem.getActivity());
        TgalleryBackPanelAdapter adpter_b = new TgalleryBackPanelAdapter(
                (Context) JmeAndroidSystem.getActivity());
        for (int i = 0; i < PANEL_NUM; i++) {
            JActorGroup group = new JActorGroup("");
            final JBoard board = new JBoard("");
            
            long t110 = System.currentTimeMillis();
            View panelFrontView = (View) adpter_f.getView(i, null, null);
            long t111 = System.currentTimeMillis();
            Log.d(TIME_DEBUG_TAG, "创建一个Android挡板的的时间：" + (t111 - t110));
            JDroidView rcChild = new JDroidView("panel_front_" + i, panelFrontView);
            long t112 = System.currentTimeMillis();
            Log.d(TIME_DEBUG_TAG, "创建一个jme结点的时间: " + (t112 - t111));
            rcChild.setEnableLighting(false);
            rcChild.setReflection(true, 0.3f, 0.5f,0.7f);
            board.attachChild(rcChild);
  
            View panelBackView = (View) adpter_b.getView(i, null, null);
            JDroidView rcChild2 = new JDroidView("panel_back_" + i, panelBackView);
            rcChild2.setEnableLighting(false);
            rcChild2.setReflection(true, 0.3f, 0.5f,0.7f);
            board.attachChild(rcChild2);       
            
            group.attachChild(board);
            rollerCoaster.attachChild(group);
            group.setOnKeyEventListener(new JActorKeyEventListener () {

                @Override
                public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                    // TODO Auto-generated method stub
                    return board.onEvent("", evt, false, tpf);
                }
                
            });
            
            /*
            if (i == 5) {
                JActorGroup group = new JActorGroup("");
                addTestPanel(group);
                
                rollerCoaster.attachChild(group);
            }
            */
        }

        rollerCoaster.requestKeyFocus();
        rollerCoaster.setLocalTranslation(0, -1.5f, 0);
        rollerCoaster.setProjectionCenterY(-1.5f);
        //rollerCoaster.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/* normal */, new Vector3f(0.0f,
        //        0.0f, -1.0f)/* up vector */);
        rootNode.attachChild(rollerCoaster);     
        
        // 增加按键回调
        rollerCoaster.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                if (evt.getKeyCode() == KeyInput.KEY_UP) {
                    return true;
                } else
                    return false;
            }
            
        });
    }
}
