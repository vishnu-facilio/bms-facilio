package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
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
				WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
				tabBean.updateWebtabWebtabGroup(tabGroup);
			}

			// increment layout version
			WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
			WebTabGroupContext webTabGroup = tabBean.getWebTabGroup(tabGroupId);
			if (webTabGroup == null) {
				throw new IllegalArgumentException("Invalid web group");
			}
			ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(webTabGroup.getLayoutId()));
		}
        return false;
    }

}
