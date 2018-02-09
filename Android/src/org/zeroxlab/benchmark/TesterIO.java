package org.zeroxlab.benchmark;
import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import org.zeroxlab.utils.BenchUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;


import jnt.scimark2.commandline;

public class TesterIO extends Tester {
    public static final String FullName = "org.zeroxlab.benchmark.TesterIO";
    public final String TAG = "CaseIoTester";
    Bundle mInfo[];

    public static String IO_TIME = "IO_TIME";
    public static String RESULT = "RESULT";
    TextView mTextView;

    protected int sleepBeforeStart() {
        return 1000;
    }

    protected int sleepBetweenRound() {
        return 200;
    }

    protected String getTag() {
        return "IO";
    };

    protected void oneRound() {
        runBenchMark(mInfo[mNow - 1]);
        decreaseCounter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int length = mRound;
        mInfo = new Bundle[length];
        for (int i = 0; i < length; i++) {
            mInfo[i] = new Bundle();
        }

        mTextView = new TextView(this);
        mTextView.setText("Running IO benchmark....");
        mTextView.setTextSize(mTextView.getTextSize() + 5);
        setContentView(mTextView);
        startTester();
    }
    @Override
    protected boolean saveResult(Intent intent) {
        Bundle result = new Bundle();
        TesterIO.average(result, mInfo);

        intent.putExtra(RESULT, result);
        return true;
    }

    public static void average(Bundle result, Bundle[] list) {
        if (result == null) {
            result = new Bundle();
        }

        if (list == null) {
            Log.i("IOTester", "Bundle Array is null");
            return;
        }

        int length = list.length;
        double time_total     = 0.0;

        for (int i = 0; i < length; i ++) {
            Bundle info = list[i];
            if (info == null) {
                Log.i("IOTester", "one item of array is null!");
                return;
            }
            time_total     += info.getDouble(IO_TIME    );
        }

        result.putDouble(RESULT    , time_total    / length);
    }

    double second_orig = -1;
    double second() {
        if (second_orig==-1) {
            second_orig = System.currentTimeMillis();
        }
        return (System.currentTimeMillis() - second_orig)/1000;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    String runBenchMark(Bundle aBundle)
    {
        double time,total;
        time = second();
        //do io read ad write
        File writeDir = new File(BenchUtil.getResultDir(this));
        if (!writeDir.exists()) {
            writeDir.mkdirs();
        }
        String filename= "";
        File file = new File(writeDir, "iotest");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e)
            {
                Log.e("IO Tester", "cant open write file");
                return "no write file";
            }
        }
        ///start ------------------
        try {
            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open("NEWS.txt");

            FileOutputStream fos = new FileOutputStream(file);

            file.createNewFile();
            String readLine;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            {
                while ((readLine = bufferedReader.readLine()) != null) {
                    String upperC = readLine.toUpperCase();

                    writer.write(upperC);
                    writer.newLine();
                    writer.flush();
                }
            }
            bufferedReader.close();
            writer.close();

        } catch (IOException e) {
            Log.i(TAG,"Error.");
            e.printStackTrace();
            return "wrtie fail";
        }catch (Exception e) {
            Log.i(TAG, "Write Failed.");
            e.printStackTrace();
            return "wrtie fail";
        }
        //wrtie ended -----------------------------------
        total = (second() - time) / 10.0D;
        aBundle.putDouble(IO_TIME, total);

        String retStr =  "IO Time: " + total + " secs";
        Log.e("IO Benchmark", retStr);
        return retStr;
    }
}

