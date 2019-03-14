package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateItemStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PurchasedItemContext> purchasedItems = (List<PurchasedItemContext>) context
				.get(FacilioConstants.ContextNames.PURCHASED_ITEM);
		if (purchasedItems != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			List<ItemTransactionsContext> inventoryTransaction = new ArrayList<>();
			for (PurchasedItemContext ic : purchasedItems) {
				ItemContext item = ItemsApi.getItems(ic.getItem().getId());
				ItemTransactionsContext transaction = new ItemTransactionsContext();
				transaction.setTransactionState(TransactionState.ADDITION.getValue());
				transaction.setPurchasedItem(ic);
				transaction.setItem(ic.getItem());
				transaction.setItemType(item.getItemType());
				transaction.setQuantity(ic.getQuantity());
				transaction.setParentId(ic.getId());
				transaction.setIsReturnable(false);
				transaction.setTransactionType(TransactionType.STOCK.getValue());

				SelectRecordsBuilder<ItemTransactionsContext> transactionsselectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
						.select(fields).table(module.getTableName()).moduleName(module.getName())
						.beanClass(ItemTransactionsContext.class)
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
								String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.IS));
				List<ItemTransactionsContext> transactions = transactionsselectBuilder.get();
				if (transactions != null && !transactions.isEmpty()) {
					ItemTransactionsContext it = transactions.get(0);
					it.setQuantity(ic.getQuantity());
					UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
							.module(module).fields(modBean.getAllFields(module.getName()))
							.andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
					updateBuilder.update(it);
				} else {
					inventoryTransaction.add(transaction);
				}
			}

			InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
					.module(module).fields(fields).addRecords(inventoryTransaction);
			readingBuilder.save();
		}
		return false;
	}

}
