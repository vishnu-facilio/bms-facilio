package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

;

public class ToolQuantityRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<? extends ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		List<Long> toolIds = (List<Long>) context
				.get(FacilioConstants.ContextNames.TOOL_IDS);
		
		if (toolTransactions != null && !toolTransactions.isEmpty()) {
			// temp check, to be changed
			if (toolTransactions.get(0) instanceof ToolTransactionContext) {
				
				Set<Long> uniqueToolIds = new HashSet<Long>();
				int totalQuantityConsumed = 0;

				for (ToolTransactionContext consumable : toolTransactions) {
					if(consumable.getTransactionStateEnum() != TransactionState.USE || consumable.getParentTransactionId() <= 0) {
					   uniqueToolIds.add(consumable.getTool().getId());
					}
				}

				// List<Long> toolIds = (List<Long>)
				// context.get(FacilioConstants.ContextNames.TOOL_IDS);
				constructToolAndTypeIds(uniqueToolIds,context);
			}
		}
		else if(CollectionUtils.isNotEmpty(toolIds)) {
			Set<Long> uniqueToolIds = new HashSet<Long>();
			for (Long id : toolIds) {
				uniqueToolIds.add(id);
			}
			constructToolAndTypeIds(uniqueToolIds,context);
		}
		return false;
	}
	
	public static void constructToolAndTypeIds(Set<Long> uniqueToolIds, Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

		
		long toolTypeId = -1;
		List<Long> toolTypesIds = new ArrayList<>();
		if (uniqueToolIds != null && !uniqueToolIds.isEmpty()) {
			List<ToolContext> toolRecords = new ArrayList<ToolContext>();
            Map<Long, List<UpdateChangeSet>> changes = new HashMap<Long, List<UpdateChangeSet>>();
			
			for (long stId : uniqueToolIds) {
				Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(toolFields);
				List<LookupField>lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) fieldsAsMap.get("toolType"));

				SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
						.select(toolFields).table(module.getTableName()).moduleName(module.getName())
						.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(stId, module))
						.fetchLookups(lookUpfields)
						;

				List<ToolContext> tools = selectBuilder.get();
				ToolContext tool = new ToolContext();
				if (tools != null && !tools.isEmpty()) {
					tool = tools.get(0);
					toolTypeId = tool.getToolType().getId();
					tool.setQuantity(getTotalQuantity(stId));
					double availableQty = 0;
					//if(!tool.getToolType().isRotating()) {
						availableQty = getTotalQuantityConsumed(stId);
					//}
					//else {
					//	availableQty = getRotatingAssetCount(stId);
					//}
					tool.setCurrentQuantity(availableQty);
					toolTypesIds.add(tool.getToolType().getId());
					if(tool.getCurrentQuantity() <= tool.getMinimumQuantity()) {
						tool.setIsUnderstocked(true);
					}
					else {
						tool.setIsUnderstocked(false);
					}
				}

				
				UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
						.module(module).fields(modBean.getAllFields(module.getName()))
						.andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
				updateBuilder.withChangeSet(ToolContext.class);
				updateBuilder.update(tool);
				Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
				changes.put(tool.getId(), (List<UpdateChangeSet>)recordChanges.get(tool.getId()));
				toolRecords.add(tool);
            
				
				context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
			}
			Map<String, Map<Long,List<UpdateChangeSet>>> finalChangeMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
			finalChangeMap.put(module.getName(), changes);
			context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(module.getName(), toolRecords));
			context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, finalChangeMap);
	
		}
		context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
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
		fields.add(FieldFactory.getField("used", "sum(case WHEN TRANSACTION_STATE = 4 AND ( PARENT_TRANSACTION_ID <= 0 OR PARENT_TRANSACTION_ID IS NULL ) THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));

		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(toolTransactionFieldMap.get("tool"), String.valueOf(toolId),
				PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0, issues = 0, returns = 0, used = 0;
			addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
			issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
			used = rs.get(0).get("used") != null ? (double) rs.get(0).get("used") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			issues += used;
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

	private static double getRotatingAssetCount(long toolId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

		SelectRecordsBuilder<AssetContext> assetBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(assetFields).table(assetModule.getTableName()).moduleName(assetModule.getName())
				.beanClass(AssetContext.class).
				andCondition(CriteriaAPI.getCondition("ROTATING_TOOL", "rotatingTool", String.valueOf(toolId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_USED", "isUsed", String.valueOf("false"), BooleanOperators.IS))
				;
		return assetBuilder.get().size();
	}
}
