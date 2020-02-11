package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
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

public class SetTenantSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			TenantContext tenant = TenantsAPI.fetchTenant((Long)context.get(FacilioConstants.ContextNames.ID));
			Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(FieldFactory.getTenantSpacesFields());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getTenantSpacesFields())
					.table(ModuleFactory.getTenantSpacesModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenantId"), String.valueOf(tenant.getId()), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<Long> ids = new ArrayList<>();
				for (Map<String, Object> prop : props) {
					ids.add((Long) prop.get("space"));
				}
				List<BaseSpaceContext> baseSpaces = SpaceAPI.getBaseSpaces(ids);
				tenant.setSpaces(baseSpaces);
			}
		}
		
		return false;
	}

}
