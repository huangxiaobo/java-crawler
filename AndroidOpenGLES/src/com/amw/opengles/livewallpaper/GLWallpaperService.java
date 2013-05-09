package com.amw.opengles.livewallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService{

    public class GLEngine extends Engine {
        private static final String TAG = "GLEngine";
        private WallpaperGLSurfaceView glsurfaceView;
        private boolean rendererHasBeenSet;
        
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";
            
            public WallpaperGLSurfaceView(Context context) {
                super(context);
                // TODO Auto-generated constructor stub
            }

            @Override
            public SurfaceHolder getHolder() {
                return  getSurfaceHolder();
            }
            
            public void onDestroy() {
                super.onDetachedFromWindow();
            }
            
            
        }
        
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glsurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }
        
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            
            if (rendererHasBeenSet) {
                if (visible)
                    glsurfaceView.onResume();
                else
                    glsurfaceView.onPause();
            }
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            glsurfaceView.onDestroy();
        }
        
        //Helper methods
        protected void setRenderer(Renderer renderer) {
            glsurfaceView.setRenderer(renderer);
            rendererHasBeenSet = true;
        }
        
        protected void setEGLContextClientVersion(int version) {
            glsurfaceView.setEGLContextClientVersion(version);
        }
    }
}
