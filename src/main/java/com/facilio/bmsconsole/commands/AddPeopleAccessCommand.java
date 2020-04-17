package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class AddPeopleAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
	    List<PeopleContext> people = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
	   Long occupantPortalAppId = (Long)context.getOrDefault(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, -1l);
		
	    if(CollectionUtils.isNotEmpty(people)) {
	    	
	    	List<AppDomain> occupantPortalApps = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, AccountUtil.getCurrentOrg().getOrgId());
	    	if(CollectionUtils.isNotEmpty(occupantPortalApps)) {
    			for(PeopleContext ppl : people) {
					PeopleAPI.updatePeoplePortalAccess(ppl, AppDomainType.SERVICE_PORTAL, occupantPortalAppId);
				}
		    }
	    }
		return false;
	}

}
