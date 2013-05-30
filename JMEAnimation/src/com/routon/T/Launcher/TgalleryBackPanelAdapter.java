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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.jme3.input.KeyInput;
import com.routon.jme_droid.R;
//import  com.routon.T.launcher.TgallerypanelView;
//import com.routon.T.launcher.R;

class TgalleryBackPanelListView extends FrameLayout {
    private final static String TAG = "TgalleryBackPanelListView";
    private final static String TIME_DEBUG_TAG = "TimeDebug";
    private Context mContext;
    private View mFrameLayout;

    private ImageView mMenuFoucs;
    private LinearLayout mMenuList, mMenuBackgorund;
    private Tpanel mPanelData;
    private MenuListAdapter mListAdapter = null;
    
    private int mItemCount = 0;
    private int mMenuListY = 0, mFocusY = 0;
    private int mSelectedIndex = 0, mFirstIndex = 0;
    private int mMaxVisibleItems = 5, mCenterIndex = 2;
    private Scroller mListScroller, mFocusScroller = null;
    private int SCROLL_DURATION = 600;
        
    public static final int ROTATE_FRONT_TO_BACK = 1;
    public static final int ROTATE_BACK_TO_FRONT = -1;
    
    public static final int PANEL_WIDTH = 256;
    public static final int PANEL_HEIGHT = 256;
    public static final int PIC_WIDTH = 250;
    public static final int PIC_HEIGHT = 44;
    public static final int TEXT_SIZE = 24;
    public static final int PANEL_TEXT_SIZE = 28;
    
    public TgalleryBackPanelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPanelData = null;
        
        initialize();
    }
    
    public TgalleryBackPanelListView(Context context, Tpanel panel) {
        super(context, null, 0);
        
        initialize();
        setPanelData(panel);
    }
   
    private void initialize() {
        
        mFrameLayout = LayoutInflater.from(mContext).inflate(R.layout.tpanel_item_back_listview, this);
       
        mMenuList =  (LinearLayout)mFrameLayout.findViewById(R.id.menuList);
        mMenuBackgorund = (LinearLayout)mFrameLayout.findViewById(R.id.menulistBackground);
        mMenuFoucs =  (ImageView)mFrameLayout.findViewById(R.id.menuFocus);
        mListScroller = new Scroller(mContext);
        mFocusScroller = new Scroller(mContext);
    }
    
    public void setPanelData(Tpanel panel) {
        mPanelData = panel;
   
        
        ArrayList<panelmenu> menu_list = mPanelData.getmenulist();
        if (menu_list != null && menu_list.size() != 0) {
            mListAdapter = new MenuListAdapter(mContext, menu_list);
            updateListAdapter();
        }
    }
    
    @Override
    public void computeScroll() {
        Log.d(TAG, "computeScroll");
        if (mListScroller.computeScrollOffset()) {
            Log.d(TAG, "mListScroller.computeScrollOffset()");
            int currY = mListScroller.getCurrY();
            // Log.d("shibojun", "mMenuList currY: " + currY);
            // mMenuBackgorund.scrollTo(0, currY);
            mMenuList.setTranslationY(currY);
            mMenuBackgorund.setTranslationY(currY);
            invalidate();
        } else if (mFocusScroller.computeScrollOffset()) {
            Log.d(TAG, "mFocusScroller.computeScrollOffset()");
            int currY = mFocusScroller.getCurrY();
            // Log.d("shibojun", "mMenuFoucs currY: " + currY);
            mMenuFoucs.setTranslationY(currY);
            invalidate();
        }
    }
    
    public void setSelection(int position) {
        int scrollCount = 0, scrollDistance = 0, scrollDirection = 0;
        int i = 0;
        View child;
        
        Log.d(TAG, mPanelData.getName() + ": " + "setSelection: " + position );
        
        if (mListAdapter == null || mItemCount <= 0)
            return;
        if (position < 0 || position >= mItemCount)
            return;

        scrollCount = position - mSelectedIndex;
        scrollDirection = (scrollCount > 0) ? 1: -1;
        if (scrollDirection == 0) return;
        
        /**
         * FIXME: 即使position可见，但是如果逐个到position的过程中需要滚动menu的话，还是可能出现问题 ，
         */
        // if the position is not visible, 
        // select the previous or next item until the position is visible
        while (position < mFirstIndex || position >= mFirstIndex + mMaxVisibleItems) {
            // Log.d("shibojun", mPanelData.getName() + ": " + "move to: " + (mSelectedIndex + scrollDirection));
            
            // mFirstIndex will be changed in the following call
            setSelection(mSelectedIndex + scrollDirection);
            scrollCount -= scrollDirection;
        }
        
        i = 0;
        while (i != scrollCount) {
            if (scrollDirection > 0)
                child = mMenuList.getChildAt(mSelectedIndex + i);
            else
                child = mMenuList.getChildAt(mSelectedIndex + i - 1);
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            scrollDistance += child.getHeight();
            i += scrollDirection;
        }
        scrollDistance = scrollDistance * scrollDirection;
        
        if (!mListScroller.isFinished()) {
            mListScroller.abortAnimation();
            mMenuList.setTranslationY(mListScroller.getFinalY());
            mMenuBackgorund.setTranslationY(mListScroller.getFinalY());
        }
        if (!mFocusScroller.isFinished()) {
            mFocusScroller.abortAnimation();
            mMenuFoucs.setTranslationY(mFocusScroller.getFinalY());   
        }

        // the focus scroll down, the list scroll up
        if (mSelectedIndex == mFirstIndex + mCenterIndex) {
            if (scrollDirection == 1 && mFirstIndex + mMaxVisibleItems < mItemCount) {
                mListScroller.startScroll(0, mMenuListY, 0, -scrollDistance, SCROLL_DURATION);
                mMenuListY -= scrollDistance;
                mMenuList.getChildAt(mFirstIndex + mMaxVisibleItems).setVisibility(VISIBLE);
                mMenuBackgorund.getChildAt(mFirstIndex + mMaxVisibleItems).setVisibility(VISIBLE);
                mFirstIndex++;
            } else if (scrollDirection == -1 && mFirstIndex > 0) {
                mListScroller.startScroll(0, mMenuListY, 0, -scrollDistance, SCROLL_DURATION);
                mMenuListY -= scrollDistance;
                mFirstIndex--;
                mMenuList.getChildAt(mFirstIndex).setVisibility(VISIBLE);
                mMenuBackgorund.getChildAt(mFirstIndex).setVisibility(VISIBLE);
            } else {
                mFocusScroller.startScroll(0, mFocusY, 0, scrollDistance, SCROLL_DURATION);
                mFocusY += scrollDistance;
            }
        } else {
            Log.d(TAG, "mSelectedIndex ！= mFirstIndex + mCenterIndex");
            mFocusScroller.startScroll(0, mFocusY, 0, scrollDistance, SCROLL_DURATION);
            mFocusY += scrollDistance;
        }
        mSelectedIndex += scrollCount;   
        invalidate();
        
        return;
    }
    
    public void setNext() {
        setSelection(mSelectedIndex + 1);
        return;
    }

    public void setPrev() {
        setSelection(mSelectedIndex - 1);
        return;
    }
    
    public int getSelection() {
        return mSelectedIndex;
    }

    
    public void updateListAdapter() {
        if (mListAdapter == null)
            return;
        
        // add the images to menuList
        ImageView child;
        int i;
        int white = Color.rgb(255, 255, 255);
        int gray = Color.rgb(230, 230, 230);
        
        mMenuList.removeAllViews();
        mMenuBackgorund.removeAllViews();

        mMenuListY = 0;
        mFocusY = 0;
        mSelectedIndex = 0;
        mFirstIndex = 0;
        mItemCount = mListAdapter.getCount();
        mMenuList.setTranslationY(0f);
        mMenuBackgorund.setTranslationY(0f);
        mMenuFoucs.setTranslationY(0f);
        
        // Log.d("shibojun", "setListAdapter " + m_OriginView.getText() + " ItemCount: " + mItemCount);
        
        for (i = 0; i < mItemCount; i++) {
            child = (ImageView)mListAdapter.getView(i, null, mMenuList);
            mMenuList.addView(child);
            ImageView imageView = new ImageView(mContext);
            imageView.setBackgroundColor((i % 2 == 0) ? gray : white);
            imageView.setLayoutParams(new ListView.LayoutParams(PIC_WIDTH, PIC_HEIGHT));
            mMenuBackgorund.addView(imageView);
        }
        mMenuList.setLayoutParams(new LayoutParams(PIC_WIDTH,  mMenuList.getChildCount() * PIC_HEIGHT));
        
        for (; i < mMaxVisibleItems; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setBackgroundColor((i % 2 == 0) ? gray : white);
            imageView.setLayoutParams(new ListView.LayoutParams(PIC_WIDTH, PIC_HEIGHT));
            mMenuBackgorund.addView(imageView);
        }
        mMenuBackgorund.setLayoutParams(new LayoutParams(PIC_WIDTH, 
                mMenuBackgorund.getChildCount() * PIC_HEIGHT));
        
        hideItemsOutOfList();
        
        invalidate();
        
        return;
    }

    private void hideItemsOutOfList() {
        int i;

        for (i = 0; i < mFirstIndex; i++) {
            mMenuList.getChildAt(i).setVisibility(INVISIBLE);
            mMenuBackgorund.getChildAt(i).setVisibility(INVISIBLE);
        }
        for (i = mFirstIndex + mMaxVisibleItems; i < mItemCount; i++) {
            mMenuList.getChildAt(i).setVisibility(INVISIBLE);
            mMenuBackgorund.getChildAt(i).setVisibility(INVISIBLE);
        }
    }
    
    public int getItemCount() {
        return mItemCount;
    }
    
    public View getDataView() {
        return mFrameLayout;
    }
    
    public void updateicons(int menunum,String icons){
        if (mListAdapter == null)
            return;

        mListAdapter.updateicons(menunum, icons);
        updateListAdapter();
        return;
    }
    
    private class MenuListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ImageView> mImageList;
        private int mImageCount;
        Bitmap newbitmap;
        
        public MenuListAdapter(Context c, List<panelmenu> list) {
            this.mContext = c;
            this.mImageList = new ArrayList<ImageView>();
            this.mImageCount = 0;
            
            String mText, mName;
            Bitmap bitmap;
            int index = 0;
            // int white = Color.rgb(255, 255, 255);
            // int gray = Color.rgb(230, 230, 230);
            
            for (panelmenu menu : list) {
                if (menu.getmenuFlag() != 0) {
                    mText = menu.getmenuText();
                    mName = menu.getmenuName();
                    bitmap = null;

                    if (mText.endsWith(".png") || mText.endsWith(".jpg") || mText.endsWith(".bmp")) {
                        bitmap = BitmapFactory.decodeFile(mText);
                    }

                    // if mText is not a picture or picture decode failed,
                    // then draw mName to the image.
                    if (bitmap == null) {
                        bitmap = Bitmap.createBitmap(PIC_WIDTH, PIC_HEIGHT, Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setTextSize(TEXT_SIZE);
                        paint.setTextAlign(Align.CENTER);
                        canvas.drawText(mName, PIC_WIDTH / 2, (PIC_HEIGHT + TEXT_SIZE)/ 2, paint);
                    }

                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageBitmap(bitmap);
                    // imageView.setBackgroundColor((index % 2 == 0) ? gray : white);
                    imageView.setLayoutParams(new ListView.LayoutParams(PIC_WIDTH, PIC_HEIGHT));
                    mImageList.add(imageView);
                    index++;
                }
            }
            mImageCount = index;
            
            // while (index < 5) {
            //     ImageView imageView = new ImageView(mContext);
            //     imageView.setBackgroundColor((index % 2 == 0) ? gray : white);
            //     imageView.setLayoutParams(new ListView.LayoutParams(PIC_WIDTH, PIC_HEIGHT));
            //     mImageList.add(imageView);
            //     index++;
            // }
        }
        public int getRealCount() {
            return mImageCount;
        }
        public void updateicons(int menunum,String mText){
            if (menunum > mImageCount - 1)
                return;
            
            if (mText.endsWith(".png") || mText.endsWith(".jpg") || mText.endsWith(".bmp")) {
                newbitmap = BitmapFactory.decodeFile(mText);
            }
            if(newbitmap!=null){
                mImageList.get(menunum).setImageBitmap(newbitmap);
                newbitmap=null;
                //notifyDataSetChanged();
            }
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageList.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mImageList.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return mImageList.get(position);
        }
    }
    

};

/******************************TgalleryBackPanelView**************************/
/*
 * Display Android View class
 * Each TgallerypanelView is a item attach with Gallery, 
 */
class TgalleryBackPanelView  extends FrameLayout{
    private final static String TAG = "TgalleryBackPanelView";
    private final static String TIME_DEBUG_TAG = "TimeDebug";
    private Context mContext;
	private View m_FrameLayout = null;
	private FrameLayout mPanelWithoutReflection;
	private TextView m_OriginView = null;
	private ImageView  m_imageView1;
	private Tpanel mPanelData;
	private Bitmap mBitmapBack = null;
	private TgalleryBackPanelListView m_ListView;
    
    public static final int PANEL_WIDTH = 256;
    public static final int PANEL_HEIGHT = 256;
    public static final int PIC_WIDTH = 250;
    public static final int PIC_HEIGHT = 44;
    public static final int TEXT_SIZE = 24;
    public static final int PANEL_TEXT_SIZE = 28;
    
    private boolean needUpdate = false;
    
	public TgalleryBackPanelView(Context hostActivity, Tpanel panel) {
		super(hostActivity);
		mContext = hostActivity;
		mPanelData = panel;
		
		long t1 = System.currentTimeMillis();
		m_FrameLayout = LayoutInflater.from(mContext).inflate(R.layout.tpanel_item_back, this);
		mPanelWithoutReflection = (FrameLayout)m_FrameLayout.findViewById(R.id.panelFrame);
		m_imageView1 = (ImageView)m_FrameLayout.findViewById(R.id.imagepanelView);
		m_ListView = (TgalleryBackPanelListView)m_FrameLayout.findViewById(R.id.listView);
		m_ListView.setPanelData(panel);
		m_OriginView = (TextView)m_FrameLayout.findViewById(R.id.m_OriginView);
		m_OriginView.setTextSize(PANEL_TEXT_SIZE);
		m_OriginView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        m_OriginView.setTextColor(Color.WHITE);
        
        long t2 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "布局初始化时间: " + (t2 - t1));
        
        Drawable draw = Drawable.createFromPath(mPanelData.getAnimationPic());
        if (draw != null)
            mBitmapBack = ((BitmapDrawable)draw).getBitmap();   
    
        switchToBackView();
        
        long t3 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "绘制背景时间 : " + (t3 - t2));
        
        mPanelWithoutReflection.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mPanelWithoutReflection.layout(0, 0, 
                mPanelWithoutReflection.getMeasuredWidth(), 
                mPanelWithoutReflection.getMeasuredHeight());
        
        long t4 = System.currentTimeMillis();
        Log.d(TIME_DEBUG_TAG, "调整布局时间 : " + (t4 - t3));
       
        this.setOnKeyListener(new OnKeyListener () {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                Log.d(TAG, "view: " + v + " keyCode: " + keyCode + " event: " + event);
               if (keyCode == KeyInput.KEY_DOWN) {
                   setNext();
                   return true;
               } else if (keyCode == KeyInput.KEY_UP) {
                   setPrev();
                   return true;
               }
               return false;
            }
        });
	}
	   
    public void setSelection(int position) {
        m_ListView.setSelection(position);
        
        return;
    }
    
    public void setNext() {
        m_ListView.setNext();
        return;
    }

    public void setPrev() {
        m_ListView.setPrev();
        return;
    }
    
    public int getSelection() {
        return m_ListView.getSelection();
    }

    public void updateicons(int menunum,String icons){
        m_ListView.updateicons(menunum, icons);
    }
   
    
    private void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            m_imageView1.setImageBitmap(bitmap);
            m_imageView1.setLayoutParams(new LayoutParams(PANEL_WIDTH, PANEL_HEIGHT));
        }
    }
    
    private void switchToBackView() {
        setImageBitmap(mBitmapBack);
        m_OriginView.setText(mPanelData.getName());
        m_OriginView.setTextColor(Color.WHITE);

        invalidate();
    }

    public int getItemCount() {
        return m_ListView.getItemCount();
    }
    
    public View getDataView() {
        return m_FrameLayout;
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

 class TgalleryBackPanelData {
	 
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

public class TgalleryBackPanelAdapter<MiddleImageView> extends TImageAdapter{
    private final static String TIME_DEBUG_TAG = "TimeDebug";
	private Context mContext;
    private  List<Tpanel> panels;
    private TgalleryBackPanelData mImageData;
    private List<TgalleryBackPanelView> m_TgallerypanelViews = null;
    int i=0,j;
    private ListView m_ListView;
	int panelnum=0;
	public Boolean First;
	
	
	public TgalleryBackPanelAdapter(Context c) {
		super(c);
		mContext = c;
		mImageData = new TgalleryBackPanelData();
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

	public int getMenuCount(int panelnum) {
	    return m_TgallerypanelViews.get(panelnum).getItemCount();
	}
	
	public int getMenuSelection(int panelnum) {
        return m_TgallerypanelViews.get(panelnum).getSelection();
	}

    public void setMenuSelection(int panelnum, int position) {
        m_TgallerypanelViews.get(panelnum).setSelection(position);
        return;
    }

    public void setMenuNext(int panelnum) {
        m_TgallerypanelViews.get(panelnum).setNext();
        return;
    }
    
    public void setMenuPrev(int panelnum) {
        m_TgallerypanelViews.get(panelnum).setPrev();
        return;
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
		m_TgallerypanelViews = new ArrayList<TgalleryBackPanelView>(panels.size());
		
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
			    long t1 = System.currentTimeMillis();
				m_TgallerypanelViews.add(new TgalleryBackPanelView(mContext, panels.get(panel_idx)));
				long t2 = System.currentTimeMillis();
                Log.d(TIME_DEBUG_TAG, "增加一个挡板的时间: " + (t2 - t1));
                
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

	public void updateicons(int panelnum,int menunum,String icons){
        m_TgallerypanelViews.get(panelnum).updateicons(menunum, icons);
		//notifyDataSetChanged();
	}
	
	public String getlistnumicon(int panelnum,int menunum){
		 return panels.get(panelnum).getmenulist().get(menunum).getmenuText();
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
