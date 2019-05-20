package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

public class ToolQuantityRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<? extends ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (toolTransactions != null && !toolTransactions.isEmpty()) {
			// temp check, to be changed
			if (toolTransactions.get(0) instanceof ToolTransactionContext) {
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
				List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

				Set<Long> uniqueToolIds = new HashSet<Long>();
				int totalQuantityConsumed = 0;

				for (ToolTransactionContext consumable : toolTransactions) {
					if(consumable.getTransactionStateEnum() != TransactionState.USE) {
					   uniqueToolIds.add(consumable.getTool().getId());
					}
				}

				// List<Long> toolIds = (List<Long>)
				// context.get(FacilioConstants.ContextNames.TOOL_IDS);
				long toolTypeId = -1;
				List<Long> toolTypesIds = new ArrayList<>();
				if (uniqueToolIds != null && !uniqueToolIds.isEmpty()) {
					for (long stId : uniqueToolIds) {
						SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
								.select(toolFields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(stId, module));

						List<ToolContext> tools = selectBuilder.get();
						ToolContext tool = new ToolContext();
						if (tools != null && !tools.isEmpty()) {
							tool = tools.get(0);
							toolTypeId = tool.getToolType().getId();
							tool.setQuantity(getTotalQuantity(stId));
							double availableQty = getTotalQuantityConsumed(stId);
							tool.setCurrentQuantity(availableQty);
							toolTypesIds.add(tool.getToolType().getId());
						}

						UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
								.module(module).fields(modBean.getAllFields(module.getName()))
								.andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));

						updateBuilder.update(tool);
						context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
					}
				}
				context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
			}
		}
		return false;
	}

	public static double getTotalQuantityConsumed(long toolId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(toolTransactionsModule.getTableName())
				.andCustomWhere(
						toolTransactionsModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(toolTransactionsModule),
						String.valueOf(toolTransactionsModule.getModuleId()), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3, -1);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		fields.add(FieldFactory.getField("issues", "sum(case WHEN TRANSACTION_STATE = 2 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		fields.add(FieldFactory.getField("returns", "sum(case WHEN TRANSACTION_STATE = 3 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(toolTransactionFieldMap.get("tool"), String.valueOf(toolId),
				PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0, issues = 0, returns = 0;
			addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
			issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			return ((addition + returns) - issues);
		}
		return 0d;
	}

	public static double getTotalQuantity(long toolId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(toolTransactionsModule.getTableName())
				.andCustomWhere(
						toolTransactionsModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(toolTransactionsModule),
						String.valueOf(toolTransactionsModule.getModuleId()), NumberOperators.EQUALS));

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(toolTransactionFieldMap.get("tool"), String.valueOf(toolId),
				PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0;
			addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
			return (addition);
		}
		return 0d;
	}
}
