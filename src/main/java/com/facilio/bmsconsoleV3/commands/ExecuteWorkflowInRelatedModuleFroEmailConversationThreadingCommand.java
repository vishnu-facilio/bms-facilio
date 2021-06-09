package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext.Message_Type;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;

public class ExecuteWorkflowInRelatedModuleFroEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		EmailConversationThreadingContext emailConversation = (EmailConversationThreadingContext) Constants.getRecordList((FacilioContext) context).get(0);
		
		ModuleBaseWithCustomFields parentRecord = V3RecordAPI.getRecord(emailConversation.getDataModuleId(), emailConversation.getRecordId());
		
		parentRecord.setDatum("currentReply", emailConversation);
		
		FacilioChain commonExecuteWorkflowChain = TransactionChainFactory.getCommonExecuteWorkflowChain();
		
		FacilioContext newContext = commonExecuteWorkflowChain.getContext();
		
		EventType eventType = getEventType(context, emailConversation); 
		
		newContext.put(FacilioConstants.ContextNames.MODULE_NAME,modBean.getModule(emailConversation.getDataModuleId()).getName());
		newContext.put(FacilioConstants.ContextNames.RECORD,parentRecord);
		newContext.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
		
		commonExecuteWorkflowChain.execute();
		
		return false;
	}

	private EventType getEventType(Context context,EmailConversationThreadingContext emailConversation) {
		// TODO Auto-generated method stub
		
		EventType returnEventType = null;
		EventType conversationEventype = Constants.getEventType(context);
		
		if(conversationEventype == EventType.CREATE) {
			if(emailConversation.getMessageTypeEnum() == Message_Type.REPLY) {
				returnEventType = EventType.EMAIL_CONVERSATION_ON_REPLY_RECIEVED;
			}
			else if(emailConversation.getMessageTypeEnum() == Message_Type.PRIVATE_NOTE || emailConversation.getMessageTypeEnum() == Message_Type.PUBLIC_NOTE) {
				returnEventType = EventType.EMAIL_CONVERSATION_ON_NOTE_ADDITION;
			}
		}
		else if (conversationEventype == EventType.EDIT) {
			if(emailConversation.getMessageTypeEnum() == Message_Type.PRIVATE_NOTE || emailConversation.getMessageTypeEnum() == Message_Type.PUBLIC_NOTE) {
				returnEventType = EventType.EMAIL_CONVERSATION_ON_NOTE_UPDATION;
			}
		}
		else if (conversationEventype == EventType.DELETE) {
			if(emailConversation.getMessageTypeEnum() == Message_Type.PRIVATE_NOTE || emailConversation.getMessageTypeEnum() == Message_Type.PUBLIC_NOTE) {
				returnEventType = EventType.EMAIL_CONVERSATION_ON_NOTE_DELETION;
			}
		}
		
		return returnEventType;
	}

}
