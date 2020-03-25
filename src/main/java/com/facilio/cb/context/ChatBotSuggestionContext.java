package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatBotSuggestionContext {
	
	String suggestion;
	Type type;
	

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}

	public void setType(int type) {
		this.type = Type.getAllTypes().get(type);
	}

	public enum Type {
		OPTIONAL_PARAM(1, "OPTIONAL_PARAM"),
		EDITABLE_PARAM(2, "EDITABLE_PARAM"),
		OTHERS(3,"Other"),
		CHAINED_INTENT(4,"CHAINED_INTENT"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();

			for (Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Type> getAllTypes() {
			return optionMap;
		}
	}

}
