package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApproveOrRejectItemCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> itemTransactionsFieldMap = FieldFactory.getAsMap(itemTransactionsFields);

		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID);
		int approvedStateVal = (int) context.get(FacilioConstants.ContextNames.ITEM_TRANSACTION_APPORVED_STATE);
		ApprovalState approvalState = ApprovalState.valueOf(approvedStateVal);
		List<Long> parentIds = new ArrayList<>();
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("item")));
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("purchasedItem")));
		lookUpfields.add(new LookupFieldMeta((LookupField) itemTransactionsFieldMap.get("itemType")));

		SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(itemTransactionsFields).table(itemTransactionsModule.getTableName())
				.moduleName(itemTransactionsModule.getName()).beanClass(ItemTransactionsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, itemTransactionsModule)).fetchLookups(lookUpfields);

		List<ItemTransactionsContext> itemTransactions = selectBuilder.get();
		for (ItemTransactionsContext transactions : itemTransactions) {
			if (approvalState == ApprovalState.APPROVED) {
				if (transactions.getItemType().individualTracking()) {
					if (transactions.getPurchasedItem().isUsed()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						PurchasedItemContext pItem = transactions.getPurchasedItem();
						pItem.setIsUsed(true);
						updatePurchasedItem(pItem);
					}
				} else {
					if (transactions.getPurchasedItem().getCurrentQuantity() < transactions.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					}
				}
				transactions.setRemainingQuantity(transactions.getQuantity());
			} else if (approvalState == ApprovalState.REJECTED) {
				if (transactions.getItemType().individualTracking()) {
					PurchasedItemContext pItem = transactions.getPurchasedItem();
					pItem.setIsUsed(false);
					updatePurchasedItem(pItem);
				}
				transactions.setRemainingQuantity(0);
			}
			transactions.setApprovedState(approvedStateVal);
			updateWorkorderItems(itemTransactionsModule, itemTransactionsFields, transactions);

			parentIds.add(transactions.getParentId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		return false;
	}

	private void updateWorkorderItems(FacilioModule module, List<FacilioField> fields,
			ItemTransactionsContext transaction) throws Exception {

		UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(transaction.getId(), module));
		updateBuilder.update(transaction);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

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
