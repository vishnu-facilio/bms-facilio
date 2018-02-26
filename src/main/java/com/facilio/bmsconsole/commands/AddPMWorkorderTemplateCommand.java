package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class AddPMWorkorderTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, List<TaskContext>> tasks = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		JSONTemplate workorderTemplate = (JSONTemplate) context.get(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE);
		if(workorderTemplate == null)
		{
			workorderTemplate = new JSONTemplate();
			workorderTemplate.setName(workorder.getSubject());
		}
		
		JSONObject content = new JSONObject();
		content.put(FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsJSON(workorder));
		if(tasks != null) {
			content.put(FacilioConstants.ContextNames.TASK_MAP, FieldUtil.getAsJSON(tasks));
		}
		workorderTemplate.setContent(content.toJSONString());

		long templateId = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), workorderTemplate);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
