package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteItemTransactionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ItemTransactionsContext> deletedItemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (deletedItemTransactions != null && !deletedItemTransactions.isEmpty()) {
			for (ItemTransactionsContext itemTransaction : deletedItemTransactions) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
				List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);

				Map<String, FacilioField> itemFieldsMap = FieldFactory.getAsMap(itemFields);
				List<LookupField>lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) itemFieldsMap.get("itemType"));

				SelectRecordsBuilder<ItemContext> itemSelectBuilder = new SelectRecordsBuilder<ItemContext>()
						.select(itemFields).table(itemModule.getTableName()).moduleName(itemModule.getName())
						.beanClass(ItemContext.class).andCustomWhere(itemModule.getTableName() + ".ID = ?",
								itemTransaction.getItem().getId())
						.fetchLookups(lookUpfields);

				List<ItemContext> items = itemSelectBuilder.get();
				if (items != null && !items.isEmpty()) {
					ItemContext item = items.get(0);
					if (item.getItemType().individualTracking()) {
						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
						List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);

						SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
								.select(fields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(PurchasedItemContext.class).andCustomWhere(module.getTableName() + ".ID = ?",
										itemTransaction.getPurchasedItem().getId());

						List<PurchasedItemContext> purchasedItems = selectBuilder.get();

						if (purchasedItems != null && !purchasedItems.isEmpty()) {
							PurchasedItemContext purchasedItem = purchasedItems.get(0);
							purchasedItem.setIsUsed(false);
							updatePurchasedItem(purchasedItem);
						}
					}
				}

			}
		}

		return false;
	}

	private void updatePurchasedItem(PurchasedItemContext purchasedItem) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		UpdateRecordBuilder<PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
		updateBuilder.update(purchasedItem);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
