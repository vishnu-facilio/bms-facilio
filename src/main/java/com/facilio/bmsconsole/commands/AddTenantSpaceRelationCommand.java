package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddTenantSpaceRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TenantContext tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
		Boolean spacesUpdate = (Boolean) context.get(FacilioConstants.ContextNames.SPACE_UPDATE);
		if (tenant == null) {
			tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		}
		
		
		if (tenant != null) {
		if ((spacesUpdate != null && spacesUpdate) || (tenant.getSpaces() != null && tenant.getSpaces().size() > 0)) {
				List<Long> spaceIds = tenant.getSpaces().stream().map(a -> a.getId()).collect(Collectors.toList());
				FacilioModule module = ModuleFactory.getTenantSpacesModule();
				List<FacilioField> fields = FieldFactory.getTenantSpacesFields();
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(module.getTableName())
						.fields(fields)
						;
				for (BaseSpaceContext space : tenant.getSpaces()) {
					TenantSpaceContext tenantSpace = new TenantSpaceContext();
					tenantSpace.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
					tenantSpace.setTenantId(tenant.getId());
					tenantSpace.setSpace(space.getId());
					insertBuilder.addRecord(FieldUtil.getAsProperties(tenantSpace));
				}
				insertBuilder.save();
			}
		}
		return false;
	}

	

}
