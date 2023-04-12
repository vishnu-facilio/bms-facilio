package com.facilio.bmsconsole.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.bean.UserBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMUserException;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Command;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.chargebee.Environment;
import com.chargebee.ListResult;
import com.chargebee.Result;
import com.chargebee.filters.enums.SortOrder;
import com.chargebee.models.Card;
import com.chargebee.models.Customer;
import com.chargebee.models.Subscription;
import com.chargebee.models.enums.Gateway;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class UserAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(UserAction.class.getName());
	private boolean emailVerificationNeeded;
	
	public boolean isEmailVerificationNeeded() {
		return emailVerificationNeeded;
	}

	public void setEmailVerificationNeeded(boolean emailVerificationNeeded) {
		this.emailVerificationNeeded = emailVerificationNeeded;
	}

	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<User> users = null;
	public List<User> getUsers() {
		return users;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	private long groupId = -1;
	public long getGroupId() {
		return this.groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private Boolean isPortal = false;

	public Boolean getIsPortal() {
		return isPortal;
	}

	public void setIsPortal(Boolean portal) {
		this.isPortal = portal;
	}

	public boolean isPortal() {
		if (isPortal != null) {
			return isPortal.booleanValue();
		}
		return false;
	}

	private String appDomain;
	
	public String getAppDomain() {
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(appDomain)) {
			return appDomain;
		}
		return AccountUtil.getDefaultAppDomain();
	}

	private String appLinkName;

	public String getAppLinkName() {
		return appLinkName;
	}

	public void setAppLinkName(String appLinkName) {
		this.appLinkName = appLinkName;
	}

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	private String filters;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}


	public void setAppDomain(String appDomain) {
		this.appDomain = appDomain;
	}
	public String userPickList() throws Exception {
		if (getGroupId() > 0) {
			List<GroupMember> memberList = AccountUtil.getGroupBean().getGroupMembers(getGroupId());
			this.users = new ArrayList<>();
			if (memberList != null) {
				this.users.addAll(memberList);
			}
		} else {
			setUsers(AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true));
		}
		return SUCCESS;
	}

	public String userVerify() throws Exception{
	//	CommonCommandUtil.verifiedUser(getUserId());
		AccountUtil.getUserBean().verifyUser(getUserId());
		return SUCCESS;
	}
	public String userList() throws Exception {
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true));
		
		return SUCCESS;
	}
	
	public String v2userList() throws Exception {
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true));
		setResult("users", getUsers());
		return SUCCESS;
	}

	public String portalUserList() throws Exception {
		setSetup(SetupLayout.getUsersListLayout());
		//now showing only service portal user listing here
		long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		if(appId > 0) {
			setUsers(AccountUtil.getOrgBean().getOrgPortalUsers(AccountUtil.getCurrentOrg().getOrgId(), appId));
			return SUCCESS;
		}
		return ERROR;

	}

	public String allPortalUserList() throws Exception {
		FacilioContext context = new FacilioContext();
		if(getFilters() != null)
		{
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());

		}

		FacilioChain chain = FacilioChainFactory.getPortalUsersListChain();
        context.put(FacilioConstants.ContextNames.STATUS,false);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "users");
		chain.execute(context);

		setSetup(SetupLayout.getUsersListLayout());
		setUsers((List<User>) context.get(FacilioConstants.ContextNames.USERS));
		return SUCCESS;
	}
	

	public String deletePortalUser() throws Exception {
		System.out.println("### Delete portal user :"+user.getEmail());
			HttpServletRequest request = ServletActionContext.getRequest(); 
		long appId = -1;
		try {
			appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
			if(appId > 0) {
				AppDomain servicePortalDomain = ApplicationApi.getAppDomainForApplication(appId);
				User orgUser = AccountUtil.getUserBean().getUser(user.getEmail(), servicePortalDomain.getIdentifier()) ;
				if(ApplicationApi.deleteUserFromApp(orgUser, appId) > 0) {
					setUserId(orgUser.getOuid());
				    return SUCCESS;
				}
			}
			
			return ERROR;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String userAgent() throws Exception{
		System.out.println("********UserAgentAnalyzer************");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		AccountUtil.getRoleBean(orgId).getRolesForApps(Collections.singletonList(app.getId()));
		// AccountUtil.getUserBean().updateUserLicense(2, 4);
		Role admin = AccountUtil.getRoleBean().getRole(orgId, "Administrator", false);
		System.out.println(admin.getId());
		
		return SUCCESS;
	}

	public String deleteUser() throws Exception {
		FacilioChain deleteUser = TransactionChainFactory.deleteUserChain();
		deleteUser.getContext().put(FacilioConstants.ContextNames.USER, user);
		deleteUser.getContext().put(FacilioConstants.ContextNames.USER_OPERATION, "deleting user");
		deleteUser.execute();
		boolean result = (boolean) deleteUser.getContext().getOrDefault(FacilioConstants.ContextNames.RESULT, false);
		if(result) {
	    	setUserId(user.getOuid());
	    	return SUCCESS;
	    }
		return ERROR;
	}
	
	private Map<Long, String> roles;
	public Map<Long, String> getRoles() {
		return roles;
	}
	public void setRoles(Map<Long, String> roles) {
		this.roles = roles;
	}
	
	public String newUser() throws Exception {
		
		setSetup(SetupLayout.getNewUserLayout());
//		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	private long appId;
	
	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String inviteRequester() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest(); 
		
		boolean isEmailEmpty = (user.getEmail() == null ||  user.getEmail().isEmpty());
		boolean isMobileEmpty = (user.getMobile() == null || user.getMobile().isEmpty());
		if(isMobileEmpty && isEmailEmpty ) {
			addFieldError("error", "Please enter a valid mobile number or email");
			return ERROR;
		}

		Organization org = AccountUtil.getCurrentOrg();
		user.setOrgId(org.getOrgId());
		if(isEmailEmpty) {
			user.setEmail(user.getMobile());
		}

		user.setUserType(UserType.REQUESTER.getValue());
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
		
		try {
			AppDomain appDomain = null;
			if(getAppId() <= 0) {
				appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
			}
			appDomain = ApplicationApi.getAppDomainForApplication(getAppId());
			user.setApplicationId(getAppId());
			user.setAppDomain(appDomain);
			
			if(AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getId(), user, isEmailVerificationNeeded(), true, appDomain.getIdentifier(), true, false) > 0) {
				setUserId(user.getId());
			}
			else {
				return ERROR;
			}
		}
		catch (Exception e) {
			if (e.getCause() != null && e.getCause() instanceof AccountException) {
				AccountException ae = (AccountException) e.getCause();
				if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS) || ae.getErrorCode().equals(AccountException.ErrorCode.USER_ALREADY_EXISTS_IN_APP)) {
					addFieldError("error", "This user already exists in this app of your organization.");
					return ERROR;
				}
			}
			log.info("Exception occurred ", e);
			addFieldError("error", e.getMessage());
			
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addUser() throws Exception {

		boolean isEmailEmpty = (user.getEmail() == null ||  user.getEmail().isEmpty());
		boolean isMobileEmpty = (user.getMobile() == null || user.getMobile().isEmpty());
		if(isEmailEmpty && isMobileEmpty) {
			addFieldError("error", "Please enter a valid mobile number or email");
			return ERROR;
		}

		if(user.getRoleId() <=0 ) {
			addFieldError("error", "Please specify a role for this user");
			return ERROR;
		}

		Organization org = AccountUtil.getCurrentOrg();
		user.setOrgId(org.getOrgId());
		if(isEmailEmpty) {
			user.setEmail(user.getMobile());
		}

		HttpServletRequest request = ServletActionContext.getRequest();
		
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


		FacilioContext context = new FacilioContext();
		//v2 authentication
		if( (AccountUtil.getCurrentOrg() != null) && (user.getTimezone() == null) ) {
			user.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
		}
		if( (AccountUtil.getCurrentUser() != null) && (user.getLanguage() == null) ) {
			user.setLanguage(AccountUtil.getCurrentUser().getLanguage());
		}
		user.setUserType(UserType.USER.getValue());
		context.put(FacilioConstants.ContextNames.USER, user);
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		context.put(FacilioConstants.ContextNames.IS_EMAIL_VERIFICATION_NEEDED, emailVerificationNeeded);
		context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, accessibleSpace);
		
		try {
				context.put(FacilioConstants.ContextNames.USER, user);
				FacilioChain addUser = FacilioChainFactory.getAddUserCommand();
				addUser.execute(context);
		}
		catch (Exception e) {
			if (e.getCause() != null && e.getCause() instanceof AccountException) {
				AccountException ae = (AccountException) e.getCause();
				if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS)) {
					throw new IllegalArgumentException("This user already exists in the app of your organization.");
				}
				else if (ae.getErrorCode().equals(AccountException.ErrorCode.NOT_PERMITTED)) {
					throw new IllegalArgumentException("Not Permitted to do this operation");
				}
				else if (ae.getErrorCode().equals(AccountException.ErrorCode.USER_ALREADY_EXISTS_IN_APP)) {
					throw new IllegalArgumentException("This user already exists in the app of your organization.");
				} else if (ae.getErrorCode().equals(AccountException.ErrorCode.DUPLICATE_USER)) {
					throw new IllegalArgumentException("User already exists in app.");
				}
			} else if (e.getCause() != null && e.getCause() instanceof IAMUserException) {
				IAMUserException ame = (IAMUserException) e.getCause();
				if(ame.getErrorCode().equals(IAMUserException.ErrorCode.USERNAME_HAS_WHITESPACE)){
					throw new IllegalArgumentException(ame.getMessage());
				}
			}
			log.info("Exception occurred ", e);
			throw new IllegalArgumentException(e.getMessage());

		}
		setUserId(user.getId());
		return SUCCESS;
	}

	public String resendInvite() throws Exception {
		try {
			AppDomain appDomain = null;
			//temp handling
			if(appId <= 0) {
				String linkname = isPortal ? FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP : SignupUtil.maintenanceAppSignup() ? FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP : FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
				appId = ApplicationApi.getApplicationIdForLinkName(linkname);
			}
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomain != null) {
				if(peopleId >0){
					userId = AccountUtil.getOrgBean().getOrgUserIdForPeople(peopleId,appId);
				}
				User appUser = AccountUtil.getUserBean().getUser(userId, false);
				if(appUser != null) {
					appUser.setAppDomain(appDomain);
					appUser.setApplicationId(appId);
					(new UserBeanImpl()).resendInvite(appUser);
					setUserId(appUser.getId());
					return SUCCESS;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return ERROR;
		
	}
	
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	private List<Long> accessibleSpace;
	public List<Long> getAccessibleSpace() {
		return accessibleSpace;
	}

	public void setAccessibleSpace(List<Long> accessibleSpace) {
		this.accessibleSpace = accessibleSpace;
	}

	private  long userId;

	public long getPeopleId() {
		return peopleId;
	}

	public void setPeopleId(long peopleId) {
		this.peopleId = peopleId;
	}

	private long peopleId;
	public  long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String editUser() throws Exception {
				
		setSetup(SetupLayout.getEditUserLayout());
//		setUser(UserAPI.getUserFromOrgUserId(getUserId()));
//		setRoles(UserAPI.getRolesOfOrgMap(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		return SUCCESS;
	}
	
	private JSONObject content;
	
	public void setContent(JSONObject content) {
		this.content = content;
	}
	
	public JSONObject getContent() {
		return this.content;
	}
	
	private JSONObject subscription;
	
	public void setSubscription(JSONObject subscription) {
		this.subscription = subscription;
	}
	
	public JSONObject getSubscription() {
		return this.subscription;
	}
	
	public String subscriptionInfo() throws Exception
	{
//		HashMap<String, Object> cus = (HashMap<String, Object>) this.getContent().get("customer");
//		String cusEmail = (String) cus.get("email");
		long orgId = AccountUtil.getCurrentOrg().getId();
        try{
			
		String site = FacilioProperties.getConfig("chargebee.site");
		String api =  FacilioProperties.getConfig("chargebee.api");
		
		Environment.configure(site, api);
		
		Map<String, Object> cusid = CommonCommandUtil.getOrgInfo(orgId, "Customer_id");
		// System.out.println("*****************customer  ID *********************" +cusid);
		ListResult result = Subscription.list()
                          .limit(5)
                          .customerId().is((String) cusid.get("value"))
                          //.status().is(Subscription.Status.ACTIVE)
                          .sortByCreatedAt(SortOrder.ASC).request();
    for(ListResult.Entry entry:result){
      Subscription subscription = entry.subscription();
      Customer customer = entry.customer();
      Card card = entry.card();
      
      JSONObject sub = (JSONObject) new JSONParser().parse(subscription.toJson());
      JSONObject cus = (JSONObject) new JSONParser().parse(customer.toJson());
      JSONObject crd = (JSONObject) new JSONParser().parse(card.toJson());
       
      this.subscription = new JSONObject();
      this.subscription.put("subscription", sub);
      this.subscription.put("customer", cus);
      this.subscription.put("card", crd);
    }
   // System.out.println("*****************Subscription Info *********************");
   
    // System.out.println(result);
        }
	catch (Exception e){
		e.printStackTrace();
	}
    return SUCCESS;
	}

	
  private JSONObject card;
  
  public String updateCard() throws Exception{
	  String site = FacilioProperties.getConfig("chargebee.site");
		String api =  FacilioProperties.getConfig("chargebee.api");
		long orgId = AccountUtil.getCurrentOrg().getId();
		Map<String, Object> cusid = CommonCommandUtil.getOrgInfo(orgId, "Customer_id");
    // Environment.configure("payfacilio-test","test_AcdMBlnceZzwYhGeAX6dkxzocvglIkJjL");
		Environment.configure(site, api);
    // System.out.println((String) card.get("firstName"));
    Result result = Card.updateCardForCustomer((String) cusid.get("value"))
                      .gateway(Gateway.CHARGEBEE)
                      .firstName((String) card.get("firstName"))
                      .lastName((String) card.get("lastName"))	
                      .number((String) card.get("cardNumber"))
                      .expiryMonth((Integer.parseInt(card.get("expiryMonth").toString())))
                      .expiryYear((Integer.parseInt(card.get("expiryYear").toString())))
                      .cvv((String) card.get("cvv")).request();
    result.customer();
    result.card();
    // System.out.println(result);
    return null;
  }

            
	
	public String updateMyProfile() throws Exception{
		subscriptionInfo();
		user.setUid(AccountUtil.getCurrentAccount().getUser().getUid());
		AccountUtil.getUserBean().updateUser(user);
		
		return SUCCESS;
	}
	
	public String updateUser() throws Exception {
		FacilioChain updateUser = TransactionChainFactory.updateUserChain();
		updateUser.getContext().put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		updateUser.getContext().put(FacilioConstants.ContextNames.USER, user);
		updateUser.getContext().put(FacilioConstants.ContextNames.USER_OPERATION, "updating user");
		updateUser.execute();
		boolean result = (boolean) updateUser.getContext().getOrDefault(FacilioConstants.ContextNames.RESULT, false);
		if(result) {
			return SUCCESS;
		}
		return ERROR;
	}
	
	private UserMobileSetting userMobileSetting;
	public UserMobileSetting getUserMobileSetting() {
		return userMobileSetting;
	}

	public void setUserMobileSetting(UserMobileSetting userMobileSetting) {
		this.userMobileSetting = userMobileSetting;
	}
	
	public String addMobileSetting() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, userMobileSetting);

		Command addUserMobileSettingCommand = FacilioChainFactory.getAddUserMobileSettingCommand();
		addUserMobileSettingCommand.execute(context);
				
		return SUCCESS;
	}
	public String removeMobileSetting() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, mobileInstanceId);

		Command addUserMobileSettingCommand = FacilioChainFactory.getDeleteUserMobileSettingCommand();
		addUserMobileSettingCommand.execute(context);
				
		return SUCCESS;
	}
	
	private String mobileInstanceId;
	public String getMobileInstanceId() {
		return mobileInstanceId;
	}
	public void setMobileInstanceId(String mobileInstanceId) {
		this.mobileInstanceId = mobileInstanceId;
	}

	public String changeStatus() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		FacilioChain updateStatus = TransactionChainFactory.updateUserStatusChain();
		updateStatus.getContext().put(FacilioConstants.ContextNames.USER, user);
		updateStatus.getContext().put(FacilioConstants.ContextNames.USER_OPERATION, "status change");
		updateStatus.getContext().put(FacilioConstants.ContextNames.USER_STATUS, user.getUserStatus());
		updateStatus.execute();
		boolean result = (boolean) updateStatus.getContext().getOrDefault(FacilioConstants.ContextNames.RESULT, false);
		if (result) {
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, true);
			return SUCCESS;
		}
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, false);
		return ERROR;
	}

	private File avatar;
	
	public File getAvatar() {
		return avatar;
	}
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}
	
	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	
	private String avatarContentType;
	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private String avatarUrl;
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	
	public String uploadUserAvatar() throws Exception {

//			long fileId = fs.addFile(getAvatarFileName(), getAvatar(), getAvatarContentType());

			String url = AccountUtil.getUserBean().updateUserPhoto(AccountUtil.getCurrentAccount().getUser().getUid(), user);
			if(url != null && !url.isEmpty() && !url.equalsIgnoreCase("null")) {
				setAvatarUrl(url);
			}


		
		return SUCCESS;
	}
	
	public String deleteUserAvatar() throws Exception {
		
		long photoId = AccountUtil.getCurrentUser().getPhotoId();
		if (photoId > 0) {
			boolean isDeleted = AccountUtil.getUserBean().deleteUserPhoto(AccountUtil.getCurrentAccount().getUser().getUid(), photoId);
			if(isDeleted){
				return SUCCESS;
			}
		}
		return ERROR;
	}
	
	private String error;
	public String getError() {
		return error;
	}

	public JSONObject getCard() {
		return card;
	}

	public void setCard(JSONObject card) {
		this.card = card;
	}
	
	/******************      V2 Api    ******************/
	
	public String v2addMobileSetting() throws Exception {
		addMobileSetting();
		setResult(FacilioConstants.ContextNames.USER_MOBILE_SETTING, userMobileSetting);
		return SUCCESS;
	}
	
	public String v2removeMobileSetting() throws Exception {
		removeMobileSetting();
		setResult(FacilioConstants.ContextNames.USER_MOBILE_SETTING, userMobileSetting);
		return SUCCESS;
	}

	public String getUserByEmail() throws Exception {
		if(StringUtils.isNotEmpty(email)) {
			if (StringUtils.isEmpty(appLinkName)) {
				appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
			}
			ApplicationContext app = ApplicationApi.getApplicationForLinkName(appLinkName);
			if (app == null) {
				setResult("message", "Invalid app link name");
				return ERROR;
			}
			List<AppDomain> appDomainList = IAMAppUtil.getAppDomainForType(app.getDomainType(), AccountUtil.getCurrentOrg().getOrgId());
			if (CollectionUtils.isNotEmpty(appDomainList)) {
				User user = AccountUtil.getUserBean().getUserFromEmail(email, appDomainList.get(0).getIdentifier(), AccountUtil.getCurrentOrg().getOrgId());
				if (user != null) {
					setResult(FacilioConstants.ContextNames.USER, user);
					return SUCCESS;
				}
			}
		}
		setResult("message", "Invalid user email");
		return ERROR;

	}
}