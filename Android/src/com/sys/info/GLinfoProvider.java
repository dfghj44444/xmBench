package com.sys.info;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.OffscreenSurface;

import java.util.Arrays;

/**
 * Created by wangxingmin on 2018/1/22.
 */

public class GLinfoProvider {
    private volatile static GLinfoProvider instance;
    private String cacheINfoString="";
    private String cacheExtString ="";
    private GLinfoProvider (){

    }

    public static GLinfoProvider getSingleton() {
        if (instance == null) {                         //Single Checked
            synchronized (GLinfoProvider.class) {
                if (instance == null) {                 //Double Checked
                    instance = new GLinfoProvider();
                }
            }
        }
        return instance ;
    }
    public String GetGLESExtInfo()
    {
        if(cacheINfoString.length()<1) {
            getGpuInfo();
        }
        return cacheExtString;
    }
    public String getGpuInfo() {
        if(cacheINfoString.length()<1) {

            // We need a GL context to examine, which means we need an EGL surface.  Create a 1x1
            // pbuffer.
            EglCore eglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
            Boolean isES3 = (eglCore.getGlVersion() == 3);
            OffscreenSurface surface = new OffscreenSurface(eglCore, 1, 1);
            surface.makeCurrent();

            StringBuilder sb = new StringBuilder();

            sb.append("\nvendor    : ");
            sb.append(isES3 ? GLES30.glGetString(GLES30.GL_VENDOR) : GLES20.glGetString(GLES20.GL_VENDOR));
            sb.append("\nversion   : ");
            sb.append(isES3 ? GLES30.glGetString(GLES30.GL_VERSION) : GLES20.glGetString(GLES20.GL_VERSION));
            sb.append("\nrenderer  : ");
            sb.append(isES3 ? GLES30.glGetString(GLES30.GL_RENDERER) : GLES20.glGetString(GLES20.GL_RENDERER));
            sb.append("\nextensions:\n");
            sb.append(formatExtensions(isES3 ? GLES30.glGetString(GLES30.GL_EXTENSIONS) : GLES20.glGetString(GLES20.GL_EXTENSIONS)));

            sb.append("\n------------- EGL Information --------------");
            sb.append("\nvendor    : ");
            sb.append(eglCore.queryString(EGL14.EGL_VENDOR));
            sb.append("\nversion   : ");
            sb.append(eglCore.queryString(EGL14.EGL_VERSION));
            sb.append("\nclient API: ");
            sb.append(eglCore.queryString(EGL14.EGL_CLIENT_APIS));
            sb.append("\nextensions:\n");
            cacheExtString=eglCore.queryString(EGL14.EGL_EXTENSIONS);
            sb.append(formatExtensions(cacheExtString));

            surface.release();
            eglCore.release();

            cacheINfoString = sb.toString();
        }
        return cacheINfoString;
    }

    private String formatExtensions(String ext) {
        String[] values = ext.split(" ");
        Arrays.sort(values);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append("  ");
            sb.append(values[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

}
