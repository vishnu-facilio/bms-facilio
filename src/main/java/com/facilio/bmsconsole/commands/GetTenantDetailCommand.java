package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


public class GetTenantDetailCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			TenantContext tenant = TenantsAPI.fetchTenant((Long)context.get(FacilioConstants.ContextNames.ID));
			if(tenant != null && tenant.getZone() != null) {
				List<BaseSpaceContext> spaces =	SpaceAPI.getZoneChildren(Collections.singletonList(tenant.getZone().getId()));
				context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
			}
			context.put(FacilioConstants.ContextNames.TENANT, tenant);
		}
		return false;
	}
}
