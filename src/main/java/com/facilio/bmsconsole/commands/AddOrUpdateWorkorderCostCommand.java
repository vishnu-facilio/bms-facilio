package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderCostCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		int costType = (int) context.get(FacilioConstants.ContextNames.WORKORDER_COST_TYPE);
		CostType cos = CostType.valueOf(costType);
		FacilioModule workorderPartsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_PARTS);
		List<FacilioField> workorderPartsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_PARTS);
		Map<String, FacilioField> workorderPartsFieldMap = FieldFactory.getAsMap(workorderPartsFields);

		SelectRecordsBuilder<WorkorderPartsContext> selectBuilder = new SelectRecordsBuilder<WorkorderPartsContext>()
				.select(workorderPartsFields).table(workorderPartsModule.getTableName())
				.moduleName(workorderPartsModule.getName()).beanClass(WorkorderPartsContext.class)
				.andCondition(CriteriaAPI.getCondition(workorderPartsFieldMap.get("parentId"), String.valueOf(parentId),
						PickListOperators.IS));

		List<WorkorderPartsContext> workorderParts = selectBuilder.get();
		long cost = 0;
		for (WorkorderPartsContext woParts : workorderParts) {
			cost += woParts.getCost();
		}

		FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
		List<FacilioField> workorderCostsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
		Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

		SelectRecordsBuilder<WorkorderCostContext> workorderCostSetlectBuilder = new SelectRecordsBuilder<WorkorderCostContext>()
				.select(workorderCostsFields).table(workorderCostsModule.getTableName())
				.moduleName(workorderCostsModule.getName()).beanClass(WorkorderCostContext.class)
				.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"), String.valueOf(parentId),
						PickListOperators.IS))
				.andCondition(
						CriteriaAPI.getCondition(workorderCostsFieldMap.get("costType"), String.valueOf(cos), EnumOperators.IS));

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

}
