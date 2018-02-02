package com.sys.info;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * Created by wangxingmin on 2018/2/2.
 */

public class SysInfoProvider {
    private volatile static SysInfoProvider instance;
    private JSONObject cachePhoneInfo=null;
    private JSONObject cacheScreenInfo=null;
//    private String cacheCpuInfoJson ="";
//    private JSONObject cacheCpuInfoJsonObj =null;
//    private int cacheCoreNums = 0;


    public static SysInfoProvider getSingleton() {
        if (instance == null) {                         //Single Checked
            synchronized (GLinfoProvider.class) {
                if (instance == null) {                 //Double Checked
                    instance = new SysInfoProvider();
                }
            }
        }
        return instance ;
    }

    /**
     * 获取手机是否root信息
     * @return
     */
    public String isRoot(){
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
     * 获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    private String getMacAddress(Context ctx){
        String result = "";
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return "手机macAdd:" + result;
    }

    public String getVersionName(Context context) {
        String versionName="Unknown";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }catch (PackageManager.NameNotFoundException e)
        {
            Log.e("SysInfo",e.toString());
        }
        return  versionName;
    }

    public Float getRereshRate(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRating = display.getRefreshRate();
        return refreshRating;
    }
    /**
     * 获取IMEI号，IESI号，手机型号
     */
    public String getInfo(Context ctx,TelephonyManager mTm) throws JSONException{
        if(cachePhoneInfo==null) {
            String imei = mTm.getDeviceId();
            if (imei == null)
                imei = "88888";
            String imsi = mTm.getSubscriberId();
            String type = android.os.Build.MODEL; // 手机型号
            String brand = android.os.Build.BRAND;//手机品牌
            String product = android.os.Build.PRODUCT;//整个产品的名称
            String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
            if (numer == null)
                numer = "0";
            String OSver = android.os.Build.VERSION.RELEASE;

            cachePhoneInfo = new JSONObject();
            cachePhoneInfo.put("手机品牌",brand);
            cachePhoneInfo.put("手机型号",type);
            cachePhoneInfo.put("手机号码",numer);
            cachePhoneInfo.put("安卓版本",OSver);
            cachePhoneInfo.put("IMEI",imei);
            cachePhoneInfo.put("IMSI",imsi);
            cachePhoneInfo.put("基带版本",getBaseBandVersion());
            cachePhoneInfo.put("Kernel版本",getKernelVersion());
            cachePhoneInfo.put("APK版本",getVersionName(ctx));
            cachePhoneInfo.put("内存",getTotalMemory(ctx));
            cachePhoneInfo.put("Root",isRoot());
        }
        return cachePhoneInfo.toString(2);
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
     * 获得基带版本
     *
     * @return String
     */
    public static String getBaseBandVersion() {
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
     * 获得系统总内存
     */
    private  String getTotalMemory(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem;
            double mb = totalMemory / 1024.0;
            double gb = totalMemory / 1048576.0;
            double tb = totalMemory / 1073741824.0;
            DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
            String lastValue = "";
            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" GB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" MB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" KB");
            } else {
                lastValue = twoDecimalForm.format(totalMemory).concat(" Byte");
            }
            return  lastValue;
        }
        else {
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
                Log.e("",e.toString());
            }
            return  Formatter.formatFileSize(ctx, initial_memory);// Byte转换为KB或者MB，内存大小规格化
        }
    }

    /**
     * 获得手机屏幕宽高
     * @return
     */
    public String getScreenInfo(Activity act) throws  JSONException{
        if(cacheScreenInfo==null) {
            cacheScreenInfo=new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DisplayMetrics metric = new DisplayMetrics();
                act.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
                int width = metric.widthPixels; // 宽度（PX）
                int height = metric.heightPixels; // 高度（PX）
                float density = metric.density; // 密度（0.75 / 1.0 / 1.5）
                int densityDpi = metric.densityDpi;
                float widthInch = metric.widthPixels / densityDpi;
                float heightInch = metric.heightPixels / densityDpi;

                cacheScreenInfo.put("宽度(px):", width);
                cacheScreenInfo.put("高度(px):", height);
                cacheScreenInfo.put("Dpi:", densityDpi);
                cacheScreenInfo.put("物理宽度(英寸):", widthInch);
                cacheScreenInfo.put("物理高度(英寸):", heightInch);
            } else {
                DisplayMetrics dm = act.getResources().getDisplayMetrics();
                int w_screen = dm.widthPixels;
                int h_screen = dm.heightPixels;

                cacheScreenInfo.put("宽度(px):", w_screen);
                cacheScreenInfo.put("高度(px):", h_screen);
                cacheScreenInfo.put("Dpi:", dm.densityDpi);
            }
            cacheScreenInfo.put("屏幕刷新率:",getRereshRate(act.getApplicationContext()));
        }

        return cacheScreenInfo.toString(2);
    }
}
