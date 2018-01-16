
package org.zeroxlab.benchmark;

import org.zeroxlab.benchmark.*;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;

public class CaseIO  extends Case {


    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 1;

    public CaseIO() {
        super("CaseIO", TesterIO.FullName, Repeat, Round);

        mType = "io";//used as unit in result
        String [] _tmp = {
            "system",
                "io"
        };
        mTags = _tmp;

        generateInfo();
    }

    public String getTitle() {
        return "IOBench";
    }

    public String getDescription() {
        return "测试内存和SD卡读写性能。";
    }

    private void generateInfo() {
        mInfo = new Bundle[Repeat];
        for (int i = 0; i < mInfo.length; i++) {
            mInfo[i] = new Bundle();
        }
    }

    @Override
    public void clear() {
        super.clear();
        generateInfo();
    }

    @Override
    public void reset() {
        super.reset();
        generateInfo();
    }

    @Override
    public String getResultOutput() {
        if (!couldFetchReport()) {
            return "No benchmark report";
        }

        return "IO Time:" + mInfo[0].getDouble(TesterIO.RESULT);
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

        Scenario s = new Scenario(getTitle(), mType, mTags);

        for (int j = 0; j < mInfo.length; j++) {
            double _tmp = mInfo[j].getDouble( TesterIO.RESULT);
                s.mResults.add(_tmp);
        }
        scenarios.add(s);

        return scenarios;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
        Bundle info = intent.getBundleExtra(TesterIO.RESULT);
        if (info == null) {
            Log.i(TAG, "Cannot find CaseIO Info");
            return false;
        } else {
            mInfo[index] = info;
        }

        return true;
    }
}
