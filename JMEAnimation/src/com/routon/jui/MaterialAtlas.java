package com.routon.jui;

import java.util.HashMap;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Mesh;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.routon.jui.AtlasRectangleMap.AtlasRectangleMapEntry;

public class MaterialAtlas {
	private static final String MATERIAL_ATLAS_SHADER = "Common/MatDefs/Misc/Unshaded.j3md";
	private static final int MAX_TEXTURE_SIZE = 2048;		// TODO : query from GLES library
	
	private RenderManager renderManager;
	private AssetManager assetManager;
	private Texture2D texture;
	private AtlasRectangleMap map;
	
	private HashMap<Mesh, Material> materialAtlasHash;
	
	public MaterialAtlas(RenderManager renderManager, AssetManager assetManager) {
		texture = new Texture2D(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, Image.Format.RGBA8);
		this.renderManager = renderManager;
		this.assetManager = assetManager;
		
		map = new AtlasRectangleMap(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
		
		materialAtlasHash = new HashMap<Mesh, Material>();
	}
	
	public Material getMaterial(String path) {
		MaterialCell cell = new MaterialCell(this, assetManager, MATERIAL_ATLAS_SHADER);
		
		cell.setTexture("ColorMap", texture);
		return cell;
	}
	
	private AtlasRectangleMapEntry reserveEntry(int width, int height, Mesh mesh) {
		// check if we can fit the rectangle into the existing map
		AtlasRectangleMapEntry entry = null;
		
		if (width * height > map.getRemainingSize())	// quick reject 
			return null;
		
		try {
			entry = map.addEntry(width, height, mesh);
		} catch (JuiException e) {
			e.printStackTrace();
		}
		
		if (entry != null) {
			entry.updateMeshTextureMap(mesh, MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
			
			return entry;
		}
		
		// if we make it here then we need to reorganize the atlas
		Texture2D dst = new Texture2D(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, Image.Format.RGBA8);
		map.reorganize(renderManager, assetManager, dst, texture);
		texture = dst;
		
		// try it again
		try {
			entry = map.addEntry(width, height, mesh);
		} catch (JuiException e) {
			e.printStackTrace();
		}
		
		if (entry != null) {
			entry.updateMeshTextureMap(mesh, MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE);
		}
		
		return entry;
	}
	
	private void removeEntry(AtlasRectangleMapEntry entry) {
		map.removeEntry(entry);
	}
}
