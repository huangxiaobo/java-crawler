package com.amw.opengles.livewallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView.Renderer;
import android.view.SurfaceHolder;

public abstract class OpenGLES2WallpaperService extends GLWallpaperService{
    @Override
    public Engine onCreateEngine() {
        return new OpenGLES2Engine();
    }
    
    class OpenGLES2Engine extends GLWallpaperService.GLEngine {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            
            final ActivityManager activityManager = 
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = 
                    activityManager.getDeviceConfigurationInfo();
            final boolean supportsES2 = 
                    configurationInfo.reqGlEsVersion >= 0x20000;
            
            if (supportsES2) {
                setEGLContextClientVersion(2);
                setRenderer(getNewRenderer());
            } else {
                return;
            }    
        }
    }
    abstract Renderer getNewRenderer();
}
