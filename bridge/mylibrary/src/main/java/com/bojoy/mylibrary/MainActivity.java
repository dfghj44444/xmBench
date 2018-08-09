package com.bojoy.mylibrary;

import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity{

    public static int myAdd(int a,int b)
    {
        return a+b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    public static String GetCPU() {
        String cpu = "";
        try {
            String str1 = "/proc/cpuinfo";
            String[] cpuInfo = { "", "" };
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            String line = null;
            while ((line = localBufferedReader.readLine()) != null) {
                if (line.toLowerCase().indexOf("hardware") != -1) {
                    cpuInfo[0] = line;
                    break;
                }
            }
            cpuInfo[1] = Build.HARDWARE;
            localBufferedReader.close();
            cpu = cpuInfo[0] + "&" + cpuInfo[1];
        } catch (Exception e) {
            Log.e("xm",e.toString());
        }
        return cpu;
    }

    public static boolean IsEmulator() {
        boolean ret = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("sdk_phone")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.BRAND.equals("Android")
                //|| Build.MANUFACTURER.startsWith("Netease")
                || "google_sdk".equals(Build.PRODUCT);
        if(ret ==false)
            ret = CheckPackageName();
        return ret;
    }

    private static boolean CheckPackageName() {
        List<String> mListPackageName = new ArrayList<>();
        mListPackageName.add("com.google.android.launcher.layouts.genymotion");
        mListPackageName.add("com.bluestacks");
        mListPackageName.add("com.bignox.app");
        if ( mListPackageName.isEmpty()) {
            return false;
        }

        final PackageManager packageManager =  UnityPlayer.currentActivity.getPackageManager();
        for (final String pkgName : mListPackageName) {
            final Intent tryIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if (tryIntent != null) {
                final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(tryIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!resolveInfos.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public  static String IsEmulatorDebug() {
        String str1 =  Build.FINGERPRINT
                +";"+  Build.MODEL
                +";"+  Build.MANUFACTURER
                +";"+  Build.BRAND
                +";"+  Build.DEVICE
                +";"+  Build.PRODUCT;

        String str2= "";

        try {

             PackageManager packageManager = UnityPlayer.currentActivity.getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES );
            for (PackageInfo pi : packages) {
                str2 += " ; " + pi.packageName;
            }
        }
        catch (RuntimeException ee)
        {
            return ee.toString();
        }
        catch (Exception e)
        {
            return e.toString();
        }

        //
       /* String command = "pm list packages -u";
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + ";");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();*/
        //
        return str2;
    }
}