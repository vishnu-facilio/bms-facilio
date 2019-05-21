package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetPurchasedItemsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			boolean showIsUsed = (boolean) context.get(FacilioConstants.ContextNames.PURCHASED_ITEM_IS_USED);
			long itemId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);

			SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
					.select(fields).table(module.getTableName()).moduleName(module.getName())
					.beanClass(PurchasedItemContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("item"),
							String.valueOf(itemId), PickListOperators.IS));
			if (!showIsUsed) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("isUsed"), String.valueOf(showIsUsed),
						BooleanOperators.IS));
			}

			List<PurchasedItemContext> purchasedItems = selectBuilder.get();

			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		}
		return false;
	}

}
