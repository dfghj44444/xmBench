package org.crender;

import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangxingmin on 2018/1/24.
 */

public class NDKGlRender implements  Renderer {
    //declare native function
    native private void onNdkSurfaceCreated ();
    native private void onNdkSurfaceChanged (int width, int height);
    native private void onNdkDrawFrame();
    @Override
    public void onDrawFrame(GL10 arg0) {
        // TODO Auto-generated method stub
        onNdkDrawFrame();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        onNdkSurfaceChanged(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        onNdkSurfaceCreated();
    }
}
