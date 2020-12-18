package com.facilio.bmsconsole.commands;

import java.util.List;

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
			
			for(WebTabGroupContext tabGroup:tabGroupList) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWebTabGroupModule().getTableName())
						.fields(FieldFactory.getWebTabGroupFields())
						.andCondition(CriteriaAPI.getIdCondition(tabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
				builder.update(FieldUtil.getAsProperties(tabGroup));
			}

		}
        return false;
    }

}
