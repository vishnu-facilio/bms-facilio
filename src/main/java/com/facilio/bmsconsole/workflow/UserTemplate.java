package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

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
	
	public static enum Type {
		EMAIL(1),
		SMS(2),
		JSON(3),
		EXCEL(4),
		WORKORDER(5),
		ALARM(6),
		TASK_GROUP(7)
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
