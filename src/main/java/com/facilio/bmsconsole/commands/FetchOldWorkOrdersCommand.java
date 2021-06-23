package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchOldWorkOrdersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		new ArrayList<>();
		if(workOrder != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
					.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
					.beanClass(WorkOrderContext.class)
					.select(fields)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
					.orderBy("ID");

			List<WorkOrderContext> workOrderContexts = builder.get();
			context.put(FacilioConstants.TicketActivity.OLD_TICKETS, workOrderContexts);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrderContexts);
		}
		
		return false;
	}

}
