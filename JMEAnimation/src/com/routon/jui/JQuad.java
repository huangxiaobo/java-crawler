package com.routon.jui;

import android.util.Log;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

public class JQuad extends Mesh {
	private static final String TAG = "JQuad";
	
	private float width, height;
	
	public JQuad(float width, float height) {
		this(width, height, 0, 0, false);
	}
	
	public JQuad(float width, float height, boolean flipCoords) {
		this(width, height, 0, 0, flipCoords);
	}
	
	public JQuad(float width, float height, float centerx, float centery) {
		this(width, height, centerx, centery, false);
	}
	
	public JQuad(float width, float height, float centerx, float centery, boolean flipCoords) {
		this.width = width;
		this.height = height;
		
		updateGeometry(width, height, centerx, centery, flipCoords);
	}
	
	public JQuad(float width, float height, float centerx, float centery, float rHeight, float rBrightness, float rTransparency, boolean flipCoords) {
		this.width = width;
		this.height = height;
		
		updateGeometry(width, height, centerx, centery, rHeight, rBrightness, rTransparency, flipCoords);
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	private void updateGeometry(float width, float height, float centerx, float centery, boolean flipCoords) {
		float halfw = width / 2;
		float halfh = height / 2;

		setBuffer(Type.Position, 3, new float[]{centerx - halfw, centery - halfh, 0,
												centerx + halfw, centery - halfh, 0,
												centerx + halfw, centery + halfh, 0,
												centerx - halfw, centery + halfh, 0});

		if (flipCoords){
			setBuffer(Type.TexCoord, 2, new float[]{0, 1,
													1, 1,
													1, 0,
													0, 0});
		}
		else {
			setBuffer(Type.TexCoord, 2, new float[]{0, 0,
													1, 0,
													1, 1,
													0, 1});
		}
		
		setBuffer(Type.Normal, 3, new float[]{0, 0, 1,
											0, 0, 1,
											0, 0, 1,
											0, 0, 1});
		
		if (height < 0) {
			setBuffer(Type.Index, 3, new short[]{0, 2, 1,
												0, 3, 2});
		}
		else {
			setBuffer(Type.Index, 3, new short[]{0, 1, 2,
												0, 2, 3});
		}

		updateBound();
	}
	
	private void updateGeometry(float width, float height, float centerx, float centery, float rHeight, float rBrightness, float rTransparency, boolean flipCoords) {
		float halfw = width / 2;
		float halfh = height / 2;
		
		setBuffer(Type.Position, 3, new float[]{ 
				centerx - halfw, centery - halfh, 0,
				centerx + halfw, centery - halfh, 0,
				centerx + halfw, centery + halfh, 0,
				centerx - halfw, centery + halfh, 0,
				
				centerx - halfw, centery - halfh, 0, 
				centerx + halfw, centery - halfh, 0,
				centerx + halfw, centery - halfh - height * rHeight, 0, 
				centerx - halfw, centery - halfh - height * rHeight, 0, });
		
		if (flipCoords){
			setBuffer(Type.TexCoord, 2, new float[]{
													0, 1,
													1, 1,
													1, 0,
													0, 0, 
													
													0, 1, 
													1, 1, 
													1, 1 - rHeight, 
													0, 1 - rHeight, });
		}
		else {
			setBuffer(Type.TexCoord, 2, new float[]{ 
													0, 0,
													1, 0,
													1, 1,
													0, 1, 
													
													0, 0,
													1, 0, 
													1, rHeight, 
													0, rHeight, });
		}
		
		setBuffer(Type.Color, 4, new float[]{
											1.0f, 1.0f, 1.0f, 1.0f, 
											1.0f, 1.0f, 1.0f, 1.0f, 
											1.0f, 1.0f, 1.0f, 1.0f, 
											1.0f, 1.0f, 1.0f, 1.0f, 
											
											rBrightness, rBrightness, rBrightness, 1.0f, 
											rBrightness, rBrightness, rBrightness, 1.0f,
											0.0f, 0.0f, 0.0f, rTransparency, 
											0.0f, 0.0f, 0.0f, rTransparency, });
		
		setBuffer(Type.Normal, 3, new float[]{
											0, 0, 1,
											0, 0, 1,
											0, 0, 1,
											0, 0, 1, 
											
											0, 0, 1, 
											0, 0, 1, 
											0, 0, 1, 
											0, 0, 1, });
		
		if (height < 0) {
			setBuffer(Type.Index, 3, new short[]{
												0, 2, 1,
												0, 3, 2, 
												7, 4, 5, 
												7, 5, 6, });
		}
		else {
			setBuffer(Type.Index, 3, new short[]{
												0, 1, 2,
												0, 2, 3, 
												7, 5, 4, 
												7, 6, 5, });
		}

		updateBound();
	}
}
