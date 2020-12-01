package com.facilio.bmsconsole.context.sensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class SensorRuleContext implements Serializable{
	
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
	private List<String> sensorRuleProps;
	
	public List<String> getSensorRuleProps() {
		return sensorRuleProps;
	}
	public void setSensorRuleProps(List<String> sensorRuleProps) {
		this.sensorRuleProps = sensorRuleProps;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	private String subject;
	private JSONObject rulePropInfo;
	
	public JSONObject getRulePropInfo() {
		return rulePropInfo;
	}
	public void setRulePropInfo(JSONObject rulePropInfo) {
		this.rulePropInfo = rulePropInfo;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	public boolean getStatus() {
		return status;
	}
	private boolean status;
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

	private List<Long> matchedResourceIds;
	public List<Long> getMatchedResourceIds() {
		return matchedResourceIds;
	}
	public void setMatchedResourceIds(List<Long> matchedResourceIds) {
		this.matchedResourceIds = matchedResourceIds;
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
	private HashMap<Long, SensorRuleAlarmMeta> alarmMetaMap;
	public HashMap<Long, SensorRuleAlarmMeta> getAlarmMetaMap() {
		return alarmMetaMap;
	}
	public void setAlarmMetaMap(HashMap<Long, SensorRuleAlarmMeta> alarmMetaMap) {
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
			return sensorRuleType.getIndex();
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
	
	public SensorEventContext constructEvent(ReadingContext reading, JSONObject defaultSeverityProps, boolean isHistorical) throws Exception {
		
		SensorEventContext sensorEvent = new SensorEventContext();
		String severity = (String) defaultSeverityProps.get("severity");
		if (StringUtils.isNotEmpty(severity)) {
			sensorEvent.setSeverityString(severity);
		}
		
		SensorRuleContext sensorRule = new SensorRuleContext();
		sensorRule.setId(this.getId());
		
		sensorEvent.setReadingFieldId(this.getReadingFieldId());
		sensorEvent.setSensorRule(sensorRule);
		sensorEvent.setSensorRuleType(this.sensorRuleType);
		sensorEvent.setMeterRollUp(this.sensorRuleType.isMeterRollUp());
		
		SensorRuleUtil.addDefaultEventProps(reading, defaultSeverityProps, sensorEvent);
		
		processNewSensorAlarmMeta(this, sensorEvent.getResource(), reading.getTtime(), sensorEvent, isHistorical);

		return sensorEvent;
	}

	public SensorEventContext constructClearEvent(ReadingContext reading, JSONObject defaultSeverityProps, boolean isHistorical) throws Exception {
		
		Map<Long, SensorRuleAlarmMeta> metaMap = this.getAlarmMetaMap();
		SensorRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(reading.getParentId()) : null;
		
		if (alarmMeta != null && !alarmMeta.isClear()) 
		{
			alarmMeta.setClear(true); //Made cleared
			
			SensorEventContext sensorEvent = new SensorEventContext();
			
			SensorRuleContext sensorRule = new SensorRuleContext();
			sensorRule.setId(this.getId());	
			sensorEvent.setReadingFieldId(this.getReadingFieldId());
			sensorEvent.setSensorRule(sensorRule);
			sensorEvent.setSensorRuleType(this.sensorRuleType);
			sensorEvent.setMeterRollUp(this.sensorRuleType.isMeterRollUp());
			
			sensorEvent.setAutoClear(true);
			sensorEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
			sensorEvent.setComment("Sensor alarm auto cleared because associated rule executed clear condition for the associated asset.");
			
			SensorRuleUtil.addDefaultEventProps(reading, defaultSeverityProps, sensorEvent);
			return sensorEvent;
		}
		return null;
	}
	
	private static void processNewSensorAlarmMeta(SensorRuleContext sensorRule, ResourceContext resource, long ttime, SensorEventContext sensorEvent, boolean isHistorical) throws Exception 
	{
		HashMap<Long, SensorRuleAlarmMeta> metaMap = sensorRule.getAlarmMetaMap();
		SensorRuleAlarmMeta alarmMeta = null;
		if(metaMap != null) {
			alarmMeta = metaMap.get(resource.getId());
		}
		else {
			metaMap = new HashMap<Long, SensorRuleAlarmMeta>();
			sensorRule.setAlarmMetaMap(metaMap);
		}
		
		if (alarmMeta == null) {
			metaMap.put(resource.getId(), SensorRuleUtil.constructNewAlarmMeta(-1, sensorRule, resource, false, sensorEvent.getEventMessage())); 	 //Assuming readings will come in ascending order of time and this is needed only for historical
		} 
		else if (alarmMeta != null && alarmMeta.isClear()) {
			alarmMeta.setClear(false); //Made active
		}
	}	
}
