package com.amw.opengles;

import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.com.example.gl.R;

@TargetApi(11)
public class FirstActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity_main);
        
        Button bt = (Button)findViewById(R.id.button1);
        
        bt.setOnClickListener(onClickListener );
        
        Button bt1 = (Button)findViewById(R.id.first_second);
        bt1.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            switch(v.getId()){
            case R.id.button1:
            	
                 ImageView iv = (ImageView)findViewById(R.id.imageView1);
                 float startProperty = iv.getAlpha();
                 float endProperty = 0.0f;
                 if (startProperty < 1.0f)
                	 endProperty = 1.0f;
                 else
                	 endProperty = 0.0f;
                 ObjectAnimator anim = ObjectAnimator.ofFloat(iv, "alpha", startProperty, endProperty);
                 anim.setDuration(1000);
                 anim.start();
            break;
            case R.id.first_second:
            	startActivity(new Intent(getApplicationContext(), SecondActivity.class));
            	break;
         }
		}
		
    };

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
