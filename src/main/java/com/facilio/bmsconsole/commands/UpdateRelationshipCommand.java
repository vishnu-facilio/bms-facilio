package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateRelationshipCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		RelationshipContext relationship = (RelationshipContext) context.get(FacilioConstants.ContextNames.RELATIONSHIP);
		if (relationship != null) {
			FacilioModule module = ModuleFactory.getRelationshipModule();
			Map<String, Object> props = FieldUtil.getAsProperties(relationship);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getRelationshipFields()).andCondition(CriteriaAPI.getIdCondition(relationship.getId(), module));
			
			updateBuilder.update(props);
		}
		return false;
	}

}
