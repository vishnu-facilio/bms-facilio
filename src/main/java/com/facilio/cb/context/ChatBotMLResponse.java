package com.facilio.cb.context;

import java.util.HashMap;
import java.util.Map;

public class ChatBotMLResponse {

	String intent;
	double accuracy;
	boolean isNotAccurate;
	
	String answer;
	
	Map<String,String> mlParams;
	
	public Map<String, String> getMlParams() {
		return mlParams;
	}
	public void setMlParams(Map<String, String> mlParams) {
		this.mlParams = mlParams;
	}
	public void addMlParams(String key,String value) {
		this.mlParams = this.mlParams == null ? new HashMap<String, String>() : this.mlParams; 
		this.mlParams.put(key, value);
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public boolean isNotAccurate() {
		return isNotAccurate;
	}
	public void setNotAccurate(boolean isNotAccurate) {
		this.isNotAccurate = isNotAccurate;
	}
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
}
