package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class HandlePortalInfoCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, Object> servicePortalMap  = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PORTALINFO);

		Long portalId = (Long) servicePortalMap.get("portalId");
		if (portalId == null) {
			throw new IllegalArgumentException("Invalid portal id during updation of portal settings");
		}

		FacilioModule module = ModuleFactory.getServicePortalModule();
		
		List<FacilioField> fields = FieldFactory.getServicePortalFields();
		FacilioField portalIdField = FieldFactory.getAsMap(fields).get("portalId");
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
												.table(module.getTableName())
												.fields(fields)
												.andCondition(CriteriaAPI.getCondition(portalIdField, portalId.toString(), NumberOperators.EQUALS))
												;
//												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		builder.update(servicePortalMap);
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
}
