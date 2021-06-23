package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class ValidateAssetTypeDeletion extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long recordID = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		FacilioModule assetModule = ModuleFactory.getAssetsModule();
		GenericSelectRecordBuilder assetSelectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(FieldFactory.getIdField(assetModule))).table(assetModule.getTableName())
				.innerJoin("Resources").on("Assets.ID=Resources.ID")
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
				.andCustomWhere("ASSET_TYPE = ? AND (Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = ?)", recordID, false).limit(1);
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
