package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;

public class DeleteTenantSpaceRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TenantContext tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
		if (tenant == null) {
			tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		}
		Boolean spacesUpdate = (Boolean) context.get(FacilioConstants.ContextNames.SPACE_UPDATE);
		Long id = tenant.getId();
		if ((spacesUpdate != null && spacesUpdate) || (tenant.getSpaces() != null && tenant.getSpaces().size() > 0)) {
			if (id != null && id > 0) {
	            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
	                    .table(ModuleFactory.getTenantSpacesModule().getTableName())
	                    .andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenantId", String.valueOf(id), NumberOperators.EQUALS));
	            builder.delete();
	        }
		}
		
		return false;
	}
}
