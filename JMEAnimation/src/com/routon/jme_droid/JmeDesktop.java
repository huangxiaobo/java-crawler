
package com.routon.jme_droid;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

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
import com.jme3.material.MatParamTexture;
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

public class JmeDesktop extends JStage {
    private final static String TAG = "JmeDesktop";

    private Vector3f lightTarget = new Vector3f(0, -1.5f, 0);

    private Vector3f lightPosition = new Vector3f(0, -1.5f, 5);

    private JFlyInGroup flyInGroup;

    private TextView droidTxt = null;
    private JDroidView droid = null;
    
    private MainStage mainStage;
    
    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            try {
                Log.d(TAG, "name: " + name + " keyCode: " + evt.getKeyCode());
                switch(evt.getKeyCode()) {
                    case KeyInput.KEY_0:
                        Log.d(TAG, "demo");
                        //演示飞入菜单
                        FlyInEffectDemo demo0 = new FlyInEffectDemo(assetManager, rootNode, mainStage);
                        mainStage.setNextToShow(demo0);
                        mainStage.hide();
                        break;
                    case KeyInput.KEY_1:
                        HorizontalTileDemo demo1 = new HorizontalTileDemo(assetManager, rootNode, mainStage);
                        mainStage.setNextToShow(demo1);
                        mainStage.hide();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simpleInitApp() {
        showFpsInfo();
        /************************************************/
        
        //viewPort.setBackgroundColor(ColorRGBA.Blue);
        rootNode.setShadowMode(ShadowMode.Off);
        /*
         * AmbientLight al = new AmbientLight();
         * al.setColor(ColorRGBA.White.mult(0.3f)); rootNode.addLight(al);
         */
  
        Light.Type lt = null; //Light.Type.Spot;
        if (lt == Light.Type.Spot) {

            SpotLight spot = new SpotLight();
            spot.setSpotRange(200);
            // spot.setSpotRange(spotRange)
            spot.setSpotInnerAngle(5 * FastMath.DEG_TO_RAD);
            spot.setSpotOuterAngle(40 * FastMath.DEG_TO_RAD);
            spot.setPosition(lightPosition);
            spot.setDirection(lightTarget.subtract(spot.getPosition()));
            spot.setColor(ColorRGBA.White.mult(1));
            rootNode.addLight(spot);
            final SpotLightShadowRenderer slsr = new
                    SpotLightShadowRenderer(assetManager, 512);
            slsr.setLight(spot);
            slsr.setShadowIntensity(0.8f);
            slsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
            viewPort.addProcessor(slsr);
        } else if (lt == Light.Type.Directional) {
            DirectionalLight dl = new DirectionalLight();
            dl.setDirection(lightTarget.subtract(new Vector3f(5, -1.5f, 5)));
            dl.setColor(ColorRGBA.White.mult(0.7f));
            rootNode.addLight(dl);

            DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager,
                    512, 3);
            dlsr.setLight(dl);
            dlsr.setLambda(0.55f);
            dlsr.setShadowIntensity(1.6f);
            dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
            // dlsr.displayFrustum();
            viewPort.addProcessor(dlsr);
        } else {
        }
        /*
        // Add fog
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        //fpp.setNumSamples(4);
        FogFilter fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(125f);
        fog.setFogDensity(0.9f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        */
        
        mainStage = new MainStage(assetManager, rootNode);
        
        mainStage.show();
                
        //FlipTest();
        // setupBg();
    }
    
    private void showFpsInfo() {
        /****************显示帧率*************************/
        droidTxt = new TextView((Context)JmeAndroidSystem.getActivity());
        droidTxt.setText("FPS:00");
        droidTxt.setTextSize(38);
        
        droid = new JDroidView("a droid view", droidTxt);
        droid.setFixOnLT(10, 620, true);
        droid.setReflection(true);
        droid.setColorGlass(ColorRGBA.Pink);
        //增加水波纹
        MatParamTexture matTex;
        
        matTex = droid.getMaterial().getTextureParam("ColorMap");
        Material mat = new Material(assetManager, "MatDefs/Misc/ripple.j3md");
        droid.setMaterial(mat);
        if (matTex != null) {
            droid.getMaterial().setTexture("ColorMap", matTex.getTextureValue());
        }       
        rootNode.attachChild(droid);
        
        JTimer fpsTimer = new JTimer(1000, 0);
        fpsTimer.setLocalTask(new JTimerTask() {
            @Override
            public boolean task() {
                // TODO Auto-generated method stub
                droidTxt.setText("FPS: " + (int)getTimer().getFrameRate());
                return false;
            }
            
        });
        fpsTimer.start();
    }
    
    private void setupBg() {
        Material mat = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.White.clone());
        mat.setColor("Ambient", ColorRGBA.White.clone());
        // mat.setColor("Specular", ColorRGBA.White.clone());
        // mat.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.setFloat("Shininess", 0);
        // mat.setBoolean("VertexLighting", true);

        Box floor = new Box(new Vector3f(0, 0, -5), 16, 8, 0.01f);
        TangentBinormalGenerator.generate(floor);
        floor.scaleTextureCoordinates(new Vector2f(5, 5));
        Geometry floorGeom = new Geometry("Floor", floor);
        floorGeom.setMaterial(mat);
        floorGeom.setShadowMode(ShadowMode.Receive);// 只接收阴影
        rootNode.attachChild(floorGeom);
    }    
}
