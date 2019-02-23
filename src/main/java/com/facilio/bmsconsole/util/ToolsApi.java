package com.facilio.bmsconsole.util;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.StockedToolsContext;
import com.facilio.bmsconsole.context.ToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ToolsApi {
	public static ToolsContext getTool(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOLS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOLS);

		SelectRecordsBuilder<ToolsContext> selectBuilder = new SelectRecordsBuilder<ToolsContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolsContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<ToolsContext> tools = selectBuilder.get();

		if (tools != null && !tools.isEmpty()) {
			return tools.get(0);
		}
		return null;
	}
}
