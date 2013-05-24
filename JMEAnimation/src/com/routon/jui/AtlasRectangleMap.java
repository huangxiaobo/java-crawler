package com.routon.jui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.Vector;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture2D;

public class AtlasRectangleMap {
	private static final String ATLAS_PRINTER_SHADER = "Common/MatDefs/Misc/Unshaded.j3md";
	
	private AtlasRectangleMapNode root;
	
	private int width, height;
	private int nRectangles;
	private int spaceRemaining;
	
	private Stack<AtlasTravellingTrack> travellingTrack;
	
	private Vector<AtlasRectangleMapEntry> lazyDelList;		// protect our critical data structure(AtlasRectangleMapNode tree) from GC

	public AtlasRectangleMap(int width, int height) {
		renew(width, height);
	}
	
	public AtlasRectangleMapEntry addEntry(int width, int height, Mesh mesh) throws JuiException {
		if (width <= 0 || height <= 0) 
			return null;
		
		// check if we have any entry need to delete
		commitLazyDelList();
		
		AtlasRectangleMapNode foundNode = null;
		int rectangleSize = width * height;
		
		// start with root node
		travellingTrack.clear();
		travellingTrack.push(new AtlasTravellingTrack(root, 0));
		
		// depth-first search for an empty node that is big enough
		while (travellingTrack.size() > 0) {
			AtlasTravellingTrack track = travellingTrack.pop();
			
			// Regardless of the type of the node, there's no point
			// descending any further if the new rectangle won't fit within it
			if (width >= track.node.entry.width && height >= track.node.entry.height && track.node.largestGap >= rectangleSize) {
				if (track.node.type == AtlasRectangleMapNodeType.EmptyLeaf) {
					// find a node we can use
					foundNode = track.node;
				}
				else if (track.node.type == AtlasRectangleMapNodeType.Branch) {
					if (track.nextTrack == 1) {
						// try right branch 
						travellingTrack.push(new AtlasTravellingTrack(track.node.right, 0));
					}
					else {
						// make sure we remember to try the right branch once we've finished descending the left branch
						travellingTrack.push(new AtlasTravellingTrack(track.node, 1));
						
						// try left branch 
						travellingTrack.push(new AtlasTravellingTrack(track.node.left, 0));
					}
				}
			}
		}
		
		if (foundNode != null) {
			// Split according to whichever axis will leave us with the largest space
			if (foundNode.entry.width - width >= foundNode.entry.height - height) {
				foundNode = atlasMapSplitHorizontally(foundNode, width);
				foundNode = atlasMapSplitVertically(foundNode, height);
			}
			else {
				foundNode = atlasMapSplitVertically(foundNode, height);
				foundNode = atlasMapSplitHorizontally(foundNode, width);
			}
			
			foundNode.type = AtlasRectangleMapNodeType.FilledLeaf;
			foundNode.mesh = mesh;
			foundNode.largestGap = 0;
			
			// walk back up the tree and update the stored largest gap for the node's sub tree
			for (AtlasRectangleMapNode node = foundNode.parent; node != null; node = node.parent) {
				// this node is a parent so it should always be a branch
				if (node.type != AtlasRectangleMapNodeType.Branch) {
					throw new JuiException("AtlasRectangleMapNode is a parent so it must always be a branch type. : NOW its type = " + node.type);
				}
				
				node.largestGap = Math.max(node.left.largestGap, node.right.largestGap);
			}
			
			// there is now an extra rectangle in the map
			nRectangles++;
			spaceRemaining -= rectangleSize;
			
			return foundNode.entry;
		}
		else 
			return null;
	}

	public void removeEntry(AtlasRectangleMapEntry entry) {
		lazyDelList.add(entry);
	}
	
	public boolean reorganize(RenderManager renderManager, AssetManager assetManager, Texture2D dst, Texture2D src) {		// TODO: we may lose control of some entries permanently if interrupted by GC ???
		// check if we have any entry need to delete
		commitLazyDelList();
		
		// get an array of all the textures currently in the atlas.
		ArrayList<AtlasRepositionData> reposition = new ArrayList<AtlasRepositionData>(nRectangles);
		travelling(new NodeProcessor(){
			@Override
			public void processor(AtlasRectangleMapNode node, Object data) {
				if (!(data instanceof ArrayList<?>))
					return ;
				
				@SuppressWarnings("unchecked")
				ArrayList<AtlasRepositionData> r = (ArrayList<AtlasRepositionData>)data;
				if (node.type == AtlasRectangleMapNodeType.FilledLeaf) {
					AtlasRepositionData d = new AtlasRepositionData();
					d.mesh = node.mesh;
					d.oldEntry = node.entry;
					
					r.add(d);
				}
			}}, reposition);
		
		// The atlas algorithm works a lot better if the rectangles are added in decreasing order of size so we'll first sort the array
		AtlasRepositionComparator comparator = new AtlasRepositionComparator();
		Collections.sort(reposition, comparator);
		
		renew(width, height);
		
		// build atlas mesh
		for (int i = 0; i < reposition.size(); i++) {
			AtlasRepositionData d = reposition.get(i);
			try {
				d.newEntry = addEntry(d.oldEntry.width, d.oldEntry.height, d.mesh);
			} catch (JuiException e) {
				// we should never reach here
				e.printStackTrace();
			} 
			
			d.newEntry.updateMeshTextureMap(d.mesh, width, height);
		}
		Mesh atlasMesh = new AtlasMesh(reposition, width, height);
		
		FrameBuffer fb = new FrameBuffer(dst.getImage().getWidth(), dst.getImage().getHeight(), 1);
		fb.setColorTexture(dst);
		
		Material printer = assetManager.loadMaterial(ATLAS_PRINTER_SHADER);
		printer.setTexture("ColorMap", src);
		Geometry atlasPrinter = new Geometry("atlas printer", atlasMesh);
		atlasPrinter.setMaterial(printer);
		
		// print the new atlas by FBO rendering
		ViewPort atlasView = new ViewPort("atlas viewport", new Camera(width, height));
		atlasView.setClearFlags(true, false, false);
		atlasView.setBackgroundColor(ColorRGBA.BlackNoAlpha);
		
		Renderer render = renderManager.getRenderer();
		render.setFrameBuffer(fb);
		
		Camera camera = renderManager.getCurrentCamera();
		
		renderManager.setCamera(atlasView.getCamera(), true);
		render.clearBuffers(true, false, false);
		renderManager.renderScene(atlasPrinter, atlasView);
		renderManager.flushQueue(atlasView);
		
		renderManager.setCamera(camera, false);		// go back to previous camera
		render.setFrameBuffer(null);	// go back to default rendering
		return true;
	}
	
	public int getRemainingSize() {
		return spaceRemaining;
	}
	
	public int getRectangles() {
		return nRectangles;
	}
	
	private void commitLazyDelList() {
		// check if we have any entry need to delete
		for (int i = lazyDelList.size() - 1; i >= 0; i--) {
			try {
				AtlasRectangleMapEntry entry = lazyDelList.remove(i);
					
				doRemoveEntry(entry);
			} catch (JuiException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				// do nothing here
			}	
		}
	}
	
	private AtlasRectangleMapNode atlasMapSplitVertically(AtlasRectangleMapNode node, int height) {
		if (node.entry.height == height)
			return node;
		
		AtlasRectangleMapNode topNode = new AtlasRectangleMapNode();
		topNode.type = AtlasRectangleMapNodeType.EmptyLeaf;
		topNode.parent = node;
		topNode.entry = new AtlasRectangleMapEntry(node.entry.x, node.entry.y, node.entry.width, height);
		topNode.largestGap = topNode.entry.width * topNode.entry.height;
		
		AtlasRectangleMapNode bottomNode = new AtlasRectangleMapNode();
		bottomNode.parent = node;
		bottomNode.entry = new AtlasRectangleMapEntry(node.entry.x + height, node.entry.y, node.entry.width, node.entry.height - height);
		bottomNode.largestGap = bottomNode.entry.width * bottomNode.entry.height;
		
		node.left = topNode;
		node.right = bottomNode;
		node.type = AtlasRectangleMapNodeType.Branch;
		
		return topNode;
	}

	private AtlasRectangleMapNode atlasMapSplitHorizontally(AtlasRectangleMapNode node, int width) {
		if (node.entry.width == width)
			return node;
		
		AtlasRectangleMapNode leftNode = new AtlasRectangleMapNode();
		leftNode.type = AtlasRectangleMapNodeType.EmptyLeaf;
		leftNode.parent = node;
		leftNode.entry = new AtlasRectangleMapEntry(node.entry.x, node.entry.y, width, node.entry.height);
		leftNode.largestGap = leftNode.entry.width * leftNode.entry.height;
		
		AtlasRectangleMapNode rightNode = new AtlasRectangleMapNode();
		rightNode.parent = node;
		rightNode.entry = new AtlasRectangleMapEntry(node.entry.x + width, node.entry.y, node.entry.width - width, node.entry.height);
		rightNode.largestGap = rightNode.entry.width * rightNode.entry.height;
		
		node.left = leftNode;
		node.right = rightNode;
		node.type = AtlasRectangleMapNodeType.Branch;
		
		return leftNode;
	}
	
	private void doRemoveEntry(AtlasRectangleMapEntry entry) throws JuiException {
		AtlasRectangleMapNode node = root;
		int rectangleSize = entry.width * entry.height;
		
		// do a binary-chop down the search tree to find the rectangle
		while (node.type == AtlasRectangleMapNodeType.Branch) {
			if (entry.x < node.left.entry.x + node.left.entry.width && entry.y < node.left.entry.y + node.left.entry.height) {
				node = node.left;
			}
			else {
				node = node.right;
			}
		}
		
		// make sure we found the right one
		if (node.type == AtlasRectangleMapNodeType.FilledLeaf && 
			node.entry.x == entry.x && 
			node.entry.y == entry.y && 
			node.entry.width == entry.width && 
			node.entry.height == entry.height) {
			
			// convert the node back to an empty node
			node.type = AtlasRectangleMapNodeType.EmptyLeaf;
			node.largestGap = rectangleSize;
			
			// walk back up the tree combining branch nodes that have two empty leaves back into a single empty leaf
			for (node = node.parent; node != null; node = node.parent) {
				// this node is a parent so it should always be a branch
				if (node.type != AtlasRectangleMapNodeType.Branch) {
					throw new JuiException("AtlasRectangleMapNode is a parent so it must always be a branch type. : NOW its type = " + node.type);
				}
				
				if (node.left.type == AtlasRectangleMapNodeType.EmptyLeaf && node.right.type == AtlasRectangleMapNodeType.EmptyLeaf) {
					node.left = null;
					node.right = null;
					
					node.type = AtlasRectangleMapNodeType.EmptyLeaf;
					node.largestGap = node.entry.width * node.entry.height;
				}
				else {
					break;
				}
			}
			
			// reduce the amount of space remaining in all of the parents further up the chain
			for ( ; node != null; node = node.parent) {
				node.largestGap = Math.max(node.left.largestGap, node.right.largestGap);
			}
			
			// there is now one less rectangle
			nRectangles--;
			if (nRectangles < 0) {
				throw new JuiException("The number of rectangles in a atlas must be >= 0. : NOW = " + nRectangles);
			}
			
			// add more space
			spaceRemaining += rectangleSize;
		}
		else {
			// something has gone wrong when we gets here 
			// should only happen if someone tried to remove a rectangle that was not in the map
		}
	}
	
	private void travelling(NodeProcessor nprocessor, Object data) {
		// start with root node
		travellingTrack.clear();
		travellingTrack.push(new AtlasTravellingTrack(root, 0));
		
		while (travellingTrack.size() > 0) {
			AtlasTravellingTrack track = travellingTrack.peek();
			
			switch (track.node.type) {
			case Branch:
				if (track.nextTrack == 0) {
					// next time we come back to this node, go to the right
					track.nextTrack = 1;
					
					// explore the left branch next
					travellingTrack.push(new AtlasTravellingTrack(track.node.left, 0));
				}
				else if (track.nextTrack == 1) {
					// next time we come back to this node, stop processing it
					track.nextTrack = 2;
					
					// explore the right branch next
					travellingTrack.push(new AtlasTravellingTrack(track.node.right, 0));
				}
				else {
					// we're finished with this node so we can call the callback
					nprocessor.processor(track.node, data);
					
					travellingTrack.pop();
				}
				break;
				
			default : // leaf
				nprocessor.processor(track.node, data);
				
				travellingTrack.pop();
				break;
			}
		}
	}
	
	private void renew(int w, int h) {
		root = new AtlasRectangleMapNode(width, height);
		
		width = w;
		height = h;
		nRectangles = 0;
		spaceRemaining = width * height;
		
		travellingTrack = new Stack<AtlasTravellingTrack>();
		lazyDelList = new Vector<AtlasRectangleMapEntry>(4);
	}
	
	class AtlasRectangleMapEntry
	{
		int x, y;
		int width, height;
		
		public AtlasRectangleMapEntry(int width, int height) {
			x = 0; 
			y = 0; 
			this.width = width;
			this.height = height;
		}
		
		public AtlasRectangleMapEntry(int x, int y, int width, int height) {
			this.x = x; 
			this.y = y; 
			this.width = width;
			this.height = height;
		}

		public void updateMeshTextureMap(Mesh mesh, int textureWidth, int textureHeight) {
			if (x + width > textureWidth || y + height > textureHeight) 
				return ;
			
//			mesh.setTexCoordsRemap(new Vector4f((float)(x + 1) / textureWidth, (float)(y + 1) / textureHeight, (float)(width - 2) / textureWidth, (float)(height - 2) / textureHeight));
		}
	};
	
	class AtlasRepositionData {
		Mesh mesh;
		
		AtlasRectangleMapEntry oldEntry;
		AtlasRectangleMapEntry newEntry;
	}
	
	private class AtlasRectangleMapNode {
		AtlasRectangleMapNodeType type;
		
		AtlasRectangleMapNode parent;
		
		AtlasRectangleMapEntry entry;
		int largestGap;
		
		// fields used when this is a branch
		AtlasRectangleMapNode left;
		AtlasRectangleMapNode right;
		
		// field used when this is a filled leaf
		Mesh mesh; 
		
		public AtlasRectangleMapNode() {
			// all empty
		}
		
		public AtlasRectangleMapNode(int width, int height) {
			type = AtlasRectangleMapNodeType.EmptyLeaf;
			parent = null;
			
			entry = new AtlasRectangleMapEntry(width, height);
			largestGap = width * height;
			
			left = null;
			right = null;
			
			mesh = null;
		}
	}
	
	private enum AtlasRectangleMapNodeType {
		Branch, 
		FilledLeaf, 
		EmptyLeaf, 
	}
	
	private class AtlasTravellingTrack {
		AtlasRectangleMapNode node;
		int nextTrack;					// either 0 to go left or 1 to go right
		
		public AtlasTravellingTrack(AtlasRectangleMapNode node, int nextTrack) {
			this.node = node;
			this.nextTrack = nextTrack;
		}
	}
	
	interface NodeProcessor {
		void processor(AtlasRectangleMapNode node, Object data);
	}
	
	private class AtlasRepositionComparator implements Comparator<AtlasRepositionData> {
		@Override
		public int compare(AtlasRepositionData arg0, AtlasRepositionData arg1) {
			int size0 = arg0.oldEntry.width * arg0.oldEntry.height;
			int size1 = arg1.oldEntry.width * arg0.oldEntry.height;
			
			return size1 - size0;		// decreasing order
		}		
	}
}
