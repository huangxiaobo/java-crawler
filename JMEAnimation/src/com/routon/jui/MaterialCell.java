package com.routon.jui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

public class MaterialCell extends Material {
	private MaterialAtlas atlas = null;
	
	public MaterialCell(MaterialAtlas materialAtlas, AssetManager assetManager, String defName) {
		super(assetManager, defName);
		
		atlas = materialAtlas;
	}
	
	protected void finalize() throws Throwable {
//		atlas.
		
	    super.finalize();  
	} 	
}
