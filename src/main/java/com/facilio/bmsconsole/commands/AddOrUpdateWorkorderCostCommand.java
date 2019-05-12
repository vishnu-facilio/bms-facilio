package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.EnumOperators;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateWorkorderCostCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		// long parentId = (long)
		// context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds != null && !parentIds.isEmpty()) {
			for (long parentId : parentIds) {
				int costType = (int) context.get(FacilioConstants.ContextNames.WORKORDER_COST_TYPE);
				CostType cos = CostType.valueOf(costType);
				double cost = 0;
				long qty = 0;
				if (costType == 1) {
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_ITEMS);
					Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
					cost = getTotalItemCost(parentId, module, fieldsMap);
					qty = getTotalNoOfItem(parentId, module, fieldsMap);
				} else if (costType == 2) {
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
					Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
					cost = getTotalToolCost(parentId, module, fieldsMap);
					qty = getTotalNoOfTool(parentId, module, fieldsMap);
				} else if (costType == 3) {
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WO_LABOUR);
					Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
					cost = getTotalLabourCost(parentId, module, fieldsMap);
					qty = getTotalNoOfLabour(parentId, module, fieldsMap);
				}

				FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
				List<FacilioField> workorderCostsFields = modBean
						.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
				Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

				SelectRecordsBuilder<WorkorderCostContext> workorderCostSetlectBuilder = new SelectRecordsBuilder<WorkorderCostContext>()
						.select(workorderCostsFields).table(workorderCostsModule.getTableName())
						.moduleName(workorderCostsModule.getName()).beanClass(WorkorderCostContext.class)
						.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
								String.valueOf(parentId), PickListOperators.IS))
						.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("costType"),
								String.valueOf(cos), EnumOperators.VALUE_IS));

				List<WorkorderCostContext> workorderCosts = workorderCostSetlectBuilder.get();
				WorkorderCostContext workorderCost = new WorkorderCostContext();
				if (workorderCosts != null && !workorderCosts.isEmpty()) {
					workorderCost = workorderCosts.get(0);
					workorderCost.setCost(cost);
					workorderCost.setQuantity(qty);
					workorderCost.setModifiedTime(System.currentTimeMillis());
					UpdateRecordBuilder<WorkorderCostContext> updateBuilder = new UpdateRecordBuilder<WorkorderCostContext>()
							.module(workorderCostsModule).fields(modBean.getAllFields(workorderCostsModule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(workorderCost.getId(), workorderCostsModule));
					updateBuilder.update(workorderCost);
					// }
				} else {
					workorderCost.setCost(cost);
					workorderCost.setParentId(parentId);
					workorderCost.setCostType(costType);
					workorderCost.setQuantity(qty);
					workorderCost.setTtime(System.currentTimeMillis());
					workorderCost.setModifiedTime(System.currentTimeMillis());
					InsertRecordBuilder<WorkorderCostContext> builder = new InsertRecordBuilder<WorkorderCostContext>()
							.moduleName(workorderCostsModule.getName()).table(workorderCostsModule.getTableName())
							.fields(modBean.getAllFields(workorderCostsModule.getName()));

					builder.insert(workorderCost);
				}
				context.put(FacilioConstants.ContextNames.TOTAL_COST, workorderCost.getCost());
				context.put(FacilioConstants.ContextNames.TOTAL_QUANTITY, qty);
				context.put(FacilioConstants.ContextNames.RECORD, workorderCost);
			}
		}
		return false;
	}

	public static double getTotalItemCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<WorkorderItemContext> builder = new SelectRecordsBuilder<WorkorderItemContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			double returnTotal = 0;
			double issueTotal = 0;
			if (rs.get(0).get("totalItemsCost") != null) {
				return (double) rs.get(0).get("totalItemsCost");
			}
			return 0d;
		}
		return 0d;
	}

	public static double getTotalToolCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<WorkorderToolsContext> builder = new SelectRecordsBuilder<WorkorderToolsContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			double returnTotal = 0;
			double issueTotal = 0;
			if (rs.get(0).get("totalItemsCost") != null) {
				return (double) rs.get(0).get("totalItemsCost");
			}
			return 0d;
		}
		return 0d;
	}

	public static double getTotalLabourCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalLabourCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<WorkOrderLabourContext> builder = new SelectRecordsBuilder<WorkOrderLabourContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalLabourCost") != null) {
				return (double) rs.get(0).get("totalLabourCost");
			}
			return 0d;
		}
		return 0d;
	}

	private long getTotalNoOfTool(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
		if (id <= 0) {
			return 0;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalTools", "COUNT(DISTINCT TOOL)", FieldType.NUMBER));

		SelectRecordsBuilder<WorkorderToolsContext> builder = new SelectRecordsBuilder<WorkorderToolsContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalTools") != null) {
				return (long) rs.get(0).get("totalTools");
			}
			return 0;
		}
		return 0;
	}

	private long getTotalNoOfItem(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
		if (id <= 0) {
			return 0;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItems", "COUNT(DISTINCT ITEM_ID)", FieldType.NUMBER));

		SelectRecordsBuilder<WorkorderItemContext> builder = new SelectRecordsBuilder<WorkorderItemContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalItems") != null) {
				return (long) rs.get(0).get("totalItems");
			}
			return 0;
		}
		return 0;
	}
	
	private long getTotalNoOfLabour(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
		if (id <= 0) {
			return 0;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalLabours", "COUNT(DISTINCT LABOUR)", FieldType.NUMBER));

		SelectRecordsBuilder<WorkOrderLabourContext> builder = new SelectRecordsBuilder<WorkOrderLabourContext>()
				.select(field).moduleName(module.getName())
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS)).setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalLabours") != null) {
				return (long) rs.get(0).get("totalLabours");
			}
			return 0;
		}
		return 0;
	}

}
