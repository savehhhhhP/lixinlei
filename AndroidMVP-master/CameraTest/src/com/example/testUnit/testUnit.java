package com.example.testUnit;

import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Appchina
 * Date: 13-8-14
 * Time: 下午3:29
 *
 */
public class testUnit extends AndroidTestCase {
    private static final String TAG = "LogTest";

    public void testOne(){
        Log.i(TAG,"test1");
        //System.out.println("aaaaa");
    }

    public void testgoSyn() {
        String test = "[{" +
                "\"user\":\"public\"," +
                "\"resources\":[" +
                "{" +
                "\"id\":\"ffb1a3e6-448d-44e0-adc6-3c6cd0b82043\"," +
                "\"timestamp\":\"2013-08-08T06:42:10.041Z\"" +
                "}," +
                "{" +
                "\"id\":\"1d0044f9-5269-4d94-a6f0-109cb14d9f3c\"," +
                "\"timestamp\":\"2013-08-08T06:42:33.691Z\"" +
                "}" +
                "]" +
                "}]";
        String test2 = "[{\"user\":\"public\"}]";
        Log.i(TAG, "test2");
        try {
            String str = "[{\"G30801\":[\"网页设计\"],\"G30701\":[\"数学\",\"语文\"]}]";

            JSONObject jsonObject = goSyn(test);

            String name = jsonObject.get("user").toString();
            Log.i(TAG, name);
        } catch (Exception e) {
            Log.i(TAG, test);
        }
    }

    /**
     * Jason数据解析
     * @param jsonStr Json数据
     */
     public JSONObject goSyn(String jsonStr) throws Exception{
         JSONArray array = new JSONArray(jsonStr);
         JSONObject jsonObject = (JSONObject)array.get(0);
         return jsonObject;
     }
}
