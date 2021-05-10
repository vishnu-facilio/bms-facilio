package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;

public class AddActivityInRelatedModuleFroEmailConversationThreadingCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(SendEmailForEmailConversationThreadingCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		
		List<FacilioModule> subModules = modBean.getSubModules(emailConversations.get(0).getDataModuleId(), FacilioModule.ModuleType.ACTIVITY);
		
		if(subModules != null && !subModules.isEmpty()) {
			
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,subModules.get(0).getName());
		}
		else {
			return false;
		}
		
		for(EmailConversationThreadingContext emailConversation :emailConversations) {

			CommonCommandUtil.addActivityToContext(emailConversation.getRecordId(), -1, emailConversation.getMessageTypeEnum().getActivityType(), new JSONObject(), (FacilioContext) context);
		}
		
		// TODO Auto-generated method stub
		return false;
	}

}
