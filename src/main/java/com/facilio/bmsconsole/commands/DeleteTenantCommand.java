package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;


public class DeleteTenantCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<Long> zoneIds = new ArrayList<Long>(); 
			for(int i=0;i<recordIds.size();i++)
			{
				TenantContext oldTenant = TenantsAPI.getTenant(recordIds.get(i));
				if (oldTenant.getZone() != null) {
					zoneIds.add(oldTenant.getZone().getId());
				}
				TenantsAPI.deleteTenantLogo(oldTenant.getLogoId());
			}
			TenantsAPI.deleteTenantSpace(recordIds);
			
			DeleteRecordBuilder<? extends ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
																				.module(module)
																				.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
//																				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																				
																				;
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, deleteBuilder.markAsDelete());
			context.put(FacilioConstants.ContextNames.RECORD_LIST, zoneIds);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, "zone");
		}
		return false;
	}

	
}

