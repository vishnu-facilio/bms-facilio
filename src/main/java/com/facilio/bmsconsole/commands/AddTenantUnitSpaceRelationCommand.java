package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddTenantUnitSpaceRelationCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TenantContext tenant = (TenantContext)context.get(FacilioConstants.ContextNames.RECORD);
		Boolean spacesUpdate = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.SPACE_UPDATE, false);
		
		if(spacesUpdate && CollectionUtils.isNotEmpty(tenant.getSpaces())) {
	     
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
		
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			StringJoiner idString = new StringJoiner(",");
			for(BaseSpaceContext space : tenant.getSpaces()) {
				idString.add(String.valueOf(space.getId()));
			}
			
			FacilioField tenantField = fieldMap.get("tenant");
			FacilioField isVacantField = fieldMap.get("isOccupied");
			
			updatedfields.add(tenantField);
			updatedfields.add(isVacantField);
			UpdateRecordBuilder<TenantUnitSpaceContext> updateBuilder = new UpdateRecordBuilder<TenantUnitSpaceContext>()
					.module(module)
					.fields(updatedfields)
					.andCondition(CriteriaAPI.getIdCondition(idString.toString(), module));
			
			Map<String, Object> value = new HashMap<>();
			value.put("tenant", FieldUtil.getAsProperties(tenant));
			value.put("isOccupied", true);
			
			updateBuilder.updateViaMap(value);

		}
	
		return false;
	}

}
