package com.facilio.license;

import com.facilio.aws.util.AwsUtil;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FreshsalesUtil {

public static void createLead(String module,String modulekey , JSONObject data) throws IOException {
		
		if (data == null || data.size() == 0) {
			return;
		}
		
		JSONObject postData = new JSONObject();
		postData.put(modulekey, data);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		headers.put("Authorization", "Token token=erLKqferUgGcY6sy_4qVJw");
		 
		String url = "https://facilio.freshsales.io/api/"+module;
		System.out.println("Ther final data to be posted is "+postData.toJSONString());
		AwsUtil.doHttpPost(url, headers, null, postData.toJSONString());
		
		try {
			if(module.equals("leads"))
			{
			JSONObject json = new JSONObject();
			
			 json.put("sender", "yoge@facilio.com");
			 json.put("to", "ananthraj.m@facilio.com,nivedha@facilio.com,vaishnavi.lm@facilio.com,yoge@facilio.com,prabhu@facilio.com,krishna@facilio.com,raj@facilio.com");
			     
			     json.put("subject", "New Lead Created "+data.get("email"));
			     json.put("message", "<!--"+postData.toJSONString()+"-->");
			     
			AwsUtil.sendEmail(json);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public static void main(String args[]) throws Exception
{
	JSONObject data = new JSONObject();
	data.put("first_name", "yoge");
	data.put("last_name", "babu");
	data.put("email", "yogebabu+312@gmail.com");
	
	
	createLead("leads","lead", data);
	
	
	
}
}
