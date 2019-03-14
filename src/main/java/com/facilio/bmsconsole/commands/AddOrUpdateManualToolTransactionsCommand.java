package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateManualToolTransactionsCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionsFieldsMap = FieldFactory.getAsMap(toolTransactionsFields);
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldsMap.get("tool")));
		List<ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<ToolTransactionContext> toolTransactionslist = new ArrayList<>();
		List<ToolTransactionContext> toolTransactionsToBeAdded = new ArrayList<>();
		long toolTypesId = -1;
		if (toolTransactions != null) {
			for (ToolTransactionContext toolTransaction : toolTransactions) {
				ToolContext tool = getStockedTools(toolTransaction.getTool().getId());
				ToolTypesContext toolTypes = getToolType(tool.getToolType().getId());
				toolTypesId = toolTypes.getId();
				if (toolTransaction.getId() > 0) {
					SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
							.select(toolTransactionsFields).table(toolTransactionsModule.getTableName())
							.moduleName(toolTransactionsModule.getName()).beanClass(ToolTransactionContext.class)
							.andCondition(CriteriaAPI.getIdCondition(toolTransaction.getId(), toolTransactionsModule))
							.fetchLookups(lookUpfields);
							;
					List<ToolTransactionContext> woIt = selectBuilder.get();
					if (woIt != null) {
						ToolTransactionContext wTool = woIt.get(0);
						if (toolTransaction.getTransactionStateEnum() == TransactionState.ISSUE && (wTool.getQuantity() + wTool.getTool().getCurrentQuantity()) < toolTransaction
								.getQuantity()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						} else {
							wTool = setWorkorderItemObj(null, toolTransaction.getQuantity(), tool, toolTransaction,toolTypes);
							// update
							wTool.setId(toolTransaction.getId());
							toolTransactionslist.add(wTool);
							updateWorkorderTools(toolTransactionsModule, toolTransactionsFields, wTool);
						}
					}
				} else {
					if (toolTransaction.getTransactionStateEnum() == TransactionState.ISSUE && tool.getCurrentQuantity() < toolTransaction.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						if (toolTypes.individualTracking()) {
							List<Long> purchasedToolIds = (List<Long>) context
									.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
							List<PurchasedToolContext> purchasedTool = getPurchasedToolsListFromId(purchasedToolIds);
							if (purchasedTool != null) {
								for (PurchasedToolContext pTool : purchasedTool) {
									ToolTransactionContext woTool = new ToolTransactionContext();
									if(toolTransaction.getTransactionStateEnum() == TransactionState.RETURN){
										pTool.setIsUsed(false);
									} else if (toolTransaction.getTransactionStateEnum() == TransactionState.ISSUE) {
										pTool.setIsUsed(true);
									}
									woTool = setWorkorderItemObj(pTool, 1, tool, toolTransaction,toolTypes);
									updatePurchasedTool(pTool);
									toolTransactionslist.add(woTool);
									toolTransactionsToBeAdded.add(woTool);
								}
							}
						} else {
							ToolTransactionContext woTool = new ToolTransactionContext();
							woTool = setWorkorderItemObj(null, toolTransaction.getQuantity(), tool, toolTransaction,toolTypes);
							toolTransactionslist.add(woTool);
							toolTransactionsToBeAdded.add(woTool);
						}
					}
				}
			}
			if (toolTransactionsToBeAdded != null && !toolTransactionsToBeAdded.isEmpty()) {
				addWorkorderTools(toolTransactionsModule, toolTransactionsFields, toolTransactionsToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, toolTransactions.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.TOOL_ID, toolTransactions.get(0).getTool().getId());
			context.put(FacilioConstants.ContextNames.TOOL_IDS,
					Collections.singletonList(toolTransactions.get(0).getTool().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactionslist);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypesId);
		}

		return false;
	}

	private ToolTransactionContext setWorkorderItemObj(PurchasedToolContext purchasedtool, double quantity,
			ToolContext tool, ToolTransactionContext toolTransaction, ToolTypesContext toolTypes) {
		ToolTransactionContext woTool = new ToolTransactionContext();
		
		woTool.setTransactionType(TransactionType.MANUAL);
		woTool.setTransactionState(toolTransaction.getTransactionStateEnum());
		woTool.setIsReturnable(true);
		if (purchasedtool != null) {
			woTool.setPurchasedTool(purchasedtool);
		}
		woTool.setQuantity(quantity);
		woTool.setTool(tool);
		woTool.setToolType(toolTypes);
		woTool.setSysModifiedTime(System.currentTimeMillis());
		woTool.setParentId(toolTransaction.getParentId());
		woTool.setParentTransactionId(toolTransaction.getParentTransactionId());
		
		return woTool;
	}

	public static ToolContext getStockedTools(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id);

		List<ToolContext> stockedTools = selectBuilder.get();

		if (stockedTools != null && !stockedTools.isEmpty()) {
			return stockedTools.get(0);
		}
		return null;
	}

	private void addWorkorderTools(FacilioModule module, List<FacilioField> fields, List<ToolTransactionContext> tools)
			throws Exception {
		InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
				.module(module).fields(fields).addRecords(tools);
		readingBuilder.save();
	}

	private void updateWorkorderTools(FacilioModule module, List<FacilioField> fields, ToolTransactionContext tool)
			throws Exception {

		UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	public static int getEstimatedWorkDuration(long issueTime, long returnTime) {
		long duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}
		int hours = (int) ((duration / (1000 * 60 * 60)));
		return hours;
	}

	public static ToolTypesContext getToolType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
				.beanClass(ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, itemTypesModule));

		List<ToolTypesContext> toolTypes = itemTypesselectBuilder.get();
		if (toolTypes != null && !toolTypes.isEmpty()) {
			return toolTypes.get(0);
		}
		return null;
	}

	public static List<PurchasedToolContext> getPurchasedToolsListFromId(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<PurchasedToolContext> purchasedToolList = selectBuilder.get();

		if (purchasedToolList != null && !purchasedToolList.isEmpty()) {
			return purchasedToolList;
		}
		return null;
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
}