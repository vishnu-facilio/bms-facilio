package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

import io.jsonwebtoken.lang.Collections;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateWorkorderTotalCostCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		// long parentId = (long)
		// context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds != null && !parentIds.isEmpty()) {
			for (long parentId : parentIds) {
				FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
				List<FacilioField> workorderCostsFields = modBean
						.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
				Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

				SelectRecordsBuilder<WorkorderCostContext> workorderCostsSetlectBuilder = new SelectRecordsBuilder<WorkorderCostContext>()
						.select(workorderCostsFields).table(workorderCostsModule.getTableName())
						.moduleName(workorderCostsModule.getName()).beanClass(WorkorderCostContext.class)
						.andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
								String.valueOf(parentId), PickListOperators.IS));

				List<WorkorderCostContext> workorderCostsList = workorderCostsSetlectBuilder.get();
				long totalcost = 0;
				for (WorkorderCostContext wo : workorderCostsList) {
					totalcost += wo.getCost();
				}

				WorkOrderContext workorder = new WorkOrderContext();
				workorder.setId(parentId);
				workorder.setTotalCost(totalcost);

				context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, parentIds);
				context.put(FacilioConstants.ContextNames.RECORD_LIST, java.util.Collections.singletonList(workorder));
				
				Chain c = TransactionChainFactory.getUpdateWorkOrderChain();
				c.execute(context);
					
				context.put(FacilioConstants.ContextNames.WO_TOTAL_COST, totalcost);
				System.out.println("totalcost" + totalcost);
			}
		}
		return false;
	}

}
