package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
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
import com.facilio.bmsconsole.criteria.EnumOperators;
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

public class AddBulkToolStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

		List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
		FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		

		List<ToolTransactionContext> toolTransaction = new ArrayList<>();
		
		SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>().select(Toolfields)
				.table(Toolmodule.getTableName()).moduleName(Toolmodule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getIdCondition(toolIds, Toolmodule));

		List<ToolContext> tools = (List<ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (tools != null && !tools.isEmpty()) {
			for(ToolContext tool : tools) {
				ToolTransactionContext transaction = new ToolTransactionContext();
				transaction.setTransactionState(TransactionState.ADDITION.getValue());
				transaction.setTool(tool);
				transaction.setQuantity(tool.getQuantity());
				transaction.setParentId(tool.getId());
				transaction.setIsReturnable(false);
				transaction.setTransactionType(TransactionType.STOCK.getValue());
				transaction.setToolType(tool.getToolType());

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
