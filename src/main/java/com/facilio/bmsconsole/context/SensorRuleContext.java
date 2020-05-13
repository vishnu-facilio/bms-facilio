package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SensorRuleUtil;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.unitconversion.Metric;
import com.facilio.util.FacilioUtil;

public class SensorRuleContext {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SensorRuleContext.class.getName());

	private long id = -1;
	private long moduleId = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
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
			LOGGER.error("Error in SensorRuleContext while fetching reading fieldid : "+readingFieldId, e);
		}
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}
	
    private FacilioModule module;
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
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
	 
	private AssetCategoryContext assetCategory;
	public AssetCategoryContext getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCategoryContext assetCategory) {
		this.assetCategory = assetCategory;
	}
	private Map<Long, ReadingRuleAlarmMeta> alarmMetaMap;
	public Map<Long, ReadingRuleAlarmMeta> getAlarmMetaMap() {
		return alarmMetaMap;
	}
	public void setAlarmMetaMap(Map<Long, ReadingRuleAlarmMeta> alarmMetaMap) {
		this.alarmMetaMap = alarmMetaMap;
	}
	
	private SensorRuleType sensorRuleType;
	public SensorRuleType getSensorRuleTypeEnum() {
		return sensorRuleType;
	}
	public void setSensorRuleType(SensorRuleType sensorRuleType) {
		this.sensorRuleType = sensorRuleType;
	}
	public int getSensorRuleType() {
		if(sensorRuleType != null) {
			return sensorRuleType.getIntValue();
		}
		return -1;
	}
	public void setSensorRuleType(int sensorRuleType) {
		if (sensorRuleType > 0) {
			this.sensorRuleType = SENSOR_RULE_TYPES[sensorRuleType - 1];
		}
		else {
			this.sensorRuleType = null;
		}
	}
	
	private static final SensorRuleType[] SENSOR_RULE_TYPES = SensorRuleType.values();
	
	public enum SensorRuleType {
		CONTINUOUSLY_RECEIVING_SAME_VALUE(new ValidateContinuouslyReceivingSameValueInSensorRule()), //SensorRuleType-1
		CONTINUOUSLY_RECEIVING_ZERO(new ValidateContinuouslyReceivingZeroInSensorRule()),
		PERMISSIBLE_LIMIT_VIOLATION(new ValidatePermissibleLimitViolationInSensorRule()),
		NEGATIVE_VALUE(new ValidateNegativeValueInSensorRule(), true),
		DECREMENTAL_VALUE(new ValidateDecrementalValueInSensorRule(), true),
		SAME_VALUE_WITH_ZERO_DELTA(new ValidateSameValueWithZeroDeltaInSensorRule(),true),
		MEAN_VARIATION(new ValidateMeanVariationInSensorRule(),true), //SensorRuleType-7
		;
		
		public int getIntValue() {
			return ordinal()+1;
		}
		
		public static SensorRuleType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
		
		private SensorRuleTypeValidationInterface sensorRuleValidationType;
		private boolean isCounterFieldType = false;

		private SensorRuleType(){
		}
		
		private SensorRuleType(SensorRuleTypeValidationInterface sensorRuleValidationTypeClass){
			this.sensorRuleValidationType = sensorRuleValidationTypeClass;
		}
		
		private SensorRuleType(SensorRuleTypeValidationInterface sensorRuleValidationType, boolean isCounterFieldType) {
			this.sensorRuleValidationType = sensorRuleValidationType;
			this.isCounterFieldType = isCounterFieldType;
		}
		
		public boolean isCounterFieldType() {
			return isCounterFieldType;
		}
		
	    public SensorRuleTypeValidationInterface getSensorRuleValidationType() {
	        return sensorRuleValidationType;
	    }
	}
	
	public SensorEventContext constructEvent(ReadingContext reading, JSONObject ruleValidatorProps, JSONObject defaultSeverityProps, boolean isHistorical) throws Exception {
		
		SensorEventContext sensorEvent = new SensorEventContext();
		String severity = (String) defaultSeverityProps.remove("severity");
		if (StringUtils.isNotEmpty(severity)) {
			sensorEvent.setSeverityString(severity);
		}
		
		SensorRuleContext sensorRule = new SensorRuleContext();
		sensorRule.setId(this.getId());
		
		sensorEvent.setReadingFieldId(this.getReadingFieldId());
		sensorEvent.setSensorRule(sensorRule);
		
		sensorEvent.setResource((ResourceContext) reading.getParent());
		sensorEvent.setSiteId(((ResourceContext) reading.getParent()).getSiteId());
		sensorEvent.setCreatedTime(reading.getTtime());
		
		String subject = (String) defaultSeverityProps.remove("subject");
		String comment = (String) defaultSeverityProps.remove("comment");
		if (StringUtils.isNotEmpty(subject)) {
			sensorEvent.setEventMessage(subject);
		}
		if (StringUtils.isNotEmpty(comment)) {
			sensorEvent.setComment(comment);;
		}
		
		processNewAlarmMeta(this, (ResourceContext) reading.getParent(), reading.getTtime(), sensorEvent, isHistorical);

		return sensorEvent;
	}
	
	private static void processNewAlarmMeta(SensorRuleContext sensorRule, ResourceContext resource, long ttime, SensorEventContext sensorEvent, boolean isHistorical) throws Exception 
	{
		Map<Long, ReadingRuleAlarmMeta> metaMap = sensorRule.getAlarmMetaMap();
		ReadingRuleAlarmMeta alarmMeta = metaMap.get(resource.getId());
		if (alarmMeta == null) {
			metaMap.put(resource.getId(), SensorRuleUtil.constructNewAlarmMeta(-1, sensorRule, resource, false, sensorEvent.getEventMessage())); 	 //Assuming readings will come in ascending order of time and this is needed only for historical
		} 
		else if (alarmMeta != null && alarmMeta.isClear()) {
			alarmMeta.setClear(false); //Made active
		}
	}

	public SensorEventContext constructClearEvent(ReadingContext reading, JSONObject ruleValidatorProps, JSONObject defaultSeverityProps, boolean isHistorical) throws Exception {
		
		Map<Long, ReadingRuleAlarmMeta> metaMap = this.getAlarmMetaMap();
		ResourceContext resource = (ResourceContext) reading.getParent();
		
		ReadingRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(resource.getId()) : null;
		if (alarmMeta != null && !alarmMeta.isClear()) 
		{
			alarmMeta.setClear(true); //Made cleared
			
			SensorEventContext sensorEvent = new SensorEventContext();
			
			SensorRuleContext sensorRule = new SensorRuleContext();
			sensorRule.setId(this.getId());	
			sensorEvent.setReadingFieldId(this.getReadingFieldId());
			sensorEvent.setSensorRule(sensorRule);
			
			sensorEvent.setAutoClear(true);
			sensorEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
			sensorEvent.setComment("Sensor alarm auto cleared because associated rule executed clear condition for the associated asset.");
			
			sensorEvent.setResource(resource);
			sensorEvent.setSiteId(resource.getSiteId());
			sensorEvent.setCreatedTime(reading.getTtime());
			
			String subject = (String) defaultSeverityProps.remove("subject");
			if (StringUtils.isNotEmpty(subject)) {
				sensorEvent.setEventMessage(subject);
			}
		}
		return null;
	}
	
}
