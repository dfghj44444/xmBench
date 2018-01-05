package org.zeroxlab.benchmark;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by wangxingmin on 2018/1/5.
 */

public class CaseInfo extends Case {
    String mStringBuf = "";
    public static String GCRESULT = "GC_RESULT";
    public static String TIME = "GC_RUNTIME";
    public static double time = 0.0;

    CaseInfo() {
        super("CaseInfo", "org.zeroxlab.benchmark.TesterGC", 1, 1); // GC benchmark only run once

        mType = "info";
        String [] _tmp = {
                "info",
                "system",
        };
        mTags = _tmp;
    }

    public String getTitle() {
        return "系统信息";
    }

    public String getDescription() {

//        if(!GetFromCache())
//            CalcIt();
        return "It create long-live binary tree of depth and array of doubles to test GC";
    }

    @Override
    public void clear() {
        super.clear();
        mStringBuf = "";
    }

    @Override
    public void reset() {
        super.reset();
        mStringBuf = "";
    }

    @Override
    public String getResultOutput() {

        if (!couldFetchReport()) {
            return "No benchmark report";
        }

        return mStringBuf;
    }

    public double getBenchmark(Scenario s) {
        return time;
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

        Scenario s = new Scenario(getTitle(), mType, mTags);
        s.mLog = getResultOutput();
        s.mResults.add(time);
        scenarios.add(s);

        return scenarios;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
        String result = intent.getStringExtra(GCRESULT);
        time = intent.getDoubleExtra(TIME, 0.0);

        if (result == null || result.equals("")) {
            mStringBuf += "\nReport not found\n";
        } else {
            mStringBuf += "\n"+result+"\n";
        }

        return true;
    }
}
