package com.sys.info;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by wangxingmin on 2018/2/2.
 */

public class SysInfoProvider {
    private volatile static SysInfoProvider instance;
    private JSONObject cachePhoneInfo=null;
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
    public String getInfo(TelephonyManager mTm) throws JSONException{
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
        }
        return cachePhoneInfo.toString(2);
    }
}
