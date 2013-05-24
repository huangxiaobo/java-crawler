package com.routon.jui;

import com.jme3.asset.AndroidImageInfo;
import com.jme3.input.event.TouchEvent;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class JDroidView extends JActor {
	private static final String TAG = "JDroidView";
	
	private static final int DROID_BITMAP_WIDTH = 2048;
	private static final int DROID_BITMAP_HEIGHT = 1024;
	
	private static final Bitmap DROID_Bitmap = Bitmap.createBitmap(DROID_BITMAP_WIDTH, DROID_BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
	private static final Canvas DROID_Canvas = new Canvas(DROID_Bitmap);

	private View droidView = null;
	private Image jmeImage = null;
	
	private Texture2D jmeTexture = null;
	private int texWidth = 0;
	private int texHeight = 0;
	
	private JDroidViewRenderTarget droidViewRenderTarget = new JDroidViewRenderTarget(DROID_Bitmap);
	
	private Rect drawingRect = new Rect();
	
	public JDroidView(String name) {
		super(name);
		
		jmeImage = new Image(Image.Format.RGBA8, 1, 1, null);
		jmeTexture = new Texture2D(jmeImage);
		
		setupTexture(jmeTexture);
	}
	
	public JDroidView(String name, View view) {
		this(name);
			
		setDroidView(view);
	}
	
	public JDroidView(String name, int resID) {
		this(name);
		
		LayoutInflater inflater = JmeAndroidSystem.getActivity().getLayoutInflater();
		setDroidView(inflater.inflate(resID, null)); 
	}
	
	public void setDroidView(View view) {
		droidView = view;
		
		if (view.getLayoutParams() == null) {
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		updateDroidViewMaterial();
	}
	
	public View getDroidView() {
		return droidView;
	}
	
	@Override
	public void ready4Rendering() {
		droidView.getDrawingRect(drawingRect);
		
		if (drawingRect.isEmpty() == false) {
			updateDroidViewMaterial();
		}
	}

	@Override
	public void requestKeyFocus() {
		super.requestKeyFocus();
		
		// Android View needs focus to make dispatch(Key/Touch/...)Event works
		droidView.setFocusable(true);
		droidView.requestFocus();
	}
	
	@Override
	public boolean onEvent(String name, TouchEvent evt, float tpf) {
		// dispatch event to Android View first
		boolean evtDone = false;
		try {
			evtDone = dispatch2DroidView(evt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return evtDone ? evtDone : super.onEvent(name, evt, tpf);
	}
	
	private boolean dispatch2DroidView(TouchEvent evt) {
		Object droidEvent = evt.getBackendEvent();
		
		if (droidEvent == null) {
			return false;
		}
		else if (droidEvent instanceof KeyEvent) {
			return droidView.dispatchKeyEvent((KeyEvent) droidEvent);
		}
		else if (droidEvent instanceof MotionEvent) {
			return droidView.dispatchTouchEvent((MotionEvent) droidEvent);
		}
		else {
			return false;
		}
	}

	private void updateDroidViewMaterial() {
		if (droidView == null) 
			return ;
		
		droidView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int width = droidView.getMeasuredWidth();
		int height = droidView.getMeasuredHeight();

		boolean subUpdate = (getMesh() != null) && (width == texWidth) && (height == texHeight);
		
		// clear canvas 
		DROID_Canvas.save(Canvas.CLIP_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);
		
		if (width > DROID_BITMAP_WIDTH || height > DROID_BITMAP_HEIGHT) {
			float ratiox = (float)DROID_BITMAP_WIDTH / width;
			float ratioy = (float)DROID_BITMAP_HEIGHT / height;
			
			width = (int)(width * ratiox + 0.5f);
			height = (int)(height * ratioy + 0.5f);
			
			drawingRect.left = (int)(drawingRect.left * ratiox + 0.5f);
			drawingRect.right = (int)(drawingRect.right * ratiox + 0.5f);
			drawingRect.top = (int)(drawingRect.top * ratioy + 0.5f);
			drawingRect.bottom = (int)(drawingRect.bottom * ratioy + 0.5f);
			
			DROID_Canvas.scale(ratiox, ratioy);
		}
		
		if (subUpdate) {
			DROID_Canvas.clipRect(drawingRect.left, drawingRect.top, drawingRect.right, drawingRect.bottom, Region.Op.REPLACE);
		}
		else {
			DROID_Canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
		}
		DROID_Canvas.drawColor(0, Mode.CLEAR);
		
		droidView.layout(0, 0, width, height);
		droidView.draw(DROID_Canvas);
		
		DROID_Canvas.restore();
		
		// TODO: update mesh according to view's LayoutParams
		if (subUpdate) {
			droidViewRenderTarget.setSubRegionInfo(true, drawingRect.left, drawingRect.top, 
					drawingRect.right - drawingRect.left, 
					drawingRect.bottom - drawingRect.top);

			droidViewRenderTarget.setSubTexUpdateInfo(true, drawingRect.left, drawingRect.top);
		}
		else {
			setupMesh(width, height, true, false);
			
			droidViewRenderTarget.setSubRegionInfo(true, 0, 0, width, height);
			
			droidViewRenderTarget.setSubTexUpdateInfo(false, 0, 0);
			
			texWidth = width;
			texHeight = height;
			
			Log.d(TAG, "New Mesh of DroidView : " + droidView);
		}
		
		// TODO: we need a better way to avoid split
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		jmeImage.setEfficentData(droidViewRenderTarget);
		JStage.getGlobalStageLog().getRenderer().setTexture(0, jmeTexture);			// update the texture
	}
/*	
	static public InputEvent jMEvt2DroidEvt(TouchEvent evt) {
		InputEvent droidEvt = null;
		
		if (evt != null) {
			TouchEvent.Type evtType = evt.getType();
			
			if (evtType == TouchEvent.Type.DOWN) {
				
			}
			else if (evtType == TouchEvent.Type.MOVE) {
				
			}
			else if (evtType == TouchEvent.Type.UP) {
				
			}
			else if (evtType == TouchEvent.Type.KEY_DOWN) {
				
			}
			else if (evtType == TouchEvent.Type.KEY_UP) {
				
			}
			else if (evtType == TouchEvent.Type.FLING) {
				
			}
			else if (evtType == TouchEvent.Type.TAP) {
				
			}
			else if (evtType == TouchEvent.Type.DOUBLETAP) {
				
			}
			else if (evtType == TouchEvent.Type.LONGPRESSED) {
				
			}
			else if (evtType == TouchEvent.Type.SCALE_START) {
				
			}
			else if (evtType == TouchEvent.Type.SCALE_MOVE) {
				
			}
			else if (evtType == TouchEvent.Type.SCALE_END) {
				
			}
			else if (evtType == TouchEvent.Type.SCROLL) {
				
			}
			else if (evtType == TouchEvent.Type.SHOWPRESS) {
				
			}
			else if (evtType == TouchEvent.Type.OUTSIDE) {
				
			}
			else if (evtType == TouchEvent.Type.IDLE) {
				
			}
			else {
				// end
			}
		}
		
		return droidEvt;
	}
*/	
}

class JDroidViewRenderTarget extends AndroidImageInfo {
	public JDroidViewRenderTarget(Bitmap droidBitmap) {
		super(null);
		
		bitmap = droidBitmap;
	}
	
	public void setBitmap(Bitmap droidBitmap) {
		bitmap = droidBitmap;
	}
	
	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	// WARNING: do NOT remove this empty method !!!
	@Override
	public void notifyBitmapUploaded() {
		// do nothing
	}
	
}
