package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMOrgUtil;

public class AddEmployeeInviteDetailCommand extends FacilioCommand {
	    @Override
	    public boolean executeCommand(Context context) throws Exception {
	    	 List<EmployeeContext> empList = (List<EmployeeContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
	         if(CollectionUtils.isEmpty(empList)) {
	        	 empList = new ArrayList<>();
	             EmployeeContext emp = (EmployeeContext) context.get(FacilioConstants.ContextNames.RECORD);
	             if(emp != null) {
	            	 empList.add(emp);
	             }
	         }
	         if(CollectionUtils.isNotEmpty(empList)) {
	             for(EmployeeContext emp : empList) {
		            PeopleContext ppl = (PeopleContext) emp;
		            AppDomain appDomain = null;
					long appId = -1;
					appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
					appDomain = ApplicationApi.getAppDomainForApplication(appId);
					if(appDomain != null)
					{
						User user = AccountUtil.getUserBean().getUser(ppl.getEmail(), appDomain.getIdentifier());
						System.out.println(user);
						boolean showInviteOption = false;
		                if(IAMOrgUtil.getAccountSSO(AccountUtil.getCurrentOrg().getId()) != null && (user == null))
		                {
		                	showInviteOption = true;
		                }
	                	emp.setShowInviteOption(showInviteOption);
					}

	             }
	         }
	         return false;
	    }
}
