package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.modules.FieldUtil;

public class FacilioUserModuleFunctions extends FacilioModuleFunctionImpl {

	
	@Override
	public void add(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		Object insertObject = objects.get(1);
		
		List<User> users = new ArrayList<>();
		
		if(insertObject instanceof Map) {
			
			User user = FieldUtil.getAsBeanFromMap(((Map<String, Object>) insertObject), User.class);
			users.add(user);
		}
		else if (insertObject instanceof Collection) {
			List<Map<String,Object>> insertList = (List<Map<String,Object>>)insertObject;
			
			users = FieldUtil.getAsBeanListFromMapList(insertList, User.class);
		}
		
		for(User user :users) {
			boolean isEmailEmpty = (user.getEmail() == null ||  user.getEmail().isEmpty());
			boolean isMobileEmpty = (user.getMobile() == null || user.getMobile().isEmpty());
			if(isEmailEmpty && isMobileEmpty) {
				throw new Exception ("Please enter a valid mobile number or email");
			}

			if(user.getRoleId() <=0 ) {
				throw new Exception ("Please specify a role for this user");
			}

			Organization org = AccountUtil.getCurrentOrg();
			user.setOrgId(org.getOrgId());
			if(isEmailEmpty) {
				user.setEmail(user.getMobile());
			}

			HttpServletRequest request = ServletActionContext.getRequest();
			String value = FacilioCookie.getUserCookie(request, "fc.authtype");
			user.setFacilioAuth("facilio".equals(value));
			user.setDomainName("app");
			
			boolean emailVerificationNeeded = true;
			
			if(emailVerificationNeeded) {
				user.setUserVerified(false);
				user.setInviteAcceptStatus(false);
				user.setInvitedTime(System.currentTimeMillis());
			}
			else {
				user.setUserVerified(true);
				user.setInviteAcceptStatus(true);
				user.setInvitedTime(System.currentTimeMillis());

			}
			
			FacilioChain addUser = FacilioChainFactory.getAddUserCommand();

			FacilioContext context = addUser.getContext();
			//v2 authentication
			if( (AccountUtil.getCurrentOrg() != null) && (user.getTimezone() == null) ) {
				user.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
			}
			if( (AccountUtil.getCurrentUser() != null) && (user.getLanguage() == null) ) {
				user.setLanguage(AccountUtil.getCurrentUser().getLanguage());
			}
			user.setUserType(UserType.USER.getValue());
			context.put(FacilioConstants.ContextNames.USER, user);
			
			try {
					context.put(FacilioConstants.ContextNames.USER, user);
					addUser.execute();
			}
			catch (Exception e) {
				if (e instanceof AccountException) {
					AccountException ae = (AccountException) e;
					if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS)) {
						throw new Exception ("This user already exists in your organization.");
					}
					if (ae.getErrorCode().equals(AccountException.ErrorCode.NOT_PERMITTED)) {
						throw new Exception ("Not Permitted to do this operation");
					}
				} else {
					throw new Exception ("This user already exists in your organization.");
				}
			}
		}
	}
}
