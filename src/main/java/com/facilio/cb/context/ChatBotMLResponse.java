package com.facilio.cb.context;

public class ChatBotMLResponse {

	String intent;
	double accuracy;
	boolean isNotAccurate;
	
	String answer;
	
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
		return accuracy*100;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
}
