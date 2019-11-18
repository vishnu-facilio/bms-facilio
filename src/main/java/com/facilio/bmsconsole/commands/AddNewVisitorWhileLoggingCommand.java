package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddNewVisitorWhileLoggingCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLogs = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLogs)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(VisitorLoggingContext vL : visitorLogs) {
				if(vL.getInvite() != null && vL.getInvite().getId() > 0) {
					vL.setIsInvited(true);
				}
				else {
					vL.setIsInvited(false);
				}
				if(vL.getAvatar() != null) {
					vL.setPhotoStatus(true);
				}
				else {
					vL.setPhotoStatus(false);
				}
				if(vL.getHost() != null && vL.getHost().getId() > 0) {
					vL.setHostStatus(true);
				}
				else {
					vL.setHostStatus(false);
				}
				if(vL.getVisitor() != null && vL.getVisitor().getId() > 0){
					VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLogging(vL.getVisitor().getId(), true);
					if(vLog != null) {
						FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
						VisitorContext visitor = null;
						if(vL.getVisitor() != null && vL.getVisitor().getId() > 0) {
							visitor = VisitorManagementAPI.getVisitor(vL.getVisitor().getId(), null);
							VisitorManagementAPI.checkOutVisitorLogging(visitor.getPhone(), c.getContext());
						}
						if(c.getContext().get("visitorLogging") != null) {
							c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, c.getContext().get("nextTransitionId"));
							VisitorLoggingContext visitorLoggingContext = (VisitorLoggingContext) c.getContext().get("visitorLogging");
							c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitorLoggingContext));
							c.execute();
						}
					}
					//throw new IllegalArgumentException("This visitor has an active record already. Kindly Checkout and proceed to CheckIn");
				}
				if(vL.getVisitor() != null && vL.getVisitor().getId() <= 0) {
					RecordAPI.addRecord(true, Collections.singletonList(vL.getVisitor()) , module, fields);
				}
				if(vL.getInvite() != null) {
					VisitorInviteContext visitorInvite = VisitorManagementAPI.getVisitorInvite(vL.getInvite().getId());
					vL.setExpectedVisitDuration(visitorInvite.getExpectedDuration());
				}
			}
		}
		return false;
	}

}
