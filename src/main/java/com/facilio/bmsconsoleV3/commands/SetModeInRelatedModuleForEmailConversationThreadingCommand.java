package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext.Email_Status_Type;
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

public class SetModeInRelatedModuleForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		
		Map<Long,List<Map<Long,Email_Status_Type>>> moduleVsRecordList = new HashMap<Long, List<Map<Long,Email_Status_Type>>>(); 
		
		Map<String,Object> props = new HashMap<String, Object>();

		for(EmailConversationThreadingContext emailConversation : emailConversations) {
			
			if(emailConversation.getFromTypeEnum() == EmailConversationThreadingContext.From_Type.ADMIN) {
				if(emailConversation.getMessageTypeEnum() != EmailConversationThreadingContext.Message_Type.PRIVATE_NOTE) {

					List<Map<Long,Email_Status_Type>> recordVsModeList = moduleVsRecordList.getOrDefault(emailConversation.getDataModuleId(), new ArrayList<Map<Long,Email_Status_Type>>());
					recordVsModeList.add(new HashMap() {{
						put(emailConversation.getRecordId(),Email_Status_Type.AGENT_REPLIED);
					}});
				
					moduleVsRecordList.put(emailConversation.getDataModuleId(), recordVsModeList);
			 }
			}
			else if(emailConversation.getFromTypeEnum() == EmailConversationThreadingContext.From_Type.CLIENT) {
				if(emailConversation.getMessageTypeEnum() != EmailConversationThreadingContext.Message_Type.PRIVATE_NOTE) {

					List<Map<Long,Email_Status_Type>> recordVsModeList = moduleVsRecordList.getOrDefault(emailConversation.getDataModuleId(), new ArrayList<Map<Long,Email_Status_Type>>());
					recordVsModeList.add(new HashMap() {{
						put(emailConversation.getRecordId(),Email_Status_Type.CUSTOMER_REPLIED);
					}});
				
					moduleVsRecordList.put(emailConversation.getDataModuleId(), recordVsModeList);
			 }
			}	
		}

		for(Map.Entry<Long,List<Map<Long,Email_Status_Type>>> moduleVsRecordMap : moduleVsRecordList.entrySet()) {
			
			FacilioModule recordModule = modBean.getModule(moduleVsRecordMap.getKey());
			FacilioField mode = modBean.getField("mode", recordModule.getName());
			if(mode != null) {
				for(Map<Long,Email_Status_Type> recordVsMode : moduleVsRecordMap.getValue()){
					for (Map.Entry<Long, Email_Status_Type> recordVsModeMap : recordVsMode.entrySet()) {
							UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
									.module(recordModule)
									.fields(modBean.getAllFields(recordModule.getName()))
									.andCondition(CriteriaAPI.getIdCondition(recordVsModeMap.getKey(), recordModule));		
													
								props.put("mode",recordVsModeMap.getValue().getTypeId());
							
							update.updateViaMap(props);
						}
					}
				}
			}
		return false;
	}
}
		