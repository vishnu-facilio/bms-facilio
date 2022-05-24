package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class DisableIsNewInRelatedModuleForEmailConversationThreadingCommand extends FacilioCommand {

	private static final String EMAIL_CONVERSATION_IS_NEW_FIELD_NAME = "emailConversationIsNewRecord";  
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		
		Map<Long,List<Long>> moduleVsRecordList = new HashMap<Long, List<Long>>(); 
		
		
		for(EmailConversationThreadingContext emailConversation : emailConversations) {
			
			if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.ADMIN.getIndex()) {
				if(emailConversation.getMessageTypeEnum() != EmailConversationThreadingContext.Message_Type.PRIVATE_NOTE) {
					
					List<Long> recordIds = moduleVsRecordList.getOrDefault(emailConversation.getDataModuleId(), new ArrayList<Long>());
					recordIds.add(emailConversation.getRecordId());
					
					moduleVsRecordList.put(emailConversation.getDataModuleId(), recordIds);
				}
			}
			
		}

		for(Long moduleId : moduleVsRecordList.keySet()) {
			
			FacilioModule recordModule = modBean.getModule(moduleId);
			FacilioField isNewfield = modBean.getField(EMAIL_CONVERSATION_IS_NEW_FIELD_NAME, recordModule.getName());
			if(isNewfield != null) {
				UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
						.module(recordModule)
						.fields(modBean.getAllFields(recordModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(moduleVsRecordList.get(moduleId), recordModule));
				
				Map<String,Object> props = new HashMap<String, Object>();
				
				props.put(EMAIL_CONVERSATION_IS_NEW_FIELD_NAME, Boolean.FALSE);
				
				update.updateViaMap(props);
			}
		}
		return false;
	}
	
}
		