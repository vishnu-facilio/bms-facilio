package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class UserTemplate implements ActionTemplate {

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private JSONArray placeholder;
	
	public JSONArray getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(JSONArray placeholder) {
		this.placeholder = placeholder;
	}
	
	public String getPlaceholderStr() {
		if(placeholder != null) {
			return placeholder.toJSONString();
		}
		return null;
	}
	public void setPlaceholderStr(String placeholderStr) throws ParseException {
		JSONParser parser = new JSONParser();
		placeholder = (JSONArray) parser.parse(placeholderStr);
	}

	private Type type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public Type getTypeEnum() {
		return this.type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = Type.TYPE_MAP.get(type);
	}
	
	@Override
	public abstract JSONObject getTemplate(Map<String, Object> placeHolders);
	
	@Override
	public abstract JSONObject getOriginalTemplate();
	
	public static enum Type {
		EMAIL(1),
		SMS(2),
		JSON(3),
		EXCEL(4),
		WORKORDER(5),
		ALARM(6),
		TASK_GROUP(7),
		PUSH_NOTIFICATION(8),
		WEB_NOTIFICATION(9),
		ASSIGNMENT(10),
		SLA(11)
		;

		
		private int intVal;
		
		private Type(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		
		private static final Map<Integer, Type> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();
			
			for(Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public static Map<Integer, Type> getAllTypes() {
			return TYPE_MAP;
		}
		
		public static Type getType(int val) {
			if(TYPE_MAP.containsKey(val)) {
				return TYPE_MAP.get(val);
			}
			else {
				throw new IllegalArgumentException("Invalid Template Type val");
			}
		}
	}

}
