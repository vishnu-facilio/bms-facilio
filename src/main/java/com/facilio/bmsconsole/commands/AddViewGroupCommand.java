package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddViewGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ViewGroups viewGroup = (ViewGroups) context.get(FacilioConstants.ContextNames.VIEW_GROUP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (viewGroup != null) {
			viewGroup.setName(viewGroup.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			
			long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
			
			
			viewGroup.setId(groupId);
			context.put(FacilioConstants.ContextNames.VIEW_GROUP, viewGroup);
		}
		return false;
	}

}
