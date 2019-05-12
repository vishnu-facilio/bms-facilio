package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApproveOrRejectToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds != null && !recordIds.isEmpty()) {
			GatePassContext record = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
			FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
			List<FacilioField> toolTransactionsFields = modBean
					.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
			Map<String, FacilioField> toolTransactionsFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

			int approvedStateVal = (int) context.get(FacilioConstants.ContextNames.TOOL_TRANSACTION_APPORVED_STATE);
			ApprovalState approvalState = ApprovalState.valueOf(approvedStateVal);
			List<Long> parentIds = new ArrayList<>();
			List<LookupField> lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) toolTransactionsFieldMap.get("tool"));
			lookUpfields.add((LookupField) toolTransactionsFieldMap.get("asset"));
			lookUpfields.add((LookupField) toolTransactionsFieldMap.get("toolType"));

			SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
					.select(toolTransactionsFields).table(toolTransactionsModule.getTableName())
					.moduleName(toolTransactionsModule.getName()).beanClass(ToolTransactionContext.class)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, toolTransactionsModule))
					.fetchLookups(lookUpfields);

			List<ToolTransactionContext> toolTransactions = selectBuilder.get();
			List<GatePassLineItemsContext> gatePassLineItems = new ArrayList<>();
			for (ToolTransactionContext transactions : toolTransactions) {
				if (approvalState == ApprovalState.APPROVED) {
					if (transactions.getToolType().isRotating()) {
						if (transactions.getAsset().isUsed()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						} else {
							AssetContext asset = transactions.getAsset();
							asset.setIsUsed(true);
							updateAsset(asset);
						}
					} else {
						if (transactions.getTool().getCurrentQuantity() < transactions.getQuantity()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						}
					}
					transactions.setRemainingQuantity(transactions.getQuantity());
				}

				else if (approvalState == ApprovalState.REJECTED) {
					if (transactions.getToolType().isRotating()) {
						AssetContext asset = transactions.getAsset();
						asset.setIsUsed(false);
						updateAsset(asset);
					}
					transactions.setRemainingQuantity(0);
				}

				transactions.setApprovedState(approvedStateVal);
				if (record != null) {
					if (record instanceof GatePassContext) {
						transactions.setGatePass(record);
					}
				}
				updateWorkorderTools(toolTransactionsModule, toolTransactionsFields, transactions);
				if (transactions.getTransactionTypeEnum() == TransactionType.WORKORDER) {
					parentIds.add(transactions.getParentId());
				}
				String serialNumber = null;
				if (transactions.getAsset() != null) {
					serialNumber = transactions.getAsset().getSerialNumber();
				}
				GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
				if (gatePassContext != null) {
					context.put(FacilioConstants.ContextNames.GATE_PASS, gatePassContext);
				}
				gatePassLineItems.add(new GatePassLineItemsContext(InventoryType.TOOL, null, transactions.getToolType(),
						transactions.getQuantity(), serialNumber));
			}
			context.put(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS, gatePassLineItems);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactions);
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		}
		return false;
	}

	private void updateAsset(AssetContext asset) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
		updateBuilder.update(asset);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");
	}

	private void updateWorkorderTools(FacilioModule module, List<FacilioField> fields,
			ToolTransactionContext transaction) throws Exception {

		UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(transaction.getId(), module));
		updateBuilder.update(transaction);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}
}