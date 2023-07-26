package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.SelectRecordsBuilder;

public class AddOrUpdateFacilioStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioStatus facilioStatus = (FacilioStatus) context.get(FacilioConstants.ContextNames.TICKET_STATUS);
		String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		if (facilioStatus != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(parentModuleName);
			if (facilioStatus.getId() > 0) {
				FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
				SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
						.beanClass(FacilioStatus.class)
						.module(ticketStatusModule)
						.select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS))
						.andCondition(CriteriaAPI.getIdCondition(facilioStatus.getId(), ticketStatusModule));
				FacilioStatus previousStatus = builder.fetchFirst();
				TicketAPI.updateStatus(facilioStatus, modBean.getModule(previousStatus.getParentModuleId()));
			}
			else {
				TicketAPI.addStatus(facilioStatus, module);
			}
		}
		context.put(FacilioConstants.ContextNames.ID,facilioStatus.getId());
		return false;
	}

}
