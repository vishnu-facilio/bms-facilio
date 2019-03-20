package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ApproveOrRejectItemCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> itemTransactionsFieldMap = FieldFactory.getAsMap(itemTransactionsFields);

		List<Long> recordIds =  (List<Long>) context
				.get(FacilioConstants.ContextNames.RECORD_ID);
		List<Long> parentIds = new ArrayList<>();
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("item")));
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("purchasedItem")));
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("itemType")));
		
		SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>().select(itemTransactionsFields)
				.table(itemTransactionsModule.getTableName()).moduleName(itemTransactionsModule.getName()).beanClass(ItemTransactionsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, itemTransactionsModule))
				.fetchLookups(lookUpfields);
		
		List<ItemTransactionsContext> itemTransactions = selectBuilder.get();
		for(ItemTransactionsContext transactions : itemTransactions) {
			parentIds.add(transactions.getParentId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		return false;
	}

}
