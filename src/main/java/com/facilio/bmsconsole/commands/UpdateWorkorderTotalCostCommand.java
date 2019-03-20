package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateWorkorderTotalCostCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
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

			FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> workorderFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);

			SelectRecordsBuilder<WorkOrderContext> workorderSetlectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
					.select(workorderFields).table(workorderModule.getTableName()).moduleName(workorderModule.getName())
					.beanClass(WorkOrderContext.class)
					.andCondition(CriteriaAPI.getIdCondition(parentId, workorderModule));

			List<WorkOrderContext> workorderList = workorderSetlectBuilder.get();
			WorkOrderContext workorder = new WorkOrderContext();
			if (workorderList != null && !workorderList.isEmpty()) {
				workorder = workorderList.get(0);
				workorder.setTotalCost(totalcost);
			}

			UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
					.module(workorderModule).fields(modBean.getAllFields(workorderModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(workorder.getId(), workorderModule));

			updateBuilder.update(workorder);

			System.out.println("totalcost" + totalcost);
		}
		return false;
	}

}
