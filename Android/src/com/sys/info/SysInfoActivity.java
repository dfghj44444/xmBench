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
            theText.append("\n -----------------屏幕信息------------------- \n" );
            theText.append(SysInfoProvider.getSingleton().getScreenInfo(this));

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
        catch(JSONException e)
        {
            Log.e( "InitViews ", e.toString());
        }
    }
}
