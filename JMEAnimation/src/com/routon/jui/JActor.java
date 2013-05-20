package com.routon.jui;

import android.util.Log;

import com.jme3.input.event.TouchEvent;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class JActor extends Geometry implements JActorGene {
	private static final String TAG = "JActor";
	
	private static final String LIGHTING_MATERIAL = "Common/MatDefs/Light/Lighting.j3md";
	private static final String LIGHTING_MATERIAL_TEXTURE_NAME = "DiffuseMap";
	private static final String UNSHADED_MATERIAL = "Common/MatDefs/Misc/Unshaded.j3md";
	private static final String UNSHADED_MATERIAL_TEXTURE_NAME = "ColorMap";
	
	private boolean enableLighting = false;
	
	private float width = 0.0f;
	private float height = 0.0f;
	
	private boolean fixOnLT = false;
	private float xOnLT = 0.0f;
	private float yOnLT = 0.0f;
	
	private JActorKeyEventListener keyEventListener = null;
	
	public JActor(String name) {
		super(name);
		
		setMaterial(new Material(JStage.getGlobalStageLog().getAssetManager(), UNSHADED_MATERIAL));
		setQueueBucket(Bucket.Transparent);
		
		getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);		// enable alpha blend
		getMaterial().getAdditionalRenderState().setDepthTest(false);				// disable depth test 
	}
	
	public void setupMesh(float width, float height) {
		setupMesh(width, -height, false);
	}
	
	public void setupMesh(float width, float height, boolean isCameraSpace) {
		// TODO: width and height record is in a uncertain space . camera / screen ?
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		
		if (isCameraSpace == false) {
			width = JStage.S2Cw(width);
			height = -JStage.S2Ch(height);			// y flip
		}

		setMesh(new JQuad(width, height));
		
		updateFixOnLT();
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setupTexture(Texture texture) {
		if (enableLighting == true) {
			setupTexture(LIGHTING_MATERIAL_TEXTURE_NAME, texture);
		}
		else {
			setupTexture(UNSHADED_MATERIAL_TEXTURE_NAME, texture);
		}
	}
	
	public void setupTexture(String name, Texture texture) {
		getMaterial().setTexture(name, texture);
	}
	
	public void setEnableLighting(boolean enable) {
		if (enableLighting != enable) {
			MatParamTexture matTex;
			
			if (enable == true) {
				matTex = getMaterial().getTextureParam(UNSHADED_MATERIAL_TEXTURE_NAME);

				setMaterial(new Material(JStage.getGlobalStageLog().getAssetManager(), LIGHTING_MATERIAL));
				if (matTex != null) {
					getMaterial().setTexture(LIGHTING_MATERIAL_TEXTURE_NAME, matTex.getTextureValue());
				}
			}
			else {
				matTex = getMaterial().getTextureParam(LIGHTING_MATERIAL_TEXTURE_NAME);
				
				setMaterial(new Material(JStage.getGlobalStageLog().getAssetManager(), UNSHADED_MATERIAL));
				if (matTex != null) {
					getMaterial().setTexture(UNSHADED_MATERIAL_TEXTURE_NAME, matTex.getTextureValue());
				}
			}
			
			getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			enableLighting = enable;
		}
	}
	
	@Override
	public void requestKeyFocus() {
		jActorRequsetKeyFocus(this);
	}
	
	@Override
	public JActorGene getJActorParent() {
		return jGetJActorParent(this);
	}

	@Override
	public void setFixOnLT(boolean fix) {
		setFixOnLT(xOnLT, yOnLT, fix);
	}
	
	@Override
	public void setFixOnLT(float x, float y, boolean fix) {
		xOnLT = x;
		yOnLT = y;
		
		fixOnLT = fix;
		
		updateFixOnLT();
	}
	
	@Override
	public boolean onEvent(String name, TouchEvent evt, float tpf) {
		return jActorOnEvent(this, name, evt, tpf);
	}
	
	@Override
	public void setOnKeyEventListener(JActorKeyEventListener listener) {
		keyEventListener = listener;
	}
	
	@Override
	public JActorKeyEventListener getOnKeyEventListener() {
		return keyEventListener;
	}
	
	static public void jActorRequsetKeyFocus(JActorGene actor) {
		JStage.getGlobalStageLog().setKeyFocus(actor);
	}
	
	static public JActorGene jGetJActorParent(JActorGene actor) {
		Node parent = null;
		
		if (actor instanceof Spatial) {
			parent = ((Spatial) actor).getParent();
			
			if (parent != null && !(parent instanceof JActorGene)) {
				parent = null;
			}
		}
		
		return (JActorGene)parent;
	}
	
	static public boolean jActorOnEvent(JActorGene actor, String name, TouchEvent evt, float tpf) {
		TouchEvent.Type evtType = evt.getType();
		
		boolean evtDone = false;
		do {
			if (evtType == TouchEvent.Type.KEY_DOWN) {
				JActorKeyEventListener listener = actor.getOnKeyEventListener();
				
				if (listener != null) {
					evtDone = listener.onKeyDown(actor, evt, tpf);
				}
			}
			else if (evtType == TouchEvent.Type.KEY_UP) {
				JActorKeyEventListener listener = actor.getOnKeyEventListener();
				
				if (listener != null) {
					evtDone = listener.onKeyUp(actor, evt, tpf);
				}
			}
		} while (evtDone == false && (actor = actor.getJActorParent()) != null);
		
		return evtDone;
	}
	
	// -------------------------------------------------------------
	private void updateFixOnLT() {
		// TODO: only works on screen space		
		if (fixOnLT) {
			setLocalTranslation(JStage.S2Cx(xOnLT + width / 2), JStage.S2Cy(yOnLT + height / 2), 0.0f);
		}
	}
}
