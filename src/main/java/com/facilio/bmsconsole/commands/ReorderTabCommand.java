package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ReorderTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<WebtabWebgroupContext> tabsGroupsList = (List<WebtabWebgroupContext>) context.get(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP);
		if (!tabsGroupsList.isEmpty()) {
			long tabGroupId = tabsGroupsList.get(0).getWebTabGroupId();
			for(WebtabWebgroupContext tabGroup:tabsGroupsList) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWebTabWebGroupModule().getTableName())
						.fields(FieldFactory.getWebTabWebGroupFields())
						.andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", String.valueOf(tabGroup.getWebTabId()), NumberOperators.EQUALS))
	                    .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "webTabGroupId", String.valueOf(tabGroup.getWebTabGroupId()), NumberOperators.EQUALS));
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
