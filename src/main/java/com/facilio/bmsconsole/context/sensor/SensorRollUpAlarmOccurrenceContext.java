package com.facilio.bmsconsole.context.sensor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext.Type;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class SensorRollUpAlarmOccurrenceContext extends AlarmOccurrenceContext {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SensorRollUpAlarmOccurrenceContext.class.getName());

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
			LOGGER.error("Error in SensorRollUpAlarmOccurrenceContext while fetching reading fieldid : "+readingFieldId, e);
		}
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}
	
	public Type getTypeEnum() {
        return Type.SENSOR_ROLLUP;
    }
}
