package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;

public class DisassociateTenantSpaceRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long tenantId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE);
		 if (tenantId != null && tenantId > 0 && spaceId != null && spaceId >0) {
	            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
	                    .table(ModuleFactory.getTenantSpacesModule().getTableName())
	                    .andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenantId", String.valueOf(tenantId), NumberOperators.EQUALS))
	            		.andCondition(CriteriaAPI.getCondition("SPACE", "space", String.valueOf(spaceId), NumberOperators.EQUALS));
	            context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
	        }
		
		return false;
	}

}
