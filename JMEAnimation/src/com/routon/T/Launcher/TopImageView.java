package com.routon.T.Launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.routon.jme_droid.R;

public class TopImageView extends FrameLayout {

	private View m_FrameLayout = null;
	private ImageView imageleft; //左边图片
	private ImageView imageright; //右边图片
	private ImageView imageicon; //右上角图标
	private TextView textType;
	private TextView texttitle; //标题
	private ImageView imagestar; //星等级
	private TextView textlever; //文字等级
	private TextView textdescription; //文字描述

	public TopImageView(Context c) {
		this(c, null);
	}

	public void add(TopImageView topImageView) {
		// TODO Auto-generated method stub

	}

	public TopImageView(Context c, AttributeSet attrs) {
		super(c, attrs);

		m_FrameLayout = LayoutInflater.from(c).inflate(
				R.layout.trecommend_item, this, true);
		imageleft = (ImageView) findViewById(R.id.imageleft);
		imageright = (ImageView) findViewById(R.id.imageright);
		imageicon = (ImageView) findViewById(R.id.imageicon);
		texttitle = (TextView) findViewById(R.id.texttitle);
		textType = (TextView) findViewById(R.id.type);
		//		   TextPaint tp = textType.getPaint(); 
		//		   tp.setFakeBoldText(true); //加粗中文字体　

		imagestar = (ImageView) findViewById(R.id.imagestar);
		//		   try
		//			{
		//				Bitmap bm = BitmapFactory.decodeFile("/sdcard/2.png");
		//				imagestar.setImageBitmap(bm);
		//			}
		//			catch(Exception e)
		//			{
		//				e.printStackTrace();
		//			}
		textlever = (TextView) findViewById(R.id.textlever);
		textdescription = (TextView) findViewById(R.id.textdescription);
	}

	public View getTopLayout() {
		return m_FrameLayout;
	}

	public ImageView getLeftimage() {
		return imageleft;
	}

	public ImageView getRightimage() {
		return imageright;
	}

	public ImageView getIcon() {
		return imageicon;
	}

	public TextView getType() {
		return textType;
	}

	public TextView getTitle() {
		return texttitle;
	}

	public ImageView getStarimage() {
		return imagestar;
	}

	public TextView getLever() {
		return textlever;
	}

	public TextView getDescription() {
		return textdescription;
	}
}
