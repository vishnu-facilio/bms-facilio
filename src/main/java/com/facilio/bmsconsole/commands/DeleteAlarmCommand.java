package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeleteAlarmCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long alarmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (alarmId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
			
			DeleteRecordBuilder<BaseAlarmContext> deleteBuilder = new DeleteRecordBuilder<BaseAlarmContext>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(alarmId, module));
			deleteBuilder.delete();
		}
		return false;
	}

}
