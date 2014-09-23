package com.hxb.orientation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Build;

public class MainActivity extends Activity {
	final static int mOrientationValues[] = new int[] {
			ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_USER,
			ActivityInfo.SCREEN_ORIENTATION_BEHIND,
			ActivityInfo.SCREEN_ORIENTATION_SENSOR,
			ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, };

	private Spinner mOrientation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mOrientation = (Spinner) findViewById(R.id.app_activity_screen_orientation);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.screen_orientations,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mOrientation.setAdapter(adapter);
		mOrientation.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				setRequestedOrientation(mOrientationValues[position]);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		});

	}
}
