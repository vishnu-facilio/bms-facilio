package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetPurchasedItemsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			boolean showIsUsed = (boolean) context.get(FacilioConstants.ContextNames.PURCHASED_ITEM_IS_USED);
			long itemId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);

			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
				fields.addAll(FieldFactory.getCurrencyPropsFields(module));
			}

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
