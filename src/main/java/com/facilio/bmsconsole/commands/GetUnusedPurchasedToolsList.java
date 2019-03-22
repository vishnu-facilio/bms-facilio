package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetUnusedPurchasedToolsList implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			boolean showIsUsed = (boolean) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL_IS_USED);
			List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID);

			SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
					.select(fields).table(module.getTableName()).moduleName(module.getName())
					.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getConditionFromList("TOOL_ID", "tool", toolIds, PickListOperators.IS));
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