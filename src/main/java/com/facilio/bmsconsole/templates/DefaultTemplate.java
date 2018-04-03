package com.facilio.bmsconsole.templates;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DefaultTemplate implements ActionTemplate {

	private String name, json;
	public DefaultTemplate(String name, JSONObject json) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.json = json.toJSONString();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(StrSubstitutor.replace(json, placeHolders));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		// TODO Auto-generated method stub
		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return json;
	}

}
