package com.sys.info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.OffscreenSurface;

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

    public String getCpuInfo()
    {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];

            localBufferedReader.close();
        } catch (IOException e) {
        }
        String cpuMaxFreq = "";
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            cpuMaxFreq = reader.readLine();
            cpuMaxFreq =  String.valueOf(Integer.parseInt(cpuMaxFreq)/1024);
            reader.close();
        }
        catch (IOException e)
        {
            cpuMaxFreq = cpuInfo[1];
            Log.e("info",e.toString());
        }
        return "CPU型号:" + cpuInfo[0] + "\nCPU频率：" + cpuMaxFreq +"MHz\nCPU核心数目："+ getNumCores();
    }

    public String getCPUInfoJSON () throws IOException {

        String output = "{";

        BufferedReader br = new BufferedReader (new FileReader ("/proc/cpuinfo"));

        String str;

        while ((str = br.readLine ()) != null) {

            String[] data = str.split (":");

            if (data.length > 1) {

                String key = data[0].trim ().replace (" ", "_");
                if (key.equals ("model_name"))
                    key = "cpu_model";

                String value = data[1].trim ();
                if (key.equals ("cpu_model"))
                    value = value.replaceAll ("\\s+", " ");
                output+="\"" + key+ "\":\""+ value+"\",";
            }
        }

        br.close ();
        output = output.substring(0, output.length()-1) + "}";
        return output;
    }

    //for Graphic Card
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

    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            Log.d("warning", "CPU Count: "+files.length);
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            Log.d("warning", "CPU Count: Failed.");
            e.printStackTrace();
            return 1;
        }
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
