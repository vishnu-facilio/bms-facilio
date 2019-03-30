package com.facilio.events.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EventToAlarmFieldMapping {
	private long eventToAlarmFieldMappingId = -1;
	public long getEventToAlarmFieldMappingId() {
		return eventToAlarmFieldMappingId;
	}
	public void setEventToAlarmFieldMappingId(long eventToAlarmFieldMappingId) {
		this.eventToAlarmFieldMappingId = eventToAlarmFieldMappingId;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private MappingType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public MappingType getTypeEnum() {
		return type;
	}
	public void setType(MappingType type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = TYPES[type - 1];
	}
	
	private String fromField;
	public String getFromField() {
		return fromField;
	}
	public void setFromField(String fromField) {
		this.fromField = fromField;
	}
	
	private String toField;
	public String getToField() {
		return toField;
	}
	public void setToField(String toField) {
		this.toField = toField;
	}
	
	private String constantValue;
	public String getConstantValue() {
		return constantValue;
	}
	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	
	private int mappingOrder = -1;
	public int getMappingOrder() {
		return mappingOrder;
	}
	public void setMappingOrder(int mappingOrder) {
		this.mappingOrder = mappingOrder;
	}

	private JSONObject mappingPairs;
	@JsonIgnore
	public JSONObject getMappingPairsJson() {
		return mappingPairs;
	}
	@JsonIgnore
	public void setMappingPairsJson(JSONObject mappingPairs) {
		this.mappingPairs = mappingPairs;
	}
	
	public String getMappingPairs() {
		if(mappingPairs != null) {
			return mappingPairs.toJSONString();
		}
		return null;
	}
	public void setMappingPairs(String mappingPairs) throws ParseException {
		JSONParser parser = new JSONParser();
		this.mappingPairs = (JSONObject) parser.parse(mappingPairs);
	}
	
	public static final MappingType[] TYPES = MappingType.values();
	public static enum MappingType {
		ADD_CONSTANT,
		FIELD_MAPPING
		;
		
		public int getIntVal() {
			return ordinal() + 1;
		}
	}
}
