package com.amw.opengles.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

public class MyWallpaperService extends OpenGLES2WallpaperService{
    @Override
    Renderer getNewRenderer() {
        return new MyLiveWallpaperEngine();
    }

}
