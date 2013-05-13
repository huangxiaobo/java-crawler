package com.routon.T.Launcher;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class TRotateYAnimation extends Animation {
	private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final float mtodepthz;
    private final boolean mReverse;
    private Camera mCamera;
    private float currentDegrees;
    private float currentDepthZ;
    private float currentPercentage;
    private boolean mStop = false;
	
	
	public TRotateYAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthZ, float todepthZ, boolean reverse) {
			mFromDegrees = fromDegrees;
	        mToDegrees = toDegrees;
	        mCenterX = centerX;
	        mCenterY = centerY;
	        mDepthZ = depthZ;
	        mReverse = reverse;
	        mtodepthz = todepthZ;
	        currentDegrees = fromDegrees;
	        currentDepthZ = depthZ;
	        currentPercentage = 0;
	}
	
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
		mStop = false;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
			if (mStop)
		            return;
		
		    final float fromDegrees = mFromDegrees;
		    float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
		    final float centerX = mCenterX;
		    final float centerY = mCenterY;
		    final Camera camera = mCamera;
		    final Matrix matrix = t.getMatrix();
		    float depthz = mDepthZ + ((mtodepthz - mDepthZ) * interpolatedTime);
		
		    currentDegrees = degrees;
		    currentDepthZ = depthz;
		    currentPercentage = interpolatedTime;
		
		    camera.save();
		    if (mReverse) {
		            camera.translate(0.0f, 0.0f, depthz);
		    } else {
		            camera.translate(0.0f, 0.0f, mtodepthz);
		    }
		    camera.rotateY(degrees);
		    camera.getMatrix(matrix);
		    camera.restore();
		    matrix.preTranslate(-centerX, -centerY);
		    matrix.postTranslate(centerX, centerY);
		}

	
	  public float getCurrentDegrees() {
          return currentDegrees;
	  }

	  public float getCurrentDepthZ() {
          return currentDepthZ;
 	}

	  public float getCurrentPercentage() {
		  return currentPercentage;
	  }

	  public void stop() {
          mStop = true;
	  }

}
