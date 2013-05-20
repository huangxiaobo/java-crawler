
package com.routon.jme_droid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.routon.T.Launcher.TgallerypanelAdapter;
import com.routon.T.Launcher.TgalleryrecommendAdapter;
import com.routon.T.Launcher.TopImageView;
import com.routon.jui.JActor;
import com.routon.jui.JDroidView;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

import java.util.List;

public class JmeDesktop extends JStage {
    static private String TAG = "HelloJME";

    private Vector3f lightTarget = new Vector3f(0, -1.5f, 0);

    private Vector3f lightPosition = new Vector3f(0, -1.5f, 5);

    private TextView droidTxt = null;

    private JRollerCoaster recomPanel;

    private static final int RECOMMAND_NUM = 10;

    private static final int PANEL_NUM = 10;

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        // Log.d(TAG, "TouchEvent = " + evt + " tpf = " + tpf);

        if (evt.getType() == TouchEvent.Type.KEY_UP) {
            try {
                droidTxt.setText("KEY UP : " + evt.getKeyCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simpleInitApp() {
        //viewPort.setBackgroundColor(ColorRGBA.Blue);
        rootNode.setShadowMode(ShadowMode.Off);
        /*
         * AmbientLight al = new AmbientLight();
         * al.setColor(ColorRGBA.White.mult(0.3f)); rootNode.addLight(al);
         */
  
        Light.Type lt = null;//Light.Type.Spot;
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

        showRecommendation();
        showRollerCoaster();

        setupBg();
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
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        List<Animation> anim = (List<Animation>) assetManager.loadAsset("Anims/plane-6.anim");
        JFocusStrategy focusStrategy = new JFocusStrategy(5, new float[] {
                0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20
        });
        JRollerCoaster rollerCoaster = new JRollerCoaster("roller coaster", anim.get(0),
                focusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);

        TgallerypanelAdapter adpter = new TgallerypanelAdapter(
                (Context) JmeAndroidSystem.getActivity());

        for (int i = 0; i < PANEL_NUM; i++) {
            View panelView = (View) adpter.getView(i, null, null);

            JDroidView rcChild = new JDroidView("panel_" + i, panelView);
            rcChild.setEnableLighting(false);
            //rcChild.setShadowMode(ShadowMode.CastAndReceive);// 产生和接收 阴影
            // rcChild.setShadowMode(ShadowMode.Receive);
            rollerCoaster.attachChild(rcChild);
            
            if (i == 5) {
                Texture tex_ml = assetManager.loadTexture("Textures/1_1.png");
                JActor waitActor = new JActor("wait");
                waitActor.setupTexture(tex_ml);
                waitActor.setupMesh(256, 256);
                waitActor.setEnableLighting(false);
                rollerCoaster.attachChild(waitActor);
                
            }
        }

        rollerCoaster.requestKeyFocus();
        rollerCoaster.setLocalTranslation(0, -1.5f, 0);
        rollerCoaster.setProjectionCenterY(-1.5f);
        rollerCoaster.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/* normal */, new Vector3f(0.0f,
                0.0f, -1.0f)/* up vector */);
        rootNode.attachChild(rollerCoaster);      
    }

    private void showRecommendation() {
        AnimationFactory animationFactory = new AnimationFactory(12f, "anim", 30);

        animationFactory.addTimeTranslation(0, new Vector3f(-15f, 0f, 0.0f));
        animationFactory.addTimeTranslation(12, new Vector3f(15f, 0f, 0.0f));
        Animation animation = animationFactory.buildAnimation();

        JFocusStrategy focusStrategy = new JFocusStrategy(2, new float[] {
                0f, 3f, 6f, 9f, 12f
        });
        recomPanel = new JRollerCoaster("Recommendation", animation, focusStrategy);
        recomPanel.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);

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

        handler.postDelayed(myRunnable, 4000);
    }

    private Handler handler = new Handler();
    private boolean run = true;

    private Runnable myRunnable = new Runnable() {
        public void run() {

            if (run) {
                // recomPanel.
                recomPanel.onAdvance();
                handler.postDelayed(this, 4000);
            }
        }
    };
}
