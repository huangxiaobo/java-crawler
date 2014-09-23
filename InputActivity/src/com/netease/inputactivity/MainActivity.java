package com.netease.inputactivity;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity{
	private static String TAG = "hxb";
	private Button button;
	public Intent mIntent;
	public static Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button = (Button) findViewById(R.id.button);

		mContext = this.getApplicationContext();

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(mContext, InputView.class));

			}
		});
		
		List<InputMethodInfo> inputMethodInfo =  ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).getInputMethodList();
		for (InputMethodInfo info : inputMethodInfo) {
			Log.d("amw", info.getServiceName());
		}
	
	}

	 @Override  
	    protected void onStart() {  
	        super.onStart();  
	        Log.e(TAG, "start onStart~~~");  
	    }  
	      
	    @Override  
	    protected void onRestart() {  
	        super.onRestart();  
	        Log.e(TAG, "start onRestart~~~");  
	    }  
	      
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        Log.e(TAG, "start onResume~~~");  
	    }  
	      
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        Log.e(TAG, "start onPause~~~");  
	    }  
	      
	    @Override  
	    protected void onStop() {  
	        super.onStop();  
	        Log.e(TAG, "start onStop~~~");  
	    }  
	      
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        Log.e(TAG, "start onDestroy~~~");  
	    }  	
}
