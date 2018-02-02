package com.sys.info;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.zeroxlab.benchmark.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SysInfoActivity extends Activity {
    //ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.ctrlActivityIndicator)
    //above is a sample

    protected Context _ctx=null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_info);
        _ctx = getApplicationContext();
        InitViews();
    }

    private void InitViews() {
        TextView theText = (TextView) this.findViewById(R.id.txtCPU);
        theText.setText("-----------------手机-------------------\n");
        try {
            theText.append(SysInfoProvider.getSingleton().getInfo(_ctx,(TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)));

        }
        catch(JSONException e)
        {
            Log.e( "InitViews ", e.toString());
        }

        theText.append("\n -----------------屏幕信息------------------- \n" );

        theText.append(getHeightAndWidth());
        theText.append("\n屏幕刷新率:"+SysInfoProvider.getSingleton().getRereshRate(_ctx));

        theText.append("\n -----------------CPU------------------- \n" );
        theText.append( CpuInfoProvider.getSingleton().getCpuInfo());
        try {
            theText.append("\n" + CpuInfoProvider.getSingleton().getCPUInfoJSON());
        }catch (IOException e)
        {
            Log.d("",e.toString());
        }
        theText.append("\n -----------------GPU------------------- \n" );
        theText.append(GLinfoProvider.getSingleton().getGpuInfo());
        theText.append("\n -----------------其他------------------- \n" );

    }




    /**
     * 获得手机屏幕宽高
     * @return
     */
    public String getHeightAndWidth(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(metric);
            int width = metric.widthPixels; // 宽度（PX）
            int height = metric.heightPixels; // 高度（PX）
            float density = metric.density; // 密度（0.75 / 1.0 / 1.5）
            int densityDpi = metric.densityDpi;
            float widthInch = metric.widthPixels/densityDpi;
            float heightInch =  metric.heightPixels/densityDpi;

            String str = "宽度(px):" + width + "\n高度(px):" + height + "\nDpi:" + densityDpi+"\n物理宽度(英寸):"+widthInch+"\n物理高度(英寸):"+heightInch;
            return str;
        }
        else
        {
            DisplayMetrics dm =getResources().getDisplayMetrics();
            int w_screen = dm.widthPixels;
            int h_screen = dm.heightPixels;
            String str= "宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi;
            return str;
        }
    }
}
