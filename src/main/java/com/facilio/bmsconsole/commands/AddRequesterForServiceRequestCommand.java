package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.dto.Role;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddRequesterForServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ServiceRequestContext> request = (List<ServiceRequestContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if (request != null && !request.isEmpty()) {
			
			for (ServiceRequestContext serviceRequestContext : request) {
				
				PeopleContext requester = serviceRequestContext.getRequester();
				
				if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail()) && requester.getId() <= 0) {
					
					PeopleContext people = PeopleAPI.getPeople(requester.getEmail());
					
					if(people != null) {
						serviceRequestContext.setRequester(people);
					}
					else {
						
						FacilioChain c = TransactionChainFactory.addPeopleChain();
						c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, false);
						c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
						c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
						c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

						Role occupantAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.PrevilegedRoleNames.OCCUPANT_ADMIN);
						c.getContext().put(FacilioConstants.ContextNames.ROLE_ID, occupantAdmin.getRoleId());

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
