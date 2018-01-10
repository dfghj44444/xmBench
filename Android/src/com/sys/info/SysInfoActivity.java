package com.sys.info;

import android.app.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import org.zeroxlab.benchmark.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10;

public class SysInfoActivity extends Activity {
    //ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.ctrlActivityIndicator)
    //above is a sample
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_info);

        InitViews();
    }

    private void InitViews() {
        TextView theText = (TextView) this.findViewById(R.id.txtCPU);
        theText.setText("-----------------手机-------------------\n");
        theText.append( getInfo());
        theText.append("\n -----------------CPU------------------- \n" );
        theText.append( getCpuInfo());
        theText.append("\n" + getTotalMemory());

        theText.append("\n -----------------GPU------------------- \n" );
        theText.append("GLES支持:GLES"+getGlVersion(getApplicationContext()));
        theText.append("\n -----------------其他------------------- \n" );

        theText.append(getHeightAndWidth());
        theText.append("\n基带:" + getBaseBandVersion());
        theText.append("\nKernel:" + getKernelVersion());
        theText.append("\n" + isRoot());
    }

    public String getGpuInfo() {

        String theStrLog= "Renderer:" + GLES10.glGetString(GL10.GL_RENDERER);
        theStrLog += "Vendor:" + GLES10.glGetString(GL10.GL_VENDOR);
         theStrLog +="GL_VERSION = " + GLES10.glGetString(GL10.GL_VERSION);
        theStrLog += "GL_EXTENSIONS = " + GLES10.glGetString(GL10.GL_EXTENSIONS);

        return theStrLog;
    }
    public String getGlVersion(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = am.getDeviceConfigurationInfo();
            return configurationInfo.getGlEsVersion();
        } else {
            return GLES10.glGetString(GLES10.GL_VERSION);
        }
    }

    /**
     * 获得基带版本
     *
     * @return String
     */
    public String getBaseBandVersion() {
        String version = "";
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Object object = clazz.newInstance();
            Method method = clazz.getMethod("get", new Class[]{String.class, String.class});
            Object result = method.invoke(object, new Object[]{"gsm.version.baseband", "no message"});
            version = (String) result;
        } catch (Exception e) {
        }
        return version;
    }

    /**
     * 获得内核版本
     *
     * @return String
     */
    public String getKernelVersion() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8 * 1024);
        String result = "";
        String info;
        try {
            while ((info = bufferedReader.readLine()) != null) {
                result += info;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (result != "") {
                String keyword = "version ";
                int index = result.indexOf(keyword);
                info = result.substring(index + keyword.length());
                index = info.indexOf(" ");
                kernelVersion = info.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;

    }

    /**
     * 获取手机是否root信息
     * @return
     */
    private String isRoot(){
        String bool = "Root:false";
        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool = "Root:false";
            } else {
                bool = "Root:true";
            }
        } catch (Exception e) {
        }
        return bool;
    }
    /**
     * 获得系统总内存
     */
    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return "总内存大小：" + Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获得手机屏幕宽高
     * @return
     */
    public String getHeightAndWidth(){
        int width=getWindowManager().getDefaultDisplay().getWidth();
        int heigth=getWindowManager().getDefaultDisplay().getHeight();
        String str = "Width:" + width+"\nHeight:"+heigth+"";
        return str;
    }
    /**
     * 获取IMEI号，IESI号，手机型号
     */
    private String getInfo() {
        TelephonyManager mTm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb= android.os.Build.BRAND;//手机品牌
        String carrier= android.os.Build.MANUFACTURER;
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        String OSver = android.os.Build.VERSION.RELEASE;
        return "手机品牌："+mtyb + " " +carrier +"\n手机型号："+mtype+"\nAndroid版本:"+ OSver +"\n手机号码："+numer+"\n手机IMEI号："+imei+"\n手机IESI号："+imsi;
    }
    /**
     * 获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    private String getMacAddress(){
        String result = "";
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return "手机macAdd:" + result;
    }
    /**
     * 手机CPU信息
     */
    private String getCpuInfo() {
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
        return "CPU型号:" + cpuInfo[0] + "\nCPU频率：" + cpuInfo[1] +"MHz\nCPU核心数目："+ getNumCores();
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
}
