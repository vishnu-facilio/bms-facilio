package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.UserBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;

public class SetUserAndPeopleForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		if(emailConversations != null && !emailConversations.isEmpty()) {
			for(EmailConversationThreadingContext emailConversation : emailConversations) {
				
				if(emailConversation.getFromPeople() != null) {
					long userID = PeopleAPI.getUserIdForPeople(emailConversation.getFromPeople().getId());
					if(userID > 0) {
						emailConversation.setDatum("fromUser", userBean.getUser(userID, false));
					}
				}
			}
		}
		return false;
	}

}
