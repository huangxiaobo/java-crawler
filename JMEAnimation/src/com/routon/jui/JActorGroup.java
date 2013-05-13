package com.routon.jui;

import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;

public class JActorGroup extends Node implements JActorGene {
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
	
	@Override
	public boolean onEvent(String name, TouchEvent evt, float tpf) {
		return JActor.jActorOnEvent(this, name, evt, tpf);
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
