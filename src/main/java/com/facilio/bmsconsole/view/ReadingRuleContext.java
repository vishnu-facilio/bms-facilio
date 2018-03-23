package com.facilio.bmsconsole.view;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;

public class ReadingRuleContext extends WorkflowRuleContext {
	private long startValue = -1;
	public long getStartValue() {
		return startValue;
	}
	public void setStartValue(long startValue) {
		this.startValue = startValue;
	}
	
	private long interval = -1;
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	private long lastValue = -1;
	public long getLastValue() {
		if(lastValue != -1) {
			return lastValue;
		}
		else if (startValue != -1) {
			return startValue - interval;
		}
		return lastValue;
	}
	public void setLastValue(long lastValue) {
		this.lastValue = lastValue;
	}
	
	private long resourceId = -1;
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private FacilioField readingField;
	public FacilioField getReadingField() {
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	private long baselineId = -1;
	public long getBaselineId() {
		return baselineId;
	}
	public void setBaselineId(long baselineId) {
		this.baselineId = baselineId;
	}

	private String aggregation;
	public String getAggregation() {
		return aggregation;
	}
	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}

	private long dateRange = -1;
	public long getDateRange() {
		return dateRange;
	}
	public void setDateRange(long dateRange) {
		this.dateRange = dateRange;
	}

	private int operatorId;
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	private String percentage;
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	private ThresholdType thresholdType;
	public int getThresholdType() {
		if (thresholdType != null) {
			return thresholdType.getValue();
		}
		return -1;
	}
	public void setThresholdType(int thresholdType) {
		this.thresholdType = ThresholdType.valueOf(thresholdType);
	}
	public ThresholdType getThresholdTypeEnum() {
		return thresholdType;
	}
	public void setThresholdType(ThresholdType thresholdType) {
		this.thresholdType = thresholdType;
	}
	
	private int flapCount = -1;
	public int getFlapCount() {
		return flapCount;
	}
	public void setFlapCount(int flapCount) {
		this.flapCount = flapCount;
	}
	
	private long flapInterval = -1;
	public long getFlapInterval() {
		return flapInterval;
	}
	public void setFlapInterval(long flapInterval) {
		this.flapInterval = flapInterval;
	}

	private double minFlapValue = -1;
	public double getMinFlapValue() {
		return minFlapValue;
	}
	public void setMinFlapValue(double minFlapValue) {
		this.minFlapValue = minFlapValue;
	}

	private double maxFlapValue = -1;
	public double getMaxFlapValue() {
		return maxFlapValue;
	}
	public void setMaxFlapValue(double maxFlapValue) {
		this.maxFlapValue = maxFlapValue;
	}

	private int flapFrequency = -1;
	public int getFlapFrequency() {
		return flapFrequency;
	}
	public void setFlapFrequency(int flapFrequency) {
		this.flapFrequency = flapFrequency;
	}

	public enum ThresholdType {
		SIMPLE,
		AGGREGATION,
		BASE_LINE,
		FLAPPING,
		ADVANCED
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ThresholdType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
