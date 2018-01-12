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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Debug;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
/* code adapted from Caliper Project */

class MicroBenchmark extends Thread {
    final static int FAILED = -1;
    final static int DONE = 0;
    final static int RUNNING = 1;

    final static String STATE = "STATE";
    final static String MSG = "MSG";
    final static String TAG = "MicroBenchmarkThread";
    final static int PRETTY_PRINT_INDENT_FACTOR = 4;
    Handler mHandler;

    //String xml;
    String json;
    String postUrl;
    String apiKey;
    String benchmarkName;

    MicroBenchmark(String _json, String _postUrl, String _apiKey, String _benchmarkName, Handler h) {
        json = _json;
        postUrl = _postUrl;
        apiKey = _apiKey;
        benchmarkName = _benchmarkName;
        mHandler = h;
    }

    private void updateState(int state, String info) {
        Bundle b = new Bundle();
        b.putInt(STATE, state);
        b.putString(MSG, info);
        Message msg = mHandler.obtainMessage();
        msg.setData(b);
        mHandler.sendMessage(msg);

        Log.e(TAG, "set state: " + state);
    }

    private void updateState(int state) {
        updateState(state, "");
    }

    public void upload() {
        updateState(RUNNING);
        if(json.length()==0) {
            updateState(FAILED,"空字符串");
            return;
        }
        String testXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result BenchVersionCode=\"19\" BenchVersionName=\"1.1.6\" apiKey=\"0000\" benchmark=\"PublicPage\" imei=\"000000000000000\" name=\"Android\" brand=\"sdk_phone_armv7\" executedTimestamp=\"2018-01-12T03:11:24GMT+00:00\" manufacturer=\"unknown\" model=\"sdk_phone_armv7:sdk_phone_armv7-userdebug 5.1.1 LMY48X 3079158 test-keys\" buildTimestamp=\"2016-07-20T21:24:46GMT+00:00\" orientation=\"1\" version=\"Linux version 3.4.67-01422-gd3ffcc7-dirty (digit@tyrion.par.corp.google.com) (gcc version 4.8 (GCC) ) #1 PREEMPT Tue Sep 16 19:34:06 CEST 2014\" cpu=\"ARMv7 Processor rev 0 (v7l):Goldfish:0000\"><scenario benchmark=\"Linpack\" unit=\"mflops\" tags=\"numeric,mflops,scientific,\">0.12979591816040717 </scenario><scenario benchmark=\"DrawCanvas\" unit=\"2d-fps\" tags=\"2d,render,view,\">19.059720993041992 19.32740592956543 19.710906982421875 </scenario><scenario benchmark=\"OpenGLCube\" unit=\"3d-fps\" tags=\"3d,opengl,render,apidemo,\">13.451165199279785 14.418779373168945 14.137472152709961 </scenario></result>";
        String ret = postJSONObject(postUrl+"entry.php",json);

        Log.e(TAG, ""+ret);
        if(!ret.equals("success"))
        {
            System.out.println(ret);
            updateState(FAILED,ret);
            return;
        }

        updateState(DONE);
    }

    private String xml2json(String theXml){
        String ret = "";
        XmlMapper xmlMapper = new XmlMapper();
        try {
            JsonNode node = xmlMapper.readTree(theXml);

            ObjectMapper jsonMapper = new ObjectMapper();
            ret = jsonMapper.writeValueAsString(node);
        }
        catch (JsonProcessingException e)
        {
            String test  =e.toString();
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        return  ret;
    }

    String postJSONObject(String myurl, String parameters) {
        HttpURLConnection conn = null;
        try {
            StringBuffer response = null;
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(parameters);
            writer.close();
            out.close();
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode" + responseCode);
            switch (responseCode) {
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                case 404:
                    return "无法链接服务器！";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "未知错误";
    }

    public void run() {
        upload();
    }
}

