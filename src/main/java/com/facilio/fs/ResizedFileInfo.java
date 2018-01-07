package com.facilio.fs;

public class ResizedFileInfo extends FileInfo {


	private long expiryTime;
	private long generatedTime;
	private int fileHeight;
	private int fileWidth;
	private String fileUrl;
	
	public int getWidth() {
		return fileWidth;
	}
	public void setWidth(int width) {
		this.fileWidth = width;
	}
	
	public int getHeight() {
		return fileHeight;
	}
	public void setHeight(int height) {
		this.fileHeight = height;
	}
	
	public long getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(long time) {
		this.generatedTime = time;
	}
	
	public long getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(long time) {
		this.expiryTime = time;
	}
	
	public String getUrl() {
		return fileUrl;
	}
	public void setUrl(String url) {
		this.fileUrl = url;
	}
}
