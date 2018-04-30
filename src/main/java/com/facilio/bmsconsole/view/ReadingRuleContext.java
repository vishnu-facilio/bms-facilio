package com.facilio.bmsconsole.view;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

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
	
	private long assetCategoryId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private List<AssetContext> categoryAssets;
	public List<AssetContext> getCategoryAssets() {
		return categoryAssets;
	}
	public void setCategoryAssets(List<AssetContext> categoryAssets) {
		this.categoryAssets = categoryAssets;
	}

	private List<Long> includedResources;
	public List<Long> getIncludedResources() {
		return includedResources;
	}
	public void setIncludedResources(List<Long> includedResources) {
		this.includedResources = includedResources;
	}
	
	private List<Long> excludedResources;
	public List<Long> getExcludedResources() {
		return excludedResources;
	}
	public void setExcludedResources(List<Long> excludedResources) {
		this.excludedResources = excludedResources;
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
		ADVANCED,
		FUNCTION
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
	
	@Override
	public boolean evaluateCriteria(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		boolean criteriaFlag = super.evaluateCriteria(moduleName, record, placeHolders, context);
		if (criteriaFlag) {
			updateLastValueForReadingRule((ReadingContext) record);
		}
		return criteriaFlag;
	}
	
	private void updateLastValueForReadingRule(ReadingContext record) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Criteria criteria = getCriteria();
		if (criteria != null) {
			Condition condition = criteria.getConditions().get(1);
			long lastValue = new Double(record.getReading(condition.getFieldName()).toString()).longValue();
			WorkflowRuleAPI.updateLastValueInReadingRule(getId(), lastValue);
		}
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		ReadingContext reading = (ReadingContext) record;
		if (!checkForParentResource(reading)) {
			return false;
		}
		Object currentReadingObj = FieldUtil.castOrParseValueAsPerType(readingField.getDataTypeEnum(), reading.getReading(readingField.getName()));
		if (currentReadingObj == null) {
			return false;
		}
		switch (thresholdType) {
			case FLAPPING:
				boolean singleFlap = false;
				Map<String, Map<String,Object>> lastReadingMap =(Map<String, Map<String,Object>>)context.get(FacilioConstants.ContextNames.LAST_READINGS);
				Map<String, Object> lastValMap = lastReadingMap.get(reading.getParentId()+"_"+readingField.getName());
				Object lastReading = FieldUtil.castOrParseValueAsPerType(readingField.getDataTypeEnum(), lastValMap.get("value"));
				if (currentReadingObj instanceof Number) {
					double diff = calculateDiff(currentReadingObj, reading, (Number) lastReading);
					double flapRange = Math.abs(maxFlapValue - minFlapValue);
					singleFlap = diff >= flapRange;
				}
				else if (currentReadingObj instanceof Boolean) {
					singleFlap = currentReadingObj != (Boolean) lastReading;
				}
				return singleFlap && isFlappedNTimes(reading);
			default:
				break;
		}
		return true;
	}
	
	private boolean checkForParentResource(ReadingContext reading) {
		if (assetCategoryId == -1) {
			boolean isParent = resourceId == reading.getParentId();
			if (isParent) {
				reading.setParent(resource);
			}
			return isParent;
		}
		else {
			long parentId = reading.getParentId();
			Optional<AssetContext> parentAsset = categoryAssets.stream().filter((asset -> asset.getId() == parentId)).findFirst();
			if (parentAsset.isPresent()) {
				reading.setParent(parentAsset.get());
				boolean presentInInclusion = includedResources == null || includedResources.isEmpty() || includedResources.contains(parentId);
				boolean notPresentInExclusion = excludedResources == null || excludedResources.isEmpty() || !excludedResources.contains(parentId);
				return presentInInclusion && notPresentInExclusion;
			}
			return false;
		}
	}
	
	private double calculateDiff(Object currentReadingObj, ReadingContext record, Number lastReading) throws Exception {
		double diff = -1;
		if (lastReading == null) {
			return 0;
		}
		if (currentReadingObj instanceof Double) {
			double lastVal =  (double)lastReading;
			if (lastVal == -1) {
				return 0;
			}
			diff = Math.abs((double) currentReadingObj - lastVal);
		}
		else if (currentReadingObj instanceof Long) {
			long lastVal = (long) lastReading;
			if (lastVal == -1) {
				return 0;
			}
			diff = Math.abs((int) currentReadingObj - lastVal);
		}
		else {
			throw new IllegalArgumentException("Flapping is supported only for Number/ Decimal data types");
		}
		return diff;
	}
	
	private boolean isFlappedNTimes(ReadingContext record) throws Exception {
		boolean flapThreshold = false;
		List<Long> flapsToBeDeleted = new ArrayList<>();
		List<Map<String, Object>> flaps = getFlaps(getId());
		int flapCount = 0;
		if (flaps != null && !flaps.isEmpty()) {
			flapCount = flaps.size();
			for(Map<String, Object> flap : flaps) {
				if (record.getTtime() - (long) flap.get("flapTime") > flapInterval) {
					flapsToBeDeleted.add((Long) flap.get("id"));
					flapCount--;
				}
			}
		}
		flapCount++;
		flapThreshold = flapCount == flapFrequency;
		if (flapThreshold) {
			//Reset prev flaps
			flapsToBeDeleted.clear();
			for(Map<String, Object> flap : flaps) {
				flapsToBeDeleted.add((Long) flap.get("id"));
			}
			flapCount = 0;
		}
		else {
			addFlap(getId(), record.getTtime());
		}
		updateFlapCount(getId(), flapCount);
		deleteOldFlaps(flapsToBeDeleted);
		return flapThreshold;
	}
	
	private List<Map<String, Object>> getFlaps(long ruleId) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleFlapsFields();
		FacilioField ruleIdField = FieldFactory.getAsMap(fields).get("ruleId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.orderBy("flapTime")
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(ruleId), PickListOperators.IS));
		
		return selectBuilder.get();
	}
	
	private long addFlap(long ruleId, long flapTime) throws Exception {
		Map<String, Object> newFlap = new HashMap<>();
		newFlap.put("ruleId", ruleId);
		newFlap.put("flapTime", flapTime);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getReadingRuleFlapsFields())
														.table(ModuleFactory.getReadingRuleFlapsModule().getTableName());
		
		return insertBuilder.insert(newFlap);
	}
	
	private void updateFlapCount(long ruleId, int flapCount) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setFlapCount(flapCount);
		
		FacilioModule module = ModuleFactory.getReadingRuleModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ruleId, module));
		
		updateBuilder.update(FieldUtil.getAsProperties(rule));
	}
	
	private void deleteOldFlaps(List<Long> flapsToBeDeleted) throws SQLException {
		// TODO Auto-generated method stub
		if (!flapsToBeDeleted.isEmpty()) {
			FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(flapsToBeDeleted, module))
															;
			deleteBuilder.delete();
		}
	}
	
	@Override
	public Map<String, Object> constructPlaceHolders(String moduleName, Object record, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> rulePlaceHolders = super.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
		Map<String, Map<String,Object>> lastReadingMap =(Map<String, Map<String,Object>>)context.get(FacilioConstants.ContextNames.LAST_READINGS);
		Map<String, Object> lastValue = lastReadingMap.get(((ReadingContext)record).getParentId()+"_"+readingField.getName());
		if (lastValue != null) {
			rulePlaceHolders.put("previousValue", FieldUtil.castOrParseValueAsPerType(readingField.getDataTypeEnum(), lastValue.get("value")));
		}
		rulePlaceHolders.put("resourceId", ((ReadingContext)record).getParentId());
		
		return rulePlaceHolders;
	}
}
