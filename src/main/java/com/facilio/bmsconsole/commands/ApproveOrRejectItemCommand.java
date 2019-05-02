package com.facilio.bmsconsole.commands;
import org.json.simple.JSONObject;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.commands.AddOrUpdateWorkorderItemsCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;

public class ApproveOrRejectItemCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds != null && !recordIds.isEmpty()) {
			FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			List<FacilioField> itemTransactionsFields = modBean
					.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			Map<String, FacilioField> itemTransactionsFieldMap = FieldFactory.getAsMap(itemTransactionsFields);

			int approvedStateVal = (int) context.get(FacilioConstants.ContextNames.ITEM_TRANSACTION_APPORVED_STATE);
			ApprovalState approvalState = ApprovalState.valueOf(approvedStateVal);
			List<Long> parentIds = new ArrayList<>();
			List<LookupField> lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) itemTransactionsFieldMap.get("item"));
			lookUpfields.add((LookupField) itemTransactionsFieldMap.get("purchasedItem"));
			lookUpfields.add((LookupField) itemTransactionsFieldMap.get("asset"));
			lookUpfields.add((LookupField) itemTransactionsFieldMap.get("itemType"));

			SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
					.select(itemTransactionsFields).table(itemTransactionsModule.getTableName())
					.moduleName(itemTransactionsModule.getName()).beanClass(ItemTransactionsContext.class)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, itemTransactionsModule))
					.fetchLookups(lookUpfields);

			List<ItemTransactionsContext> itemTransactions = selectBuilder.get();
			List<Object> woitemactivity = new ArrayList<>();
			for (ItemTransactionsContext transactions : itemTransactions) {
				ItemContext item = AddOrUpdateWorkorderItemsCommand.getItem(transactions.getItem().getId());
				Long itemTypesId = item.getItemType().getId();
				ItemTypesContext itemType = AddOrUpdateWorkorderItemsCommand.getItemType(itemTypesId);
				JSONObject info = new JSONObject();
				info.put("itemid", item.getId());
				info.put("itemtype", itemType.getName());
				info.put("quantity", transactions.getQuantity());
				if (approvalState == ApprovalState.APPROVED) {
					if (transactions.getItemType().isRotating()) {
						if (transactions.getAsset().isUsed()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						} else {
							AssetContext asset = transactions.getAsset();
							asset.setIsUsed(true);
							updateAsset(asset);
							info.put("serialno", asset.getSerialNumber());
							info.put("issuedToId", transactions.getParentId());
							woitemactivity.add(info);
						}
					} else {
						if (transactions.getPurchasedItem().getCurrentQuantity() < transactions.getQuantity()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						}
						else {
							info.put("issuedToId", transactions.getParentId());
							woitemactivity.add(info);
						}
					}
					transactions.setRemainingQuantity(transactions.getQuantity());
				} else if (approvalState == ApprovalState.REJECTED) {
					if (transactions.getItemType().isRotating()) {
						AssetContext asset = transactions.getAsset();
						asset.setIsUsed(false);
						updateAsset(asset);
						info.put("serialno", asset.getSerialNumber());
					}
					woitemactivity.add(info);
					transactions.setRemainingQuantity(0);
				}
				transactions.setApprovedState(approvedStateVal);
				updateWorkorderItems(itemTransactionsModule, itemTransactionsFields, transactions);

				if (transactions.getTransactionTypeEnum() == TransactionType.WORKORDER) {
					parentIds.add(transactions.getParentId());
				}
				
				JSONObject newinfo = new JSONObject();
				if (approvalState == ApprovalState.REJECTED) {
					newinfo.put("rejected", woitemactivity);
					CommonCommandUtil.addActivityToContext(itemTypesId, -1, ItemActivityType.ITEM_REJECTED, newinfo, (FacilioContext) context);
				}
				else if (approvalState == ApprovalState.APPROVED) {
					newinfo.put("approved", woitemactivity);
					CommonCommandUtil.addActivityToContext(itemTypesId, -1, ItemActivityType.ITEM_APPROVED, newinfo, (FacilioContext) context);
				}
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		}
		return false;
	}

	private void updateWorkorderItems(FacilioModule module, List<FacilioField> fields,
			ItemTransactionsContext transaction) throws Exception {

		UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(transaction.getId(), module));
		updateBuilder.update(transaction);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	private void updateAsset(AssetContext asset) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
		updateBuilder.update(asset);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
