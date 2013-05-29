package com.routon.jui;

import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;

public class JActorGroup extends Node implements JActorGene {
	private float mWidth;
	private float mHeight;
	
	private JActorKeyEventListener keyEventListener = null;
	
	public JActorGroup(String name) {
		super(name);
	}

	@Override
	public void requestKeyFocus() {
		JActor.jActorRequsetKeyFocus(this);
	}
	
	@Override
	public JActorGene getJActorParent() {
		return JActor.jGetJActorParent(this);
	}

	@Override
	public void setFixOnLT(boolean fix) {
		// TODO: 
	}
	
	@Override
	public void setFixOnLT(float x, float y, boolean fix) {
		// TODO:  
		setLocalTranslation(x, y, 0);
	}
	
	public void setupSize(float width, float height) {
		setupSize(width, height, false);
	}
	
	@Override
	public void setupSize(float width, float height, boolean isCameraSpace) {
		if (isCameraSpace == false) {
			width = JStage.S2Cw(width);
			height = JStage.S2Ch(height);
		}
		
		this.mWidth = width;
		this.mHeight = height;
	}

	@Override
	public float getWidth() {
		return mWidth;
	}
	
	@Override
	public float getHeight() {
		return mHeight;
	}
	
	@Override
	public boolean onEvent(String name, TouchEvent evt, boolean bubble, float tpf) {
		return JActor.jActorOnEvent(this, name, evt, bubble, tpf);
	}

	@Override
	public void setOnKeyEventListener(JActorKeyEventListener listener) {
		keyEventListener = listener;
	}

	@Override
	public JActorKeyEventListener getOnKeyEventListener() {
		return keyEventListener;
	}
}
