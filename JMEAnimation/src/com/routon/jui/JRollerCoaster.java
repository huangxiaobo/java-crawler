package com.routon.jui;

import android.util.Log;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.TimeCheckListener;
import com.jme3.cinematic.PlayState;
import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class JRollerCoaster extends JActorGroup implements TimeCheckListener {
	private static final String TAG = "JRollerCoaster";
	
	private static final String ROLLER_COASTER_CONTROL_TAG = "JRollerCoaster Control";
	
	public static final int ROLLER_COASTER_LOOP_NON = 0;
	public static final int ROLLER_COASTER_LOOP_REVERSE = -1;
	public static final int ROLLER_COASTER_LOOP_FUTURE_EXTENSION_RESERVE = 1;
	
	private Animation advance = null;
	private Animation retreat = null;;
	
	private JFocusStrategy focusStrategy = null;
	
	private int[] aKeys = {KeyInput.KEY_RIGHT};
	private int[] rKeys = {KeyInput.KEY_LEFT};
	
	private int rcHead = 0;
	private float rcSpeed = 1.0f;
	
	private float rcSpeedBaseline = 2.0f;
	private float rcSpeedFactor = 3.918f;
	
	private int rcLoop = ROLLER_COASTER_LOOP_NON;
	
	public JRollerCoaster(String name, Animation advance, JFocusStrategy focusStrategy) {
		this(name, advance, null, focusStrategy);
	}
	
	public JRollerCoaster(String name, Animation advance, Animation retreat, JFocusStrategy focusStrategy) {
		super(name);
		
		this.advance = advance;
		this.retreat = retreat;
		
		this.focusStrategy = focusStrategy;
	}
	
	public void setAdvanceKeys(int[] keys) {
		aKeys = keys;
	}
	
	public void setRetreatKeys(int[] keys) {
		rKeys = keys;
	}
	
	public void setLoopMode(int loop) {
		if (loop == ROLLER_COASTER_LOOP_NON || loop == ROLLER_COASTER_LOOP_REVERSE) {
			rcLoop = loop;
		}
	}
	
	public int getLoopMode() {
		return rcLoop;
	}
	
	private int gearSwitcher(int key) {
		for (int i = 0; i < aKeys.length; i++) {
			if (key == aKeys[i]) {
				return 1;
			}
		}
		
		for (int i = 0; i < rKeys.length; i++) {
			if (key == rKeys[i]) {
				return -1;
			}
		}
		
		return 0;
	}
	
	public void onAdvance() {
		int childrenNum = getChildren().size();
		int go = focusStrategy.advance(childrenNum);
		
		if (go > 0) {
			Log.d(TAG, "onAdvance ---- rcHead = " + rcHead + " " + focusStrategy.getFocus() + " " + focusStrategy.getAnchorage() + " " + focusStrategy.getLastPosition());

			int gear = focusStrategy.getGear();
			if (gear == -1) {
				focusStrategy.setLastPosition(focusStrategy.getAnchorage());
			}
			
			focusStrategy.deltaAnchorage(1);
			focusStrategy.updateFocus();
			
			Log.d(TAG, "onAdvance ++++ rcHead = " + rcHead + " " + focusStrategy.getFocus() + " " + focusStrategy.getAnchorage() + " " + focusStrategy.getLastPosition());
			updateRollerCoasterSpeed();
		}
		else {
			go = -go;
			
			if (rcLoop == ROLLER_COASTER_LOOP_REVERSE) {
				Log.d(TAG, "ROLLER_COASTER_LOOP_REVERSE --- retreat : " + go);
				for (int i = 0; i < go; i++) {
					onRetreat();
				}
			}
		}
	}
	
	private void onRetreat() {
		int childrenNum = getChildren().size();
		int go = focusStrategy.retreat(childrenNum);
		
		if (go > 0) {
			Log.d(TAG, "onRetreat ---- rcHead = " + rcHead + " " + focusStrategy.getFocus() + " " + focusStrategy.getAnchorage() + " " + focusStrategy.getLastPosition());

			int gear = focusStrategy.getGear();
			if (gear == 1) {
				focusStrategy.setLastPosition(focusStrategy.getAnchorage());
			}
			
			focusStrategy.deltaAnchorage(-1);
			focusStrategy.updateFocus();
			
			Log.d(TAG, "onRetreat ++++ rcHead = " + rcHead + " " + focusStrategy.getFocus() + " " + focusStrategy.getAnchorage() + " " + focusStrategy.getLastPosition());
			updateRollerCoasterSpeed();
		}
		else {
			go = -go;
			
			if (rcLoop == ROLLER_COASTER_LOOP_REVERSE) {
				Log.d(TAG, "ROLLER_COASTER_LOOP_REVERSE --- advance : " + go);
				for (int i = 0; i < go; i++) {
					onAdvance();
				}
			}
		}
	}
	
	@Override
	public boolean onEvent(String name, TouchEvent evt, float tpf) {
		if (focusStrategy.isFocused()) {			// dispatch to focus spatial
			Spatial child = getChild(focusStrategy.getFocus());
			if (child instanceof JActorGene) {
				if (((JActorGene) child).onEvent(name, evt, tpf) == true) {
					return true;
				}
			}
		}
		
		if (evt.getType() == TouchEvent.Type.KEY_DOWN && advance != null) {		// dispatch to roller coaster
			int gear = gearSwitcher(evt.getKeyCode());
			
			if (gear == 1) {		// advance
				onAdvance();
				return true;
			}
			else if (gear == -1) {	// retreat
				onRetreat();
				return true;
			}
		}
		
		return super.onEvent(name, evt, tpf);									// dispatch to normal path
	}
	
	@Override
	protected void childAttachNotify(Spatial child, int index) {
		if (child.getUserData(ROLLER_COASTER_CONTROL_TAG) == null) {
			// add animations to child
			AnimControl control = new AnimControl();
			AnimChannel channelAdvance = null;
			AnimChannel channelRetreat = null;
			
			if (advance != null) {
				control.addAnim(advance);
				
				channelAdvance = control.createChannel();
				channelAdvance.setAnim(advance.getName());
				
				channelAdvance.addListener(this);
			}
			if (retreat != null) {
				control.addAnim(retreat);
				
				channelRetreat = control.createChannel();
				channelRetreat.setAnim(retreat.getName());
				
				channelRetreat.addListener(this);
			}
			
			child.addControl(control);
			child.setUserData(ROLLER_COASTER_CONTROL_TAG, control);
		}
		
		updateChildren(index);
	}

	@Override
	protected void childDetachNotify(Spatial child, int index) {
		// remove animations from child
		Object control = child.getUserData(ROLLER_COASTER_CONTROL_TAG);
		if (control instanceof Control) {
			child.removeControl((Control)control);
		}
		child.setUserData(ROLLER_COASTER_CONTROL_TAG, null);
		
		updateChildren(index);
	}
	
	private void updateChildren(int index) {
		int channelIndex = 0;
		
		int childIndex = index;
		int shotIndex = focusStrategy.getAnchorage() + index - focusStrategy.getFocus();
				
		if (index >= focusStrategy.getFocus()) {
			for (int k = focusStrategy.getCameraShotNum(); childIndex < children.size() && shotIndex < k; childIndex++, shotIndex++) {
				updateChild(childIndex, shotIndex, channelIndex);
			}
			
			if (childIndex < children.size()) {
				children.get(childIndex).setVisibility(false);
			}
		}
		else {
			for (; childIndex >= 0 && shotIndex >= 0; childIndex--, shotIndex--) {
				updateChild(childIndex, shotIndex, channelIndex);
			}
			
			if (childIndex >= 0) {
				children.get(childIndex).setVisibility(false);
			}
		}
	}
	
	private void updateChild(int childIndex, int shotIndex, int channelIndex) {
		Spatial child = null;
		AnimControl control = null;
		AnimChannel channel = null;
		
		child = children.get(childIndex);
		
		control = child.getUserData(ROLLER_COASTER_CONTROL_TAG);
		if (control instanceof Control) {
			channel = control.getChannel(channelIndex);
			channel.setTime(focusStrategy.getCameraShotTime(shotIndex));
			
			channel.pause();
		}
		
		child.setVisibility(true);
	}
	
	private void playChildSpatial(int index, int channelIndex, float time, float check, float speed) {
		Spatial spatial = children.get(index);
		AnimControl control = spatial.getUserData(ROLLER_COASTER_CONTROL_TAG);
		AnimChannel channel = control.getChannel(channelIndex);
		
		spatial.setVisibility(true);
		
		if (time >= 0)
			channel.setTime(time);
		if (check >= 0)
			channel.setTimeCheckPoint(check);
		
		channel.setSpeed(speed);
		channel.play();
	}
	
	@Override
	public void onTimePointReach(float time, AnimControl control, AnimChannel channel) {
		Spatial spatial = control.getSpatial();
		int index = children.indexOf(spatial);
		int position = focusStrategy.getCameraShotPosition(time);
		if (index < 0 || position < 0) {
			channel.pause();
			return ;
		}
		
		int gear = focusStrategy.getGear(index, position);
//		Log.d(TAG, "onTimePointReach spatial = " + spatial.getName() + " time = " + time + " gear = " + gear);
		if (gear > 0) {
			if (position == focusStrategy.getCameraShotNum() - 1) {								// out of region 
				spatial.setVisibility(false);
				channel.pause();
			}
			else {
				if (position == 1 && index > 0) {												// trigger the new comer
					playChildSpatial(index - 1, 0, focusStrategy.getFirstCameraShotTime(), time, rcSpeed);
					
					rcHead = index - 1;
				}
				
				channel.setTimeCheckPoint(focusStrategy.getCameraShotTime(position + 1)); 		// continue to next position
			}
		}
		else if (gear < 0) {
			if (position == 0) {																// out of region
				spatial.setVisibility(false);
				channel.pause();
				
				rcHead = index + 1;
			}
			else {
				if (position == focusStrategy.getCameraShotNum() - 2 && index < children.size() - 1) {		// trigger the new comer
					playChildSpatial(index + 1, (retreat == null) ? 0 : 1, focusStrategy.getLastCameraShotTime(), time, -rcSpeed);
				}
				
				channel.setTimeCheckPoint(focusStrategy.getCameraShotTime(position - 1));			// continue to previous position
			}
		}
		else {
			int newComerIndex = -1;
			float newComerTime = 0.0f;

			if (position == 1) {
				if (index > 0) {
					newComerIndex = index - 1;
					newComerTime = focusStrategy.getFirstCameraShotTime();
					
					rcHead = newComerIndex;
				}
			}
			else if (position == focusStrategy.getCameraShotNum() - 2) {
				if (index < children.size() - 1) {
					newComerIndex = index + 1;
					newComerTime = focusStrategy.getLastCameraShotTime();
				}
			}
			
			if (newComerIndex >= 0) {
				Spatial newComer = children.get(newComerIndex);
				
				if (newComer.isVisible() == false) {
					AnimControl ncControl = newComer.getUserData(ROLLER_COASTER_CONTROL_TAG);
					int channelIndex = 0;
					AnimChannel ncChannel = ncControl.getChannel(channelIndex);
					
					ncChannel.setTime(newComerTime);
					ncChannel.pause();
					
					newComer.setVisibility(true);
				}
			}
			
			channel.pause();
		}

		// update last position of the focus
		focusStrategy.setLastPosition(index, position);
	}

	private void updateRollerCoasterSpeed() {	
		int childrenNum = children.size();
		int cameraShotNum = focusStrategy.getCameraShotNum();
		
		int gear = focusStrategy.getGear();
		if (gear == 0) 
			return ;
		
		rcSpeed = rcSpeedBaseline + rcSpeedFactor * (Math.abs(gear) - 1);
		
		Spatial spatial = null;
		AnimControl control = null;
		AnimChannel channel = null;
		
		float time = 0.0f, position = 0.0f;
		int i, j, channelIndex = 0, startHead = rcHead;

L_rc_launcher: 		
		for (i = rcHead, j = focusStrategy.getAnchorage(i); i < childrenNum && position <= cameraShotNum - 1; i++, j++) {
			spatial = children.get(i);	
			control = spatial.getUserData(ROLLER_COASTER_CONTROL_TAG);
			
			time = -1.0f;
			if (retreat != null) {		// check retreat channel first
				channel = control.getChannel(1);
				if (channel.getPlayState() == PlayState.Playing) {
					time = channel.getTime();
					
					channelIndex = 1;
				}
			}
			if (time < 0.0f) {			// in advance channel
				channel = control.getChannel(0);
				time = channel.getTime();
				
				channelIndex = 0;
			}
			
			if (i == startHead && time >= focusStrategy.getCameraShotTime(1) && rcHead > 0) {
				rcHead--;
				
				continue L_rc_launcher;
			}
			
			position = focusStrategy.getCameraShotPositionFloat(time);
			if (position == 0.0f && j < 0) {
				spatial.setVisibility(false);
				
				rcHead++;
				continue;
			}

			if (position >= cameraShotNum - 1) {
				if (j > cameraShotNum - 1) {
					spatial.setVisibility(false);
					
					break;
				}
			}
			
			spatial.setVisibility(true);
			if (position < j) {
				if (channelIndex == 1) {						// switch to advance channel
					channel.pause();
					
					channel = control.getChannel(0);
					channel.setTime(time);
				}
				
				channel.setTimeCheckPoint(focusStrategy.getCameraShotTime((int) (position + 1.0f)));
				channel.setSpeed(rcSpeed);
				channel.play();
			}
			else if (position > j) {
				if (channelIndex == 0 && retreat != null) {		// switch to retreat channel
					channel.pause();
					
					channel = control.getChannel(1);
					channel.setTime(time);
				}
				
				int prevPos = (int) (position);
				if (prevPos == position) 
					prevPos--;
				
				channel.setTimeCheckPoint(focusStrategy.getCameraShotTime(prevPos));	
				channel.setSpeed(-rcSpeed);
				channel.play();
			}
			else {		// position == j
//				channel.pause();
			}
			
			position += 1.0f;
		}		
	}
}
