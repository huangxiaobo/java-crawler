package com.netease.tdg;

import android.app.Activity;
import android.os.Bundle;

public class Client extends Activity 
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	@Override
	public void onBackPressed()
	{
	}
}
