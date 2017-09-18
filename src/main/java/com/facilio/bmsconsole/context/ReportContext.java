package com.facilio.bmsconsole.context;

public class ReportContext {
	private String device;
	public String getDevice() {
         System.out.println("Device"+device);
		return device;
	}
	public void getDataquery(String device) {
		this.device = device;
	}
	private String duration;
	public String getduration() {
		return duration;
	}
	public void getDuration(String duration) {
		this.duration = duration;
	}
}
