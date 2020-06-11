package com.facilio.bmsconsole.context.sensor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class SensorRollUpAlarmContext extends BaseAlarmContext{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SensorRollUpAlarmContext.class.getName());

	private SensorRuleContext sensorRule;
	public SensorRuleContext getSensorRule() {
		return sensorRule;
	}
	public void setSensorRule(SensorRuleContext sensorRule) {
		this.sensorRule = sensorRule;
	}
	
	private long readingFieldId;
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
			LOGGER.error("Error in SensorRollUpAlarmContext while fetching reading fieldid : "+readingFieldId, e);
		}
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}
}