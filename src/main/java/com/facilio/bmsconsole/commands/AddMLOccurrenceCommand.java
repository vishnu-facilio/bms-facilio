package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.MLAlarmOccurrenceContext;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddMLOccurrenceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.RECORD);
		Boolean isNewEvent = (Boolean) context.get(FacilioConstants.ContextNames.IS_NEW_EVENT);
		if (isNewEvent == null) {
			isNewEvent = false;
		}
		
		if(alarm != null && alarm instanceof MLAlarmContext && isNewEvent) {
			MLAlarmOccurrenceContext occurrence = new MLAlarmOccurrenceContext();
			occurrence.setParentAlarm((MLAlarmContext) alarm);
			occurrence.setTtime(alarm.getModifiedTime());
			occurrence.setRuleId(((MLAlarmContext) alarm).getRuleId());
			
			ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			new InsertRecordBuilder<MLAlarmOccurrenceContext>()
					.fields(modbean.getAllFields(FacilioConstants.ContextNames.ML_ALARM_OCCURRENCE))
					.moduleName(FacilioConstants.ContextNames.ML_ALARM_OCCURRENCE)
					.insert(occurrence)
					;
		}
		return false;
	}

}
