package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteViewGroupCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long viewGroupId = (long) context.get(FacilioConstants.ContextNames.VIEW_GROUP_ID);
		ViewGroups viewGroup = ViewAPI.getGroup(viewGroupId);

		if (viewGroup == null) {
			throw new IllegalArgumentException("ViewGroup not found");
		}

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getViewGroupsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(viewGroupId, ModuleFactory.getViewGroupsModule()));

		deleteRecordBuilder.delete();

		context.put(FacilioConstants.ContextNames.VIEW_GROUP, viewGroup);
		return false;
	}

	

}
