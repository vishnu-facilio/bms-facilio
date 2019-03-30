package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetWOForPMReminderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PMReminder pmReminder = (PMReminder) context.get(FacilioConstants.ContextNames.PM_REMINDER);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		boolean onlyPost = (boolean) context.get(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE);
		WorkOrderContext wo = null;
		
		if (onlyPost) {
			long woId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			SelectRecordsBuilder<WorkOrderContext> woBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																	.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																	.module(woModule)
																	.beanClass(WorkOrderContext.class)
																	.andCondition(CriteriaAPI.getIdCondition(woId, woModule))
																	;
			
			List<WorkOrderContext> woList = woBuilder.get();
			if(woList != null && !woList.isEmpty()) {
				wo = woList.get(0);
			}
		}
		else {
			long templateId = pm.getTemplateId();
			long currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			wo = template.getWorkorder();
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			wo.setCreatedTime((currentExecutionTime+pmReminder.getDuration())*1000);
		}
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		return false;
	}

}
