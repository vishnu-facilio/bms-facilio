package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.license.LicenseApi;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class ReadingRuleContext extends WorkflowRuleContext implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(ReadingRuleContext.class.getName());
	
	List<ReadingRuleMetricContext> ruleMetrics;
	
	public List<ReadingRuleMetricContext> getRuleMetrics() {
		return ruleMetrics;
	}
	public void setRuleMetrics(List<ReadingRuleMetricContext> ruleMetrics) {
		this.ruleMetrics = ruleMetrics;
	}
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
				if(AwsUtil.isDevelopment()) {
					workflowContext.setLogNeeded(true);
				}
				if(workflowContext.getId() == 5391l || workflowContext.getId() == 5739l) { 
					workflowContext.setLogNeeded(true);
				}
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
			if (flag && CollectionUtils.isNotEmpty(flaps)) { //Remove oldest entry alone
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
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
					LOGGER.info("Clearing new alarm ");
					constructAndAddClearEvent(context, reading);
				}
				else {
					ReadingRuleAPI.addClearEvent(context, placeHolders, this, reading.getId(), val, reading.getTtime(), reading.getParentId());
				}
			}
			super.executeFalseActions(record, context, placeHolders);
		}
	}

	public ReadingEventContext constructEvent(JSONObject obj, ReadingContext reading) throws Exception {
		ReadingEventContext event = null;
		if (obj == null) {
			event = new ReadingEventContext();
		}
		else {
			String severity = (String) obj.remove("severity");
			obj.remove("alarmType");
			event = FieldUtil.getAsBeanFromJson(obj, ReadingEventContext.class);
			if (StringUtils.isNotEmpty(severity)) {
				event.setSeverityString(severity);
			}
		}
		if (obj.containsKey("subject")) {
			String subject = (String) obj.get("subject");
			event.setEventMessage(subject);
		}
		event.setReadingFieldId(this.getReadingFieldId());
		event.setRuleId(this.getRuleGroupId());
		event.setSubRuleId(this.getId());
		DateRange range = getRange(reading);
		event.setDescription(getMessage(range, reading));
		event.setResource((ResourceContext) reading.getParent());
		event.setSiteId(((ResourceContext) reading.getParent()).getSiteId());
		event.setCreatedTime(reading.getTtime());


		return event;
	}

	public ReadingEventContext constructAndAddClearEvent(Context context, ReadingContext reading) throws Exception {
		Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
		if (isHistorical == null) {
			isHistorical = false;
		}
		Map<Long, ReadingRuleAlarmMeta> metaMap = null;
		if (isHistorical) {
			LOGGER.info("Meta map during clearing : "+metaMap);
			metaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
		}
		else {
			metaMap = this.getAlarmMetaMap();
		}
		ReadingRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(resourceId) : null;
		if (alarmMeta != null && !alarmMeta.isClear()) {
			alarmMeta.setClear(true);

			ReadingEventContext event = null;
			event.setReadingFieldId(this.getReadingFieldId());
			event.setRuleId(this.getRuleGroupId());
			event.setSubRuleId(this.getId());
			event.setComment("System auto cleared Alarm because associated rule executed clear condition for the associated resource");
			event.setCreatedTime(reading.getTtime());
			event.setAutoClear(true);
			event.setSiteId(((ResourceContext)reading.getParent()).getSiteId());
			event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);

//			JSONObject json = AlarmAPI.constructClearEvent(alarm, "System auto cleared Alarm because associated rule executed clear condition for the associated resource", ttime);
//			if (alarm.getSourceTypeEnum() == SourceType.THRESHOLD_ALARM) {
//				json.put("readingDataId", readingDataId);
//				json.put("readingVal", readingVal);
//			}
//
//			if (isHistorical) {
//				LOGGER.info("Clearing alarm for rule : "+readingRuleContext.getId()+" for resource : "+resourceId);
//			}
			context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(event));
			if (!isHistorical) {
				Chain addEvent = TransactionChainFactory.getV2AddEventChain();
				addEvent.execute(context);
			}
			return event;
		}
		return null;
	}

	private DateRange getRange(ReadingContext reading) {
		DateRange range = null;
		switch (this.getThresholdTypeEnum()) {
			case SIMPLE:
				if (this.getCriteria() != null) {
					range = new DateRange();
					range.setStartTime(reading.getTtime());
				}
				else {
					WorkflowContext workflow = this.getWorkflow();
					ExpressionContext expression = (ExpressionContext) workflow.getExpressions().get(0);
					if (expression.getLimit() != null) {
						range = new DateRange();
						range.setStartTime(reading.getTtime());
					}
					else {
						Condition condition = expression.getCriteria().getConditions().get("2");
						if(condition != null) {
							range = ((DateOperators) condition.getOperator()).getRange(condition.getValue());
						}
						else {
							range = new DateRange();
							range.setStartTime(reading.getTtime());
						}
					}
				}
				break;
			case AGGREGATION:
			case BASE_LINE:
				range = DateOperators.LAST_N_HOURS.getRange(String.valueOf(this.getDateRange())+","+reading.getTtime());
				break;
			case FLAPPING:
				range = new DateRange();
				range.setEndTime(reading.getTtime());
				range.setStartTime(range.getEndTime() - this.getFlapInterval());
				break;
			case ADVANCED:
			case FUNCTION:
				range = new DateRange();
				range.setStartTime(reading.getTtime());
				break;
		}
		return range;
	}

	private String getMessage(DateRange range, ReadingContext reading) throws Exception {
		StringBuilder msgBuilder = new StringBuilder();
		if (this.getAggregation() != null) {
			if(this.getDateRange() == 1) {
				msgBuilder.append("Hourly ")
						.append(this.getAggregation());
			}
			else {
				msgBuilder.append(WordUtils.capitalize(this.getAggregation()));
			}
			msgBuilder.append(" of ");
		}
		msgBuilder.append("'")
				.append(this.getReadingField().getDisplayName())
				.append("' ");

		NumberOperators operator = (NumberOperators) Operator.getOperator(this.getOperatorId());
		switch (this.getThresholdTypeEnum()) {
			case SIMPLE:
				appendSimpleMsg(msgBuilder, operator, reading);
				appendOccurences(msgBuilder);
				break;
			case AGGREGATION:
				appendSimpleMsg(msgBuilder, operator, reading);
				break;
			case BASE_LINE:
				appendBaseLineMsg(msgBuilder, operator);
				break;
			case FLAPPING:
				appendFlappingMsg(msgBuilder);
				break;
			case ADVANCED:
				appendAdvancedMsg(msgBuilder, reading);
				break;
			case FUNCTION:
				appendFunctionMsg(msgBuilder, reading);
				break;
		}

		if (range.getEndTime() != -1) {
			msgBuilder.append(" between ")
					.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(DateTimeUtil.READABLE_DATE_FORMAT))
					.append(" and ")
					.append(DateTimeUtil.getZonedDateTime(range.getEndTime()).format(DateTimeUtil.READABLE_DATE_FORMAT));
		}
		else {
			msgBuilder.append(" at ")
					.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(DateTimeUtil.READABLE_DATE_FORMAT));
		}

		return msgBuilder.toString();
	}

	private void appendOccurences (StringBuilder msgBuilder) {
		WorkflowContext workflow = this.getWorkflow();
		if (workflow != null) {
			ExpressionContext expression = (ExpressionContext) workflow.getExpressions().get(0);
			if (expression.getAggregateCondition() != null && !expression.getAggregateCondition().isEmpty()) {
				msgBuilder.append(" ")
						.append(getInWords(Integer.parseInt(this.getPercentage())));
				if (expression.getLimit() != null) {
					msgBuilder.append(" consecutively");
				}
			}
		}
	}

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private void appendSimpleMsg(StringBuilder msgBuilder, NumberOperators operator, ReadingContext reading) {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was ");
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't ");
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went below ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("exceeded ");
				break;
		}

		String value = null;
		if (this.getWorkflow() != null) {

			ExpressionContext expr = (ExpressionContext) this.getWorkflow().getExpressions().get(0);

			if (expr.getAggregateCondition() != null && !expr.getAggregateCondition().isEmpty()) {
				Condition aggrCondition = expr.getAggregateCondition().get(0);
				value = aggrCondition.getValue();
			}
		}
		if (value == null) {
			value = this.getPercentage();
		}

		if ("${previousValue}".equals(value)) {
			msgBuilder.append("previous value (")
					.append(DECIMAL_FORMAT.format(reading.getReading(this.getReadingField().getName())))
					.append(")");
		}
		else {
			msgBuilder.append(value);
		}
		appendUnit(msgBuilder);
	}

	private void appendBaseLineMsg (StringBuilder msgBuilder, NumberOperators operator) throws Exception {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was along ");
				updatePercentage(this.getPercentage(), msgBuilder);
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't along ");
				updatePercentage(this.getPercentage(), msgBuilder);
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(this.getPercentage(), msgBuilder);
				msgBuilder.append("lower than ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(this.getPercentage(), msgBuilder);
				msgBuilder.append("higher than ");
				break;
		}

		BaseLineContext bl = BaseLineAPI.getBaseLine(this.getBaselineId());
		msgBuilder.append("the ");
		msgBuilder.append("base line ")
				.append("'")
				.append(bl.getName())
				.append("'");
	}

	private static String getInWords (int val) {
		switch (val) {
			case 1:
				return "once";
			case 2:
				return "twice";
			case 3:
				return "thrice";
			default:
				return val+" times";
		}
	}

	private void appendFlappingMsg (StringBuilder msgBuilder) {
		msgBuilder.append("flapped ")
				.append(getInWords(this.getFlapFrequency()));

		switch (this.getReadingField().getDataTypeEnum()) {
			case NUMBER:
			case DECIMAL:
				msgBuilder.append(" below ")
						.append(this.getMinFlapValue());
				appendUnit(msgBuilder);
				msgBuilder.append(" and beyond ")
						.append(this.getMaxFlapValue());
				appendUnit(msgBuilder);
				break;
			default:
				break;
		}
	}

	private static Object formatValue (ReadingContext reading, FacilioField field) {
		if (field.getDataTypeEnum() == FieldType.DECIMAL) {
			return DECIMAL_FORMAT.format(FieldUtil.castOrParseValueAsPerType(field, reading.getReading(field.getName())));
		}
		else {
			return reading.getReading(field.getName());
		}
	}

	private void appendAdvancedMsg (StringBuilder msgBuilder, ReadingContext reading) {
		msgBuilder.append("recorded ")
				.append(formatValue(reading, this.getReadingField()));

		appendUnit(msgBuilder);

		msgBuilder.append(" when the complex condition set in '")
				.append(this.getName())
				.append("'")
				.append(" rule evaluated to true");
	}

	private void appendFunctionMsg (StringBuilder msgBuilder, ReadingContext reading) {
		msgBuilder.append("recorded ")
				.append(formatValue(reading, this.getReadingField()));
		appendUnit(msgBuilder);

		String functionName = null;
		if (this.getWorkflow() != null) {
			ExpressionContext expr = (ExpressionContext) this.getWorkflow().getExpressions().get(1);
			functionName = expr.getDefaultFunctionContext().getFunctionName();
		}

		msgBuilder.append(" when the function (")
				.append(functionName)
				.append(") set in '")
				.append(this.getName())
				.append("'")
				.append(" rule evaluated to true");
	}

	private void appendUnit(StringBuilder msgBuilder) {
		if (this.getReadingField() instanceof NumberField && ((NumberField)this.getReadingField()).getUnit() != null) {
			msgBuilder.append(((NumberField)this.getReadingField()).getUnit());
		}
	}

	private void updatePercentage(String percentage, StringBuilder msgBuilder) {
		if (percentage != null && !percentage.equals("0")) {
			msgBuilder.append(percentage)
					.append("% ");
		}
	}
}
