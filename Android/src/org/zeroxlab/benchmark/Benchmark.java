/*
 * Copyright (C) 2010-2011 0xlab - http://0xlab.org/
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
 *
 * Authored by Julian Chu <walkingice@0xlab.org> and
 *             Joseph Chang (bizkit) <bizkit@0xlab.org>
 */

package org.zeroxlab.benchmark;

import android.os.PowerManager;
import android.util.Log;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import com.sys.info.SysInfoActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.zeroxlab.utils.BenchUtil;

/* Construct a basic UI */
public class Benchmark extends TabActivity implements View.OnClickListener {

    public final static String TAG     = "Benchmark";
    public final static String PACKAGE = "org.zeroxlab.benchmark";

    private final static String mOutputFile = "xmBenchmark";

    private final static int GROUP_DEFAULT = 0;
    private final static int SETTINGS_ID = Menu.FIRST;

    private static String mXMLResult;
    private static String mJSONResult;
    private final static String mOutputXMLFile = "xmBenchmark.xml";
    private final static String mOutputJSONFile = "xmBenchmark.bundle";

    private Button   mRun;
    private Button   mShow;
    private CheckBox mCheckList[];
    private TextView mDesc[];

    private TabHost mTabHost;

    LinkedList<Case> mCases;
    boolean mTouchable = true;
    private int orientation = Configuration.ORIENTATION_UNDEFINED;


    private final String MAIN = "Main";
    private final String D2 = "2D";
    private final String D3 = "3D";
    private final String MATH = "Math";
    private final String IO = "IO";
    private final String INFO = "Info";

    private CheckBox d2CheckBox;
    private CheckBox d3CheckBox;
    private CheckBox mathCheckBox;
    private CheckBox ioCheckBox;

    private HashMap< String, HashSet<Case> > mCategory = new HashMap< String, HashSet<Case> >();

    private final String ladderUrl = "https://xmperf.haowanyou.com/ladder.php";

    boolean mCheckMath = false;
    boolean mCheck2D = false;
    boolean mCheck3D = false;
    boolean mCheckVM = false;
    boolean mCheckIO = false;
    //boolean mCheckMisc = false;
    boolean mAutoUpload = true;
    boolean mAutoRun = true;//自动运行开关


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientation = getResources().getConfiguration().orientation;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);
        mCases = new LinkedList<Case>();
        Case arith  = new CaseArithmetic();
        Case javascript = new CaseJavascript();
        Case scimark2  = new CaseScimark2();
        Case canvas = new CaseCanvas();
        Case glcube = new CaseGLCube();
        Case circle = new CaseDrawCircle();
        Case nehe08 = new CaseNeheLesson08();
        Case nehe16 = new CaseNeheLesson16();
        Case teapot = new CaseTeapot();

        Case libIO = new CaseIO();
        //Case caseInfo = new CaseInfo();
        Case dc2 = new CaseDrawCircle2();
        Case dr = new CaseDrawRect();
        Case da = new CaseDrawArc();
        Case di = new CaseDrawImage();
        Case dt = new CaseDrawText();
        //主页面添加多选框
        mCategory.put(D2, new HashSet<Case>());
        mCategory.put(D3, new HashSet<Case>());
        mCategory.put(MATH, new HashSet<Case>());
        // mCategory.put(VM, new HashSet<Case>());
        mCategory.put(IO, new HashSet<Case>());
        // mCategory.put(MISC, new HashSet<Case>());
        mCategory.put(INFO, new HashSet<Case>());
        // mflops
        mCases.add(arith);
        mCases.add(scimark2);
        // mCases.add(javascript);
        // mCases.add(caseInfo);
        mCategory.get(MATH).add(arith);
        mCategory.get(MATH).add(scimark2);
        // mCategory.get(MISC).add(javascript);

        // 2d
        mCases.add(canvas);
        mCases.add(circle);
        mCases.add(dc2);
        mCases.add(dr);
        mCases.add(da);
        mCases.add(di);
        mCases.add(dt);

        mCategory.get(D2).add(canvas);
        mCategory.get(D2).add(circle);
        mCategory.get(D2).add(dc2);
        mCategory.get(D2).add(dr);
        mCategory.get(D2).add(da);
        mCategory.get(D2).add(di);
        mCategory.get(D2).add(dt);

        // 3d
        mCases.add(glcube);
        mCases.add(nehe08);
        mCases.add(nehe16);
        mCases.add(teapot);

        mCategory.get(D3).add(glcube);
        mCategory.get(D3).add(nehe08);
        mCategory.get(D3).add(nehe16);
        mCategory.get(D3).add(teapot);

        // vm
        //mCases.add(gc);
        //mCategory.get(VM).add(gc);

        // native
       // mCases.add(libMicro);
       // mCases.add(libUbench);
        mCases.add(libIO);
        //mCategory.get(IO).add(libMicro);
        //mCategory.get(IO).add(libUbench);
        mCategory.get(IO).add(libIO);
        initViews();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mAutoRun = bundle.getBoolean("autorun");
            mCheckMath = bundle.getBoolean("math");
            mCheck2D = bundle.getBoolean("2d");
            mCheck3D = bundle.getBoolean("3d");
            mCheckVM = bundle.getBoolean("vm");
            mCheckIO = bundle.getBoolean("io");
            mAutoUpload = bundle.getBoolean("autoupload");
        }

        if (mCheckMath && !mathCheckBox.isChecked()) {
            mathCheckBox.performClick();
        }

        if (mCheck2D && !d2CheckBox.isChecked()) {
            d2CheckBox.performClick();
        }

        if (mCheck3D && !d3CheckBox.isChecked()) {
            d3CheckBox.performClick();
        }

//        if (mCheckVM && !vmCheckBox.isChecked()) {
//            vmCheckBox.performClick();
//        }

        if (mCheckIO && !ioCheckBox.isChecked()) {
            ioCheckBox.performClick();
        }

//        if (mCheckMisc && !miscCheckBox.isChecked()) {
//            miscCheckBox.performClick();
//        }
        /*
        if (intent.getBooleanExtra("AUTO", false)) {
            ImageView head = (ImageView)findViewById(R.id.banner_img);
            head.setImageResource(R.drawable.icon_auto);
            mTouchable = false;
            initAuto();
        }
        */

        CheckPowerManage();//如果大于7.0，自动开启SustainedPerformance mode
        if (mAutoRun) {
            onClick(mRun);
        }
    }

    void CheckPowerManage()
    {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
             if (pm.isSustainedPerformanceModeSupported()) {
                 Log.i("BenchMark", "Sustained Performance Mode is supported\n");
                 getWindow().setSustainedPerformanceMode(true);
             }
         }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item1 = menu.add(GROUP_DEFAULT, SETTINGS_ID, Menu.NONE, R.string.menu_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        if (menu.getGroupId() == GROUP_DEFAULT && menu.getItemId() == SETTINGS_ID) {
            org.zeroxlab.utils.Util.launchActivity(this, "org.zeroxlab.benchmark.ActivitySettings");
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTouchable) {
            return super.dispatchTouchEvent(event);
        } else {
            return true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mTouchable) {
            return super.dispatchKeyEvent(event);
        } else {
            return true;
        }
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (mTouchable) {
            return super.dispatchTrackballEvent(event);
        } else {
            return true;
        }
    }

    private void _checkTagCase(String [] Tags) {
        Arrays.sort(Tags);
        for (int i = 0; i < mCheckList.length; i++) {
            String [] caseTags = mCases.get(i).mTags;
            for (String t: caseTags) {
                int search = Arrays.binarySearch(Tags, t);
                if (search >= 0)
                    mCheckList[i].setChecked(true);
            }
        }
    }

    private void _checkCatCase(String [] Cats) {
        Arrays.sort(Cats);
        for (int i = 0; i < mCheckList.length; i++) {
            int search = Arrays.binarySearch(Cats, mCases.get(i).mType);
            if (search  >= 0)
                mCheckList[i].setChecked(true);
        }
    }

    private void _checkAllCase(boolean check) {
        for (int i = 0; i < mCheckList.length; i++)
            mCheckList[i].setChecked(check);
    }

    private void initAuto() {
        Intent intent = getIntent();
        String TAG = intent.getStringExtra("TAG");
        String CAT = intent.getStringExtra("CAT");


        _checkAllCase(false);
        if (TAG != null)
            _checkTagCase( TAG.split(",") );
        if (CAT != null)
            _checkCatCase( CAT.split(",") );
        if (TAG == null && CAT == null)
            _checkAllCase(true);
        final Handler h = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0x1234)
                    onClick(mRun);
            }
    };
    
    final ProgressDialog dialog = new ProgressDialog(this).show(this, "Starting Benchmark", "Please wait...", true, false);
    new Thread() {
            public void run() {
                SystemClock.sleep(1000);
                dialog.dismiss();
                Message m = new Message();
                m.what = 0x1234;
                h.sendMessage(m);
            }
        }.start();
        mTouchable = true;
    }

    private void initViews() {
        /*
        mRun = (Button)findViewById(R.id.btn_run);
        mRun.setOnClickListener(this);

        mShow = (Button)findViewById(R.id.btn_show);
        mShow.setOnClickListener(this);
        mShow.setClickable(false);

        mLinearLayout = (LinearLayout)findViewById(R.id.list_container);
        mMainView = (LinearLayout)findViewById(R.id.main_view);

        mBannerInfo = (TextView)findViewById(R.id.banner_info);
        mBannerInfo.setText("Hello!\nSelect cases to Run.\nUploaded results:\nhttp://benchmark.bojoy.com");
        */

        mTabHost = getTabHost();

        int length = mCases.size();
        mCheckList = new CheckBox[length];
        mDesc      = new TextView[length];
        for (int i = 0; i < length; i++) {
            mCheckList[i] = new CheckBox(this);
            mCheckList[i].setText(mCases.get(i).getTitle());
            mDesc[i] = new TextView(this);
            mDesc[i].setText(mCases.get(i).getDescription());
            mDesc[i].setTextSize(mDesc[i].getTextSize() - 2);
            mDesc[i].setPadding(42, 0, 10, 10);
        }

        TabContentFactory mTCF = new TabContentFactory() {
            public View createTabContent(String tag) {
                ViewGroup.LayoutParams fillParent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                ViewGroup.LayoutParams fillWrap = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams wrapContent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                wrapContent.gravity = Gravity.CENTER;
                LinearLayout.LayoutParams weightedFillWrap = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                weightedFillWrap.weight = 1;

                if (tag.equals(MAIN)) {
                    LinearLayout mMainView = new LinearLayout(Benchmark.this);
                    mMainView.setOrientation(LinearLayout.VERTICAL);
                    ScrollView mListScroll = new ScrollView(Benchmark.this);

                    LinearLayout mMainViewContainer = new LinearLayout(Benchmark.this);
                    mMainViewContainer.setOrientation(LinearLayout.VERTICAL);
                    ImageView mIconView = new ImageView(Benchmark.this);
                    mIconView.setImageResource(R.drawable.icon);

                    TextView mBannerInfo = new TextView(Benchmark.this);
                    mBannerInfo.setText("选择一个或多个测试进行跑分:");

                    d2CheckBox = new CheckBox(Benchmark.this);
                    d2CheckBox.setText(D2);
                    d2CheckBox.setOnClickListener(Benchmark.this);
                    //d2CheckBox.setChecked(true);

                    d3CheckBox = new CheckBox(Benchmark.this);
                    d3CheckBox.setText(D3);
                    d3CheckBox.setOnClickListener(Benchmark.this);
                    //d3CheckBox.setChecked(true);

                    mathCheckBox = new CheckBox(Benchmark.this);
                    mathCheckBox.setText(MATH);
                    mathCheckBox.setOnClickListener(Benchmark.this);
                    //mathCheckBox.setChecked(true);
//                    vmCheckBox = new CheckBox(Benchmark.this);
//                    vmCheckBox.setText(VM);
//                    vmCheckBox.setOnClickListener(Benchmark.this);

                    ioCheckBox = new CheckBox(Benchmark.this);
                    ioCheckBox.setText(IO);
                    ioCheckBox.setOnClickListener(Benchmark.this);
                    //ioCheckBox.setChecked(true);
//                    miscCheckBox = new CheckBox(Benchmark.this);
//                    miscCheckBox.setText(MISC);
//                    miscCheckBox.setOnClickListener(Benchmark.this);


                    TextView mWebInfo = new TextView(Benchmark.this);
                    mWebInfo.setText("手机天梯:\nhttps://xmperf.haowanyou.com/ladder.php");

                    LinearLayout mButtonContainer = new LinearLayout(Benchmark.this);
                    mRun = new Button(Benchmark.this);
                    mShow = new Button(Benchmark.this);
                    mRun.setText("Run");
                    mShow.setText("Show");
                    mRun.setOnClickListener(Benchmark.this);
                    mShow.setOnClickListener(Benchmark.this);
                    mButtonContainer.addView(mRun, weightedFillWrap);
                    mButtonContainer.addView(mShow, weightedFillWrap);
                    WebView mTracker = new WebView(Benchmark.this);
                    mTracker.clearCache(true);
                    mTracker.setWebViewClient(new WebViewClient () {
                        public void onPageFinished(WebView view, String url) {
                            Log.i(TAG, "Tracker: " + view.getTitle() + " -> " + url);
                        }
                        public void onReceivedError(WebView view, int errorCode,
                                                    String description, String failingUrl) {
                            Log.e(TAG, "Track err: " + description);
                        }
                    });
                    mTracker.loadUrl(ladderUrl);
                    mMainViewContainer.addView(mIconView,wrapContent);
                    mMainViewContainer.addView(mBannerInfo);
                    mMainViewContainer.addView(mathCheckBox);
                    mMainViewContainer.addView(d2CheckBox);
                    mMainViewContainer.addView(d3CheckBox);
//                    mMainViewContainer.addView(vmCheckBox);
                    mMainViewContainer.addView(ioCheckBox);
//                    mMainViewContainer.addView(miscCheckBox);
                    mMainViewContainer.addView(mWebInfo);
                    mMainViewContainer.addView(mButtonContainer, fillWrap);
                    mMainViewContainer.addView(mTracker, 0,0);
                    mListScroll.addView(mMainViewContainer, fillParent);
                    mMainView.addView(mListScroll, fillWrap);

                    return mMainView;

                }

                LinearLayout mMainView = new LinearLayout(Benchmark.this);
                mMainView.setOrientation(LinearLayout.VERTICAL);
                ScrollView mListScroll = new ScrollView(Benchmark.this);
                LinearLayout mListContainer = new LinearLayout(Benchmark.this);
                mListContainer.setOrientation(LinearLayout.VERTICAL);
                mListScroll.addView(mListContainer, fillParent);
                mMainView.addView(mListScroll, fillWrap);

                boolean gray = true;
                int length = mCases.size();
                Log.i(TAG, "L: " + length);
                Log.i(TAG, "TCF: " + tag);
                for (int i = 0; i < length; i++) {
                    if (!mCategory.get(tag).contains(mCases.get(i)))
                        continue;
                    Log.i(TAG, "Add: " + i); 
                    mListContainer.addView(mCheckList[i], fillWrap);
                    mListContainer.addView(mDesc[i], fillWrap);
                    if (gray) {
                        int color = 0xFF333333; //ARGB
                        mCheckList[i].setBackgroundColor(color);
                        mDesc[i].setBackgroundColor(color);
                    }
                    gray = !gray;
                }
                return mMainView;
            }
        };

        mTabHost.addTab(mTabHost.newTabSpec(MAIN).setIndicator(MAIN, getResources().getDrawable(R.drawable.ic_eye)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(D2).setIndicator(D2, getResources().getDrawable(R.drawable.ic_2d)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(D3).setIndicator(D3, getResources().getDrawable(R.drawable.ic_3d)).setContent(mTCF));
        mTabHost.addTab(mTabHost.newTabSpec(MATH).setIndicator(MATH, getResources().getDrawable(R.drawable.ic_pi)).setContent(mTCF));
        //mTabHost.addTab(mTabHost.newTabSpec(VM).setIndicator(VM, getResources().getDrawable(R.drawable.ic_vm)).setContent(mTCF));关闭GC测试
        mTabHost.addTab(mTabHost.newTabSpec(IO).setIndicator(IO, getResources().getDrawable(R.drawable.ic_c)).setContent(mTCF));
        //mTabHost.addTab(mTabHost.newTabSpec(MISC).setIndicator(MISC, getResources().getDrawable(R.drawable.ic_misc)).setContent(mTCF));关闭SunSpider
        //getLayoutInflater().inflate(R.id.unit_data, mTabHost.getTabContentView(), true);
        Intent intent = new Intent().setClass(this, SysInfoActivity.class);// 还愿这句回到原始的正常版本
        //Intent intent = new Intent().setClass(this, NdkGlActivity.class);//这是为了测试Native Renderer
        mTabHost.addTab(mTabHost.newTabSpec(INFO).setIndicator(INFO, getResources().getDrawable(R.drawable.ic_info)).setContent(intent));
    }

    public void onClick(View v)
    {
        if (v == mRun)
        {
            if(mAutoRun)
            {
                for (int i = 0; i < mCheckList.length; i++)
                      mCases.get(i).reset();
                    runCase(mCases);
            }
            else {
                int numberOfCaseChecked = 0;
                for (int i = 0; i < mCheckList.length; i++) {
                    if (mCheckList[i].isChecked()) {
                        mCases.get(i).reset();
                        numberOfCaseChecked++;
                    } else {
                        mCases.get(i).clear();
                    }
                }
                if (numberOfCaseChecked > 0)
                    runCase(mCases);
            }
        } else if (v == mShow) {
            String result = getResult();
            Log.i(TAG,"\n\n"+result+"\n\n");
            writeResult(mOutputFile, result);
            Intent intent = new Intent();
            intent.putExtra(Report.REPORT, result);
            intent.putExtra(Report.XML, mXMLResult);
            intent.putExtra(Report.JSON, mJSONResult);
            if (mAutoUpload) {
                intent.putExtra(Report.AUTOUPLOAD, true);
                mAutoUpload = false;
            }
            intent.setClassName(Report.packageName(), Report.fullClassName());
            startActivity(intent);
        } else if (v == d2CheckBox || v == d3CheckBox || v == mathCheckBox ||
                   v == ioCheckBox) {
            int length = mCases.size();
            String tag = ((CheckBox)v).getText().toString();
            for (int i = 0; i < length; i++) {
                if (!mCategory.get(tag).contains(mCases.get(i)))
                    continue;
                mCheckList[i].setChecked(((CheckBox)v).isChecked());
            }
        }
    }

    public void runCase(LinkedList<Case> list) {
        Case pointer = null;
        boolean finish = true;
        for (int i = 0; i < list.size(); i++) {
            pointer = list.get(i);
            if (!pointer.isFinish()) {
                finish = false;
                break;
            }
        }

        if (finish) {
//            mBannerInfo.setText("Benchmarking complete.\nClick Show to upload.\nUploaded results:\nhttp://benchmark.bojoy.com");
            String result = getResult();
            writeResult(mOutputFile, result);

            final ProgressDialog dialogGetXml = new ProgressDialog(this).show(this, "正在生成报告", "请稍候...", true, false);
            new Thread() {
                public void run() {
                    mJSONResult = getJSONResult();
                    //mXMLResult = getXMLResult()删除于20180129byxingmin
                    Log.d(TAG, "XML: " + mXMLResult);
                    writeResult(mOutputXMLFile, mXMLResult);
                    Log.d(TAG, "JSON: " + mJSONResult);
                    writeResult(mOutputJSONFile, mJSONResult);
                    mShow.setClickable(true);
                    onClick(mShow);
                    mTouchable = true;
                    dialogGetXml.dismiss();
                }
            }.start();
        } else {
            Intent intent = pointer.generateIntent();
            if (intent != null) {
                startActivityForResult(intent, 0);
            }
        }
    }

    /*
     * Add Linaro Dashboard Bundle's JSON format support
     * https://launchpad.net/linaro-python-dashboard-bundle/trunk
     */
    public String getJSONResult() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        JSONObject result = new JSONObject();
        try {
            JSONArray testRunsArray = new JSONArray();
            JSONObject testRunsObject = new JSONObject();
            testRunsObject.put("analyzer_assigned_date", sdf.format(date));
            // testRunsObject.put("time_check_performed", false);
            // TODO: should be UUID version 1
            //testRunsObject.put("analyzer_assigned_uuid", UUID.randomUUID().toString());
            //testRunsObject.put("test_id", "xmbench");

            JSONArray testResultsList = new JSONArray();
            Case myCase;
            for (int i = 0; i < mCases.size(); i++) {
                myCase = mCases.get(i);
                JSONArray caseResultList = myCase.getJSONBenchmark();
                for (int j = 0; j < caseResultList.length(); j++) {
                    testResultsList.put(caseResultList.get(j));
                }
            }
            testRunsObject.put("test_results", testResultsList);
            int finalScore = CalcFinalScore(testResultsList);
            testRunsArray.put(testRunsObject);
            result.put("test_runs", testRunsArray);
            result.put("score", finalScore);
        }
        catch (JSONException jsonE) {
            jsonE.printStackTrace();
        }
        return result.toString();
    }

   int  CalcFinalScore(JSONArray theArray)   {

        ArrayList<Double> mathScores = new ArrayList();
        ArrayList<Double> d2Scores   = new ArrayList();
        ArrayList<Double> d3Scores   = new ArrayList();
        ArrayList<Double> ioScores   = new ArrayList();
       for(int i = 0; i< theArray.length();i++) {
           try {
               JSONObject theJson = theArray.getJSONObject(i);
               if(theJson.get("units").equals("io")){//io score calc
                   if(theJson.has("score")){
                       ioScores.add(theJson.optDouble("score",50.0));
                   }
               }
               else if(theJson.get("units").equals("math")){//io score calc
                   if(theJson.has("score")){
                       mathScores.add(theJson.optDouble("score",50.0));
                   }
               }
               else if(theJson.get("units").equals("3d-fps")){//io score calc
                   if(theJson.has("score")){
                       d3Scores.add(theJson.optDouble("score",50.0));
                   }
               }
               else if(theJson.get("units").equals("2d-fps")){//io score calc
                   if(theJson.has("score")){
                       d2Scores.add(theJson.optDouble("score",50.0));
                   }
               }
           }catch (JSONException e) {
               Log.d("",e.toString());
           }
       }

       float mathAvg = calculateAverage(mathScores);
       float d2Avg = calculateAverage(d2Scores);
       float d3Avg = calculateAverage(d3Scores);
       float ioAvg = calculateAverage(ioScores);
       double ret = mathAvg*0.2+ioAvg*0.3+d2Avg*0.2+d3Avg*0.3;
       return (int)ret;
   }

    private float calculateAverage(List<Double> marks) {
        if (marks.size()==0)
            return 1;
        float sum = 0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    public String getResult() {
        String result = "";
        Case mycase;
        for (int i = 0; i < mCases.size(); i++) {
            mycase = mCases.get(i);
            if ( !mycase.couldFetchReport() ) continue;
            result += "============================================================\n";
            result += mycase.getTitle() + "\n";
            result += "------------------------------------------------------------\n";
            result += mycase.getResultOutput().trim() + "\n";
        }
        result += "============================================================\n";

        return result;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.i(TAG, "oooops....Intent is null");
            return;
        }

        Case mycase;
        for (int i = 0; i < mCases.size(); i++) {
            mycase = mCases.get(i);
            if (mycase.realize(data)) {
                mycase.parseIntent(data);
                break;
            }
        }
        runCase(mCases);
    }

    private boolean writeResult(String filename, String output) {
        File writeDir = new File(BenchUtil.getResultDir(this));
        if (!writeDir.exists()) {
            writeDir.mkdirs();
        }

        File file = new File(writeDir, filename);
        if (file.exists()) {
            Log.w(TAG, "File exists, delete " + writeDir.getPath() + filename);
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            file.createNewFile();

            fos.write(output.getBytes());
            fos.flush();
        } catch (Exception e) {
            Log.i(TAG, "Write Failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
