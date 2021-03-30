package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class FetchEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		List<Long> recordIds = Constants.getRecordIds(context);
		
		FacilioModule module = modBean.getModule(moduleName);
		
		List<FacilioField> emailConversationFields = modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		
		Map<String, FacilioField> emailConversationFieldMap = FieldFactory.getAsMap(emailConversationFields);
		
		SelectRecordsBuilder<EmailConversationThreadingContext> select = new SelectRecordsBuilder<EmailConversationThreadingContext>()
				.select(emailConversationFields)
				.moduleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(emailConversationFieldMap.get("recordId"), recordIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(emailConversationFieldMap.get("dataModuleId"), module.getModuleId()+"", NumberOperators.EQUALS))
				;
		
		List<EmailConversationThreadingContext> conversations = select.get();
		
		// add this conversation in service request module data;
		
		return false;
	}

}
