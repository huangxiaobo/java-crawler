package com.routon.jui;

import com.jme3.input.event.TouchEvent;

public interface JActorGene {
	public void requestKeyFocus();
	public JActorGene getJActorParent();

	public void setFixOnLT(boolean fix);
	public void setFixOnLT(float x, float y, boolean fix);
	
	public boolean onEvent(String name, TouchEvent evt, float tpf);
	
	public void setOnKeyEventListener(JActorKeyEventListener listener);
	public JActorKeyEventListener getOnKeyEventListener();
}
