package com.sys.info;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

/**
 * Created by wangxingmin on 2018/2/2.
 */

public class CpuInfoProvider {
    private volatile static CpuInfoProvider instance;
    private String cacheCpuInfo="";
    private String cacheCpuInfoJson ="";
    private JSONObject cacheCpuInfoJsonObj =null;
    private int cacheCoreNums = 0;

    String[] mArmArchitecture=new String[2];
    public static CpuInfoProvider getSingleton() {
        if (instance == null) {                         //Single Checked
            synchronized (GLinfoProvider.class) {
                if (instance == null) {                 //Double Checked
                    instance = new CpuInfoProvider();
                }
            }
        }
        return instance ;
    }

    public String getCpuInfo()
    {
        if(cacheCpuInfo.length()<1) {
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
                cpuMaxFreq = String.valueOf(Integer.parseInt(cpuMaxFreq) / 1024);
                reader.close();
            } catch (IOException e) {
                cpuMaxFreq = cpuInfo[1];//type 2
                Log.e("info", e.toString());
            }
            cacheCpuInfo="CPU型号:" + cpuInfo[0] + "\nCPU频率：" + cpuMaxFreq +"MHz\nCPU核心数目："+ getNumCores() +"\n支持的指令集:\n"+GetArchitecture();
        }

        return cacheCpuInfo;
    }

    public String getCPUInfoJSON () throws IOException {
        if(cacheCpuInfoJson.length()<1) {
            String output = "{";

            BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));

            String str;
            while ((str = br.readLine()) != null) {
                String[] data = str.split(":");

                if (data.length > 1) {

                    String key = data[0].trim().replace(" ", "_");
                    if (key.equals("model_name"))
                        key = "cpu_model";

                    String value = data[1].trim();
                    if (key.equals("cpu_model"))
                        value = value.replaceAll("\\s+", " ");
                    output += "\"" + key + "\":\"" + value + "\",";
                }
            }

            br.close();
            output = output.substring(0, output.length() - 1) + "}";
            cacheCpuInfoJson = output;
            try {
                cacheCpuInfoJsonObj = new JSONObject(cacheCpuInfoJson);

                if(cacheCpuInfoJsonObj.has("processor"))
                    cacheCpuInfoJsonObj.remove("processor");
                cacheCpuInfoJsonObj.put("Cores",getNumCores());
                cacheCpuInfoJson = cacheCpuInfoJsonObj.toString();
            }
            catch (JSONException e){
                Log.e("getCPUInfoJSON: ",e.toString() );
            }
        }
        return cacheCpuInfoJson;
    }

    private int getNumCores() {
        if(cacheCoreNums>0)
            return cacheCoreNums;

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

    /**
     *
     * [获取cpu类型和架构]
     *
     * @return
     * 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
     */
    public  String GetArchitecture() {
        String ret= "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for(int i=0;i< Build.SUPPORTED_ABIS.length;i++){
                ret+=android.os.Build.SUPPORTED_ABIS[i]+"\n";
            }
        }
        else
            ret= Build.CPU_ABI+"\n"+Build.CPU_ABI2;
        return ret;
    }
}
