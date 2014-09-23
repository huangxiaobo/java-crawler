package com.netease.inputactivity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputView extends Activity {
	private static String TAG = "hxb";

	EditText editText;
	String contentString = "";

	public Intent mIntent;
	public static Context mContext;

	private boolean hasShow = false;

	final static int TYPE_NORMAL = 0;
	final static int TYPE_NUMBER = 1;
	final static int TYPE_ALPHABET = 2;
	final static int TYPE_ALPHANUMERIC = 3;
	final static int TYPE_EMAILADDRESS = 4;

	final static int TYPE_NONE = 5;

	private String filter_parten = ".*[^\\x20-\\x7E].*";
	
	boolean isOpened = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);

		/*
		 * findViewById(R.id.view_bg).setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub inputFinish(); } });
		 */

		editText = (EditText) findViewById(R.id.edit_text);
		// editText.setInputType(InputType.TYPE_CLASS_TEXT |
		// InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		editText.setText(contentString);
		editText.setSelection(contentString.length());

		int type_code = TYPE_EMAILADDRESS;
		// filter_parten = "[a-zA-Z ]+";


		editText.requestFocus();
		setListnerToRootView();

		Timer timer = new Timer(); // 设置定时器
		timer.schedule(new TimerTask() {
			@Override
			public void run() { // 弹出软键盘的代码
				InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText,
						InputMethodManager.RESULT_SHOWN);
				inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 10); // 设置300毫秒的时长

		editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					inputFinish();

					return true;
				}
				return false;
			}
		});

		filter_parten = getMatchPatern(type_code);
		if (filter_parten.length() != 0) {
			editText.setFilters(new InputFilter[] { new InputFilter() {
				public CharSequence filter(CharSequence src, int start,
						int end, Spanned dst, int dstart, int dend) {
					if (src.equals("")) { // for backspace
						return src;
					}
					if (src.toString().matches(filter_parten)) {
						return src;
					}
					return "";
				}
			} });
		}


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/*
	public void setListnerToRootView(){
		// 监听layout的变化
		final View activityRootView = getWindow().getDecorView().getRootView();
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						Log.d(TAG, "onGlobalLayout. heightDiff: " + heightDiff + "hasShow: " + hasShow);
						if (heightDiff > 50) { // if more than 100 pixels, its
												// probably a keyboard...
							hasShow = true;
						} else {
							if (hasShow) {
								inputFinish();
							}
						}
					}
				});
	}

	
*/
		 public void setListnerToRootView(){
			 final View activityRootView = getWindow().getDecorView().getRootView();
		    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		        @Override
		        public void onGlobalLayout() {
		        	Rect r = new Rect();
					// r will be populated with the coordinates of your view
					// that area still visible.
					activityRootView.getWindowVisibleDisplayFrame(r);
		            int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top); //activityRootView.getHeight();
		            Log.d(TAG, "onGlobalLayout. r.height: " + r.height());
		            Log.d(TAG, "onGlobalLayout. visible : " + activityRootView.getRootView().getHeight());		            
		            Log.d(TAG, "onGlobalLayout. heightDiff: " + heightDiff + "isOpened: " + isOpened);
		            if (heightDiff > 100 ) { // 99% of the time the height diff will be due to a keyboard.
		                Toast.makeText(getApplicationContext(), "Gotcha!!! softKeyboardup", 0).show();

		                if(isOpened == false){
		                    //Do two things, make the view top visible and the editText smaller
		                }
		                isOpened = true;
		            }else if(isOpened == true){
		                Toast.makeText(getApplicationContext(), "softkeyborad Down!!!", 0).show();                  
		                isOpened = false;
		                inputFinish();
		            }
		         }
		    });
		}

		 
	@Override
	protected void onResume() {
		super.onResume();
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
		Log.e(TAG, "start onResume~~~");  
	}

	private void inputFinish() {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		Log.d(TAG, "showInputView: text: " + editText.getText().toString());
		// NativeOnInputFinish(editText.getText().toString());
		InputView.this.finish();
	}

	private String getMatchPatern(int type) {
		switch (type) {
		case TYPE_NORMAL:
			return "";
		case TYPE_NUMBER:
			return "[0-9].*";
		case TYPE_ALPHABET:
			return "[a-zA-Z].*";
		case TYPE_ALPHANUMERIC:
			return "[0-9a-zA-Z].*";
		case TYPE_EMAILADDRESS:
			return "[a-zA-Z0-9._\\-@].*";
		default:
			return "";
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			inputFinish();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return true;
	}
	
	

}
