package com.routon.jme_droid;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.widget.TextView;

import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.system.android.JmeAndroidSystem;
import com.routon.jui.JActor;
import com.routon.jui.JActorGroup;
import com.routon.jui.JDroidView;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JQuad;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

public class HelloJME extends JStage {
	static private String TAG = "HelloJME";
	
	private TextView droidTxt = null;
	private TextView droidLabel = null;
	
	private JDroidView droid = null;
	private float xtrans = 0;
	
	@Override
	public void onEvent(String name, TouchEvent evt, float tpf) {
//		Log.d(TAG, "TouchEvent = " + evt + " tpf = " + tpf);
		
		if (evt.getType() == TouchEvent.Type.KEY_UP) {
			try {
				droidTxt.setText("KEY UP : " + evt.getKeyCode());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

	@Override
	public void simpleInitApp() {
		droidTxt = new TextView((Context)JmeAndroidSystem.getActivity());
		droidTxt.setText("ASK中文");
		droidTxt.setTextSize(38);
		
		droid = new JDroidView("a droid view", droidTxt);
		droid.setFixOnLT(xtrans, 620, true);
		droid.setReflection(true);
		droid.setColorGlass(ColorRGBA.Pink);
		rootNode.attachChild(droid);
		
//		droid.setVisibility(false);
		
		try {
			droidLabel = new TextView((Context)JmeAndroidSystem.getActivity());
			droidLabel.setText("This is a Android View demo on Java Engine.");
			droidLabel.setTextSize(58);
			droidLabel.setWidth(600);
			droidLabel.setSingleLine();
			droidLabel.setEllipsize(TruncateAt.MARQUEE);
			droidLabel.setMarqueeRepeatLimit(-1);
			droidLabel.setSelected(true);
			
			JDroidView label = new JDroidView("a droid view", droidLabel);
			label.setFixOnLT(0, 0, true);
			label.setReflection(true, 0.622f, 1.0f, 0.0f);
			rootNode.attachChild(label);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
//		testProj();
		
		testRollerCoaster();
		
//		testDepth();
	}
	
	private void testProj() {
		Quad quad = new Quad(6, 3);
		Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_stl.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		
		Geometry rect = new Geometry("Rectangle", quad);
	    rect.setMaterial(mat_stl);
	    rect.move(3.0f, 1.0f, 0.0f);
	    rect.rotate(0.0f, 0.8f, 0.0f);
	    rootNode.attachChild(rect);
		
		Geometry rectProj = new Geometry("Rectangle", quad);
		rectProj.setMaterial(mat_stl);
		rectProj.move(3.0f, -4.0f, 0.0f);
		rectProj.rotate(0.0f, 0.8f, 0.0f);
	 
		rectProj.setProjectionCenter(0.0f, -2.5f);
	    rootNode.attachChild(rectProj);

	    Node projC = new Node();
	    JQuad jquad = new JQuad(4.0f, 3.0f);
	    Geometry rectUpvector = new Geometry("Rectangle", jquad);
//	    Box box = new Box(2.0f, 0.3f, 1.5f);
//	    Geometry rectUpvector = new Geometry("Rectangle", box);
	    rectUpvector.setMaterial(mat_stl);
	    rectUpvector.setLocalTranslation(-7.8519f, -5.44365e-007f, -12.4536f);
		rectUpvector.setLocalRotation(new Quaternion(0.653282f, 0.270598f, -0.270598f, 0.653281f));
		
//		rectUpvector.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/*normal*/, new Vector3f(0.0f, 0.0f, -1.0f)/*up vector*/);	
		projC.attachChild(rectUpvector);
		
		projC.move(0.0f, -2f, 0.0f);
		projC.setProjectionCenterY(-2f);
		projC.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/*normal*/, new Vector3f(0.0f, 0.0f, -1.0f)/*up vector*/);	
		rootNode.attachChild(projC);
	}
	
	private void testRollerCoaster() {
		AnimationFactory animationFactory = new AnimationFactory(4.0f, "anim", 2);
		
		animationFactory.addTimeTranslation(0, new Vector3f(-3.0f, 1.5f, 1.0f));
		animationFactory.addTimeTranslation(1, new Vector3f(-1.5f, 3.0f, 2.0f));
		animationFactory.addTimeTranslation(2, new Vector3f( 0.0f, 2.5f, -1.0f));
		animationFactory.addTimeTranslation(3, new Vector3f( 1.5f, -1.5f, 0.0f));
		animationFactory.addTimeTranslation(3.5f, new Vector3f( 2.5f, 0.5f, 2.0f));
		animationFactory.addTimeTranslation(4.0f, new Vector3f( 3.0f, 3.5f, -1.0f));
		
		animationFactory.addTimeColorGlass(0, new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
		animationFactory.addTimeColorGlass(1, new ColorRGBA(0.8f, 0.8f, 0.8f, 0.8f));
		animationFactory.addTimeColorGlass(2, new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		animationFactory.addTimeColorGlass(3, new ColorRGBA(0.8f, 0.8f, 0.8f, 0.8f));
		animationFactory.addTimeColorGlass(4, new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
		
		Animation animation = animationFactory.buildAnimation();
		
		JFocusStrategy focusStrategy = new JFocusStrategy(2, new float[] {0, 1, 2, 3, 4});
		JRollerCoaster rollerCoaster = new JRollerCoaster("roller coaster", animation, focusStrategy);
		rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);
/*		
		JQuad jQuad = new JQuad(1.0f, 1.0f);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		
		for (int i = 0; i < 8; i++) {
			Geometry child = new Geometry("child " + i, jQuad);
			child.setMaterial(mat);
			
			rollerCoaster.attachChild(child);
		}
*/
		for (int i = 0; i < 10; i++) {
			TextView alphabet = new TextView((Context)JmeAndroidSystem.getActivity());
			alphabet.setText("" + i);
			alphabet.setTextColor(0xFFFF0000);
			alphabet.setTextSize(88);
			
			JDroidView rcChild = new JDroidView("RC Child " + i, alphabet);
//			rcChild.setEnableLighting(true);
			rollerCoaster.attachChild(rcChild);
		}
		
		rollerCoaster.requestKeyFocus();
		rollerCoaster.setShadowMode(ShadowMode.Cast);
		rootNode.attachChild(rollerCoaster);
		
		JActor bg = new JActor("background");
		bg.setupMesh(918, 398);
		bg.setupTexture(assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		bg.setLocalTranslation(0.0f, 0.0f, -2.0f);
//		bg.setEnableLighting(true);
		bg.setShadowMode(ShadowMode.Receive);
		bg.setReflection(true);
		rootNode.attachChild(bg);
		
//		testSpotLight();
//		testDirectionalLight();
	}
	
	private void testDepth() {
		JActorGroup group = new JActorGroup("depth group");
		
		for (int i = 0; i < 10; i++) {
			TextView alphabet = new TextView((Context)JmeAndroidSystem.getActivity());
			alphabet.setText(i + "");
			alphabet.setTextSize(226);
			
			JDroidView child = new JDroidView("Depth Child " + i, alphabet);
			group.attachChild(child);
			
			if (i % 2 == 0) {
				child.setLocalTranslation(0.3f * i, -0.3f * i, 0.005f);
			}
			else {
				child.setLocalTranslation(0.3f * i, -0.3f * i, 0.0f);
			}
		}
		
		group.setLocalTranslation(0.0f, 0.2f, 0.0f);
		rootNode.attachChild(group);
	}
	
	private void testSpotLight() {
		// add a spot light
		SpotLight spot = new SpotLight();

        spot.setSpotRange(1000);
        spot.setSpotInnerAngle(6.0f * FastMath.DEG_TO_RAD);
        spot.setSpotOuterAngle(22.0f * FastMath.DEG_TO_RAD);
        spot.setPosition(new Vector3f(0.0f, 0.0f, 9.0f));
        spot.setDirection(new Vector3f(0.0f, 0.0f, -1.0f));
        rootNode.addLight(spot);
        
        // add spot light shadow
//        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 512);
//        slsr.setLight(spot);       
//        slsr.setShadowIntensity(0.5f);
//        slsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
//        viewPort.addProcessor(slsr);
//        
//        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 512);
//        slsf.setLight(spot);    
//        slsf.setShadowIntensity(0.5f);
//        slsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);  
//        slsf.setEnabled(false);
//        
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(slsf);
//        viewPort.addProcessor(fpp);
	}
	
	private void testDirectionalLight() {
		// add a directional light
		DirectionalLight l = new DirectionalLight();
        l.setDirection(new Vector3f(-1, -1, -1));
        rootNode.addLight(l);
        
		// add directional light shadow
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512, 1);
        dlsr.setLight(l);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
//        dlsr.displayDebug();
        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 512, 1);
        dlsf.setLight(l);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(false);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		xtrans += tpf * 12;
		if (xtrans > 1200) 
			xtrans = 0;
		
		droid.setFixOnLT(xtrans, 620, true);
		
//		if (xtrans % 120 == 0) {
//			Log.d(TAG, " ------- tpf = " + tpf);
//		}
    }
}
