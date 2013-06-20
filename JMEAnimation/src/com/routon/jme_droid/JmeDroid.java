package com.routon.jme_droid;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.util.Log;

import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
 
public class JmeDroid extends AndroidHarness {
	public JmeDroid() {
		Log.d("TIME TRACE : ", Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + " --- " + SystemClock.uptimeMillis());
		
		// Set the application class to run
		appClass = "com.routon.jme_droid.Jme3DSMaxAnimDemo";
		
		// Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
		eglConfigType = ConfigType.BEST;
		
		// Exit Dialog title & message
		exitDialogTitle = "Exit?";
		exitDialogMessage = "Press Yes";

		// Choose screen orientation
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		// Invert the MouseEvents X (default = true)
		mouseEventsInvertX = true;
		// Invert the MouseEvents Y (default = true)
		mouseEventsInvertY = true;
		
		maxFPS = 60;   
	}
}
