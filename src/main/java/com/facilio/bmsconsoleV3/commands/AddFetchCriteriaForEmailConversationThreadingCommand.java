package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddFetchCriteriaForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		if(currentApp != null && !currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
			
			Criteria criteria = new Criteria();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    	
	    	List<FacilioField> selectFields = new ArrayList<FacilioField>();
	    	
	    	selectFields.addAll(modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
	    	
	    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
	    	
	    	criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("messageType"), EmailConversationThreadingContext.Message_Type.PRIVATE_NOTE.getIndex()+"", NumberOperators.NOT_EQUALS));
			
			context.put(Constants.BEFORE_FETCH_CRITERIA, criteria);
		}
		
		return false;
	}

}
