package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;
@Log4j
public class AddRequesterForServiceRequestCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3ServiceRequestContext> request = Constants.getRecordList((FacilioContext) context);
		
		LOGGER.info("Entered AddRequesterForServiceRequestCommand");

		if (request != null && !request.isEmpty()) {
			LOGGER.info("REQUEST IS NOT EMPTY");
			for (V3ServiceRequestContext serviceRequestContext : request) {
				
				if(serviceRequestContext.getDescription() != null && serviceRequestContext.getDescription().length() > V3ServiceRequestContext.DESCIPTION_LENGTH) {
					serviceRequestContext.setDescription(serviceRequestContext.getDescription().substring(0, V3ServiceRequestContext.DESCIPTION_LENGTH));
				}
				
				PeopleContext requester = serviceRequestContext.getRequester();
				
				LOGGER.info("REQUESTER EMAIL- "+ ((requester != null) ? (requester.getEmail()+"::"+requester.getId()) : "requester is null"));
				
				
				if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail()) && requester.getId() <= 0) {
					
					PeopleContext people = PeopleAPI.getPeople(requester.getEmail());
					
					if(people != null) {
						serviceRequestContext.setRequester(people);
					}
					else {
						if(requester.getName() == null) {
							requester.setName(MailMessageUtil.getNameFromEmail.apply(requester.getEmail()));
						}
						EmployeeContext emp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(requester), EmployeeContext.class);

						FacilioChain c = TransactionChainFactory.addEmployeeChain();
						c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, false);
						c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
						c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
						c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

						c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(emp));
						c.execute();
						if(emp.getId() > 0){
							requester= FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(emp), PeopleContext.class);
							serviceRequestContext.setRequester(requester);
						}
					}
				}
				else {
					if(requester != null && requester.getId()>0) {
						requester=PeopleAPI.getPeopleForId(requester.getId());
					}
					else {
						requester = PeopleAPI.getPeopleForId(AccountUtil.getCurrentUser().getPeopleId());
					}
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
