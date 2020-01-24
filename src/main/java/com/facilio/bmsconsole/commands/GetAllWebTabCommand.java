package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllWebTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long tabGroupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
		if (tabGroupId != null && tabGroupId > 0) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
					.andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(tabGroupId),
							NumberOperators.EQUALS));
			List<WebTabContext> tabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
			if (tabs != null && !tabs.isEmpty()) {
				for (WebTabContext tab : tabs) {
					tab.setModuleIds(ApplicationApi.getModuleIdsForTab(tab.getId()));
				}
			}
			context.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
		}
		return false;
	}
}
