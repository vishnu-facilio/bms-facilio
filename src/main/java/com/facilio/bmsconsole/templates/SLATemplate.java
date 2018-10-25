package com.facilio.bmsconsole.templates;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SLATemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSONArray slaPolicyJson;
	public JSONArray getSlaPolicyJson() {
		return slaPolicyJson;
	}
	public void setSlaPolicyJson(JSONArray slaPolicyJson) {
		this.slaPolicyJson = slaPolicyJson;
	}
	
	public String getSlaPolicyJsonStr() {
		if(slaPolicyJson != null) {
			return slaPolicyJson.toJSONString();
		}
		return null;
	}
	public void setSlaPolicyJsonStr(String slaPolicyJsonStr) throws ParseException {
		JSONParser parser = new JSONParser();
		slaPolicyJson = (JSONArray) parser.parse(slaPolicyJsonStr);
	}
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.SLA.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.SLA;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("slaPolicyJson", slaPolicyJson);
		return json;
	}
}
