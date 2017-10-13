package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.WorkorderTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddWorkorderTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		WorkorderTemplate workorderTemplate = new WorkorderTemplate();
		JSONObject content = FieldUtil.getPropertiesAsJson(workorder);
		workorderTemplate.setContent(content.toString());
		
		workorderTemplate.setName(workorder.getSubject());
		workorderTemplate.setType(UserTemplate.Type.WORKORDER);

		long templateId = TemplateAPI.addWorkorderTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), workorderTemplate);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
