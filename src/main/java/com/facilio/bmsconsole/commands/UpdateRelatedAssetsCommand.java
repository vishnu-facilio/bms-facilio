package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RelatedAssetsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateRelatedAssetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		RelatedAssetsContext relatedAsset = (RelatedAssetsContext) context.get(FacilioConstants.ContextNames.RELATED_ASSETS);
		if (relatedAsset != null) {
			FacilioModule module = ModuleFactory.getRelatedAssetsModule();
			Map<String, Object> props = FieldUtil.getAsProperties(relatedAsset);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getRelatedAssetesFields()).andCondition(CriteriaAPI.getIdCondition(relatedAsset.getId(), module));
			
			updateBuilder.update(props);
		}
		return false;
	}

}
