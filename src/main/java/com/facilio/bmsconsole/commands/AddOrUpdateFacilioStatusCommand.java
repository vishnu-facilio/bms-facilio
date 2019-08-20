package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class AddOrUpdateFacilioStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioStatus facilioStatus = (FacilioStatus) context.get(FacilioConstants.ContextNames.TICKET_STATUS);
		String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		if (facilioStatus != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(parentModuleName);
			if (facilioStatus.getId() > 0) {
				TicketAPI.updateStatus(facilioStatus, module);
			}
			else {
				TicketAPI.addStatus(facilioStatus, module);
			}
		}
		return false;
	}

}
