package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
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
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (WebTabContext tab : tabs) {
					tab.setModuleIds(ApplicationApi.getModuleIdsForTab(tab.getId()));

					if (CollectionUtils.isNotEmpty(tab.getModuleIds())) {
						List<FacilioModule> modules = new ArrayList<>();
						for (Long moduleId : tab.getModuleIds()) {
							modules.add(modBean.getModule(moduleId));
						}
						tab.setModules(modules);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
		}
		return false;
	}
}
