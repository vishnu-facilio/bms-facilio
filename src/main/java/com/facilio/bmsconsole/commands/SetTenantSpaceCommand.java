package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class SetTenantSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TenantContext tenant= (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		List<BaseSpaceContext> spaces = TenantsAPI.fetchTenantSpaces(tenant.getId());
		if (CollectionUtils.isNotEmpty(spaces)) {
			tenant.setSpaces(spaces);
		}
		
		return false;
	}

}
