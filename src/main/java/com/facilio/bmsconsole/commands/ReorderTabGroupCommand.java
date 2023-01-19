package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ReorderTabGroupCommand extends FacilioCommand {

	@Override
    public boolean executeCommand(Context context) throws Exception {
		List<WebTabGroupContext> tabGroupList = (List<WebTabGroupContext>) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUPS);
		if (!tabGroupList.isEmpty()) {
			long tabGroupId = tabGroupList.get(0).getId();	// get any one tab group, assuming client will send only tabgroup ids of same layout
			
			for(WebTabGroupContext tabGroup:tabGroupList) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWebTabGroupModule().getTableName())
						.fields(FieldFactory.getWebTabGroupFields())
						.andCondition(CriteriaAPI.getIdCondition(tabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
				builder.update(FieldUtil.getAsProperties(tabGroup));
			}

			// increment layout version
			WebTabGroupContext webTabGroup = ApplicationApi.getWebTabGroup(tabGroupId);
			if (webTabGroup == null) {
				throw new IllegalArgumentException("Invalid web group");
			}
			ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(webTabGroup.getLayoutId()));
		}
        return false;
    }

}
