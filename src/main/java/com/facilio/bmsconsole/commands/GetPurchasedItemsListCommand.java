package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetPurchasedItemsListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			long itemId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			
			SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
					.select(fields).table(module.getTableName())
					.moduleName(module.getName()).beanClass(PurchasedItemContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("item"),
							String.valueOf(itemId), PickListOperators.IS));
			
			List<PurchasedItemContext> purchasedItems = selectBuilder.get();
			
			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		}		return false;
	}

}
