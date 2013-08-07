package com.raindrop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	
	public static HttpClient httpClient=new DefaultHttpClient();
	
	public static String getRequest(String appendUrl) throws Exception{
		HttpGet get=new HttpGet(Contants.ServiceURL+appendUrl);
		HttpResponse response=httpClient.execute(get);
		if(response.getStatusLine().getStatusCode()==200){
			String result=EntityUtils.toString(response.getEntity());
			return result;
		}
		return null; 
	}
	
	public  static String postRequest(String appendUrl,Map <String,String> params) throws Exception{
		HttpPost post=new HttpPost(Contants.ServiceURL+appendUrl);
//		HttpPost post=new HttpPost("http://115.28.35.182:8080/");
		List<NameValuePair> postParams=new ArrayList<NameValuePair>();
		if(params!=null){
			for(String key : params.keySet()){
				postParams.add(new BasicNameValuePair(key, params.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(postParams,"gbk"));
		}
		HttpResponse response=httpClient.execute(post);
		if(response.getStatusLine().getStatusCode()==200){
			String result=EntityUtils.toString(response.getEntity());
			return result;
		}
		return null; 
	}
	
	
}
