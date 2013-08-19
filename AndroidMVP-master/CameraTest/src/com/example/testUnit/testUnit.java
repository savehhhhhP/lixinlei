package com.example.testUnit;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.cameratest.R;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.activation.FileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.example.testUnit.MediaFile.isAudioFileType;


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
        try{
            getResourceString("http://115.28.35.182/file/ffb1a3e6-448d-44e0-adc6-3c6cd0b82043");
        }catch (Exception e){
            Log.i(TAG,"test1 error"+e.toString());
        }
        Log.i(TAG,"test1");
    }

    /**
     * 获取网页数据                  GET
     * @param urlPath  路径
     * @return Json数据
     * @throws Exception
     */

    public void getResourceString(String urlPath) throws Exception {
        File extDir = Environment.getExternalStorageDirectory();
        String filename = "downloadedMusic22.mp3";
        File fullFilename = new File(extDir, filename);

        //File file = new File("a.mp3");    // c://test
        fullFilename.createNewFile();
        fullFilename.setWritable(Boolean.TRUE);
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        //对应的字符编码转换
        int size =0;
        FileOutputStream fos =null;
        BufferedInputStream bis = null;
        byte[] buf = new byte[8096];
        bis  = new BufferedInputStream(connection.getInputStream());
        fos = new FileOutputStream(fullFilename);
        while((size = bis.read(buf))!=-1){
            fos.write(buf,0,size);
        }
        fos.close();
        bis.close();
        connection.disconnect();
        if(isAudioFileType(extDir+filename)){
            Log.i(TAG, "是mp3");
        }
    }

    public void testgoSyn() {
        Log.i(TAG, "test2");
        try {
            // String str = "[{\"G30801\":[\"网页设计\"],\"G30701\":[\"数学\",\"语文\"]}]";
            //String jsonData = "{\"username\":\"arthinking\",\"userId\":001}";
            String test = "{" +
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
                    "}";

            String test2 = "{ \"_id\" : ObjectId(\"52036ab669c061a3e3aff731\"), \"id\" : \"ffb1a3e6-448d-44e0-adc6-" +
                    "3c6cd0b82043\", \"filename\" : \"s1_11.mp3\" }";
                    //"{ \"_id\" : ObjectId(\"52036ad369c061a3e3aff732\"), \"id\" : \"1d0044f9-5269-4d94-a6f0-" +
                    //"109cb14d9f3c\", \"filename\" : \"p1_11.jpg\" }";

            Map<String,String> map =  getMongodb(test2);

            Log.i(TAG,map.get("_id"));
            Log.i(TAG,map.get("id"));
            Log.i(TAG,map.get("filename"));

        } catch (Exception e) {
            Log.i(TAG, "error");
        }
    }

    /**
     * 解析manifest
     * @param jsonData json字符串
     * @return Map<String,String> - user -resources
     */
    public Map<String, String> getMainifest(String jsonData) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        int i = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            String tagName = reader.nextName();
            if (tagName.equals("user")) {
                map.put(tagName, reader.nextString());
            } else if (tagName.equals("resources")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String tagName2 = reader.nextName();
                        if (tagName2.equals("id")) {
                            map.put("id_" + i, reader.nextString());
                        } else if (tagName2.equals("timestamp")) {
                            map.put("timestamp_" + i, reader.nextString());
                        }
                    }
                    i++;
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();
        return map;
    }


    /**
     * 服务器 mongodb
     * @param jsonData json字符串
     * @return Map<String,String> - user -resources
     */
    public Map<String,String> getMongodb(String jsonData) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        reader.beginObject();
        while (reader.hasNext()) {
            String tagName = reader.nextName();
            if (tagName.equals("_id")) {
                map.put(tagName, reader.nextString());
            } else if (tagName.equals("id")) {
                map.put(tagName, reader.nextString());
            } else if(tagName.equals("filename"));{
                map.put(tagName, reader.nextString());
            }
        }
        reader.endObject();
        return map;
    }

}
