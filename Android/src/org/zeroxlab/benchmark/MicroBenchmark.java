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

    String xml;
    String postUrl;
    String apiKey;
    String benchmarkName;

    MicroBenchmark(String _xml, String _postUrl, String _apiKey, String _benchmarkName, Handler h) {
        xml = _xml;
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
        if(xml.length()==0) {
            updateState(FAILED,"空字符串");
            return;
        }

        String ret = postJSONObject(postUrl+"entry.php",xml2json(xml));

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
        return null;
    }

    public void run() {
        upload();
    }
}

