package com.facilio.cb.context;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class ChatBotMLResponse {

	String intent;
	double accuracy;
	boolean isNotAccurate;
	String answer;
	
	JSONObject entityJson;
	
	public JSONObject getEntityJson() {
		return entityJson;
	}
	public void setEntityJson(JSONObject entityJson) {
		this.entityJson = entityJson;
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
