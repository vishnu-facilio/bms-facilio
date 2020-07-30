package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
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
		ViewGroups viewGroup = (ViewGroups) context.get(FacilioConstants.ContextNames.VIEW_GROUP);
		if (viewGroup != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getViewGroupsModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(viewGroup.getId(), ModuleFactory.getViewGroupsModule()));
			
			deleteRecordBuilder.delete();
			
		}
		return false;
	}

	

}
