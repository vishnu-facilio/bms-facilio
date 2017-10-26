package com.facilio.bmsconsole.context;

import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ReadingContext extends ModuleBaseWithCustomFields {

	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public Map<String, Object> getReadings() {
		return super.getCustomProps();
	}
	public void setReadings(Map<String, Object> readings) {
		super.setCustomProps(readings);
	}
	public void addReading(String key, Object value) {
		super.setCustomProp(key, value);
	}
	public Object getReading(String key) {
		return super.getCustomProp(key);
	}
} 
