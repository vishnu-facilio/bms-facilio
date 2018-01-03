package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;

public class AddWorkorderTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		
		JSONTemplate workorderTemplate = new JSONTemplate();
		JSONObject content = new JSONObject();
		content.put(FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsJSON(workorder));
		if(tasks != null) {
			content.put(FacilioConstants.ContextNames.TASK_LIST, FieldUtil.getAsJSONArray(tasks, TaskContext.class));
		}
		workorderTemplate.setContent(content.toJSONString());
		
		workorderTemplate.setName(workorder.getSubject());

		long templateId = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), workorderTemplate);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
