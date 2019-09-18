package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateWorkorderTotalCostCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
				double totalcost = 0;
				for (WorkorderCostContext wo : workorderCostsList) {
					totalcost += wo.getCost();
				}

				WorkOrderContext workorder = new WorkOrderContext();
				workorder.setId(parentId);
				workorder.setTotalCost(totalcost);

				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
				context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, parentIds);
				context.put(FacilioConstants.ContextNames.RECORD_LIST, java.util.Collections.singletonList(workorder));
				
				FacilioChain c = TransactionChainFactory.getUpdateWorkOrderChain();
				c.execute(context);
					
				context.put(FacilioConstants.ContextNames.WO_TOTAL_COST, totalcost);
				
			}
		}
		return false;
	}

}
