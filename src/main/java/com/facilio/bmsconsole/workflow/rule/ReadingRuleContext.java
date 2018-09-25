package com.facilio.bmsconsole.workflow.rule;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
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
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class ReadingRuleContext extends WorkflowRuleContext {
	private static final Logger LOGGER = LogManager.getLogger(ReadingRuleContext.class.getName());
	
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
	
	private long assetCategoryId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private Map<Long, ResourceContext> matchedResources;
	public Map<Long, ResourceContext> getMatchedResources() {
		return matchedResources;
	}
	public void setMatchedResources(Map<Long, ResourceContext> matchedResources) {
		this.matchedResources = matchedResources;
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
	
	private int occurences = -1;
	public int getOccurences() {
		return occurences;
	}
	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}
	
	private long overPeriod = -1; //In Seconds
	public long getOverPeriod() {
		return overPeriod;
	}
	public void setOverPeriod(long overPeriod) {
		this.overPeriod = overPeriod;
	}
	
	private Boolean consecutive;
	public Boolean getConsecutive() {
		return consecutive;
	}
	public void setConsecutive(Boolean consecutive) {
		this.consecutive = consecutive;
	}
	public void setConsecutive(boolean consecutive) {
		this.consecutive = consecutive;
	}
	public boolean isConsecutive() {
		if (consecutive != null) {
			return consecutive.booleanValue();
		}
		return false;
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
	
	@Override
	public boolean evaluateWorkflowExpression(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		boolean workflowFlag = evalWorkflow(placeHolders, (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META));
		if (overPeriod != -1 && occurences == -1) {
			return evalOverPeriod(workflowFlag, (ReadingContext) record);
		}
		else if (overPeriod != -1 && occurences != -1) {
			return evalOverPeriodAndOccurences(workflowFlag, (ReadingContext) record);
		}
		else if (isConsecutive() && occurences != -1) {
			return evalConsecutive(workflowFlag, (ReadingContext) record);
		}
		else {
			return workflowFlag;
		}
	}
	
	private boolean evalWorkflow(Map<String, Object> placeHolders, Map<String, ReadingDataMeta> currentRDM) throws Exception {
		try {
			boolean workflowFlag = true;
			if (getWorkflow() != null) {
				Object result = WorkflowUtil.getWorkflowExpressionResult(getWorkflow().getWorkflowString(), placeHolders, currentRDM, true, true);
				 if(result instanceof Boolean) {
					 workflowFlag = (Boolean) result;
				 }
				 else {
					 double resultDouble = (double) result;
					 workflowFlag = resultDouble == 1;
				 }
			}
			return workflowFlag;
		}
		catch (ArithmeticException e) {
			LOGGER.error("Arithmetic exception during execution of workflow for rule : "+getId(), e);
			return false;
		}
	}
	
	private static final int OVER_PERIOD_BUFFER = 20 * 60 * 1000;
	private boolean evalOverPeriod(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) { ////If there is no prev flap, add flap and return false
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			if (flaps.isEmpty()) {
				addFlap(reading.getTtime(), reading.getParentId());
				return false;
			}
			else {
				long firstFlapDiff = reading.getTtime() - (long)flaps.get(0).get("flapTime");
				if (firstFlapDiff < (overPeriod * 1000)) { // If the first flap is within over period, add flap and return false
					addFlap(reading.getTtime(), reading.getParentId());
					return false;
				}
				else if (firstFlapDiff <= ((overPeriod * 1000) + OVER_PERIOD_BUFFER)) {
					deleteAllFlaps(reading.getParentId());
					return true;
				}
				else {
					deleteAllFlaps(reading.getParentId());
					addFlap(reading.getTtime(), reading.getParentId());
					return false;
				}
			}
		}
		else {
			deleteAllFlaps(reading.getParentId());
			return false;
		}
	}
	
	private boolean evalOverPeriodAndOccurences(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) {
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			List<Long> flapsToBeDeleted = new ArrayList<>();
			Iterator<Map<String, Object>> itr = flaps.iterator();
			while (itr.hasNext()) {
				Map<String, Object> flap = itr.next();
				long flapTime = (long) flap.get("flapTime");
				if ((reading.getTtime() - flapTime) > (overPeriod * 1000)) {
					flapsToBeDeleted.add((Long) flap.get("id"));
					itr.remove();
				}
			}
			boolean flag = checkOccurences(flaps, reading);
			if (!flag) {
				deleteOldFlaps(flapsToBeDeleted);
			}
			return flag;
		}
		else {
			return false;
		}
	}
	
	private boolean evalConsecutive(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) {
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			return checkOccurences(flaps, reading);
		}
		else {
			deleteAllFlaps(reading.getParentId());
			return false;
		}
	}
	
	private boolean checkOccurences(List<Map<String, Object>> flaps, ReadingContext reading) throws Exception {
		if (flaps.size() + 1 == occurences) { //Old flaps + current flap
			deleteAllFlaps(reading.getParentId());
			return true;
		}
		else {
			addFlap(reading.getTtime(), reading.getParentId());
			return false;
		}
	}
	
	private void updateLastValueForReadingRule(ReadingContext record) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Criteria criteria = getCriteria();
		if (criteria != null) {
			Condition condition = criteria.getConditions().get(1);
			long lastValue = new Double(record.getReading(condition.getFieldName()).toString()).longValue();
			ReadingRuleAPI.updateLastValueInReadingRule(getId(), lastValue);
		}
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		ReadingContext reading = (ReadingContext) record;
		if (!checkForParentResource(reading)) {
			return false;
		}
		Object currentReadingObj = FieldUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
		if (currentReadingObj == null) {
			return false;
		}
		switch (thresholdType) {
			case FLAPPING:
				boolean singleFlap = false;
				Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
				ReadingDataMeta meta = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), readingField));
				Object prevValue = meta.getValue();
				if (currentReadingObj instanceof Number) {
					double prevVal = Double.valueOf(prevValue.toString());
					double currentVal = Double.valueOf(currentReadingObj.toString());
					double minVal = Math.min(prevVal, currentVal);
					double maxVal = Math.max(prevVal, currentVal);
					
					singleFlap = minVal <= minFlapValue && maxVal >= maxFlapValue;
				}
				else if (currentReadingObj instanceof Boolean) {
					singleFlap = currentReadingObj != (Boolean) prevValue;
				}
				return singleFlap && isFlappedNTimes(reading);
			default:
				break;
		}
		return true;
	}
	
	private boolean checkForParentResource(ReadingContext reading) {
		if (matchedResources != null && !matchedResources.isEmpty()) {
			ResourceContext parent = matchedResources.get(reading.getParentId());
			if (parent != null) {
				reading.setParent(parent);
				return true;
			}
		}
		return false;
	}
	
	private boolean isFlappedNTimes(ReadingContext record) throws Exception {
		boolean flapThreshold = false;
		List<Long> flapsToBeDeleted = new ArrayList<>();
		List<Map<String, Object>> flaps = getFlaps(record.getParentId());
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
			addFlap(record.getTtime(), record.getParentId());
		}
		updateFlapCount(getId(), flapCount);
		deleteOldFlaps(flapsToBeDeleted);
		return flapThreshold;
	}
	
	private List<Map<String, Object>> getFlaps(long resourceId) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleFlapsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleIdField = fieldMap.get("ruleId");
		FacilioField resourceIdField = fieldMap.get("resourceId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.orderBy("flapTime")
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(getId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), PickListOperators.IS))
														;
		
		return selectBuilder.get();
	}
	
	private long addFlap(long flapTime, long resourceId) throws Exception {
		Map<String, Object> newFlap = new HashMap<>();
		newFlap.put("ruleId", getId());
		newFlap.put("flapTime", flapTime);
		newFlap.put("resourceId", resourceId);
		
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
	
	private void deleteAllFlaps(long resourceId) throws SQLException {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingRuleFlapsFields());
		FacilioField ruleIdField = fieldMap.get("ruleId");
		FacilioField resourceIdField = fieldMap.get("resourceId");
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(getId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), PickListOperators.IS))
														;
		deleteBuilder.delete();
	}
	
	@Override
	public Map<String, Object> constructPlaceHolders(String moduleName, Object record, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> rulePlaceHolders = super.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
		Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		ReadingDataMeta meta = metaMap.get(ReadingsAPI.getRDMKey(((ReadingContext)record).getParentId(), readingField));
		if (meta != null) {
			Object prevValue = meta.getValue();
			rulePlaceHolders.put("previousValue", FieldUtil.castOrParseValueAsPerType(readingField, prevValue));
		}
		rulePlaceHolders.put("resourceId", ((ReadingContext) record).getParentId());
		rulePlaceHolders.put("inputValue", ((ReadingContext) record).getReading(readingField.getName()));
		rulePlaceHolders.put("ttime", ((ReadingContext) record).getTtime());
		return rulePlaceHolders;
	}
}
