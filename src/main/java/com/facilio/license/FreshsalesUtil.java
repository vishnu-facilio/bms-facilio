package com.facilio.license;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;

public class FreshsalesUtil {

public static void createLead(String module,JSONObject data) throws IOException {
		
		if (data == null || data.size() == 0) {
			return;
		}
		
		JSONObject postData = new JSONObject();
		postData.put(module, data);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		headers.put("Authorization", "Token token=erLKqferUgGcY6sy_4qVJw");
		 
		String url = "https://facilio.freshsales.io/api/"+module;
		System.out.println("Ther final data to be posted is "+postData.toJSONString());
		AwsUtil.doHttpPost(url, headers, null, postData.toJSONString());
	}
}
