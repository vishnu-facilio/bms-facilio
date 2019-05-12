package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionState;
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

public class AddBulkToolStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
		if (toolIds != null && !toolIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

			FacilioModule ptmodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
			List<FacilioField> ptfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
			Map<String, FacilioField> ptoolsFieldMap = FieldFactory.getAsMap(ptfields);
			List<LookupField> ptlookUpfields = new ArrayList<>();
			ptlookUpfields.add((LookupField) ptoolsFieldMap.get("toolType"));

			FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
			List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
			Map<String, FacilioField> toolsFieldMap = FieldFactory.getAsMap(Toolfields);
			List<LookupField> lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) toolsFieldMap.get("toolType"));

			List<ToolTransactionContext> toolTransaction = new ArrayList<>();

			SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>()
					.select(Toolfields).table(Toolmodule.getTableName()).moduleName(Toolmodule.getName())
					.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(toolIds, Toolmodule))
					.fetchLookups(lookUpfields);

			List<ToolContext> tools = (List<ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			Map<Long, List<PurchasedToolContext>> toolVsPurchaseTool = (Map<Long, List<PurchasedToolContext>>) context
					.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
			if (tools != null && !tools.isEmpty()) {
				for (ToolContext tool : tools) {
					ToolTransactionContext transaction = new ToolTransactionContext();
					if (tool.getToolType().isRotating()) {
						List<PurchasedToolContext> purchasedTools = toolVsPurchaseTool.get(tool.getId());
						if (purchasedTools != null && !purchasedTools.isEmpty()) {
							for (PurchasedToolContext purchaseTool : purchasedTools) {
								transaction.setQuantity(1);
								transaction.setTransactionState(TransactionState.ADDITION.getValue());
								transaction.setTool(tool);
								transaction.setParentId(tool.getId());
								transaction.setIsReturnable(false);
								transaction.setTransactionType(TransactionType.STOCK.getValue());
								transaction.setToolType(tool.getToolType());
								transaction.setPurchasedTool(purchaseTool);
								transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
								toolTransaction.add(transaction);
							}
						}
					} else {
						transaction.setQuantity(tool.getQuantity());
						transaction.setTransactionState(TransactionState.ADDITION.getValue());
						transaction.setTool(tool);
						transaction.setParentId(tool.getId());
						transaction.setIsReturnable(false);
						transaction.setTransactionType(TransactionType.STOCK.getValue());
						transaction.setToolType(tool.getToolType());
						transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
						toolTransaction.add(transaction);
					}
				}
			}

			InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
					.module(module).fields(fields).addRecords(toolTransaction);
			readingBuilder.save();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
		}
		return false;
	}

	private List<PurchasedToolContext> getPurchasedTools(long toolId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule ptmodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> ptfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		Map<String, FacilioField> ptoolsFieldMap = FieldFactory.getAsMap(ptfields);

		SelectRecordsBuilder<PurchasedToolContext> toolselectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
				.select(ptfields).table(ptmodule.getTableName()).moduleName(ptmodule.getName())
				.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getCondition(ptoolsFieldMap.get("tool"),
						String.valueOf(toolId), NumberOperators.EQUALS));
		List<PurchasedToolContext> purchasedTools = toolselectBuilder.get();
		if (purchasedTools != null && !purchasedTools.isEmpty()) {
			return purchasedTools;
		}
		return null;
	}

}
