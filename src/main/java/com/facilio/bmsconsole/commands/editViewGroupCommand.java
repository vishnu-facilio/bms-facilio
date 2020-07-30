package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class editViewGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ViewGroups viewGroup = (ViewGroups) context.get(FacilioConstants.ContextNames.VIEW_GROUP);
		if (viewGroup != null) {
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getViewGroupsModule().getTableName())
					.fields(FieldFactory.getViewGroupFields())
					.andCondition(CriteriaAPI.getIdCondition(viewGroup.getId(), ModuleFactory.getViewGroupsModule()));

			Map<String, Object> props = FieldUtil.getAsProperties(viewGroup);
			updateBuilder.update(props);
			
		}
		return false;
	}

	
}
