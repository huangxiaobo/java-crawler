package com.routon.jui;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

public class JQuad extends Mesh {
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
		updateGeometry(width, height, centerx, centery, flipCoords);
	}
	
	public void updateGeometry(float width, float height, float centerx, float centery, boolean flipCoords) {
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
}
