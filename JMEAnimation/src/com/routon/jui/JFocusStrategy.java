package com.routon.jui;

import android.util.Log;

public class JFocusStrategy {
	private static String TAG = "JFocusStrategy";
	
	private int pacesetter = 0;
	private float[] cameraShot = null;
	
	private int focus = 0;	
	private int lastPos = 0;
	private int anchorage = 0;
	
	public JFocusStrategy(int pacesetter, float[] cameraShot) {
		this.pacesetter = pacesetter;
		this.cameraShot = cameraShot;
		
		this.lastPos = pacesetter;
		this.anchorage = pacesetter;
	}
	
	public int getPacesetter() {
		return pacesetter;
	}
	
	public void setFocus(int focus) {
		this.focus = focus;
	}
	
	public int getFocus() {
		return focus;
	}
	
	public void setAnchorage(int anchorage) {
		this.anchorage = anchorage;
	}
	
	public void deltaAnchorage(int delta) {
		this.anchorage += delta;
	}
	
	public int getAnchorage() {
		return anchorage;
	}
	
	public int getAnchorage(int index) {
		return index - focus + anchorage;
	}
	
	public void setLastPosition(int pos) {
		this.lastPos = pos;
	}

	public void setLastPosition(int index, int pos) {
		this.lastPos = focus - index + pos;
	}
	
	public void deltaLastPosition(int delta) {
		this.lastPos += delta;
	}
	
	public int getLastPosition() {
		return lastPos;
	}
	
	public int getLastPosition(int index) {
		return index - focus + lastPos;
	}
	
	public int getCameraShotNum() {
		return cameraShot.length;
	}
	
	public float getCameraShotTime(int index) {
		if (index >= 0 && index < cameraShot.length) 
			return cameraShot[index];
		else 
			return 0.0f;
	}
		
	public float getFirstCameraShotTime() {
		return cameraShot[0];
	}
	
	public float getLastCameraShotTime() {
		return cameraShot[cameraShot.length - 1];
	}
	
	public float getPrevCameraShotTime(float time) {
		if (time <= cameraShot[0]) 
			return -1.0f;
		
		for (int i = 1; i < cameraShot.length; i++) {
			if (time > cameraShot[i]) {
				return cameraShot[i - 1];
			}
		}
		
		return cameraShot[cameraShot.length - 1];
	}
	
	public float getNextCameraShotTime(float time) {
		if (time >= cameraShot[cameraShot.length - 1])
			return -1.0f;
			
		for (int i = cameraShot.length - 2; i >= 0; i++) {
			if (time < cameraShot[i]) {
				return cameraShot[i + 1]; 
			}
		}
		
		return cameraShot[0];
	}
	
	public int getCameraShotPosition(float time) {
		int pos = -1;
		
		// TODO: improve it with binary-search
		for (int i = 0; i < cameraShot.length; i++) {
			if (cameraShot[i] == time) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	public float getCameraShotPositionFloat(float time) {
		if (time < cameraShot[0]) 
			return -1.0f;
		
		float pos = -1.0f;
		for (int i = 0; i < cameraShot.length - 1; i++) {
			if (time >= cameraShot[i] && time < cameraShot[i + 1]) {
				pos = i + (time - cameraShot[i]) / (cameraShot[i + 1] - cameraShot[i]);
				break;
			}
		}
		if (pos == -1.0f && time == cameraShot[cameraShot.length - 1]) {
			pos = cameraShot.length - 1;
		}
		
		return pos;
	}
	
	public int getGear() {
		return anchorage - lastPos;
	}
	
	public int getGear(int index, int position) {
		return index - focus + anchorage - position;
	}
	
	public void updateFocus() {
		int newFocus = getNewFocus();
		
		anchorage = newFocus - focus + anchorage;
		lastPos = newFocus - focus + lastPos;
		
		focus = newFocus;
	}
	
	// ---------------------------------------------------  
	
	public int getNewFocus() {
		return focus + (pacesetter - anchorage);
	}
	
	public boolean isFocused() {
		// TODO: 
		return lastPos == anchorage;
	}
	
	public int advance(int childrenNum) {
		if (anchorage - focus < pacesetter)
			return 1;
		else 
			return -(getAnchorage(childrenNum - 1) - pacesetter);
	}
	
	public int retreat(int childrenNum) {
		if (anchorage + (childrenNum - focus - 1) > pacesetter)
			return 1;
		else 
			return -(pacesetter - getAnchorage(0));
	}
}
