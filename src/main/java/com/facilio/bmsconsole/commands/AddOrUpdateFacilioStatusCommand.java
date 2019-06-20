package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class AddOrUpdateFacilioStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioStatus facilioStatus = (FacilioStatus) context.get(FacilioConstants.ContextNames.TICKET_STATUS);
		String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		if (facilioStatus != null) {
			if (facilioStatus.getId() > 0) {
				TicketAPI.updateStatus(facilioStatus);
			}
			else {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(parentModuleName);
				TicketAPI.addStatus(facilioStatus, module);
			}
		}
		return false;
	}

}
