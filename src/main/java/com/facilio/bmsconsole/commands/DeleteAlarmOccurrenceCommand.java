package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeleteAlarmOccurrenceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long alarmOccurrenceId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (alarmOccurrenceId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			
			DeleteRecordBuilder<AlarmOccurrenceContext> deleteBuilder = new DeleteRecordBuilder<AlarmOccurrenceContext>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(alarmOccurrenceId, module));
			deleteBuilder.delete();
		}
		return false;
	}

}
