package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

public class GetPMWorkOrders extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(pm != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField pmField = modBean.getField("pm", FacilioConstants.ContextNames.WORK_ORDER);
			SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		.beanClass(WorkOrderContext.class)
																		.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																		.andCondition(CriteriaAPI.getCondition(pmField, String.valueOf(pm.getId()), PickListOperators.IS))
																		.orderBy("WorkOrders.CREATED_TIME DESC")
																		;

			JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
			int perPage;
			int page;
			if (pagination != null) {
				page = (int) pagination.get("page");
				perPage = (int) pagination.get("perPage");
			} else {
				page = 1;
				perPage = 50;
			}

			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			selectBuilder.offset(offset);

			selectBuilder.limit(perPage);


			pm.setWorkorders(selectBuilder.get());
		}
		return false;
	}

}
