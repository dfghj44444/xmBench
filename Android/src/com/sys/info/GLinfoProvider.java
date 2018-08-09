package com.sys.info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.OffscreenSurface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by wangxingmin on 2018/1/22.
 */

public class GLinfoProvider {
    private volatile static GLinfoProvider instance;
    private JSONObject cacheInfoJSON=null;
    private JSONObject cacheEGLJSON=null;
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

    public String GetEGLInfo() throws  JSONException
    {
        if(cacheEGLJSON == null) {
                GetGpuInfo();
        }
        return cacheEGLJSON.toString(2);
    }

    public String GetGLESExtInfoString() throws  JSONException
    {
        if(cacheInfoJSON == null) {
            GetGpuInfo();
        }
        return cacheInfoJSON.get("extensions").toString();
    }

    public String GetEGLExtInfoString() throws  JSONException
    {
        if(cacheEGLJSON == null) {
                GetGpuInfo();
        }
        return cacheEGLJSON.get("extensions").toString();
    }
    public String GetGPURenderer() throws  JSONException
    {
        if(cacheInfoJSON == null) {
            GetGpuInfo();
        }
        return cacheInfoJSON.get("renderer").toString();
    }


    //for Graphic Card
    public String GetGpuInfo() throws JSONException{
        if(cacheInfoJSON==null) {
            cacheInfoJSON = new JSONObject();
            // We need a GL context to examine, which means we need an EGL surface.  Create a 1x1
            // pbuffer.
            EglCore eglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
            Boolean isES3 = (eglCore.getGlVersion() == 3);
            OffscreenSurface surface = new OffscreenSurface(eglCore, 1, 1);
            surface.makeCurrent();

            cacheInfoJSON.put("vender",isES3 ? GLES30.glGetString(GLES30.GL_VENDOR) : GLES20.glGetString(GLES20.GL_VENDOR));
            cacheInfoJSON.put("version",isES3 ? GLES30.glGetString(GLES30.GL_VERSION) : GLES20.glGetString(GLES20.GL_VERSION));
            cacheInfoJSON.put("renderer", isES3 ? GLES30.glGetString(GLES30.GL_RENDERER) : GLES20.glGetString(GLES20.GL_RENDERER));
            cacheInfoJSON.put("extensions",isES3 ? GLES30.glGetString(GLES30.GL_EXTENSIONS) : GLES20.glGetString(GLES20.GL_EXTENSIONS));

            cacheEGLJSON = new JSONObject();
            cacheEGLJSON.put("vendor",eglCore.queryString(EGL14.EGL_VENDOR));
            cacheEGLJSON.put("version",eglCore.queryString(EGL14.EGL_VERSION));
            cacheEGLJSON.put("client API",eglCore.queryString(EGL14.EGL_CLIENT_APIS));
            cacheEGLJSON.put("extensions",eglCore.queryString(EGL14.EGL_EXTENSIONS));
            //put into cacheExtJSON

            cacheExtString=formatExtensions(cacheExtString);

            surface.release();
            eglCore.release();

        }
        return cacheInfoJSON.toString(2);
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
