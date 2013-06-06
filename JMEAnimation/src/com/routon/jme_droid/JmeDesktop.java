
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

public class JmeDesktop extends JStage {
    private final static String TAG = "JmeDesktop";
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
    
    private static final ColorRGBA grayColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);

    private TextView droidTxt = null;
    private JDroidView droid = null;
    
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
        /****************显示帧率*************************/
        droidTxt = new TextView((Context)JmeAndroidSystem.getActivity());
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
                droidTxt.setText("FPS: " + (int)getTimer().getFrameRate());
                return false;
            }
            
        });
        fpsTimer.start();
        /************************************************/
        
        long t0 = System.currentTimeMillis();
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
        
        AnimationFactory af = new AnimationFactory(12, "scale-anim");
        af.addTimeScale(0, new Vector3f(1f, 1f, 1f));
        af.addTimeScale(3, new Vector3f(1f, 1f, 1f));
        af.addTimeScale(6, new Vector3f(1.08f, 1.08f, 1.08f));
        af.addTimeScale(9, new Vector3f(1f, 1f, 1f));
        af.addTimeScale(12, new Vector3f(1f, 1f, 1f));
      
        af.addTimeColorGlass(0, new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        af.addTimeColorGlass(3, new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        af.addTimeColorGlass(6, new ColorRGBA(1f, 1f, 1f, 1f));
        af.addTimeColorGlass(9, new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        af.addTimeColorGlass(12, new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
       
        Animation scale_anim = af.buildAnimation();
        scale_track = scale_anim.getTracks()[0];
        
        long t1 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "t1 - t0: " + (t1 - t0));
        showRecommendation();
        
        long t2 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "t2 - t1: " + (t2 - t1));
        
        
        showRollerCoaster();
        
        long t3 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "t3 - t2: " + (t3 - t2));
        
        rollerCoasterGetFocus();
        recomPanelLoseFocus();

        //FlipTest();
        // setupBg();
        long t4 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "simpleInitApp总时间: " + (t4 - t0));
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

    private void showRollerCoaster() {
        long t10 = System.currentTimeMillis();
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        JAnimLoader loader = new JAnimLoader();
        
        List<Animation> anim = null;
        try {
            loader.addExtraAnimFactor("Plane006_NA_1", JAnimLoader.ExtraAnimFactorType_ColorGlass, 
                    new float[] {
                    0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20
            },
            new ColorRGBA[] {
                    new ColorRGBA(0.1f, 0.1f, 0.1f, 1f),
                    new ColorRGBA(0.2f, 0.2f, 0.2f, 1f),
                    new ColorRGBA(0.3f, 0.3f, 0.3f, 1f), 
                    new ColorRGBA(0.4f, 0.4f, 0.4f, 1f),
                    new ColorRGBA(0.5f, 0.5f, 0.5f, 1f),
                    
                    new ColorRGBA(1, 1, 1, 1),
                    
                    new ColorRGBA(0.5f, 0.5f, 0.5f, 1f),
                    new ColorRGBA(0.4f, 0.4f, 0.4f, 1f),
                    new ColorRGBA(0.3f, 0.3f, 0.3f, 1f), 
                    new ColorRGBA(0.2f, 0.2f, 0.2f, 1f),
                    new ColorRGBA(0.1f, 0.1f, 0.1f, 1f),
            });
            AssetInfo info = assetManager.locateAsset(new AssetKey("Anims/plane-8.anim"));
            anim = (List<Animation>)loader.load(info);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        long t11 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "动画加载时间: " + (t11 - t10));
        //List<Animation> anim = (List<Animation>) assetManager.loadAsset("Anims/plane-8.anim");
        rollerCoasterFocusStrategy = new JFocusStrategy(5, new float[] {
                0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20
        });
        rollerCoaster = new JRollerCoaster("roller coaster", anim.get(0),
                rollerCoasterFocusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);
        rollerCoaster.setSpeed(2, 4);

        TgalleryFrontPanelAdapter adpter_f = new TgalleryFrontPanelAdapter(
                (Context) JmeAndroidSystem.getActivity());
        TgalleryBackPanelAdapter adpter_b = new TgalleryBackPanelAdapter(
                (Context) JmeAndroidSystem.getActivity());
        long t12 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "创建roller对象以及PanelAdapter时间: " + (t12 - t11));
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
        long t13 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "创建挡板时间: " + (t13 - t12));

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
                    if (recomPanel != null) {
                        recomPanelGetFocus();
                        rollerCoasterLoseFocus();
                    }
                    return true;
                } else
                    return false;
            }
            
        });
        long t14 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "设置事件监听时间: " + (t14 - t13));
    }
    
    private void rollerCoasterGetFocus() {
        rollerCoaster.requestKeyFocus();
        rollerCoaster.setColorGlass(new ColorRGBA(1f, 1f, 1f, 1.0f));
        return;
    }
    
    private void rollerCoasterLoseFocus() {
        rollerCoaster.setColorGlass(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        return;
    }
    
    private void showRecommendation() {        
        AnimationFactory animationFactory = new AnimationFactory(12f, "anim", 1);
        animationFactory.addTimeTranslation(0, new Vector3f(-15f, 0f, 0.0f));
        animationFactory.addTimeTranslation(12, new Vector3f(15f, 0f, 0.0f));
        recomPanelAnim = animationFactory.buildAnimation();

        recomPanelFocusStrategy = new JFocusStrategy(2, new float[] {
                0f, 3f, 6f, 9f, 12f
        });
        recomPanel = new JRollerCoaster("Recommendation", recomPanelAnim, 
                recomPanelFocusStrategy);
        recomPanel.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_CYCLE);
        recomPanel.setSpeed(4, 4);

        TgalleryrecommendAdapter adapter = new TgalleryrecommendAdapter(
                (Context) JmeAndroidSystem.getActivity());

        for (int i = 0; i < RECOMMAND_NUM; i++) {
            TopImageView recommendView = (TopImageView) adapter.getView(i, null, null);
            recommendView.getTitle().setText(i + "-般喏大悲咒般喏大悲咒般喏大悲咒");
            // recommendView.getTitle().setSelected(true);
            // recommendView.getTitle().setHorizontalFadingEdgeEnabled(false);
            recommendView.getType().setText("教育");
            recommendView
                    .getDescription()
                    .setText(i + ".南無喝囉怛那哆囉夜耶。南無阿唎耶婆盧羯帝爍缽囉耶。菩提薩埵婆耶。摩訶薩埵婆耶。" +
                            "摩訶迦盧尼迦耶。唵。薩皤囉罰曳。數怛那怛寫。南無悉吉栗埵伊蒙阿唎耶。婆盧吉帝室佛囉。" +
                            "楞馱婆。南無那囉謹墀。醯唎。摩訶皤哆沙咩。薩婆阿他。豆輸朋。阿逝孕。薩婆薩哆。那摩婆薩多。" +
                            "那摩婆伽摩罰特豆。怛姪他。唵。阿婆盧醯。盧迦帝。迦羅帝。夷醯唎。摩訶菩提薩埵。薩婆薩婆。" +
                            "摩囉摩囉。摩醯摩醯唎馱孕。俱盧俱盧羯蒙。度盧度盧罰闍耶帝。摩訶罰闍耶帝。陀囉陀囉。地唎尼。" +
                            "室佛囉耶。遮囉遮囉。麼麼罰摩囉。穆帝隸。伊醯伊醯。室那室那。阿囉參佛囉舍利。罰沙罰參。" +
                            "佛囉舍耶。呼盧呼盧摩囉。呼盧呼盧醯利。娑囉娑囉。悉唎悉唎。蘇嚧蘇嚧。菩提夜。菩提夜。菩馱夜。" +
                            "菩馱夜。彌帝唎夜。那囉謹墀。地利瑟尼那。婆夜摩那。娑婆訶。悉陀夜。娑婆訶。摩訶悉陀夜。娑婆訶。" +
                            "悉陀喻藝室皤囉夜。娑婆訶。那囉謹墀。娑婆訶。摩囉那囉。娑婆訶。悉囉僧阿穆佉耶。娑婆訶。娑婆摩訶阿悉陀夜。" +
                            "娑婆訶。者吉囉阿悉陀夜。娑婆訶。波陀摩羯悉哆夜。娑婆訶。那囉謹墀皤伽囉耶。娑婆訶。摩婆利勝羯囉夜。" +
                            "娑婆訶。南無喝囉怛那哆囉夜耶。南無阿利耶婆羅吉帝爍皤囉夜。娑婆訶。唵。悉殿都。漫多囉。跋陀耶。娑婆訶。");
            recommendView.getLever().setText("9.5");
            recommendView.getLeftimage().setImageResource(R.drawable.beauty);

            JDroidView jDroidView = new JDroidView("recommend_" + i, recommendView);
            jDroidView.setShadowMode(ShadowMode.Off);
            recomPanel.attachChild(jDroidView);
        }

        recomPanel.requestKeyFocus();
        rootNode.attachChild(recomPanel);
        recomPanel.move(0, 2.5f, 0);
        recomPanel.setProjectionCenterY(2.5f);

        recomTimer = new JTimer(4000);
        recomTimer.setLocalSyncTask(new JTimerTask() {

            @Override
            public boolean task() {
                // TODO Auto-generated method stub
                recomPanel.onRetreat();
                return false;
            }
        });
        
        recomPanel.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                
                if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
                    if (rollerCoaster != null) {
                      rollerCoasterGetFocus();
                      recomPanelLoseFocus();
                    }
                    return true;
                } else
                    return false;
            }
            
        });
    }
    
    private void recomPanelGetFocus () {
        try {
            recomTimer.pause(); // recomPanel不再自动播放
            recomPanel.requestKeyFocus(); // recomPanel得到键盘事件
            recomPanel.onCancel();
            if (recomPanelFocusStrategy.isFocused()) {
                JDroidView s = (JDroidView) recomPanel.getChild(recomPanelFocusStrategy.getFocus());
                s.setLocalScale(1.08f);
                s.setColorGlass(new ColorRGBA(1f, 1f, 1f, 1.0f));
            }
            //recomPanel.setColorGlass(new ColorRGBA(1f, 1f, 1f, 1.0f));
            recomPanelAnim.addTrack(scale_track);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    private void recomPanelLoseFocus() {
        try {
        if (1 == 1 || recomPanelFocusStrategy.isFocused()) {//有焦点
            for (Spatial s : recomPanel.getChildren()) {
                s.setLocalScale(1);
                s.setColorGlass(grayColor);
            }
         
        }
        recomPanelAnim.removeTrack(scale_track);
        recomTimer.start();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    
    //////////////////////////////////////////////////////
    private boolean playing = false;
    String jps[] = {"1_1.png", "1.jpg"};

    private void FlipTest() {    
        JBoard board = new JBoard("board");
        for (int j = 0; j < 2; ++j) {
            Texture tex_ml = assetManager.loadTexture("Textures/" + jps[j]);
            JActor actor = new JActor("anonymous_" + j);
            actor.setupTexture(tex_ml);
            actor.setupMesh(256, 256);
            actor.setEnableLighting(false);
            actor.setReflection(true, 0.3f, 0.5f, 0.7f);
            board.attachChild(actor);
        }
        rootNode.attachChild(board);
        board.requestKeyFocus();
       
    }
}
