
package com.routon.jme_droid;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.jme3.animation.LoopMode;
import com.jme3.animation.MeshAnimChannel;
import com.jme3.animation.MeshAnimControl;
import com.jme3.animation.MeshAnimTrack;
import com.jme3.animation.MeshAnimation;
import com.jme3.animation.SpatialTrack;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.ChaseCamera;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.BatchNode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.routon.jui.JActor;
import com.routon.jui.JActorGroup;
import com.routon.jui.JDroidView;
import com.routon.jui.JStage;
import com.routon.jui.JTimer;
import com.routon.jui.JTimer.JTimerTask;

import java.util.Random;

public class JmeSceneBackground extends JActorGroup {
    private final static String TAG = "JmeSceneBackground";
    protected AssetManager assetManager;
    protected BatchNode batchNode;
    Material mat1;
    Material mat2;
    Material mat3;
    Node terrain;
    Node sun_node;
    Node fog1_node;
    private MotionPath path;
    private MotionEvent cameraMotionControl;
    private ChaseCamera chaser;
    private CameraNode camNode;
    private Camera cam;
    
    public JmeSceneBackground(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    public JmeSceneBackground(String name, AssetManager am, Camera c) {
        super(name);
        
        assetManager = am;
               
        cam = c;
        
        //viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        batchNode = new BatchNode("BatchNode");

        randomGenerator();

        //cam.setLocation(new Vector3f(-34.403286f, 126.65158f, 434.791f));
        //cam.setRotation(new Quaternion(0.022630932f, 0.9749435f, -0.18736298f, 0.11776358f));

        batchNode.batch();


        terrain = new Node("terrain");
        terrain.setLocalTranslation(0, 0, -800);
        terrain.attachChild(batchNode);
        this.attachChild(terrain);


        camNode = new CameraNode("Motion cam", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setEnabled(false);
        path = new MotionPath();
        path.setCycle(true);
        path.addWayPoint(new Vector3f(200, 100, 0));
        path.addWayPoint(new Vector3f(0, 100, 200));
        path.addWayPoint(new Vector3f(-200, -100, 0));
        path.addWayPoint(new Vector3f(-300, -100, -300));
        path.addWayPoint(new Vector3f(0, -100, -200));
        path.setCurveTension(0.83f);
        //path.enableDebugShape(assetManager, rootNode);

        cameraMotionControl = new MotionEvent(camNode, path);
        cameraMotionControl.setLoopMode(LoopMode.Loop);
        //cameraMotionControl.setDuration(15f);
        cameraMotionControl.setLookAt(batchNode.getWorldTranslation(), Vector3f.UNIT_Y);
        cameraMotionControl.setDirectionType(MotionEvent.Direction.LookAt);

        this.attachChild(camNode);

        //flyCam.setEnabled(false);
        chaser = new ChaseCamera(cam, batchNode);
        chaser.setSmoothMotion(true);
        chaser.setMaxDistance(2000);
        chaser.setMinDistance(500);
        chaser.setDefaultDistance(1000);

        chaser.setEnabled(false);
        camNode.setEnabled(true);
        cameraMotionControl.play();
    }
    protected static final Vector3f fog1_pos = new Vector3f(-300, -140, 80);
    protected static final Vector3f fog2_pos = new Vector3f(-250, -250, 450);
    protected static final Vector3f fog3_pos = new Vector3f(60, -300, 180);
    Quad q = new Quad(4, 4);
    
    public void randomGenerator() {
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex1_ml = assetManager.loadTexture("Models/Star/Star88.png");
        tex1_ml.setMinFilter(MinFilter.BilinearNearestMipMap);
        mat1.setTexture("ColorMap", tex1_ml);
        //mat1.setColor("Color", ColorRGBA.White);
        mat1.getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
        mat1.getAdditionalRenderState().setDepthTest(false);
        //mat1.getAdditionalRenderState().setAlphaTest(true);

      
    
        
        Spatial model = assetManager.loadModel("Models/Star/star.mesh.xml");
        model.setMaterial(mat1);
        batchNode.attachChild(model);
        
        generateSun();
        generateFog();
    }
    
    private void generateSun() {
         //add sun
        BillboardControl control_sun = new BillboardControl();
        control_sun.setAlignment(BillboardControl.Alignment.Camera);

        sun_node = new Node();
        sun_node.addControl(control_sun);

        Material sun_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex_ml_sun = assetManager.loadTexture("Models/Star/Fog2_Images.png");
        sun_mat.setTexture("ColorMap", tex_ml_sun);
        sun_mat.getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
        //sun_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        sun_mat.getAdditionalRenderState().setDepthTest(false);
        
        
        Geometry sun = new Geometry("Quad", new Quad(150, 150));
        //sun.setQueueBucket(RenderQueue.Bucket.Translucent);

        sun.setLocalTranslation(new Vector3f(50, -30, 420));
        sun.setMaterial(sun_mat);

        sun_node.attachChild(sun);
        batchNode.attachChild(sun_node);

    }
    
    private void generateFog() {
        
         //add fog1
        BillboardControl control_fog = new BillboardControl();
        control_fog.setAlignment(BillboardControl.Alignment.Camera);
        fog1_node = new Node();
        fog1_node.addControl(control_fog);
        
        Material fog_mat_1, fog_mat_2, fog_mat_3;
        Texture tex_ml = assetManager.loadTexture("Models/Star/Fog1_Images.png");
        
        fog_mat_1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fog_mat_1.setTexture("ColorMap", tex_ml);
        fog_mat_1.setColor("Color", ColorRGBA.Pink);
        fog_mat_1.getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
        fog_mat_1.getAdditionalRenderState().setDepthTest(false);
        
        fog_mat_2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fog_mat_2.setTexture("ColorMap", tex_ml);
        fog_mat_2.setColor("Color", ColorRGBA.Blue);
        fog_mat_2.getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
        fog_mat_2.getAdditionalRenderState().setDepthTest(false);
        
        fog_mat_3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fog_mat_3.setTexture("ColorMap", tex_ml);
        fog_mat_3.setColor("Color", ColorRGBA.Red);
        fog_mat_3.getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
        fog_mat_3.getAdditionalRenderState().setDepthTest(false);

        Geometry fog1 = new Geometry("Quad", new Quad(450, 450));
        //fog1.setQueueBucket(RenderQueue.Bucket.Translucent);
        //fog1.setQueueBucket(RenderQueue.Bucket.Transparent);
        fog1.setLocalTranslation(fog1_pos);
        fog1.setMaterial(fog_mat_1);
        fog1_node.attachChild(fog1);
        
        Geometry fog2 = new Geometry("Quad", new Quad(400, 400));
        //fog2.setQueueBucket(RenderQueue.Bucket.Translucent);
        //fog1.setQueueBucket(RenderQueue.Bucket.Transparent);
        fog2.setLocalTranslation(fog2_pos);
        fog2.setMaterial(fog_mat_2);
        fog1_node.attachChild(fog2);
        
        Geometry fog3 = new Geometry("Quad", new Quad(350, 350));
        //fog3.setQueueBucket(RenderQueue.Bucket.Translucent);
        //fog1.setQueueBucket(RenderQueue.Bucket.Transparent);
        fog3.setLocalTranslation(fog3_pos);
        fog3.setMaterial(fog_mat_3);
        fog1_node.attachChild(fog3);
        
        batchNode.attachChild(fog1_node);
    }
}
