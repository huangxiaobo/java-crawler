package com.routon.jui;

public class JScrollView extends JActorGroup {
	private static final String TAG = "JScrollView";
	
	protected float mAdjustX = 0.0f;
	protected float mAdjustY = 0.0f;
	
	public JScrollView(String name) {
		super(name);
	}

	public void setAdjustX(float adjustX) {
		mAdjustX = adjustX;
	}
	
	public void setAdjustY(float adjustY) {
		mAdjustY = adjustY;
	}
}
