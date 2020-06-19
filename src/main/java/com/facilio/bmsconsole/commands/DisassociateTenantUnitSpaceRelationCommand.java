package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class DisassociateTenantUnitSpaceRelationCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
	
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioField tenantField = fieldMap.get("tenant");
		FacilioField isVacantField = fieldMap.get("isOccupied");
		
		updatedfields.add(tenantField);
		updatedfields.add(isVacantField);
		
		UpdateRecordBuilder<TenantUnitSpaceContext> updateBuilder = new UpdateRecordBuilder<TenantUnitSpaceContext>()
				.module(module)
				.fields(updatedfields)
				.andCondition(CriteriaAPI.getIdCondition(spaceId, module));
		
		Map<String, Object> value = new HashMap<>();
		TenantContext tenant = new TenantContext();
		tenant.setId(-99L);
		value.put("tenant", FieldUtil.getAsProperties(tenant));
		value.put("isOccupied", false);
		
		updateBuilder.updateViaMap(value);

		return false;
	}

}
