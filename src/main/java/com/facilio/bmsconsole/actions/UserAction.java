package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
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
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

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

	public String userPickList() throws Exception {
		if (getGroupId() > 0) {
			List<GroupMember> memberList = AccountUtil.getGroupBean().getGroupMembers(getGroupId());
			this.users = new ArrayList<>();
			if (memberList != null) {
				this.users.addAll(memberList);
			}
		} else {
			setUsers(AccountUtil.getOrgBean().getAllOrgUsers(AccountUtil.getCurrentOrg().getOrgId()));
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
		setUsers(AccountUtil.getOrgBean().getAllOrgUsers(AccountUtil.getCurrentOrg().getOrgId()));
		
		return SUCCESS;
	}

	public String portalUserList() throws Exception {
		setSetup(SetupLayout.getUsersListLayout());
		setUsers(AccountUtil.getOrgBean().getOrgPortalUsers(AccountUtil.getCurrentOrg().getOrgId()));
		return SUCCESS;
	}
	

	public String deletePortalUser() throws Exception {
		System.out.println("### Delete portal user :"+user.getEmail());
		try	{
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
			  checkforTenantPrimaryContact(user.getEmail());
			}
			PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(AccountUtil.getCurrentOrg().getOrgId(), false);
			User userTobeDeleted = AccountUtil.getUserBean().getPortalUsers(user.getEmail(), portalInfo.getPortalId());
			if(AccountUtil.getUserBean().deleteUser(userTobeDeleted.getOuid())) {
				setUserId(userTobeDeleted.getOuid());
			    return SUCCESS;
			}
			return ERROR;
		} catch (Exception e) {
			return ERROR;
		}
	}
	
	private void checkforTenantPrimaryContact(String email) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> fields = modBean.getAllFields(tenantModule.getName());
		
		
		User requestorToBeDeleted = AccountUtil.getUserBean().getUser(email);
		
		SelectRecordsBuilder<TenantContext> selectBuilder = new SelectRecordsBuilder<TenantContext>()
												.table(tenantModule.getTableName())
												.module(tenantModule)
												.select(fields)
												.andCondition(CriteriaAPI.getCondition("CONTACT_ID", "contact_id", requestorToBeDeleted.getOuid()+"", NumberOperators.EQUALS))
												.beanClass(TenantContext.class)
												;								
        List<TenantContext> records = selectBuilder.get();
		if(selectBuilder.get().size() > 0) {
			throw new IllegalArgumentException("Deletion not permitted as the requester is a primary contact for the tenant "+records.get(0).getName());
		}
	}
	
	public String userAgent() throws Exception{
		System.out.println("********UserAgentAnalyzer************");
//		UserAgentAnalyzer uaa;
//
//		uaa = UserAgentAnalyzer
//		        .newBuilder()
//		        .withField("DeviceClass")
//		        .withField("AgentNameVersionMajor")
//		        .build();
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		AccountUtil.getRoleBean(orgId).getRoles();
		// AccountUtil.getUserBean().updateUserLicense(2, 4);
		Role admin = AccountUtil.getRoleBean().getRole(orgId, "Administrator", false);
		System.out.println(admin.getId());
//		for ( Role role : roles){
//		AccountUtil.getUserBean().addUserLicense(orgId, role.getRoleId(), 2);
//		}
//		List<Map<String, Object>> sessions = AccountUtil.getUserBean().getUserSessions(AccountUtil.getCurrentUser().getUid(), true);
//		
//		UserAgentAnalyzer uaa = UserAgentAnalyzer
//                .newBuilder()
//                .hideMatcherLoadStats()
//                .withCache(25000)
//                .build();
//		
//		if (sessions != null) {
//			for (Map<String, Object> session : sessions) {
//				UserAgent agent = uaa.parse((String) session.get("userAgent"));
//				
//				for (String fieldName: agent.getAvailableFieldNamesSorted()) {
//			        System.out.println(fieldName + " = " + agent.getValue(fieldName));
//			    }
//			}
//		}
		
			return SUCCESS;
	}

	public String deleteUser() throws Exception {
		
	    if(AccountUtil.getUserBean().deleteUser(user.getOuid())) {
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
	public String inviteRequester() throws Exception {

		boolean isEmailEmpty = (user.getEmail() == null ||  user.getEmail().isEmpty());
		boolean isMobileEmpty = (user.getMobile() == null || user.getMobile().isEmpty());
		if(isMobileEmpty && isEmailEmpty ) {
			addFieldError("error", "Please enter a valid mobile number or email");
			return ERROR;
		}

		Organization org = AccountUtil.getCurrentOrg();
		user.setOrgId(org.getOrgId());
		user.setDomainName(org.getDomain());
		if(isEmailEmpty) {
			user.setEmail(user.getMobile());
		}

		user.setUserType(UserType.REQUESTER.getValue());
		//if(emailVerificationNeeded) {
			user.setUserVerified(false);
			user.setInviteAcceptStatus(false);
			user.setInvitedTime(System.currentTimeMillis());
//		}
//		else {
//			user.setUserVerified(true);
//			user.setInviteAcceptStatus(true);
//			user.setInvitedTime(System.currentTimeMillis());
//		}
		
		try {
			if(AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getId(), user, true) > 0) {
				setUserId(user.getId());
			}
			else {
				return ERROR;
			}
		}
		catch (Exception e) {
			if (e instanceof AccountException) {
				AccountException ae = (AccountException) e;
				if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS)) {
					addFieldError("error", "This user already exists in your organization.");
				}
			}
			else {
				log.info("Exception occurred ", e);
				addFieldError("error", "This user already exists in your organization.");
			}
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
		String value = FacilioCookie.getUserCookie(request, "fc.authtype");
		user.setFacilioAuth("facilio".equals(value));
		user.setDomainName("app");
		
		//if(emailVerificationNeeded) {
			user.setUserVerified(false);
			user.setInviteAcceptStatus(false);
			user.setInvitedTime(System.currentTimeMillis());
//		}
//		else {
//			user.setUserVerified(true);
//			user.setInviteAcceptStatus(true);
//			user.setInvitedTime(System.currentTimeMillis());
//
//		}


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
		context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, accessibleSpace);
		
		try {
				context.put(FacilioConstants.ContextNames.USER, user);
				Chain addUser = FacilioChainFactory.getAddUserCommand();
				addUser.execute(context);
		}
		catch (Exception e) {
			if (e instanceof AccountException) {
				AccountException ae = (AccountException) e;
				if (ae.getErrorCode().equals(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS)) {
					addFieldError("error", "This user already exists in your organization.");
				}
				if (ae.getErrorCode().equals(AccountException.ErrorCode.NOT_PERMITTED)) {
					addFieldError("error", "Not Permitted to do this operation");
				}
			} else {
				log.info("Exception occurred ", e);
				addFieldError("error", "This user already exists in your organization.");
			}
			return ERROR;
		}
		setUserId(user.getId());
		return SUCCESS;
	}

	
	public String resendInvite() throws Exception {
		ServletActionContext.getRequest().getParameter("portal");
		
		user = AccountUtil.getUserBean().getUser(getUserId());
		System.out.println("##########userOrgid   :"+user.getOrgId());
		if(user.getUserType() == AccountConstants.UserType.REQUESTER.getValue())
		{
			// requestore
			PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(AccountUtil.getCurrentOrg().getOrgId(), false);
			long portalid = portalInfo.getPortalId();
			user.setPortalId(portalid);
			(new UserBeanImpl()).resendInvite(user.getOuid());
		}
		else
		{
			long orgId=user.getOrgId();
			// normal user 
			AccountUtil.getTransactionalUserBean(orgId).resendInvite(getUserId());
		}
		return SUCCESS;
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
			
		String site = AwsUtil.getConfig("chargebee.site");
		String api =  AwsUtil.getConfig("chargebee.api"); 
		
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
	  String site = AwsUtil.getConfig("chargebee.site");
		String api =  AwsUtil.getConfig("chargebee.api"); 
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
		AccountUtil.getUserBean().updateUser(user);
		
		return SUCCESS;
	}
	
	public String updateUser() throws Exception {
		if(AccountUtil.getUserBean().updateUser(user)) {
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
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER, user);
		if(user.getUserStatus()) {
			if(AccountUtil.getUserBean().enableUser(user.getOuid())) {
				return SUCCESS;
			}
		}
		else {
			if(AccountUtil.getUserBean().disableUser(user.getOuid())) {
				return SUCCESS;
			}
		}
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
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(getAvatarFileName(), getAvatar(), getAvatarContentType());
		
		AccountUtil.getUserBean().updateUserPhoto(userId, fileId);
		
		setAvatarUrl(fs.getPrivateUrl(fileId));
		
		return SUCCESS;
	}
	
	public String deleteUserAvatar() throws Exception {
		
		long photoId = AccountUtil.getCurrentUser().getPhotoId();
		if (photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.deleteFile(photoId);
		}
		
		return SUCCESS;
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
}