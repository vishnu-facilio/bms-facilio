package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.sso.AccountSSO;
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
			 AccountSSO accSso = IAMOrgUtil.getAccountSSO(AccountUtil.getCurrentOrg().getId());

	         if(CollectionUtils.isNotEmpty(empList)) {
	             for(EmployeeContext emp : empList) {
					boolean showInviteOption = false;
					if(accSso != null)
					{
						if(accSso.getIsActive() && !emp.getIsAppAccess())
		                {
		                	showInviteOption = true;
		                }
					}
	            	emp.setShowInviteOption(showInviteOption);
	             }
	         }
	         return false;
	    }
}
