package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;

public class AddSuplimentsForEmailBaseMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<SupplementRecord> supplements = MailMessageUtil.getMailMessageSupliments();
		
		if(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME.equals(Constants.getModuleName(context))) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
			
			supplements.add((LookupField)fieldsAsMap.get("fromPeople"));
		}
	    context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
		
		return false;
	}

}
