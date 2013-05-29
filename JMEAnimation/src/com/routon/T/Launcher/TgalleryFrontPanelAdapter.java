package  com.routon.T.Launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.routon.jme_droid.R;
//import  com.routon.T.launcher.TgallerypanelView;
//import com.routon.T.launcher.R;

/*
 * Display Android View class
 * Each TgallerypanelView is a item attach with Gallery, 
 */
class TgalleryFrontPanelView  extends FrameLayout {
    private Context mContext;
	private View m_FrameLayout = null;
	private FrameLayout mPanelWithoutReflection;
	private TextView m_OriginView = null;
	private ImageView  m_imageView1;
	private Tpanel mPanelData;
	private Bitmap mBitmapFront = null;

	private int mItemCount = 0;
	private int mMenuListY = 0, mFocusY = 0;
	private int mSelectedIndex = 0, mFirstIndex = 0;
	private int mMaxVisibleItems = 5, mCenterIndex = 2;
    private Scroller mListScroller, mFocusScroller;
    private int SCROLL_DURATION = 600;
    
     
    public static final int ROTATE_FRONT_TO_BACK = 1;
    public static final int ROTATE_BACK_TO_FRONT = -1;
    
    public static final int PANEL_WIDTH = 256;
    public static final int PANEL_HEIGHT = 256;
    public static final int PIC_WIDTH = 250;
    public static final int PIC_HEIGHT = 44;
    public static final int TEXT_SIZE = 24;
    public static final int PANEL_TEXT_SIZE = 28;
    
    private boolean needUpdate = false;
    
	public TgalleryFrontPanelView(Context hostActivity, Tpanel panel) {
		super(hostActivity);
		mContext = hostActivity;
		mPanelData = panel;
		
		m_FrameLayout = LayoutInflater.from(hostActivity).inflate(R.layout.tpanel_item_front, this);
		mPanelWithoutReflection = (FrameLayout)m_FrameLayout.findViewById(R.id.panelFrame);
		m_imageView1 = (ImageView)m_FrameLayout.findViewById(R.id.imagepanelView);
		m_OriginView = (TextView)m_FrameLayout.findViewById(R.id.m_OriginView);
		m_OriginView.setTextSize(PANEL_TEXT_SIZE);
		m_OriginView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        m_OriginView.setTextColor(Color.BLACK);

        mListScroller = new Scroller(hostActivity);
        mFocusScroller = new Scroller(hostActivity);
        
        Drawable draw = Drawable.createFromPath(mPanelData.getStyle());
        if (draw != null)
            mBitmapFront = ((BitmapDrawable)draw).getBitmap();
             
        switchToFrontView();

        mPanelWithoutReflection.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mPanelWithoutReflection.layout(0, 0, 
                mPanelWithoutReflection.getMeasuredWidth(), 
                mPanelWithoutReflection.getMeasuredHeight());
              
	}
	
    @Override
    public void computeScroll() {
        if (mListScroller.computeScrollOffset()) {
            int currY = mListScroller.getCurrY();
            // Log.d("shibojun", "mMenuList currY: " + currY);
            // mMenuBackgorund.scrollTo(0, currY);
            invalidate();
        } else if (mFocusScroller.computeScrollOffset()) {
            int currY = mFocusScroller.getCurrY();
            // Log.d("shibojun", "mMenuFoucs currY: " + currY);
            invalidate();
        }
    }
    
    public void setMask(float alpha) {
        //alphaImage.setAlpha(alpha);   // Commented by hxb
    }
    
   
    
    public View getDataView() {
        return m_FrameLayout;
    }
    
    private void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            m_imageView1.setImageBitmap(bitmap);
            m_imageView1.setLayoutParams(new LayoutParams(PANEL_WIDTH, PANEL_HEIGHT));
        }
    }
  
    
    private void switchToFrontView() {
        setImageBitmap(mBitmapFront);
        m_OriginView.setText(mPanelData.getName());
        m_OriginView.setTextColor(Color.BLACK);
        m_OriginView.setRotationY(0f);
        // Next lines commented by hxb
        /*
        if (mBitmapInvertedFront != null)
            mInvertedImage.setImageBitmap(mBitmapInvertedFront);
            */
        invalidate();
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
    	// TODO Auto-generated method stub
    	super.onDraw(canvas);
    	needUpdate = true;
    }
    
    public boolean ifNeedUpdate(){
    	if(needUpdate){
    		needUpdate = false;
    		return true;
    	}
    	else {
			return false;
		}
    }
}

 class TgalleryFrontPanelData {
	 
	 public List<Tpanel> updateTgallerypanelData(){
		 
		 String xml_name="desktop_panel.xml";
		 String ini_name = "desktop-menu_ch.ini";
		 String flaglini_name = "panelflag.ini";
		 List<Tpanel> panels;
		 InputStream is;
		try {
			TiniReader iniread = new TiniReader( "/hdisk/etc/desktop/"
					 + ini_name);
			TiniReader flagread = new TiniReader( "/hdisk/etc/"
					 + flaglini_name);
			File f=new File("/hdisk/etc/desktop/"
					 + xml_name);
			if(!f.exists()){
				Runtime.getRuntime().exec("cp /hdisk/etc/desktop/default_desktop_panel.xml /hdisk/etc/desktop/"+ xml_name);
			}
			is = new FileInputStream("/hdisk/etc/desktop/"
					 + xml_name);
			Tpullpanelparse parser = new Tpullpanelparse();  //鍒涘缓pullpanelParser瀹炰緥
        	panels = parser.parse(is);  //瑙ｆ瀽杈撳叆娴�        	
        	ArrayList<panelmenu> pms;
			for (Tpanel  panel: panels) {
			
				String style = iniread.getValue(panel.getId(),"Style");
				if(style!=null)
					panel.setStyle(style);
				String text = iniread.getValue(panel.getId(),"Text");
				if(text!=null)
					panel.setText(text);
				String name = iniread.getValue(panel.getId(),"Name");
				if(name!=null)
					panel.setName(name);
				String animationpic = iniread.getValue(panel.getId(),"AnimationPic");
				if(animationpic!=null)
					panel.setAnimationPic(animationpic);
				String moduleid = iniread.getValue(panel.getId(),"MODULE_ID");
				if(moduleid!=null)
					panel.setMODULE_ID(moduleid);
				String exec = iniread.getValue(panel.getId(),"Exec");
				if(exec!=null)
					panel.setExec(exec);
				String android_exec = iniread.getValue(panel.getId(),"Android_exec");
				if(android_exec!=null)
					panel.setAndroidExec(android_exec);
				String args = iniread.getValue(panel.getId(),"Args");
				if(args!=null)
					panel.setArgs(args);
				String type = iniread.getValue(panel.getId(),"Type");
				if(type!=null)
					panel.setType(type);
				String cursor = iniread.getValue(panel.getId(),"Cursor");
				if(cursor!=null)
					panel.setCursor(cursor);
				//added by wukai 2013.01.11
				String flag = flagread.getValue(panel.getId(),"flag");
				//Log.i("wukai", "id:"+panel.getId()+",and panel is :" + flag);
				if(flag!=null)
					panel.setFlagIni(Integer.parseInt(flag));
			//Log.i("panel", "panel is :" + panel.toString());
			//Log.i("panel1", iniread.getValue(panel.getId(), "Style") + iniread.getValue(panel.getId(), "Text"));
				pms = panel.getmenulist();
				if(panel.getmenulist() != null)
				{
					for (panelmenu  menu: pms)
					{
						String mtext = iniread.getValue(menu.getmenuId(),"Text");
						if(mtext!=null)
							menu.setmenuText(mtext);
						String mname = iniread.getValue(menu.getmenuId(), "Name");
						if(name!=null)
							menu.setmenuName(mname);
						String mmoduleid = iniread.getValue(menu.getmenuId(),"MODULE_ID");
						if(mmoduleid!=null)
							menu.setmenuMODULE_ID(moduleid);
						String mexec = iniread.getValue(menu.getmenuId(),"Exec");
						if(mexec!=null)
							menu.setmenuExec(mexec);
						String mandroid_exec = iniread.getValue(menu.getmenuId(),"Android_exec");
						if(mandroid_exec!=null)
							menu.setmenuAndroidExec(mandroid_exec);
						String margs = iniread.getValue(menu.getmenuId(),"Args");
						if(margs!=null)
							menu.setmenuArgs(margs);
						String mtype = iniread.getValue(menu.getmenuId(),"Type");
						if(mtype!=null)
							menu.setmenuType(mtype);
						String mcursor = iniread.getValue(menu.getmenuId(),"Cursor");
						if(mcursor!=null)
							menu.setmenuCursor(mcursor);
					//	Log.i("menu", "menu is :" + menu.toString());
					}
				}
			}
			return panels;
		} catch (Exception e) 
		{
			Log.i("recommend", "recommend parse failed");
			
		//	Log.e("recommend", e.getMessage());
		}
		return null;
	}//updateMiddleImageData
	 
}

public class TgalleryFrontPanelAdapter<MiddleImageView> extends TImageAdapter{
	private Context mContext;
    private  List<Tpanel> panels;
    private TgallerypanelData mImageData;
    private List<TgalleryFrontPanelView> m_TgallerypanelViews = null;
    int i=0,j;
    private ListView m_ListView;
	int panelnum=0;
	public Boolean First;
	
	
	public TgalleryFrontPanelAdapter(Context c) {
		super(c);
		mContext = c;
		mImageData = new TgallerypanelData();
		 First=true;
		updatemiddleadapter();
	}

	@Override
	public int getCount(){
		//Log.i("zhenghui","getCount");
		return panelnum;
	}
	
	public int getpanelnum() {
		return panelnum;
	}

	public  List<Tpanel> getpanels(){
		return panels;
	}
	
	public void updatemiddleadapter() {
		boolean recommendstate=true;
		Log.i("zhenghui","into updatemiddleadapter1111111111");
		if(!First){
//			FocusHelper.setclear();
		}
		First =false;
		if(panels!=null)
				panels=null;
		panels = mImageData.updateTgallerypanelData();
		// Log.i("zhenghui","panels="+panels);
		panelnum = panels.size();
		//Log.i("zhenghui","panels.size="+panels.size());
		if (m_TgallerypanelViews != null) {
			m_TgallerypanelViews =null;
		}
		m_TgallerypanelViews = new ArrayList<TgalleryFrontPanelView>(panels.size());
		
		int panel_idx = 0;
		for (i = 0; i < panelnum; i++) {
			if(panels.get(panel_idx).getId().contentEquals("00101")){
				Log.i("zhenghui","into false");
					recommendstate=false;
			}
			if (panels.get(panel_idx).getFlag() == 0 || (panels.get(panel_idx).getFlag()!=2 && panels.get(panel_idx).getFlagIni() == 0)){
				panels.remove(panel_idx);
				continue;
			} else {
				m_TgallerypanelViews.add(new TgalleryFrontPanelView(mContext, panels.get(panel_idx)));
				panel_idx++;
			}
		}
		/* the number of panels that flag != 0 */
		panelnum = panel_idx;
//		if(recommendstate){
//	    	 Intent intent1 = new Intent("com.routon.searchunvisible");//隐藏云搜索
//	    	 mContext.sendBroadcast(intent1);
//	    	 if(Launcher.mlauncher.galleryrecommend!=null){
//	    			Launcher.mlauncher.galleryrecommend.setVisibility(View.INVISIBLE);
//	    	 } 
//	    	 else{
//	    	     Handler	handler =new Handler();
//	    	     handler.postDelayed(new Runnable() {
//	    	         public void run() {
//	    	        	 if(Launcher.mlauncher.galleryrecommend!=null){
//	    	        		 Launcher.mlauncher.galleryrecommend.setVisibility(View.INVISIBLE);
//	    	        	 }
//	    	         }
//	    	     }, 2000);
//	    	 }
//		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		position = position % m_TgallerypanelViews.size();
		if (convertView == null)
		    //return imageview[position];
		    return  m_TgallerypanelViews.get(position).getDataView();
		else
		    return convertView;
		//return  m_TgallerypanelViews.get(position).getDataView();
		//return (ImageView) createReflectedImages(((TgallerypanelView) m_TgallerypanelViews.get(i)).getDataView(),256,256);
	}
} //public class TgallerypanelAdapter
