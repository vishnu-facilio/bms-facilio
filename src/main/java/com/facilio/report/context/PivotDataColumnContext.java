package com.facilio.report.context;

import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;

public class PivotDataColumnContext extends PivotColumnContext {
	private AggregateOperator aggr;

	public AggregateOperator getAggrEnum() {
		return aggr;
	}

	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}

	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}

	private JSONObject visualConfig;

	public JSONObject getVisualConfig() {
		return visualConfig;
	}

	public void setVisualConfig(JSONObject visualConfig) {
		this.visualConfig = visualConfig;
	}

	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}

	private ReportPivotFieldContext readingField;

	public ReportPivotFieldContext getReadingField() {
		return readingField;
	}

	public void setReadingField(ReportPivotFieldContext readingField) {
		this.readingField = readingField;

	}
	private long startTime = -1;
	private long endTime = -1;
	private boolean excludeFromTimelineFilter = false;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isExcludeFromTimelineFilter() {
		return excludeFromTimelineFilter;
	}

	public void setExcludeFromTimelineFilter(boolean excludeFromTimelineFilter) {
		this.excludeFromTimelineFilter = excludeFromTimelineFilter;
	}

	private String moduleType;

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	private Integer datePeriod;

	public Integer getDatePeriod() {
		return datePeriod;
	}

	public void setDatePeriod(Integer datePeriod) {
		this.datePeriod = datePeriod;
	}

	private long dateFieldId = -1;

	public long getDateFieldId() {
		return dateFieldId;
	}

	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}

	private long subModuleFieldId = -1;

	public long getSubModuleFieldId() {
		return subModuleFieldId;
	}

	public void setSubModuleFieldId(long subModuleFieldId) {
		this.subModuleFieldId = subModuleFieldId;
	}

	private long parentModuleId = -1;

	public long getParentModuleId() {
		return parentModuleId;
	}

	public void setParentModuleId(long parentModuleId) {
		this.parentModuleId = parentModuleId;
	}
	private String baselineLabel;

	public String getBaselineLabel() {
		return baselineLabel;
	}

	public void setBaselineLabel(String baselineLabel) {
		this.baselineLabel = baselineLabel;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField() + "";
	}
}