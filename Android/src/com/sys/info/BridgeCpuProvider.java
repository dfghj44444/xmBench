package com.sys.info;

import java.io.BufferedReader;
import java.io.FileReader;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;

public class BridgeCpuProvider extends UnityPlayerActivity{


    public static int myAdd(int a,int b)
    {
        return a+b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static String GetCPU(Activity currentActivity) {
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
            if (Utile.isDebug())
                Utile.LogError(e.toString());
        }
        return cpu;
    }


}