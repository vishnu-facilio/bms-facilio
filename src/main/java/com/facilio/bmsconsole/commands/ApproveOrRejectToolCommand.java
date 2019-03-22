package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ApproveOrRejectToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionsFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID);
		int approvedStateVal = (int) context.get(FacilioConstants.ContextNames.TOOL_TRANSACTION_APPORVED_STATE);
		ApprovalState approvalState = ApprovalState.valueOf(approvedStateVal);
		List<Long> parentIds = new ArrayList<>();
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("tool")));
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("purchasedTool")));
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("toolType")));

		SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
				.select(toolTransactionsFields).table(toolTransactionsModule.getTableName())
				.moduleName(toolTransactionsModule.getName()).beanClass(ToolTransactionContext.class)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, toolTransactionsModule)).fetchLookups(lookUpfields);

		List<ToolTransactionContext> toolTransactions = selectBuilder.get();
		for (ToolTransactionContext transactions : toolTransactions) {
			if (approvalState == ApprovalState.APPROVED) {
				if (transactions.getToolType().individualTracking()) {
					if (transactions.getPurchasedTool().isUsed()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						PurchasedToolContext pTool = transactions.getPurchasedTool();
						pTool.setIsUsed(true);
						updatePurchasedTool(pTool);
					}
				} else {
					if (transactions.getTool().getCurrentQuantity() < transactions.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					}
				}
			}
			transactions.setApprovedState(approvedStateVal);
			updateWorkorderTools(toolTransactionsModule, toolTransactionsFields, transactions);
			parentIds.add(transactions.getParentId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactions);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		return false;
	}

	private void updatePurchasedTool(PurchasedToolContext purchasedTool) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		UpdateRecordBuilder<PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedTool.getId(), module));
		updateBuilder.update(purchasedTool);

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