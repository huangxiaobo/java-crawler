package com.routon.jui;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class JTimer {
	private static final String TAG = "JTimer";
	
	private static final long JTimer_MAX_DEFINTION = 1;
	
	private static final int JTimer_LOCAL_TASK = 0;
	private static final int JTimer_LOCAL_SYNC_TASK = 1;
	
	private static final int JTimer_ACTIVE = 0;
	private static final int JTimer_PAUSE = 1;
	
	private int status = JTimer_PAUSE;
	
	private long delay = 0;
	private long period = 0;
	
	private JTimerTask localTask = null;
	private JTimerTask asyncTask = null;
	private JTimerTask localSyncTask = null;
	
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() { 
		 public void run() {  
			 if (status != JTimer_ACTIVE) 
				 return ;
			 
			 // queue local task
			 Message localMsg = new Message();
			 localMsg.what = JTimer_LOCAL_TASK;
			 handler.sendMessage(localMsg);

			 // run async task
			 if (asyncTask != null) 
				 asyncTask.task();

			 // queue local sync task 
			 Message localSyncMsg = new Message();
			 localSyncMsg.what = JTimer_LOCAL_SYNC_TASK;
			 handler.sendMessage(localSyncMsg);
		 }
	};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case JTimer_LOCAL_TASK: 
				if (localTask != null)
					localTask.task();

				break; 
			
			case JTimer_LOCAL_SYNC_TASK: 
				if (localSyncTask != null) 
					localSyncTask.task();

				break; 
			
			default : 
				// do nothing
				break; 
			}
			
			super.handleMessage(msg);
		}
	};
	
	public JTimer(long period) {
		this(period, (long)0);
	}
	
	public JTimer(long period, long delay) {
		if (period < JTimer_MAX_DEFINTION) 
			period = JTimer_MAX_DEFINTION;
		
		if (delay < JTimer_MAX_DEFINTION) 
			delay = JTimer_MAX_DEFINTION;
		
		this.period = period;
		this.delay = delay;
	}
	
	public void start() {
		status = JTimer_ACTIVE;
		
		Log.i(TAG, "start timer : " + delay + " " + period);
		timer.scheduleAtFixedRate(task, delay, period);
	}
	
	public void pause() {
		status = JTimer_PAUSE;
	}
	
	public void cancel() {
		timer.cancel();
	}
	
	public void setLocalTask(JTimerTask task) {
		this.localTask = task;
	}
	
	public void setAsyncTask(JTimerTask task) {
		this.asyncTask = task;
	}
	
	public void setLocalSyncTask(JTimerTask task) {
		this.localSyncTask = task;
	}
	
	public interface JTimerTask {
		public boolean task();
	}
}
