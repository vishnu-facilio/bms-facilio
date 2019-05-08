package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class HandlePortalInfoCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, Object> servicePortalMap  = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PORTALINFO);
		FacilioModule module = ModuleFactory.getServicePortalModule();
		
		List<FacilioField> fields = FieldFactory.getServicePortalFields();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
												.table(module.getTableName())
												.fields(fields);
//												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		builder.update(servicePortalMap);
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
}
