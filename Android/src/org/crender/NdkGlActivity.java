package org.crender;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by wangxingmin on 2018/1/24.
 */

public class NdkGlActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView surface = new GLSurfaceView(this);
        surface.setRenderer(new NDKGlRender());
        setContentView(surface);
    }
    static {
        //load library
        System.loadLibrary("NdkGLRenderer");
    }

}
