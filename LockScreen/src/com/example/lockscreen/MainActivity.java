package com.example.lockscreen;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
	private static final String TAG = "Activity1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		ServiceManager m_service_mgr = new ServiceManager();
	}



	@Override
	protected void onStart() {
		Log.i(TAG, "onStart called!");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart called!");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume called!");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause called!");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop called!");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy called!");
		super.onDestroy();
	}

	/** 当点击屏幕时，进入Activity2 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Toast.makeText(this, "按下", Toast.LENGTH_SHORT).show();
		return super.onTouchEvent(event);
	}

}
