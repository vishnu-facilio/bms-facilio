package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ReadingRuleContext extends WorkflowRuleContext implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(ReadingRuleContext.class.getName());
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
	}  
	private long startValue = -1;
	public long getStartValue() {
		return startValue;
	}
	public void setStartValue(long startValue) {
		this.startValue = startValue;
	}
	
	private int triggerExecutePeriod = -1;
	public int getTriggerExecutePeriod() {	//in sec
		return triggerExecutePeriod;
	}
	public void setTriggerExecutePeriod(int triggerExecutePeriod) {
		this.triggerExecutePeriod = triggerExecutePeriod;
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
	public FacilioField getReadingField(){
		try {
			if(readingField == null && readingFieldId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				readingField = modBean.getField(readingFieldId);
			}
		}
		catch(Exception e) {
			LOGGER.error("Error while fetching reading fieldid : "+readingFieldId, e);
		}
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
	
	private Boolean clearAlarm;
	public Boolean getClearAlarm() {
		return clearAlarm;
	}
	public void setClearAlarm(Boolean clearAlarm) {
		this.clearAlarm = clearAlarm;
	}
	public boolean clearAlarm() {
		if (clearAlarm != null) {
			return clearAlarm.booleanValue();
		}
		return false;
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
	
	Long alarmSeverityId;

	public Long getAlarmSeverityId() {
		return alarmSeverityId;
	}
	public void setAlarmSeverityId(Long alarmSeverityId) {
		this.alarmSeverityId = alarmSeverityId;
	}
	
	private long ruleGroupId = -1l;
	public long getRuleGroupId() {
		return ruleGroupId;
	}
	public void setRuleGroupId(long ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}
	
	private ReadingRuleType readingRuleType;
	public ReadingRuleType getReadingRuleTypeEnum() {
		return readingRuleType;
	}
	public void setReadingRuleType(ReadingRuleType readingRuleType) {
		this.readingRuleType = readingRuleType;
	}
	public int getReadingRuleType() {
		if (readingRuleType != null) {
			return readingRuleType.getValue();
		}
		return -1;
	}
	public void setReadingRuleType(int readingRuleType) {
		this.readingRuleType = ReadingRuleType.valueOf(readingRuleType);
	}
	
	private double upperBound = -1;
	public double getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	
	private double lowerBound = -1;
	public double getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
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
	
	public enum ReadingRuleType {
		THRESHOLD_RULE,
		ML_RULE
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ReadingRuleType valueOf(int value) {
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
		if (criteriaFlag && record != null) {
			updateLastValueForReadingRule((ReadingContext) record);
		}
		return criteriaFlag;
	}
	
	@Override
	public boolean evaluateWorkflowExpression(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		boolean workflowFlag = evalWorkflow(placeHolders, (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META));
		
		if (record == null) {
			return workflowFlag;
		}
		
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
				WorkflowContext workflowContext = getWorkflow();
				workflowContext.setLogNeeded(true);
				workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(workflowContext, placeHolders, currentRDM, false, true);
			}
			return workflowFlag;
		}
		catch (ArithmeticException e) {
			LOGGER.error("Arithmetic exception during execution of workflow for rule : "+getId(), e);
			return false;
		}
	}
	
	private static final int OVER_PERIOD_BUFFER = 5 * 60 * 1000;
	private boolean evalOverPeriod(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) { ////If there is no prev flap, add flap and return false
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			List<Long> flapsToBeDeleted = filterFlapsAndGetOldIds(flaps, reading);
			boolean result = false;
			addFlap(reading.getTtime(), reading.getParentId()); //Add flap if it's true
			if (flaps.isEmpty()) {
				result = false;
			}
			else {
				long firstFlapDiff = reading.getTtime() - (long)flaps.get(0).get("flapTime");
				if (AccountUtil.getCurrentOrg().getId() == 134) {
					LOGGER.info(getId()+"::First flap diff : "+firstFlapDiff+"::"+overPeriod);
				}
				if (firstFlapDiff < (overPeriod * 1000)) { // If the first flap is within over period, add flap and return false
					if (AccountUtil.getCurrentOrg().getId() == 134) {
						LOGGER.info(getId()+"::Within over period so ignoring");
					}
					result = false;
				}
				else if (firstFlapDiff <= ((overPeriod * 1000) + OVER_PERIOD_BUFFER)) {
//					deleteAllFlaps(reading.getParentId());
					if (AccountUtil.getCurrentOrg().getId() == 134) {
						LOGGER.info(getId()+"::Rule passed");
					}
					result = true;
				}
				else { //This shouldn't happen. We can check anyway
//					deleteAllFlaps(reading.getParentId());
					addFlap(reading.getTtime(), reading.getParentId());
//					if (AccountUtil.getCurrentOrg().getId() == 135) {
						LOGGER.info(getId()+"::Over buffer");
//					}
					result = false;
				}
			}
			if (!flapsToBeDeleted.isEmpty()) {
				deleteOldFlaps(flapsToBeDeleted);
			}
			return result;
		}
		else {
			deleteAllFlaps(reading.getParentId());
			return false;
		}
	}
	
	private List<Long> filterFlapsAndGetOldIds (List<Map<String, Object>> flaps, ReadingContext reading) throws Exception {
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
		return flapsToBeDeleted;
	}
	
//	private void deleteFlapsBeyondPeriod(List<Map<String, Object>> flaps, long overPeriod, ReadingContext reading) throws SQLException {
//		List<Long> flapsToBeDeleted = flaps.stream()
//											.filter(flap -> (reading.getTtime() - (long) flap.get("flapTime")) > (overPeriod * 1000))
//											.map(flap -> (long) flap.get("id"))
//											.collect(Collectors.toList());
//		deleteOldFlaps(flapsToBeDeleted);
//	}
	
	private boolean evalOverPeriodAndOccurences(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) {
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			List<Long> flapsToBeDeleted = filterFlapsAndGetOldIds(flaps, reading);
			boolean flag = checkOccurences(flaps, reading);
			deleteOldFlaps(flapsToBeDeleted);
			return flag;
		}
		else {
			return false;
		}
	}
	
	private boolean evalConsecutive(boolean workflowFlag, ReadingContext reading) throws Exception {
		if (workflowFlag) {
			List<Map<String, Object>> flaps = getFlaps(reading.getParentId());
			boolean flag = checkOccurences(flaps, reading);
			if (flag) { //Remove oldest entry alone
				deleteOldFlaps(Collections.singletonList((Long) flaps.get(0).get("id")));
			}
			return flag;
		}
		else {
			deleteAllFlaps(reading.getParentId());
			return false;
		}
	}
	
	private boolean checkOccurences(List<Map<String, Object>> flaps, ReadingContext reading) throws Exception {
		addFlap(reading.getTtime(), reading.getParentId()); //Add flap if it's true
		if (flaps.size() + 1 == occurences) { //Old flaps + current flap
//			deleteAllFlaps(reading.getParentId());
			return true;
		}
		else {
			return false;
		}
	}
	
	private void updateLastValueForReadingRule(ReadingContext record) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Criteria criteria = getCriteria();
		if (criteria != null) {
			Condition condition = criteria.getConditions().get("1");
			long lastValue = new Double(record.getReading(condition.getFieldName()).toString()).longValue();
			ReadingRuleAPI.updateLastValueInReadingRule(getRuleGroupId(), lastValue);
		}
	}
	
	public Object getMetric(ReadingContext reading) {
		if(reading != null) {
			if (!isMatchingResource(reading)) {
				if (this.getId() == 6448) {
					LOGGER.info("Parent didn't match and so returning metric as null");
				}
				return null;
			}
			if (this.getId() == 6448) {
				LOGGER.info("Reading field "+readingField);
			}
			if(readingField == null) {
				return null;
			}
			Object currentMetric = FieldUtil.castOrParseValueAsPerType(readingField, reading.getReading(readingField.getName()));
			return currentMetric;
		}
		return null;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		if(this.getTriggerExecutePeriod() <= 0 || (this.getTriggerExecutePeriod() > 0 && (Boolean) context.get(FacilioConstants.ContextNames.IS_READING_RULE_EXECUTE_FROM_JOB))) {
			ReadingContext reading = (ReadingContext) record;
			
			if (reading == null || readingField == null) {
				return true;
			}
			
			Object currentMetric = getMetric(reading);
			
			if (this.getId() == 6448) {
				LOGGER.info("Metric obtained in misc of rule : "+currentMetric);
			}
			
			if (currentMetric == null) {
				return false;
			}
			if(thresholdType != null) {
				switch (thresholdType) {
				case FLAPPING:
					boolean singleFlap = false;
					Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
					ReadingDataMeta meta = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), readingField));
					Object prevValue = meta.getValue();
					if (currentMetric instanceof Number) {
						double prevVal = Double.valueOf(prevValue.toString());
						double currentVal = Double.valueOf(currentMetric.toString());
						double minVal = Math.min(prevVal, currentVal);
						double maxVal = Math.max(prevVal, currentVal);
						
						singleFlap = minVal <= minFlapValue && maxVal >= maxFlapValue;
					}
					else if (currentMetric instanceof Boolean) {
						singleFlap = currentMetric != (Boolean) prevValue;
					}
					return singleFlap && isFlappedNTimes(reading);
				default:
					break;
				}
			}
		}
		else if(this.getTriggerExecutePeriod() > 0) {
			FacilioTimer.scheduleOneTimeJob(this.getId(), FacilioConstants.Job.SCHEDULED_ALARM_TRIGGER_RULE_JOB_NAME, this.getTriggerExecutePeriod(), FacilioConstants.Job.EXECUTER_NAME_FACILIO);
			this.setTerminateExecution(true);
			return false;
		}
		
		return true;
	}
	
	private boolean isMatchingResource(ReadingContext reading) {
		if (matchedResources != null && !matchedResources.isEmpty()) {
			if (this.getId() == 6448) {
				LOGGER.info("Matched resources : "+matchedResources.keySet()+"\n Reading parent id : "+reading.getParentId()+"\nMatched resource class : "+matchedResources.keySet().stream().findFirst().get().getClass());
			}
			ResourceContext parent = matchedResources.get(reading.getParentId());
			if (this.getId() == 6448) {
				LOGGER.info("Matched parent : "+parent);
			}
			if (parent != null) {
				reading.setParent(parent);
				if (this.getId() == 6448) {
					LOGGER.info("Parent matched and so returning true");
				}
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
		
		if (record != null && readingField != null) {
			Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
			ReadingDataMeta meta = metaMap.get(ReadingsAPI.getRDMKey(((ReadingContext)record).getParentId(), readingField));
			if (meta != null) {
				Object prevValue = meta.getValue();
				rulePlaceHolders.put("previousValue", FieldUtil.castOrParseValueAsPerType(readingField, prevValue));
			}
			rulePlaceHolders.put("resourceId", ((ReadingContext) record).getParentId());
			rulePlaceHolders.put("inputValue", ((ReadingContext) record).getReading(readingField.getName()));
			rulePlaceHolders.put("ttime", ((ReadingContext) record).getTtime());
		}
		return rulePlaceHolders;
	}
	
	private Map<Long, ReadingRuleAlarmMeta> alarmMetaMap;
	public Map<Long, ReadingRuleAlarmMeta> getAlarmMetaMap() {
		return alarmMetaMap;
	}
	public void setAlarmMetaMap(Map<Long, ReadingRuleAlarmMeta> alarmMetaMap) {
		this.alarmMetaMap = alarmMetaMap;
	}
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = getId();
		List<ActionContext>	actions = ActionAPI.getActiveActionsFromWorkflowRule(ruleId);
		if(actions != null) {
			for(ActionContext action : actions) {
				if(alarmSeverityId != null) {
					JSONTemplate jsonTemplate = (JSONTemplate) action.getTemplate();
					JSONObject templateJson = jsonTemplate.getOriginalTemplate();
					templateJson.put("severity", AlarmAPI.getAlarmSeverity(alarmSeverityId).getSeverity());
					jsonTemplate.setContent(templateJson.toJSONString());
				}
				action.executeAction(placeHolders, context, this, record);
			}
		}
	}
	
	@Override
	public void executeFalseActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		// TODO Auto-generated method stub
		ReadingContext reading = (ReadingContext) record;
		Object val = getMetric(reading);
		if (val != null || getEvent().getActivityTypeEnum().isPresent(EventType.SCHEDULED_READING_RULE.getValue())) {
			if (clearAlarm()) {
				ReadingRuleAPI.addClearEvent(context, placeHolders, this, reading.getId(), val, reading.getTtime(), reading.getParentId());
			}
			
			super.executeFalseActions(record, context, placeHolders);
		}
	}
}
