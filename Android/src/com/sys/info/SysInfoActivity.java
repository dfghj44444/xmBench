package com.sys.info;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.zeroxlab.benchmark.R;
import java.io.IOException;

public class SysInfoActivity extends Activity {

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
        theText.setText("-----------------手机------------------- \n");
        try {
            theText.append(SysInfoProvider.getSingleton().GetInfoString(_ctx,(TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)));
            theText.append("\n -----------------屏幕信息------------------- \n" );
            theText.append(SysInfoProvider.getSingleton().getScreenInfo(this));

            theText.append("\n -----------------CPU------------------- \n" );
            theText.append( CpuInfoProvider.getSingleton().getCpuInfo());
                theText.append("\n" + CpuInfoProvider.getSingleton().getCPUInfoJSON());

            theText.append("\n -----------------GPU-------------------- \n" );
            theText.append(GLinfoProvider.getSingleton().GetGpuInfo());
            theText.append("\n------------- EGL Information ----------- \n");
            theText.append(GLinfoProvider.getSingleton().GetEGLInfo());
            theText.append("\n -----------------其他------------------- \n" );
        }
        catch(JSONException e)
        {
            Log.e( "InitViews ", e.toString());
        }
        catch (IOException e)
        {
            Log.d("",e.toString());
        }
    }
}
