package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetWorkorderCostListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			
			SelectRecordsBuilder<WorkorderCostContext> selectBuilder = new SelectRecordsBuilder<WorkorderCostContext>()
					.select(fields).table(module.getTableName())
					.moduleName(module.getName()).beanClass(WorkorderCostContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS));
			
			List<WorkorderCostContext> workorderCosts = selectBuilder.get();
			double totalCost = 0;
			if(workorderCosts!=null && !workorderCosts.isEmpty()) {
				for(WorkorderCostContext woCosts : workorderCosts) {
					woCosts.setCostType(CostType.valueOf(woCosts.getCostType()));
					totalCost += woCosts.getCost();
				}
			}
			context.put(FacilioConstants.ContextNames.WORKORDER_COST, workorderCosts);
			context.put(FacilioConstants.ContextNames.WO_TOTAL_COST, totalCost);
		}
		return false;
	}

}
