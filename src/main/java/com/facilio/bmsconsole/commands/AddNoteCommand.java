package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddNoteCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note != null)
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				String ticketModule = (String) context.get(FacilioConstants.ContextNames.TICKET_MODULE);
				if (ticketModule == null || ticketModule.isEmpty()) {
					throw new IllegalArgumentException("Module name for ticket notes should be specified");
				}
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			note.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			note.setCreatedTime(System.currentTimeMillis());
			note.setCreatedBy(AccountUtil.getCurrentUser());
			InsertRecordBuilder<NoteContext> noteBuilder = new InsertRecordBuilder<NoteContext>()
																	.module(module)
																	.fields(fields)
																	;
			
			note.setId(noteBuilder.insert(note));
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES))
			{
				context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ADD_TICKET_NOTE);
				if(note.getNotifyRequester())
				{
					JSONObject mailJson = new JSONObject();
					mailJson.put("sender", "support@thingscient.com");
					mailJson.put("to", "shivaraj@thingscient.com");
					mailJson.put("subject", "New note added by ");
					mailJson.put("message", note.getBody());
					AwsUtil.sendEmail(mailJson);
				}
			}
		}
		return false;
	}
}
