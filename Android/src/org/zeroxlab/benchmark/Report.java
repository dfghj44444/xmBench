/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zeroxlab.benchmark;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.*;

import com.sys.info.GLinfoProvider;

import org.json.JSONException;

import java.util.HashSet;


/* Construct a basic UI */
public class Report extends Activity implements View.OnClickListener {

    public final static String TAG = "Repord";
    public final static String REPORT = "REPORT";
    public final static String XML = "XML";
    public final static String JSON = "JSON";
    public final static String AUTOUPLOAD = "AUTOUPLOAD";
    private TextView mTextView;

    private Button mUpload;
    private Button mBack;
    private String mXMLResult;
    private String mJSONResult;
    boolean mAutoUpload = false;
    MicroBenchmark mb;
    Handler mUploadHandler;
    String mHash;
    HashSet<String> mHashSet = new HashSet<String>();


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.report);

        mTextView = (TextView)findViewById(R.id.report_text);

        mUpload = (Button)findViewById(R.id.btn_upload);
        mUpload.setOnClickListener(this);

        mBack = (Button)findViewById(R.id.btn_back);
        mBack.setOnClickListener(this);

        Intent intent = getIntent();
        String report = intent.getStringExtra(REPORT);
        mXMLResult = intent.getStringExtra(XML);
        mJSONResult = intent.getStringExtra(JSON);
        mAutoUpload = intent.getBooleanExtra(AUTOUPLOAD, false);

        if (report == null || report.equals("")) {
            mTextView.setText("oooops...report not found");
        } else {
            mTextView.setText(report);
        }

        if (mJSONResult == null) {
            mUpload.setEnabled(false);
        }

        mUploadHandler = new Handler() {
            public void handleMessage(Message msg) {
                int state = msg.getData().getInt(MicroBenchmark.STATE);
                if (state != MicroBenchmark.RUNNING) {
                    try {
                        dismissDialog(0);
                        removeDialog(0);
                    } catch (Exception e) {
                        Log.e("upload",e.toString());
                    }
                    if (state == MicroBenchmark.DONE) {
                        showDialog(3);
                        showDialog(1);
                        mHashSet.add(mHash);
                    }
                    else {
                        showDialog(2);
                    }
                    Log.e(TAG, msg.getData().getString(MicroBenchmark.MSG));
                }
            }
        };
        if (mAutoUpload) {
            onClick(mUpload);
        }
    }

    public void onClick(View v) {
        if (v == mBack) {
            finish();
        }
        else if (v == mUpload) {
           // Intent intent = new Intent();
           // intent.putExtra(Upload.XML, mXMLResult);
           // intent.putExtra(Upload.JSON, mJSONResult);
           // if (mAutoUpload) {
           //     intent.putExtra(Upload.AUTOUPLOAD, true);
           // }
            //intent.setClassName(Upload.packageName(), Upload.fullClassName());
            
            //startActivity(intent);
            //skip the Activity ,upload directly
            String benchName = getString(R.string.default_benchname);

            String versionName = "";
            int versionCode = 0;
            int flag = 0;
            try {
                PackageInfo pinfo = getPackageManager().getPackageInfo("org.zeroxlab.benchmark", flag);
                versionCode = pinfo.versionCode;
                versionName = pinfo.versionName;
            }
            catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "PackageManager.NameNotFoundException");
            }

            String attr = "";
            attr += "\"VersionCode\":\"" + String.valueOf(versionCode) + "\",";
            attr += "\"VersionName\":\"" + versionName + "\",";
            attr += "\"benchmark\":\"" + benchName + "\",";
            attr += "\"imei\":\"" + GetImei() + "\",";
            attr += "\"name\":\"" +  android.os.Build.MODEL + "\",";
            attr += "\"brand\":\"" + android.os.Build.BRAND + "\",";
            try {
                attr += "\"eglext\":\"" + GLinfoProvider.getSingleton().GetEGLExtInfoString() + "\",";
                attr += "\"glesext\":\"" + GLinfoProvider.getSingleton().GetGLESExtInfoString() + "\",";
            }
            catch (JSONException e)
            {
                Log.e("",e.toString());
            }
            StringBuffer _mJSON = new StringBuffer(mJSONResult);

            _mJSON.insert(1, attr);
            Log.e(TAG, _mJSON.toString());

            String theURL = "http://" + getString(R.string.default_appspot) + ":80/";
            mb = new MicroBenchmark(_mJSON.toString() , theURL , benchName , mUploadHandler) ;
            // this is not really a hash
            mHash =  benchName;
            if (!mHashSet.contains(mHash)){
                showDialog(0);
                mb.start();
            } else {
                showDialog(4);
            }
        }
    }

    protected String GetImei(){
        TelephonyManager mTm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        if(imei == null )
            imei="888888";
        return imei;
    }

    public static String fullClassName() {
        return "org.zeroxlab.benchmark.Report";
    }

    public static String packageName() {
        return "org.zeroxlab.benchmark";
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case (0):
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading, please wait...");
                return dialog;
            case (1):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Upload complete.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                return builder.create();
            case (2):
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("上传失败.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                return builder2.create();
            case (3):
                String url = "http://" + getString(R.string.default_appspot) + "/ladder.php";

                final AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setMessage( "Please goto " + url + " for results" )
                        .setTitle("Result URL")
                        .setPositiveButton("OK",  null );

                return builder3.create();
            case (4):
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setMessage( "你已经上传过这个位置了。" )
                        .setTitle("Error")
                        .setPositiveButton("OK", null)
                ;
                return builder4.create();
            case (6):
                ProgressDialog dialog2 = new ProgressDialog(this);
                dialog2.setMessage("Connecting, please wait...");
                return dialog2;

            default:
                return null;
        }
    }

}
