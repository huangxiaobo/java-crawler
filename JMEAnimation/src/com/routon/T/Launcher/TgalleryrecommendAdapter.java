package com.routon.T.Launcher;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.routon.jme_droid.R;

public class TgalleryrecommendAdapter extends TImageAdapter{
	private ArrayList<TopImageView> mImageViews;
	private ArrayList<TRecommendStruct> mImageDatas;
	 private ArrayList<TopImageView> mRecommendImageViews;
	private Context mContext;
	private int resCnt;
	private final String[][]  recom_type =
		{
			{"电影","電影","Movie"},
			{"下载","下載","Download"},
			{"娱乐","娛樂","Amuse"},
			{"凤凰","鳳凰","Ifeng"},
			{"图片","圖片","Photo"},
			{"剧集","劇集","T V"},
			{"生活","生活","Life"},
			{"科教","科教","EDU"},
			{"资讯","資訊","INFO"},
			{"文史","文史","L & H"},
			{"健康","健康","Health"},
			{"幼教","幼教","ECE"},
		};
	public TgalleryrecommendAdapter(Context c) {
		super(c);
		mContext =c;
		resCnt = 0;
		mRecommendImageViews = new ArrayList<TopImageView>();
		mImageViews = null;
		mImageDatas = null;
		for(int i=0; i<5; i++){
			mRecommendImageViews.add(new TopImageView(mContext));
		}
		this.resCnt =5;
		// TODO Auto-generated constructor stub
	}
	
	private String getStarGrayFile(float grade)
	{
		String filename = "/hdisk/rc/pics/downlist/star_gray-";
		if(grade >= 10.0f)
		{
			filename += "10.png";
		}
		else if(grade <= 0.0f)
		{
			filename += "0.png";
		}
		else
		{
			filename = filename + (int)Math.ceil(grade) + ".png";
		}
		return filename;
	}
	
	private String getTypeIconFile(int type)
	{
		String stringType=null;
		if (type > 0 && type < recom_type.length)
		{
			stringType = recom_type[type-1][0];
		}
		
		return stringType;
	}
	
	public void updateRecommendInfoAdapter(ArrayList<TRecommendStruct> imageDatas){
		this.mImageDatas = imageDatas;
		this.resCnt = mImageDatas.size();
		int i;
		int num = mImageDatas.size();
		Log.i("zhenghui","into updateRecommendInfoAdapter");
		if (mRecommendImageViews == null) {
			mRecommendImageViews = new ArrayList<TopImageView>(num);
		}

		TRecommendStruct dataItem = null;
		String type = null;
		Bitmap bm = null;
		int num1=mRecommendImageViews.size();
//		for(i=0;i<num1;i++){
//			mRecommendImageViews.remove(0);
//		}
		//Log.i("zhenghui","num="+num + "num1="+num1);
		if (num1>num){
			for(i=num;i<num1;i++)
				mRecommendImageViews.remove(num1+num-1-i);
		}
		else if(num1<num){
			for(i=num1;i<num;i++)
				mRecommendImageViews.add(new TopImageView(mContext));
		}
		for(i=0; i<num; i++){
			dataItem = mImageDatas.get(i);
			//mRecommendImageViews.add(new TopImageView(mContext));
			mRecommendImageViews.get(i).getTitle().setText(dataItem.getName());
			
			if((type = getTypeIconFile(dataItem.getType())) != null)
			{
				mRecommendImageViews.get(i).getIcon().setVisibility(View.VISIBLE);
				mRecommendImageViews.get(i).getType().setText(type);
				type = null;
			}
			try
			{
				bm = BitmapFactory.decodeFile(getStarGrayFile(dataItem.getGrade()));
				mRecommendImageViews.get(i).getStarimage().setImageBitmap(bm);
				bm=null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			mRecommendImageViews.get(i).getLever().setText(String.valueOf(dataItem.getGrade()));
			//Log.i("recommend", "detail" + dataItem.getDetail());
			mRecommendImageViews.get(i).getDescription().setText(dataItem.getDetail());
		}
	//	notifyDataSetChanged();
	//	this.mImageDatas = null; 
	}
	
	public ArrayList<TRecommendStruct>  getImageData(){
		return mImageDatas;
	}
	
	public void updateRecommendPicAdapter(int index, String pic1, String pic2)
	{
		if (mRecommendImageViews == null || this.resCnt < index || index < 0)
		{
			Log.i("updateRecommendPicAdapter", "param is invalid");
			return ;
		}
		if(pic1 != null)
		{
			try
			{
				Drawable drawable= Drawable.createFromPath(pic1);
				mRecommendImageViews.get(index).getLeftimage().setImageDrawable(drawable);
				drawable=null;	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(pic2 != null)
		{
			try
			{
				Drawable drawable= Drawable.createFromPath(pic2);
				mRecommendImageViews.get(index).getRightimage().setImageDrawable(drawable);
				mRecommendImageViews.get(index).getRightimage().setAlpha(0x1f);
				drawable=null;	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	//	notifyDataSetChanged();
	}
	
	public int getrecommendcnt(){
		return this.resCnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		position= position % this.resCnt;
		//Log.i("recommend=" + this.resCnt,"mRecommendImageViews[" + position + "]=" + mRecommendImageViews.get(position).getTopLayout());
		return mRecommendImageViews.get(position).getTopLayout();
	}

}














