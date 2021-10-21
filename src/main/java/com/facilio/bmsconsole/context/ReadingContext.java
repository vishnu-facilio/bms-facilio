package com.facilio.bmsconsole.context;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
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
	public int getSourceType() {
		if (sourceType != null) {
			return sourceType.getIndex();
		}
		return -1;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = SourceType.valueOf(sourceType);
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	private long sourceId=-1;
	
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
	
	public ReadingContext clone()
	{ 
		ReadingContext r =  new ReadingContext(actualTtime, ttime, zdt, parentId, marked, parent, newReading, sourceType, sourceId);
		r.setId(getId());
		r.setReadings(new HashMap<>(getReadings()));
		return r;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
}
