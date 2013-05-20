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
class TgallerypanelView  extends FrameLayout implements AnimatorUpdateListener{
    private Context mContext;
	private View m_FrameLayout = null;
	private FrameLayout mPanelWithoutReflection;
	private TextView m_OriginView = null;
	private ImageView  m_imageView1;
	//private ImageView mInvertedImage;    //comment --add by hxb
	//private ImageView alphaImage;        //comment --add by hxb
	private ImageView mMenuFoucs;
	private LinearLayout mMenuList, mMenuBackgorund;
	private Tpanel mPanelData;
	private Bitmap mBitmapFront = null, mBitmapBack = null;
	private Bitmap mBitmapInvertedFront = null, mBitmapInvertedBack = null;
	private MenuListAdapter mListAdapter = null;
	private boolean isFrontSide = true;
	
	private int mItemCount = 0;
	private int mMenuListY = 0, mFocusY = 0;
	private int mSelectedIndex = 0, mFirstIndex = 0;
	private int mMaxVisibleItems = 5, mCenterIndex = 2;
    private Scroller mListScroller, mFocusScroller;
    private int SCROLL_DURATION = 600;
    
    private ObjectAnimator rotateAnim1 = null, scaleXAnim1 = null, scaleYAnim1 = null;
    private ObjectAnimator rotateAnim2 = null, scaleXAnim2 = null, scaleYAnim2 = null;
    private AnimatorSet animSet1 = null, animSet2 = null;
    private AnimatorListenerAdapter animLis1 = null, animLis2 = null;
    private Runnable runFinished1 = null, runFinished2 = null;
    private boolean animStarted = false;
    private boolean needSwitch = false;
    
    private float SWITCH_ANGLE_MIN = 85f, SWITCH_ANGLE_MAX = 90f;
    private float ROTATE_SCALE_MIN = 1.0f, ROTATE_SCALE_MAX = 1.3f;
    private int ROTATE_DURATION = 400;
    
    public static final int ROTATE_FRONT_TO_BACK = 1;
    public static final int ROTATE_BACK_TO_FRONT = -1;
    
    public static final int PANEL_WIDTH = 256;
    public static final int PANEL_HEIGHT = 256;
    public static final int PIC_WIDTH = 250;
    public static final int PIC_HEIGHT = 44;
    public static final int TEXT_SIZE = 24;
    public static final int PANEL_TEXT_SIZE = 28;
    
    private boolean needUpdate = false;
    
	public TgallerypanelView(Context hostActivity, Tpanel panel) {
		super(hostActivity);
		mContext = hostActivity;
		mPanelData = panel;
		
		m_FrameLayout = LayoutInflater.from(hostActivity).inflate(R.layout.tpanel_item, this);
		mPanelWithoutReflection = (FrameLayout)m_FrameLayout.findViewById(R.id.panelFrame);
		m_imageView1 = (ImageView)m_FrameLayout.findViewById(R.id.imagepanelView);
		m_OriginView = (TextView)m_FrameLayout.findViewById(R.id.m_OriginView);
		m_OriginView.setTextSize(PANEL_TEXT_SIZE);
		m_OriginView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        m_OriginView.setTextColor(Color.BLACK);
        // TextPaint tp = m_OriginView .getPaint();
        // tp.setFakeBoldText(true);
        // Next 2 lines commented by hxb
        //mInvertedImage = (ImageView)m_FrameLayout.findViewById(R.id.invertedImage);
        //alphaImage = (ImageView)m_FrameLayout.findViewById(R.id.alphaImage);
        mMenuList =  (LinearLayout)m_FrameLayout.findViewById(R.id.menuList);
        mMenuList.setRotationY(180f);
        mMenuBackgorund = (LinearLayout)m_FrameLayout.findViewById(R.id.menulistBackground);
        mMenuFoucs =  (ImageView)m_FrameLayout.findViewById(R.id.menuFocus);
        mListScroller = new Scroller(hostActivity);
        mFocusScroller = new Scroller(hostActivity);
        
        Drawable draw = Drawable.createFromPath(mPanelData.getStyle());
        if (draw != null)
            mBitmapFront = ((BitmapDrawable)draw).getBitmap();
        
        draw = Drawable.createFromPath(mPanelData.getAnimationPic());
        if (draw != null)
            mBitmapBack = ((BitmapDrawable)draw).getBitmap();
        
        ArrayList<panelmenu> menu_list = mPanelData.getmenulist();
        if (menu_list != null && menu_list.size() != 0) {
            mListAdapter = new MenuListAdapter(mContext, menu_list);
            updateListAdapter();
        }
        
        switchToFrontView();

        mPanelWithoutReflection.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mPanelWithoutReflection.layout(0, 0, 
                mPanelWithoutReflection.getMeasuredWidth(), 
                mPanelWithoutReflection.getMeasuredHeight());
        
        updateReflection();
        
        createAnimation();
	}
	
    @Override
    public void computeScroll() {
        if (mListScroller.computeScrollOffset()) {
            int currY = mListScroller.getCurrY();
            // Log.d("shibojun", "mMenuList currY: " + currY);
            // mMenuBackgorund.scrollTo(0, currY);
            mMenuList.setTranslationY(currY);
            mMenuBackgorund.setTranslationY(currY);
            updateReflection();
            invalidate();
        } else if (mFocusScroller.computeScrollOffset()) {
            int currY = mFocusScroller.getCurrY();
            // Log.d("shibojun", "mMenuFoucs currY: " + currY);
            mMenuFoucs.setTranslationY(currY);
            updateReflection();
            invalidate();
        }
    }
    
    public void setMask(float alpha) {
        //alphaImage.setAlpha(alpha);   // Commented by hxb
    }
    
    public void setSelection(int position) {
        int scrollCount = 0, scrollDistance = 0, scrollDirection = 0;
        int i = 0;
        View child;
        
        // Log.d("shibojun", mPanelData.getName() + ": " + "setSelection: " + position);
        
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
            mFocusScroller.startScroll(0, mFocusY, 0, scrollDistance, SCROLL_DURATION);
            mFocusY += scrollDistance;
        }
        mSelectedIndex += scrollCount;
        
        // Log.d("shibojun", mPanelData.getName());
        // Log.d("shibojun", "mFirstIndex: " + mFirstIndex);
        // Log.d("shibojun", "scrollDistance: " + scrollDistance);
        // Log.d("shibojun", "scrollDirection: " + scrollDirection);
        // Log.d("shibojun", "mSelectedIndex: " + mSelectedIndex);
        
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
        return m_FrameLayout;
    }
    
    private void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            m_imageView1.setImageBitmap(bitmap);
            m_imageView1.setLayoutParams(new LayoutParams(PANEL_WIDTH, PANEL_HEIGHT));
        }
    }

    private void switchToBackView() {
        setImageBitmap(mBitmapBack);
        mMenuList.setVisibility(VISIBLE);
        mMenuBackgorund.setVisibility(VISIBLE);
        mMenuFoucs.setVisibility(VISIBLE);
        m_OriginView.setText(mPanelData.getName());
        m_OriginView.setTextColor(Color.WHITE);
        m_OriginView.setRotationY(180f);
        isFrontSide = false;
        // Next lines commented by hxb
        /*
        if (mBitmapInvertedBack != null)
            mInvertedImage.setImageBitmap(mBitmapInvertedBack);
        else
            updateReflection();
            */
        invalidate();
    }
    
    private void switchToFrontView() {
        setImageBitmap(mBitmapFront);
        mMenuList.setVisibility(INVISIBLE);
        mMenuBackgorund.setVisibility(INVISIBLE);
        mMenuFoucs.setVisibility(INVISIBLE);
        m_OriginView.setText(mPanelData.getName());
        m_OriginView.setTextColor(Color.BLACK);
        m_OriginView.setRotationY(0f);
        isFrontSide = true;
        // Next lines commented by hxb
        /*
        if (mBitmapInvertedFront != null)
            mInvertedImage.setImageBitmap(mBitmapInvertedFront);
            */
        invalidate();
    }

    private void updateReflection() {
        // Follow lines commented by hxb
        /*
        Bitmap invertedBitmap = createInvertedBitmap();
        if (invertedBitmap != null) {
            if (isFrontSide) {
                mBitmapInvertedFront = invertedBitmap;
            } else {
                mBitmapInvertedBack = invertedBitmap;
            }
            mInvertedImage.setImageBitmap(invertedBitmap);
            mInvertedImage.setTop(PANEL_HEIGHT);
            invalidate();
        }
     */
    }
    
    private Bitmap createInvertedBitmap() {
        invalidate();
        
        //mPanelWithoutReflection.buildDrawingCache();
        //Bitmap originalImage = mPanelWithoutReflection.getDrawingCache();
        
        Bitmap originalImage = Bitmap.createBitmap(PANEL_WIDTH, PANEL_HEIGHT, Bitmap.Config.ARGB_8888);
        if (originalImage == null)
            return null;
        
        Canvas canvas = new Canvas(originalImage);
        mPanelWithoutReflection.draw(canvas);
        
        int width = PANEL_WIDTH;
        int height = PANEL_HEIGHT / 2;
        
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, PANEL_HEIGHT / 2, width, height, matrix, false);
        
        if (reflectionImage == null)
            return null;
        
        // Log.d("shibojun", "originalImage: " + "width: " + originalImage.getWidth() + ", height: " + originalImage.getHeight());
        
        LinearGradient shader = new LinearGradient(0, 0, 0, height, 0xdd404040, 0x00000000, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));
        //paint.setAntiAlias(true);
        canvas = new Canvas(reflectionImage);
        canvas.drawRect(0, 0, width, reflectionImage.getHeight(), paint);
        
        //originalImage.recycle();
        //originalImage = null;
        
        //mPanelWithoutReflection.setDrawingCacheEnabled(false);
        
        return reflectionImage;
    }
    
    public void setFinishedRunnable(Runnable run1, Runnable run2) {
        if (run1 != null)
            runFinished1 = run1;
        if (run2 != null)
            runFinished2 = run2;
    }
    
    /**
     * 中间慢两端快的插值算法
     */
    @Deprecated
    class AnimInterpolator implements Interpolator {
        
        public AnimInterpolator() {
        }
        
        public AnimInterpolator(Context context, AttributeSet attrs) {
        }
        
        public float getInterpolation(float input) {
            if (input < 0.5f)
                return (float) (0.5f * Math.sqrt(Math.sin(input * Math.PI)));
            else
                return (float) (-0.5f * Math.sqrt(Math.sin(input * Math.PI)) + 1);
        }
    }

    private void createAnimation() {
        rotateAnim1 = ObjectAnimator.ofFloat(this, "rotationY", 0f, 180f);
        scaleXAnim1 = ObjectAnimator.ofFloat(this, "scaleX", ROTATE_SCALE_MIN, ROTATE_SCALE_MAX);
        scaleYAnim1 = ObjectAnimator.ofFloat(this, "scaleY", ROTATE_SCALE_MIN, ROTATE_SCALE_MAX);
        animSet1 =  new AnimatorSet();
        animSet1.playTogether(rotateAnim1, scaleXAnim1, scaleYAnim1);
        animSet1.setDuration(ROTATE_DURATION);
       // animSet1.setInterpolator(new AnimInterpolator());
        rotateAnim1.addUpdateListener(this);
        
        rotateAnim2 = ObjectAnimator.ofFloat(this, "rotationY", 180f, 0f);
        scaleXAnim2 = ObjectAnimator.ofFloat(this, "scaleX", ROTATE_SCALE_MAX, ROTATE_SCALE_MIN);
        scaleYAnim2 = ObjectAnimator.ofFloat(this, "scaleY", ROTATE_SCALE_MAX, ROTATE_SCALE_MIN);
        animSet2 =  new AnimatorSet();
        animSet2.playTogether(rotateAnim2, scaleXAnim2, scaleYAnim2);
        animSet2.setDuration(ROTATE_DURATION);
       // animSet2.setInterpolator(new AnimInterpolator());
        rotateAnim2.addUpdateListener(this);
        
        // listener1: front to back finished
        animLis1 = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                animStarted = false;
                switchToBackView();
                needSwitch = false;
                if (runFinished1 != null)
                    runFinished1.run();
                anim.removeAllListeners();
                // Log.d("shibojun", "animLis1: " + "animStarted: " + animStarted + ", " + "needSwitch: " + needSwitch);
            }
        };
        // listener2: back to front finished
        animLis2 = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                animStarted = false;
                switchToFrontView();
                needSwitch = false;
                if (runFinished2 != null)
                    runFinished2.run();
                anim.removeAllListeners();
                // Log.d("shibojun", "animLis2: " + "animStarted: " + animStarted + ", " + "needSwitch: " + needSwitch);
            }
        };
    }
    
    public void flipit() {
        // Log.d("shibojun", "flipit: " + "animStarted: " + animStarted + ", " + "needSwitch: " + needSwitch);
        if (animSet1.isRunning()) {
            //Log.d("shibojun", "animSet1 isRunning");
        } else if (animSet2.isRunning()) {
            // Log.d("shibojun", "animSet2 isRunning");
            long curTime = ROTATE_DURATION - rotateAnim2.getCurrentPlayTime();
            animSet2.removeAllListeners();
            animSet2.end();
            animSet1.addListener(animLis1);
            animSet1.start();
            rotateAnim1.setCurrentPlayTime(curTime);
            scaleXAnim1.setCurrentPlayTime(curTime);
            scaleYAnim1.setCurrentPlayTime(curTime);
        } else {
            // Log.d("shibojun", "no anim isRunning");
            animSet1.removeAllListeners();
            animSet2.removeAllListeners();
            animSet1.addListener(animLis1);
            animSet1.start();
            animStarted = true;
        }
        // Log.d("shibojun", "after flipit: " + "animStarted: " + animStarted + ", " + "needSwitch: " + needSwitch);
    }

    public void backit() {
        // Log.d("shibojun", "backit: " + "animStarted: " + animStarted + ", "+ "needSwitch: " + needSwitch);
        if (animSet1.isRunning()) {
            // Log.d("shibojun", "animSet1 isRunning");
            long curTime = ROTATE_DURATION - rotateAnim1.getCurrentPlayTime();
            animSet1.removeAllListeners();
            animSet1.end();
            animSet2.addListener(animLis2);
            animSet2.start();
            rotateAnim2.setCurrentPlayTime(curTime);
            scaleXAnim2.setCurrentPlayTime(curTime);
            scaleYAnim2.setCurrentPlayTime(curTime);
        } else if (animSet2.isRunning()) {
            // Log.d("shibojun", "animSet2 isRunning");
        } else {
            // Log.d("shibojun", "no anim isRunning");
            hideItemsOutOfList();
            animSet1.removeAllListeners();
            animSet2.removeAllListeners();
            animSet2.addListener(animLis2);
            animSet2.start();
            animStarted = true;
        }
        // Log.d("shibojun", "after backit: " + "animStarted: " + animStarted + ", " + "needSwitch: " + needSwitch);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator arg0) {
        if (animStarted) {
            float currAngle = this.getRotationY();
         // Log.i("zhenghui","currAngle="+currAngle);
            if (arg0.equals(rotateAnim1)) {
                //Log.i("zhenghui","currAngle="+currAngle);
                if (currAngle < SWITCH_ANGLE_MIN) {
                    if (!needSwitch) {
                        needSwitch = true;
                    }
                } else {
                    if (needSwitch) {
                        switchToBackView();
                        needSwitch = false;
                    }
                }
            } else if (arg0.equals(rotateAnim2)) {
                if (currAngle > SWITCH_ANGLE_MAX) {
                    if (!needSwitch) {
                        needSwitch = true;
                    }
                } else {
                    if (needSwitch) {
                        switchToFrontView();
                        needSwitch = false;
                    }
                }
            }
        }
        invalidate();
        /*
        View parentView = (View)this.getParent();
        if (parentView != null)
            parentView.invalidate();
        */
        return;
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

 class TgallerypanelData {
	 
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

public class TgallerypanelAdapter<MiddleImageView> extends TImageAdapter{
	private Context mContext;
    private  List<Tpanel> panels;
    private TgallerypanelData mImageData;
    private List<TgallerypanelView> m_TgallerypanelViews = null;
    int i=0,j;
    private ListView m_ListView;
	int panelnum=0;
	public Boolean First;
	
	
	public TgallerypanelAdapter(Context c) {
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
	
	public void rotate(final int position, int direction, Runnable runFinished) {
        if (direction == TgallerypanelView.ROTATE_FRONT_TO_BACK) {
            m_TgallerypanelViews.get(position).setFinishedRunnable(runFinished, null);
            m_TgallerypanelViews.get(position).flipit();
        } else if (direction == TgallerypanelView.ROTATE_BACK_TO_FRONT){
            m_TgallerypanelViews.get(position).setFinishedRunnable(null, runFinished);
            m_TgallerypanelViews.get(position).backit();
        }
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
		m_TgallerypanelViews = new ArrayList<TgallerypanelView>(panels.size());
		
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
				m_TgallerypanelViews.add(new TgallerypanelView(mContext, panels.get(panel_idx)));
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
