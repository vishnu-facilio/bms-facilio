package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;


public class AddNewVisitorsWhilePreRegisteringCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorInviteContext visitorInviteRecords = (VisitorInviteContext)context.get(FacilioConstants.ContextNames.RECORD);
		List<VisitorContext> visitors = (List<VisitorContext>)context.get(FacilioConstants.ContextNames.INVITEES);
		if(visitorInviteRecords.getInviteHost() == null) {
			throw new IllegalArgumentException("Invite Host cannot be null");
		}
		if(CollectionUtils.isNotEmpty(visitors)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(VisitorContext inviteVisitor : visitors) {
				if(inviteVisitor.getId() <= 0) {
					RecordAPI.addRecord(true, Collections.singletonList(inviteVisitor) , module, fields);
				}
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitorInviteRecords));
		}
		return false;
	}

}
