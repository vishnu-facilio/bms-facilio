package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.MLAlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;

public class AddMLOccurrenceCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(AddMLOccurrenceCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.RECORD);
		Boolean isNewEvent = (Boolean) context.get(FacilioConstants.ContextNames.IS_NEW_EVENT);
		if (isNewEvent == null) {
			isNewEvent = false;
		}
		
//		LOGGER.info("Adding MLOccurence COmmand");
		
		if(alarm != null && alarm instanceof MLAlarmContext && isNewEvent) {
			LOGGER.info("Add occurrence for MLAlarm : "+alarm.getId());
			
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
