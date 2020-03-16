package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class AddPeopleAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
	    List<PeopleContext> people = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
	    if(CollectionUtils.isNotEmpty(people)) {
	    	AppDomain facilioApp = IAMAppUtil.getAppDomain(AppDomainType.FACILIO);
	    	AppDomain occupantPortalApp = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL);
	    	AppDomain vendorPortalApp = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL);
	    	AppDomain tenantPortalApp = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL);
	    	AppDomain clientPortalApp = IAMAppUtil.getAppDomain(AppDomainType.CLIENT_PORTAL);
	    	
//	    	for(PeopleContext person : people) {
//	    		if(facilioApp != null && person.getPeopleTypeEnum() == PeopleType.EMPLOYEE && ((EmployeeContext)person).isAppAccess()) {
//	    			PeopleAPI.addAppUser(person, facilioApp != null ? facilioApp.getDomain() : null);
//	    		}
//	    		if(occupantPortalApp != null && ((person.getPeopleTypeEnum() == PeopleType.EMPLOYEE && ((EmployeeContext)person).isOccupantPortalAccess())|| (person.getPeopleTypeEnum() == PeopleType.TENANT_CONTACT && ((TenantContactContext)person).isOccupantPortalAccess()))) {
//	    	  		PeopleAPI.addPortalAppUser(person, occupantPortalApp != null ? occupantPortalApp.getDomain() : null, 1);
//	    		}
//	    		if(vendorPortalApp != null && person.getPeopleTypeEnum() == PeopleType.VENDOR_CONTACT && ((VendorContactContext)person).isVendorPortalAccess()) {
//	    	  		PeopleAPI.addPortalAppUser(person, vendorPortalApp != null ? vendorPortalApp.getDomain() : null, vendorPortalApp.getId());
//	    		}
//	    		if(tenantPortalApp != null && person.getPeopleTypeEnum() == PeopleType.TENANT_CONTACT && ((TenantContactContext)person).isTenantPortalAccess()) {
//	    	  		PeopleAPI.addPortalAppUser(person, tenantPortalApp != null ? tenantPortalApp.getDomain() : null, 1);
//	    		}
//	    		if(clientPortalApp != null && person.getPeopleTypeEnum() == PeopleType.CLIENT_CONTACT && ((ClientContactContext)person).isClientPortalAccess()) {
//	    	  		PeopleAPI.addPortalAppUser(person, tenantPortalApp != null ? clientPortalApp.getDomain() : null, 1);
//	    		}
//	    		
//		    }
	    }
		return false;
	}

}
