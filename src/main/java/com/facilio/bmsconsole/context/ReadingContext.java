package com.facilio.bmsconsole.context;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.DateTimeUtil;

public class ReadingContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long actualTtime = -1;
	public long getActualTtime() {
		return actualTtime;
	}
	public void setActualTtime(long actualTtime) {
		this.actualTtime = actualTtime;
	}

	private long ttime = -1;
	private ZonedDateTime zdt;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
		this.zdt = DateTimeUtil.getDateTime(ttime);
	}
	
	public String getDate() {
		if(zdt != null) {
			return zdt.toLocalDate().toString();
		}
		return null;
	}
	
	public int getYear() {
		if(zdt != null) {
			return zdt.getYear();
		}
		return -1;
	}
	
	public int getMonth() {
		if(zdt != null) {
			return zdt.getMonthValue();
		}
		return -1;
	}
	
	public int getWeek() {
		if(zdt != null) {
			return zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		}
		return -1;
	}
	
	public int getDay() {
		if(zdt != null) {
			return zdt.getDayOfWeek().getValue();
		}
		return -1;
	}
	
	public int getHour() {
		if(zdt != null) {
			return zdt.getHour();
		}
		return -1;
	}

	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private Boolean marked;
	public Boolean getMarked() {
		return marked;
	}
	public void setMarked(Boolean marked) {
		this.marked = marked;
	}
	public boolean isMarked() {
		if (marked != null) {
			return marked.booleanValue();
		}
		return false;
	}
	
	public Map<String, Object> getReadings() {
		return super.getData();
	}
	public void setReadings(Map<String, Object> readings) {
		super.setData(readings);
	}
	public void addReading(String key, Object value) {
		super.setDatum(key, value);
	}
	public Object getReading(String key) {
		return super.getDatum(key);
	}
	
	private Object parent;
	public Object getParent() {
		return parent;
	}
	public void setParent(Object parent) {
		this.parent = parent;
	}
	
	private boolean newReading = false;
	public boolean isNewReading() {
		return newReading;
	}
	public void setNewReading(boolean newReading) {
		this.newReading = newReading;
	}
	
	private SourceType sourceType;
	public SourceType getSourceTypeEnum() {
		return sourceType;
	}
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	public int getSourceType() {
		if (sourceType != null) {
			return sourceType.getValue();
		}
		return -1;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = SourceType.valueOf(sourceType);
	}

	public static enum SourceType {
		WEB_ACTION,
		IMPORT,
		FORMULA,
		SHIFT_READING,
		KINESIS
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static SourceType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	@Override
	public String toString() {

		if(getReadings()!=null) {
			
			Map<String, Object> localReadings=new HashMap<>(getReadings());
			localReadings.put ("parentId",parentId);
			localReadings.put ("ttime",ttime);
			localReadings.put("id", getId());
			return localReadings.toString();
		}
		return null;
	}
} 
