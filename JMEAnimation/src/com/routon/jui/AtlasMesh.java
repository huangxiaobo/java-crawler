package com.routon.jui;

import java.util.ArrayList;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

import com.routon.jui.AtlasRectangleMap.AtlasRepositionData;

public class AtlasMesh extends Mesh {
	public AtlasMesh(ArrayList<AtlasRepositionData> reposition, int width, int height) {
		updateGeometry(reposition, width, height);
	}

	private void updateGeometry(ArrayList<AtlasRepositionData> reposition, float width, float height) {
		float[] pos = new float[reposition.size() * 4 * 2];
		float[] tex = new float[reposition.size() * 4 * 2];
		short[] index = new short[reposition.size() * 3 * 2];
		
		int p = 0, t = 0, i = 0;
		for (int atlas = 0; atlas < reposition.size(); atlas++) {
			AtlasRepositionData d = reposition.get(atlas);
			
			pos[p++] = d.newEntry.x;
			pos[p++] = d.newEntry.y;
			pos[p++] = d.newEntry.x + d.newEntry.width;
			pos[p++] = d.newEntry.y;
			pos[p++] = d.newEntry.x + d.newEntry.width;
			pos[p++] = d.newEntry.y + d.newEntry.height;
			pos[p++] = d.newEntry.x;
			pos[p++] = d.newEntry.y + d.newEntry.height;
			
			tex[t++] = (d.oldEntry.x) / width;
			tex[t++] = (d.oldEntry.y) / height;
			tex[t++] = (d.oldEntry.x + d.oldEntry.width) / width;
			tex[t++] = (d.oldEntry.y) / height;
			tex[t++] = (d.oldEntry.x + d.oldEntry.width) / width;
			tex[t++] = (d.oldEntry.y + d.oldEntry.height) / height;
			tex[t++] = (d.oldEntry.x) / width;
			tex[t++] = (d.oldEntry.y + d.oldEntry.height) / height;
			
			short offset = (short) (atlas * 4);
			index[i++] = (short) (0 + offset);
			index[i++] = (short) (1 + offset);
			index[i++] = (short) (2 + offset);
			index[i++] = (short) (0 + offset);
			index[i++] = (short) (2 + offset);
			index[i++] = (short) (3 + offset);
		}
		
		setBuffer(Type.Position, 2, pos);
		setBuffer(Type.TexCoord, 2, tex);
		setBuffer(Type.Index, 3, index);
	}
}
