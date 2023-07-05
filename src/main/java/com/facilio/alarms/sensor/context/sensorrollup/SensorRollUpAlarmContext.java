package com.facilio.alarms.sensor.context.sensorrollup;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

@Getter
@Setter
@Log4j
public class SensorRollUpAlarmContext extends BaseAlarmContext{

	private static final long serialVersionUID = 1L;
	private SensorRuleContext sensorRule;
	private long readingFieldId = -1;
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