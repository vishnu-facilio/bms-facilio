package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

;

public class AddBulkToolStockTransactionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
			CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
			Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

			Map<String, FacilioField> toolsFieldMap = FieldFactory.getAsMap(Toolfields);
			List<LookupField> lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) toolsFieldMap.get("toolType"));

			List<V3ToolTransactionContext> toolTransaction = new ArrayList<>();

			List<V3ToolContext> tools = (List<V3ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			Map<Long, List<V3PurchasedToolContext>> toolVsPurchaseTool = (Map<Long, List<V3PurchasedToolContext>>) context
					.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
			if (tools != null && !tools.isEmpty()) {
				for (V3ToolContext tool : tools) {
					V3ToolTransactionContext transaction = new V3ToolTransactionContext();
					if (tool.getToolType().isRotating()) {
						List<V3PurchasedToolContext> purchasedTools = toolVsPurchaseTool.get(tool.getId());
						if (purchasedTools != null && !purchasedTools.isEmpty()) {
							for (V3PurchasedToolContext purchaseTool : purchasedTools) {
								transaction.setQuantity(1.0);
								transaction.setTransactionState(TransactionState.ADDITION.getValue());
								transaction.setTool(tool);
								transaction.setParentId(tool.getId());
								transaction.setIsReturnable(false);
								transaction.setTransactionType(TransactionType.STOCK.getValue());
								transaction.setToolType(tool.getToolType());
								transaction.setPurchasedTool(purchaseTool);
								transaction.setStoreRoom(tool.getStoreRoom());
								transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
								CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(transaction, baseCurrency, currencyMap, purchaseTool.getCurrencyCode(), purchaseTool.getExchangeRate());
								toolTransaction.add(transaction);
							}
						}
					} else {
						if (tool.getQuantity() > 0) {
							CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(transaction, baseCurrency, currencyMap, tool.getCurrencyCode(), tool.getExchangeRate());
							transaction.setQuantity(tool.getQuantity());
							transaction.setTransactionState(TransactionState.ADDITION.getValue());
							transaction.setTool(tool);
							transaction.setParentId(tool.getId());
							transaction.setIsReturnable(false);
							transaction.setTransactionType(TransactionType.STOCK.getValue());
							transaction.setToolType(tool.getToolType());
							transaction.setStoreRoom(tool.getStoreRoom());
							transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
							toolTransaction.add(transaction);
						}
					}
				}
			}
			V3Util.createRecordList(module, FieldUtil.getAsMapList(toolTransaction,V3ToolTransactionContext.class),null,null);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
		}
		return false;
	}

	private List<V3PurchasedToolContext> getPurchasedTools(long toolId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule ptmodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> ptfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		Map<String, FacilioField> ptoolsFieldMap = FieldFactory.getAsMap(ptfields);

		SelectRecordsBuilder<V3PurchasedToolContext> toolselectBuilder = new SelectRecordsBuilder<V3PurchasedToolContext>()
				.select(ptfields).table(ptmodule.getTableName()).moduleName(ptmodule.getName())
				.beanClass(V3PurchasedToolContext.class).andCondition(CriteriaAPI.getCondition(ptoolsFieldMap.get("tool"),
						String.valueOf(toolId), NumberOperators.EQUALS));
		List<V3PurchasedToolContext> purchasedTools = toolselectBuilder.get();
		if (purchasedTools != null && !purchasedTools.isEmpty()) {
			return purchasedTools;
		}
		return null;
	}

}
