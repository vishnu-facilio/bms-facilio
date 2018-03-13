package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWOForPMReminderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PMReminder pmReminder = (PMReminder) context.get(FacilioConstants.ContextNames.PM_REMINDER);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		ReminderType type = (ReminderType) context.get(FacilioConstants.ContextNames.PM_REMINDER_TYPE);
		WorkOrderContext wo = null;
		
		switch (type) {
			case BEFORE:
				long templateId = pm.getTemplateId();
				long currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
				JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(templateId);
				JSONObject content = template.getTemplate(null);
				wo = FieldUtil.getAsBeanFromJson((JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
				wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
				wo.setCreatedTime((currentExecutionTime+pmReminder.getDuration())*1000);
				break;
			case AFTER:
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
				break;
		}
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		return false;
	}

}
