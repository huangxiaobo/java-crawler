package com.amw.opengles;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.com.example.gl.R;

public class SecondActivity extends Activity{
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.second_activity_main);
	        
	        Button bt = (Button)findViewById(R.id.second_first);
	        bt.setOnClickListener(onClickListener);
	   }
	   

	    private OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            switch(v.getId()){
	            case R.id.second_first:
	            	startActivity(new Intent(getApplicationContext(), FirstActivity.class));
	            	break;
	         }
			}
			
	    };
}
