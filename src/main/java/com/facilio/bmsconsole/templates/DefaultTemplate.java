package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;

public class DefaultTemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DefaultTemplate() {
		// TODO Auto-generated constructor stub
		super.setType(Type.DEFAULT);
	}
	
	private JSONObject json;
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		return json;
	}
}
