package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RelatedAssetsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddRelatedAssetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		RelatedAssetsContext relatedAsset = (RelatedAssetsContext) context.get(FacilioConstants.ContextNames.RELATED_ASSETS);
		if (relatedAsset != null) {
			FacilioModule module = ModuleFactory.getRelatedAssetsModule();
			Map<String, Object> props = FieldUtil.getAsProperties(relatedAsset);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
					.fields(FieldFactory.getRelatedAssetesFields()).addRecord(props);
			insertBuilder.save();
		}
		return false;
	}

}
