package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class AddAlarmTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		
		JSONTemplate alarmTemplate = new JSONTemplate();
		JSONObject content = FieldUtil.getAsJSON(alarm);
		alarmTemplate.setContent(content.toString());
		
		alarmTemplate.setName(alarm.getSubject());

		long templateId = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), alarmTemplate);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
