package com.routon.jui;

import com.jme3.app.SimpleApplication;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public abstract class JStage extends SimpleApplication implements TouchListener {
	private static JStage GLOBAL_STAGE_LOG = null;
	
	private JActorGene keyFocusedActor = null;
	
	@Override
    public void initialize() {
		GLOBAL_STAGE_LOG = this;
		
		super.initialize();
		
		initKeys();
		
		// register JUI Loader
		assetManager.registerLoader(com.routon.jui.JAnimLoader.class, "anim");
	}
	
	public void setKeyFocus(JActorGene focus) {
		if (focus instanceof Spatial && keyFocusedActor != focus && ((Spatial) focus).isVisible()) {
			// TODO: emit focus in and focus out signal
			keyFocusedActor = focus;
		}
	}
	
	public JActorGene getKeyFocus() {
		return keyFocusedActor;
	}
	
	private void initKeys() {
		inputManager.addMapping("Touch", new TouchTrigger(TouchInput.ALL));
		
		inputManager.addListener(this, new String[]{"Touch"});
	}
	
	public void onTouch(String name, TouchEvent evt, float tpf) {
		if (keyFocusedActor == null || keyFocusedActor.onEvent(name, evt, true, tpf) == false) { 
			onEvent(name, evt, tpf);
		}
	}
	
	static public JStage getGlobalStageLog() {
		return GLOBAL_STAGE_LOG;
	}
	
	static public float S2Cw(float w) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cx = cam.getFrustumRight() * cam.getLocation().z / cam.getFrustumNear();
		
		return (w / cam.getWidth()) * cx * 2;
	}
	
	static public float C2Sw(float w) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cx = cam.getFrustumRight() * cam.getLocation().z / cam.getFrustumNear();
		
		return  w * cam.getWidth() / cx / 2.0f;
	}
	
	static public float S2Cx(float x) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cx = cam.getFrustumRight() * cam.getLocation().z / cam.getFrustumNear();
		
		x *= 2;
		return (x / cam.getWidth() - 1) * cx;
	}
	
	static public float S2Ch(float h) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cy = cam.getFrustumTop() * cam.getLocation().z / cam.getFrustumNear();
		
		return (h / cam.getHeight()) * cy * 2;
	}
	
	static public float C2Sh(float h) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cy = cam.getFrustumTop() * cam.getLocation().z / cam.getFrustumNear();
		
		return h * cam.getHeight() / cy / 2.0f;
	}
	
	static public float S2Cy(float y) {
		Camera cam = GLOBAL_STAGE_LOG.getCamera();
		
		float cy = cam.getFrustumTop() * cam.getLocation().z / cam.getFrustumNear();
		
		y *= 2;
		return (1 - y / cam.getHeight()) * cy;
	}
	
	abstract public void onEvent(String name, TouchEvent evt, float tpf);
}
