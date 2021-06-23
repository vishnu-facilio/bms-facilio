package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
				List<TenantSpaceContext> tenantSpaces = new ArrayList<>();
				for (BaseSpaceContext space : tenant.getSpaces()) {
					TenantSpaceContext tenantSpace = new TenantSpaceContext();
					tenantSpace.setTenant(tenant);
					tenantSpace.setSpace(space);
					tenantSpaces.add(tenantSpace);
				}
				InsertRecordBuilder<TenantSpaceContext> insertBuilder = new InsertRecordBuilder<TenantSpaceContext>()
									.table(module.getTableName()).module(module).fields(fields)
									.addRecords(tenantSpaces);
				insertBuilder.save();
			}
		}
		return false;
	}

	

}
