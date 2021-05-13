package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class AddRequesterForServiceRequestCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3ServiceRequestContext> request = Constants.getRecordList((FacilioContext) context);
		if (request != null && !request.isEmpty()) {
			for (V3ServiceRequestContext serviceRequestContext : request) {
				
				PeopleContext requester = serviceRequestContext.getRequester();
				
				if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail()) && requester.getId() <= 0) {
					
					PeopleContext people = PeopleAPI.getPeople(requester.getEmail());
					
					if(people != null) {
						serviceRequestContext.setRequester(people);
					}
					else {
						if(requester.getName() == null) {
							requester.setName(MailMessageUtil.getNameFromEmail.apply(requester.getEmail()));
						}
						FacilioChain c = TransactionChainFactory.addPeopleChain();
						c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, false);
						c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
						c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
						c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

						c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(requester));
						c.execute();
					}
				}
				else {
					requester = PeopleAPI.getPeopleForId(AccountUtil.getCurrentUser().getPeopleId());
					serviceRequestContext.setRequester(requester);
				}
				
				Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
				
				if (isPublicRequest != null && isPublicRequest) {
					setRequesterAsCurrentUser(requester);
				}
			}
		}
		return false;
	}
	
	private void setRequesterAsCurrentUser(PeopleContext requester) throws Exception {
		// TODO Auto-generated method stub
		
		User requestorUser = AccountUtil.getUserBean().getUserFromEmail(requester.getEmail(), null, AccountUtil.getCurrentOrg().getOrgId());
		if(requestorUser != null) {
			Account acct = new Account(AccountUtil.getCurrentOrg(), requestorUser);
			AccountUtil.setCurrentAccount(acct);
		}
	}

}
