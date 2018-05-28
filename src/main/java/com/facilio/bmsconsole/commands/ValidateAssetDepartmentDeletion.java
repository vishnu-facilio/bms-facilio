package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ValidateAssetDepartmentDeletion implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long recordID = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		FacilioModule assetModule = ModuleFactory.getAssetsModule();
		GenericSelectRecordBuilder assetSelectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(FieldFactory.getIdField(assetModule))).table(assetModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
				.andCustomWhere("DEPARTMENT_ID = ?", recordID).limit(1);
		List<Map<String, Object>> result = assetSelectBuilder.get();

		List<String> moduleNames = new ArrayList<>();
		context.put(FacilioConstants.ContextNames.MODULE_NAMES, moduleNames);
		if (!result.isEmpty()) {
			moduleNames.add(assetModule.getDisplayName());
			return true;
		}
		return false;
	}

}
