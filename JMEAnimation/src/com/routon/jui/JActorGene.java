package com.routon.jui;

import com.jme3.input.event.TouchEvent;

public interface JActorGene {
	public void requestKeyFocus();
	public JActorGene getJActorParent();

	public void setFixOnLT(boolean fix);
	public void setFixOnLT(float x, float y, boolean fix);
	
	public void setupSize(float width, float height);
	public void setupSize(float width, float height, boolean isCameraSpace);
	
	public float getWidth();
	public float getHeight();
	
	public boolean onEvent(String name, TouchEvent evt, boolean bubble, float tpf);
	
	public void setOnKeyEventListener(JActorKeyEventListener listener);
	public JActorKeyEventListener getOnKeyEventListener();
}
