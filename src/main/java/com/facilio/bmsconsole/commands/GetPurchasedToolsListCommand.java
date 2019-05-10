package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.db.criteria.BooleanOperators;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetPurchasedToolsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			boolean showIsUsed = (boolean) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL_IS_USED);
			long toolId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);

			SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
					.select(fields).table(module.getTableName()).moduleName(module.getName())
					.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("tool"),
							String.valueOf(toolId), PickListOperators.IS));
			if (!showIsUsed) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("isUsed"), String.valueOf(showIsUsed),
						BooleanOperators.IS));
			}

			List<PurchasedToolContext> purchasedTools = selectBuilder.get();

			context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		}
		return false;
	}

}