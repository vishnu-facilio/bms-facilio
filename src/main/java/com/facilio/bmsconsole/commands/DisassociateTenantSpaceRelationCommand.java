package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class DisassociateTenantSpaceRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long tenantId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE);
		 if (tenantId != null && tenantId > 0 && spaceId != null && spaceId >0) {
			 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
				Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		        DeleteRecordBuilder<? extends ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
						.module(module)
						.andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("space"), String.valueOf(spaceId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"),  String.valueOf(tenantId), NumberOperators.EQUALS))
						;
	            context.put(FacilioConstants.ContextNames.ROWS_UPDATED, deleteBuilder.delete());
	        }
		
		return false;
	}

}
