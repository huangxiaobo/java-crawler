package com.routon.T.Launcher;

//import com.routon.T.launcher.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TImageAdapter extends BaseAdapter{
	
	 private Context mContext;
	 int mGalleryItemBackground;
	 
	 public TImageAdapter(Context c){
		  mContext =c;
		//  TypedArray attr =mContext.obtainStyledAttributes(R.styleable.HelloGallery);
		//  mGalleryItemBackground = attr.getResourceId(R.styleable.HelloGallery_android_galleryItemBackground,0);
		//  attr.recycle();
	  }
	  
	  public int getCount(){
		  //Log.i("zhenghui","getCount");
		  return Integer.MAX_VALUE;
	  }
	  public Object getItem(int position){
		  //Log.i("getItem","position="+position);
		  return position;
	  }
	  public long getItemId(int position){
		 // Log.i("getItemId","position="+position);
		  return position;
	  }
	  
	 
	  public float getScale(boolean focused, int offset){
		  return Math.max(0, 1.0f/(float)Math.pow(2,Math.abs(offset)));
	  }
	  
	  public boolean createReflectedImages(int[] imgs1,ImageView[] mImages,int x,int y){
		  final int reflectionGap = 0;
		  int index =0;
		  for (int imageId:imgs1){
			//  Log.i("zhenghui","imageId="+imageId);
			  Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(),imageId);
			  int width = originalImage.getWidth();
			  int height = originalImage.getHeight();
			  Matrix matrix = new Matrix();
			  matrix.preScale(1,-1);
			  Bitmap reflectionImage= Bitmap.createBitmap(originalImage,0,height/2,width,height/2,matrix,false);
			  Bitmap bitmapWithReflection = Bitmap.createBitmap(width,(height+height/2),Config.ARGB_8888);
			  Canvas canvas = new Canvas(bitmapWithReflection);
			  canvas.drawBitmap(originalImage,0,0,null);
			  Paint defaultPaint = new Paint();
			  canvas.drawRect(0, height,width,height+reflectionGap,defaultPaint);
			  canvas.drawBitmap(reflectionImage,0,height+reflectionGap,null);
			  Paint paint = new Paint();
			  LinearGradient shader = new LinearGradient(0,originalImage.getHeight(),0,bitmapWithReflection.getHeight()+reflectionGap,0x70ffffff,0x00ffffff,TileMode.CLAMP);
			  paint.setShader(shader);
			  paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
			  canvas.drawRect(0,height,width,bitmapWithReflection.getHeight()+reflectionGap, paint);
			  ImageView imageView = new ImageView(mContext);
			  imageView.setImageBitmap(bitmapWithReflection);
//			  imageView.setLayoutParams(new TgalleryFlow.LayoutParams(x,y));
			  mImages[index++] = imageView;
			  
		  }
		  return true;
	  }
	  
	  public View createReflectedImages(View view,int x,int y){
		  final int reflectionGap = 0;
	
		   view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	        view.setDrawingCacheEnabled(true);
	        view.buildDrawingCache();
	        Bitmap originalImage = view.getDrawingCache();
			//  Log.i("zhenghui","imageId="+imageId);
		  		//Bitmap originalImage = ((BitmapDrawable)convertView.getDrawable()).getBitmap();
			 // Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(),convertView.getId());
			  int width = originalImage.getWidth();
			  int height = originalImage.getHeight();
			  Matrix matrix = new Matrix();
			  matrix.preScale(1,-1);
			  Bitmap reflectionImage= Bitmap.createBitmap(originalImage,0,height*2/3,width,height/3,matrix,false);
			  Bitmap bitmapWithReflection = Bitmap.createBitmap(width,(height+height/2),Config.ARGB_8888);
			  Canvas canvas = new Canvas(bitmapWithReflection);
			  canvas.drawBitmap(originalImage,0,0,null);
			//  Paint defaultPaint = new Paint();
			//  defaultPaint.setAntiAlias(true);
			//  canvas.drawRect(0, height,width,height+reflectionGap,defaultPaint);
			  canvas.drawBitmap(reflectionImage,0,height+reflectionGap,null);
			  Paint paint = new Paint();
			//  paint.setAntiAlias(true);
			  LinearGradient shader = new LinearGradient(0,height,0,bitmapWithReflection.getHeight()+reflectionGap,0xdd404040,0x00000000,TileMode.CLAMP);
			  paint.setShader(shader);
			  paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));//改变透明方式，改成颜色叠加模式；
			  canvas.drawRect(0,height,width,bitmapWithReflection.getHeight()+reflectionGap, paint);
			  
			  ImageView imageView = new ImageView(mContext);
//			  BitmapDrawable bd = new BitmapDrawable(bitmapWithReflection);
			  originalImage.recycle();     //bitmap释放资源
			  reflectionImage.recycle();
			  originalImage=null;
			  reflectionImage=null;
		//	 bd.setAntiAlias(true);
//			 imageView.setImageDrawable(bd);//打开锯齿消除
			  imageView.setImageBitmap(bitmapWithReflection);
//			  imageView.setLayoutParams(new TgalleryFlow.LayoutParams(x,y));
			  view.setDrawingCacheEnabled(false);
		     return imageView;
	  }
	  
	   public Bitmap createReflectedBitmap(View view, boolean reverse) {
	          final int reflectionGap = 0;
	    
	           view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	            view.setDrawingCacheEnabled(true);
	            view.buildDrawingCache();
	            Bitmap originalImage = view.getDrawingCache();
	            //  Log.i("zhenghui","imageId="+imageId);
	                //Bitmap originalImage = ((BitmapDrawable)convertView.getDrawable()).getBitmap();
	             // Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(),convertView.getId());
	              int width = originalImage.getWidth();
	              int height = originalImage.getHeight();
	              Matrix matrix = new Matrix();
	              matrix.preScale(1,-1);
	              Bitmap reflectionImage= Bitmap.createBitmap(originalImage,0,height*2/3,width,height/3,matrix,false);
	              Bitmap bitmapWithReflection = Bitmap.createBitmap(width,(height+height/2),Config.ARGB_8888);
	              Canvas canvas = new Canvas(bitmapWithReflection);
	              canvas.drawBitmap(originalImage,0,0,null);
	            //  Paint defaultPaint = new Paint();
	            //  defaultPaint.setAntiAlias(true);
	            //  canvas.drawRect(0, height,width,height+reflectionGap,defaultPaint);
	              canvas.drawBitmap(reflectionImage,0,height+reflectionGap,null);
	              Paint paint = new Paint();
	            //  paint.setAntiAlias(true);
	              LinearGradient shader = new LinearGradient(0,height,0,bitmapWithReflection.getHeight()+reflectionGap,0xdd404040,0x00000000,TileMode.CLAMP);
	              paint.setShader(shader);
	              paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));//改变透明方式，改成颜色叠加模式；
	              canvas.drawRect(0,height,width,bitmapWithReflection.getHeight()+reflectionGap, paint);
	              
	              // BitmapDrawable bd = new BitmapDrawable(bitmapWithReflection);
	              // bd.setAntiAlias(true);
	             
	             if (reverse) {
	                 matrix.reset();
	                 matrix.preScale(-1, 1);
	                 Bitmap temp = Bitmap.createBitmap(bitmapWithReflection,0,0,width,height*3/2,matrix,false);
	                 bitmapWithReflection.recycle();
	                 bitmapWithReflection = temp;
	             }
	             
                 originalImage.recycle();     //bitmap释放资源
                 reflectionImage.recycle();
                 originalImage=null;
                 reflectionImage=null;
                 view.setDrawingCacheEnabled(false);
                 
	             return bitmapWithReflection;
	      }
	   
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
