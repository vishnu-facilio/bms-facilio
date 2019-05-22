package com.facilio.bmsconsole.templates;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;

public class DefaultTemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DefaultTemplate() {
		// TODO Auto-generated constructor stub
		super.setType(Type.DEFAULT);
	}
	
	private DefaultTemplateType defaultTemplateType;
	
	public DefaultTemplateType getDefaultTemplateType() {
		return defaultTemplateType;
	}
	public void setDefaultTemplateType(DefaultTemplateType defaultTemplateType) {
		this.defaultTemplateType = defaultTemplateType;
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
	
	public enum DefaultTemplateType {
		ACTION("Action"),
		RULE("Rule")
		;
		
		String name;
		
		DefaultTemplateType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static DefaultTemplateType valueOf (int value) {
			return defaultTemplateMap.get(value);
		}
		static final Map<Integer,DefaultTemplateType> defaultTemplateMap = Collections.unmodifiableMap(initTypeMap()); 
		
		public static Collection<DefaultTemplateType> getAllDefaultTemplateType() {
			return defaultTemplateMap.values();
		}
		
		private static Map<Integer,DefaultTemplateType> initTypeMap() {
			Map<Integer,DefaultTemplateType> defaultTemplateMap = new HashMap<>();
			for(DefaultTemplateType type : values()) {
				defaultTemplateMap.put(type.getValue(), type);
			}
			return defaultTemplateMap;
		}
	}
}
