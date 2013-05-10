package com.routon.jui;

import com.jme3.input.event.TouchEvent;

public interface JActorKeyEventListener {
	boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf);
	boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf);
}
