package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class AddOrUpdateWorkorderCostCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		int costType = (int) context.get(FacilioConstants.ContextNames.WORKORDER_COST_TYPE);
		CostType cos = CostType.valueOf(costType);
		double cost = 0;

		if (costType == 1) {
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
			List<FacilioField> fields = modBean
					.getAllFields(FacilioConstants.ContextNames.WORKORDER_ITEMS);
			Map<String, FacilioField> fieldsMap = FieldFactory
					.getAsMap(fields);
			cost = getTotalCost(parentId, module, fieldsMap);
		}
		else if(costType == 2) {
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
			List<FacilioField> fields = modBean
					.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
			Map<String, FacilioField> fieldsMap = FieldFactory
					.getAsMap(fields);
			cost = getTotalCost(parentId, module, fieldsMap);
		}
		
		FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
		List<FacilioField> workorderCostsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
		Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

		SelectRecordsBuilder<WorkorderCostContext> workorderCostSetlectBuilder = new SelectRecordsBuilder<WorkorderCostContext>()
				.select(workorderCostsFields).table(workorderCostsModule.getTableName())
				.moduleName(workorderCostsModule.getName()).beanClass(WorkorderCostContext.class)
				.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"), String.valueOf(parentId),
						PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("costType"), String.valueOf(cos),
						EnumOperators.IS));

		List<WorkorderCostContext> workorderCosts = workorderCostSetlectBuilder.get();
		WorkorderCostContext workorderCost = new WorkorderCostContext();
		if (workorderCosts != null && !workorderCosts.isEmpty()) {
			workorderCost = workorderCosts.get(0);
			workorderCost.setCost(cost);
			workorderCost.setModifiedTime(System.currentTimeMillis());
			UpdateRecordBuilder<WorkorderCostContext> updateBuilder = new UpdateRecordBuilder<WorkorderCostContext>()
					.module(workorderCostsModule).fields(modBean.getAllFields(workorderCostsModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(workorderCost.getId(), workorderCostsModule));
			updateBuilder.update(workorderCost);

		} else {
			workorderCost.setCost(cost);
			workorderCost.setParentId(parentId);
			workorderCost.setCostType(costType);
			workorderCost.setTtime(System.currentTimeMillis());
			workorderCost.setModifiedTime(System.currentTimeMillis());
			InsertRecordBuilder<WorkorderCostContext> builder = new InsertRecordBuilder<WorkorderCostContext>()
					.moduleName(workorderCostsModule.getName()).table(workorderCostsModule.getTableName())
					.fields(modBean.getAllFields(workorderCostsModule.getName()));

			builder.insert(workorderCost);

		}
		context.put(FacilioConstants.ContextNames.RECORD, workorderCost);

		return false;
	}
	
	
	public static double getTotalCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {

		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.andCustomWhere(module.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

		builder.select(field);

		builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module),
				String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		
		builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS));
		

		List<Map<String, Object>> rs = builder.get();
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
}
