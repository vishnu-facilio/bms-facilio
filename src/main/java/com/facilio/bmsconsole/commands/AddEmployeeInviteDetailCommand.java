package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
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
	         long orgId = AccountUtil.getCurrentOrg().getId();
			 AccountSSO accSso = IAMOrgUtil.getAccountSSO(orgId);
			 DomainSSO domainSso = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.SERVICE_PORTAL, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL, AppDomain.DomainType.DEFAULT);
	         if(CollectionUtils.isNotEmpty(empList)) {
	             for(EmployeeContext emp : empList) {
					boolean showInviteOption = false,showPortalInviteOption = false;
					if(accSso != null)
					{
						if(accSso.getIsActive() && !emp.isAppAccess())
		                {
		                	showInviteOption = true;
		                }
					}
					if(domainSso != null)
					{
						if(domainSso.getIsActive() && !emp.isOccupantPortalAccess())
						{
							showPortalInviteOption = true;
						}
					}
	            	emp.setShowAppInviteOption(showInviteOption);
	            	emp.setShowPortalInviteOption(showPortalInviteOption);

	             }
	         }
	         return false;
	    }
}
