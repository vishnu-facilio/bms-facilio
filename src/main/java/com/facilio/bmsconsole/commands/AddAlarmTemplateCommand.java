package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddAlarmTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		
		AlarmTemplate alarmTemplate = new AlarmTemplate();
		JSONObject content = FieldUtil.getAsJSON(alarm);
		alarmTemplate.setContent(content.toString());
		
		alarmTemplate.setName(alarm.getSubject());
		alarmTemplate.setType(UserTemplate.Type.ALARM);

		long templateId = TemplateAPI.addAlarmTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), alarmTemplate);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
