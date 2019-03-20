package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateToolStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		long toolId = (long) context.get(FacilioConstants.ContextNames.TOOL_ID);
		long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
		List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
		List<Long> toolTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_TYPES_IDS);
		FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

		SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>().select(Toolfields)
				.table(Toolmodule.getTableName()).moduleName(Toolmodule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getIdCondition(toolIds, Toolmodule));

		List<ToolContext> tools = toolselectBuilder.get();
		ToolContext tool = new ToolContext();
		if (tools != null && !tools.isEmpty()) {
			tool = tools.get(0);
		}

		FacilioModule ToolTypemodule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> ToolTypefields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> toolTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(ToolTypefields).table(ToolTypemodule.getTableName()).moduleName(ToolTypemodule.getName())
				.beanClass(ToolTypesContext.class)
				.andCondition(CriteriaAPI.getIdCondition(toolTypeId, ToolTypemodule));

		List<ToolTypesContext> toolTypes = toolTypesselectBuilder.get();
		ToolTypesContext toolType = null;
		if (toolTypes != null && !toolTypes.isEmpty()) {
			toolType = toolTypes.get(0);
		}

		if (toolType == null) {
			throw new IllegalArgumentException("No such tool found");
		}

		List<ToolTransactionContext> toolTransaction = new ArrayList<>();

		if (toolType.individualTracking()) {

			List<PurchasedToolContext> pts = (List<PurchasedToolContext>) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL);

			if (pts != null && !pts.isEmpty()) {
				for (PurchasedToolContext pt : pts) {
					ToolTransactionContext transaction = new ToolTransactionContext();
					transaction.setTransactionState(TransactionState.ADDITION.getValue());
					transaction.setPurchasedTool(pt);
					transaction.setTool(pt.getTool());
					transaction.setQuantity(1);
					transaction.setParentId(pt.getId());
					transaction.setIsReturnable(false);
					transaction.setTransactionType(TransactionType.STOCK.getValue());
					transaction.setToolType(toolType);

					SelectRecordsBuilder<ToolTransactionContext> transactionsselectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
							.select(fields).table(module.getTableName()).moduleName(module.getName())
							.beanClass(ToolTransactionContext.class)
							.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
									String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.VALUE_IS))
							.andCondition(CriteriaAPI.getCondition(fieldMap.get("purchasedTool"), String.valueOf(pt.getId()), PickListOperators.IS));
					List<ToolTransactionContext> transactions = transactionsselectBuilder.get();
					if (transactions != null && !transactions.isEmpty()) {
						ToolTransactionContext it = transactions.get(0);
						it.setQuantity(1);
						UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
								.module(module).fields(modBean.getAllFields(module.getName()))
								.andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
						updateBuilder.update(it);
					} else {
						toolTransaction.add(transaction);
					}
				}
			}
		} else {
			ToolTransactionContext transaction = new ToolTransactionContext();
			transaction.setTransactionState(TransactionState.ADDITION.getValue());
			transaction.setTool(tool);
			transaction.setQuantity(tool.getQuantity());
			transaction.setParentId(tool.getId());
			transaction.setIsReturnable(false);
			transaction.setTransactionType(TransactionType.STOCK.getValue());
			transaction.setToolType(toolType);

			SelectRecordsBuilder<ToolTransactionContext> transactionsselectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
					.select(fields).table(module.getTableName()).moduleName(module.getName())
					.beanClass(ToolTransactionContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
							String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.VALUE_IS));
			List<ToolTransactionContext> transactions = transactionsselectBuilder.get();
			if (transactions != null && !transactions.isEmpty()) {
				ToolTransactionContext it = transactions.get(0);
				it.setQuantity(1);
				UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
						.module(module).fields(modBean.getAllFields(module.getName()))
						.andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
				updateBuilder.update(it);
			} else {
				toolTransaction.add(transaction);
			}
		}
		InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
				.module(module).fields(fields).addRecords(toolTransaction);
		readingBuilder.save();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
		return false;
	}

}
