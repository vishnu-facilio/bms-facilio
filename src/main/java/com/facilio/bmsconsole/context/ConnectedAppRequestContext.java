package com.facilio.bmsconsole.context;

import java.util.Map;

import org.json.simple.JSONObject;

public class ConnectedAppRequestContext {
	private static final long serialVersionUID = 1L;

	private long connectedAppId;
	private String connector;
	private String url;
	private String method;
	private Map<String,String> params;
	private Map<String,String> headers;
	private String data;
	private String contentType;
	
	public long getConnectedAppId() {
		return connectedAppId;
	}
	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}
	public String getConnector() {
		return connector;
	}
	public void setConnector(String connector) {
		this.connector = connector;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setData(JSONObject data) {
		if (data != null) {
			this.data = data.toJSONString();
			this.contentType = "application/json";
		}
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}