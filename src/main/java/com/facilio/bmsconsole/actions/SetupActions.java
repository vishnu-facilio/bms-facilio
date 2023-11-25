package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.service.FacilioService;
import com.facilio.services.email.ImapsClient;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.sso.SamlSSOConfig;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.auth.SAMLUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class SetupActions<T> extends ActionSupport implements ServletRequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = LogManager.getLogger(SetupActions.class.getName());

	static
	{
		System.out.println("###############Setup Action Class Loaded#############");
	}
	
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	
	private SetupLayout<T> setup;
	public SetupLayout<T> getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout<T> setup) {
		this.setup = setup;
	}
	
	public String mySettings() throws Exception {
		
		setSetup(SetupLayout.getPersonalSettingsLayout());
		return SUCCESS;
	}

	public String assignmentRules() throws Exception {
		
		setSetup(SetupLayout.getAssignmentRules());
		return SUCCESS;
	}
	public String newAssignmentRules() throws Exception {
		
		setSetup(SetupLayout.getNewAssignmentRules());
		return SUCCESS;
	}
public String importData() throws Exception {
		
		setSetup(SetupLayout.getImportLayout());
		return SUCCESS;
	}
	
	private ServicePortalInfo servicePortal;
	
	public ServicePortalInfo getServicePortal() {
		return servicePortal;
	}

	public void setServicePortal(ServicePortalInfo servicePortal) {
		this.servicePortal = servicePortal;
	}

	
	
	public String orgSettings() throws Exception {
		
		setSetup(SetupLayout.getCompanySettingsLayout());
	
		setOrg(AccountUtil.getCurrentOrg());
		
		return SUCCESS;
	}
	
	
	private Organization org;
	public void setOrg(Organization org) {
		this.org = org;
	}
	
	public Organization getOrg() {
		return this.org;
	}
	
	private File orgPhoto;
	
	public File getOrgPhoto() {
		return orgPhoto;
	}
	public void setOrgPhoto(File orgPhoto) {
		this.orgPhoto = orgPhoto;
	}
	
	private String orgPhotoFileName;

	public String getOrgPhotoFileName() {
		return orgPhotoFileName;
	}

	public void setOrgPhotoFileName(String orgPhotoFileName) {
		this.orgPhotoFileName = orgPhotoFileName;
	}
	
	private String orgPhotoContentType;
	public String getOrgPhotoContentType() {
		return orgPhotoContentType;
	}

	public void setOrgPhotoContentType(String orgPhotoContentType) {
		this.orgPhotoContentType = orgPhotoContentType;
	}	
	
	public String updateOrgSettings() throws Exception {
		org.getName();
		
//		if (getOrgPhoto() != null) {
//			FileStore fs = FacilioFactory.getFileStore();
//			long fileId = fs.addFile(getOrgPhotoFileName(), getOrgPhoto(), getOrgPhotoContentType());
//			org.setLogoId(fileId);
//		}
//		
		AccountUtil.getOrgBean().updateOrg(AccountUtil.getCurrentOrg().getOrgId(), org);
		return SUCCESS;
	}

	@Getter
	@Setter
	private DomainSSO domainSSO;
	
	private AccountSSO sso;
	
	public void setSso(AccountSSO sso) {
		this.sso = sso;
	}
	
	public AccountSSO getSso() {
		return this.sso;
	}
	
	private String spEntityId;
	
	public String getSpEntityId() {
		return spEntityId;
	}

	public void setSpEntityId(String spEntityId) {
		this.spEntityId = spEntityId;
	}
	
	private String spAcsURL;

	public String getSpAcsURL() {
		return spAcsURL;
	}

	public void setSpAcsURL(String spAcsURL) {
		this.spAcsURL = spAcsURL;
	}
	
	private String spMetadataURL;

	public String getSpMetadataURL() {
		return spMetadataURL;
	}

	public void setSpMetadataURL(String spMetadataURL) {
		this.spMetadataURL = spMetadataURL;
	}
	
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Getter
	@Setter
	private JSONObject domainInfo;

	public String getDomainInfo() throws Exception {
		this.domainInfo = IAMAppUtil.getAppDomainInfo(this.httpServletRequest.getServerName());
		return SUCCESS;
	}

	@Getter
	@Setter
	private boolean createUserStatus;

	public String updateCreateUserStatus() throws Exception {
		var appDomainType = AppDomain.AppDomainType.getByServiceName(getAppDomainType());
		var appDomain = IAMAppUtil.getAppDomainForType(appDomainType.getIndex(), AccountUtil.getCurrentOrg().getOrgId()).get(0);

		IAMOrgUtil.updateDomainSSOStatus(appDomain.getDomain(), createUserStatus);
		return SUCCESS;
	}

	public String updatePortalSSOSettings() throws Exception {
		if (domainSSO == null) {
			return ERROR;
		}

		if (domainSSO.getId() <= 0) {
			domainSSO.setCreatedBy(AccountUtil.getCurrentUser().getId());
			domainSSO.setCreatedTime(System.currentTimeMillis());
		}
		domainSSO.setModifiedBy(AccountUtil.getCurrentUser().getId());
		domainSSO.setModifiedTime(System.currentTimeMillis());

		if (domainSSO.getConfig() != null) {
			SamlSSOConfig ssoConfig = (SamlSSOConfig) domainSSO.getSSOConfig();
			if (ssoConfig.getCertificate() != null) {
				try {
					SAMLUtil.loadCertificate(ssoConfig.getCertificate());
				}
				catch (Exception e) {
					e.printStackTrace();
					setErrorMessage("Invalid certificate.");
					return ERROR;
				}
			}
		}
		
		var appDomainType = AppDomain.AppDomainType.getByServiceName(getAppDomainType());
		var appDomain = IAMAppUtil.getAppDomainForType(appDomainType.getIndex(), AccountUtil.getCurrentOrg().getOrgId()).get(0);

		domainSSO.setAppDomainId(appDomain.getId());

		IAMOrgUtil.addOrUpdateDomainSSO(domainSSO);

		setDomainSSO(IAMOrgUtil.getDomainSSODetails(domainSSO.getId()));

		return SUCCESS;
	}

	public String updateSSOSettings() throws Exception {
		
		if (sso != null) {
			if (sso.getId() <= 0) {
				sso.setCreatedBy(AccountUtil.getCurrentUser().getId());
				sso.setCreatedTime(System.currentTimeMillis());
			}
			sso.setModifiedBy(AccountUtil.getCurrentUser().getId());
			sso.setModifiedTime(System.currentTimeMillis());
			
			if (sso.getSSOConfig() != null) {
				SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();
				if (ssoConfig.getCertificate() != null) {
					try {
						SAMLUtil.loadCertificate(ssoConfig.getCertificate());
					}
					catch (Exception e) {
						e.printStackTrace();
						setErrorMessage("Invalid certificate.");
						return ERROR;
					}
				}
			}
			
			IAMOrgUtil.addOrUpdateAccountSSO(AccountUtil.getCurrentOrg().getOrgId(), sso);
			
			setSso(IAMOrgUtil.getAccountSSO(AccountUtil.getCurrentOrg().getOrgId()));
		}
		
		return SUCCESS;
	}

	@Getter
	@Setter
	private String appDomainType;

	public String fetchPortalSSOSettings() throws Exception {
		var appDomainType = AppDomain.AppDomainType.getByServiceName(getAppDomainType());
		var appDomain = IAMAppUtil.getAppDomainForType(appDomainType.getIndex(), AccountUtil.getCurrentOrg().getOrgId()).get(0);
		var domainSSODetails = IAMOrgUtil.getDomainSSODetails(appDomain.getDomain());
		if (domainSSODetails != null) {
			setDomainSSO(domainSSODetails);
			setSpEntityId(SSOUtil.getSPMetadataURL(domainSSODetails));
			setSpMetadataURL(SSOUtil.getSPMetadataURL(domainSSODetails));
			setSpAcsURL(SSOUtil.getSPAcsURL(domainSSODetails));
		} else {
			setDomainSSO(null);
			setSpEntityId(null);
			setSpMetadataURL(null);
			setSpAcsURL(null);
		}
		return SUCCESS;
	}
	
	public String getSSOSettings() throws Exception {
		
		AccountSSO sso = IAMOrgUtil.getAccountSSO(AccountUtil.getCurrentOrg().getOrgId());
		if (sso != null) {
			setSso(sso);
			setSpEntityId(SSOUtil.getSPMetadataURL(sso));
			setSpMetadataURL(SSOUtil.getSPMetadataURL(sso));
			setSpAcsURL(SSOUtil.getSPAcsURL(sso));
		}
		else {
			setSso(null);
			setSpEntityId(null);
			setSpMetadataURL(null);
			setSpAcsURL(null);
		}
		
		return SUCCESS;
	}

	public String deletePortalSSOSettings() throws Exception {
		var appDomainType = AppDomain.AppDomainType.getByServiceName(getAppDomainType());
		var appDomain = IAMAppUtil.getAppDomainForType(appDomainType.getIndex(), AccountUtil.getCurrentOrg().getOrgId()).get(0);
		IAMOrgUtil.deleteDomainSSO(appDomain.getDomain());
		return SUCCESS;
	}
	
	public String deleteSSOSettings() throws Exception {
		
		IAMOrgUtil.deleteAccountSSO(AccountUtil.getCurrentOrg().getOrgId());
		
		return SUCCESS;
	}
	
	private FileInfo fileInfo;
	private InputStream downloadStream;
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}
	
	public String viewOrgPhoto() {
		
		try {
			FileStore fs = FacilioFactory.getFileStore();
			this.org = AccountUtil.getCurrentOrg();
			fileInfo = fs.getFileInfo(this.org.getLogoId());
			if (fileInfo != null) {
				downloadStream = fs.readFile(this.org.getLogoId());
			}
			else {
				return ERROR;
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		
		return SUCCESS;
	}
	
	public String showWONotificationSettings() throws Exception {
		
		setSetup(SetupLayout.getEmailNotificationsLayout());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
		//OrgContext org = OrgApi.getOrgContext();
		List<WorkflowRuleContext> rules= WorkflowRuleAPI.getWorkflowRules(woModule.getModuleId());
		ActionContext.getContext().getValueStack().set("emailNotifications", rules);
		return SUCCESS;
	}
	
    public String showAlarmsNotificationSettings() throws Exception {
		
		setSetup(SetupLayout.getEmailNotificationsLayout());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.ALARM);
		
		//OrgContext org = OrgApi.getOrgContext();
		List<WorkflowRuleContext> rules= WorkflowRuleAPI.getWorkflowRules(woModule.getModuleId());
		ActionContext.getContext().getValueStack().set("emailNotifications", rules);
		return SUCCESS;
	}
	
	
	public String showNotificationSettings() throws Exception {
		
		setSetup(SetupLayout.getEmailNotificationsLayout());
		
		//OrgContext org = OrgApi.getOrgContext();
		List<WorkflowRuleContext> rules= WorkflowRuleAPI.getWorkflowRules();
		ActionContext.getContext().getValueStack().set("emailNotifications", rules);
		return SUCCESS;
	}
	
	public String updateNotificationSettings() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_UPDATE, workflowRule);
		System.out.println("........ notification----->  " + workflowRule);
		FacilioChain updateNotification= FacilioChainFactory.getUpdateNotificationSettingChain();
		updateNotification.execute(context);
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	public String showEmailSettings() throws Exception {
		FacilioContext context  = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_MAIL_MESSAGE);
		context.put(FacilioConstants.ContextNames.RULE_TYPE, WorkflowRuleContext.RuleType.MODULE_RULE.getIntVal());
		FacilioChain emailSetting = FacilioChainFactory.getEmailSettingChain();
		emailSetting.execute(context);
		
		setSetup(SetupLayout.getEmailSettingsLayout());
		setEmailSetting((EmailSettingContext) context.get(FacilioConstants.ContextNames.EMAIL_SETTING));
		setSupportEmails((List<SupportEmailContext>) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST));
		if (supportEmails != null && supportEmails.size() > 0) {
			List<Long> ruleIds = supportEmails.stream().map(SupportEmailContext::getSupportRuleId).filter(Objects::nonNull).collect(Collectors.toList());
			if (ruleIds != null && ruleIds.size() > 0) {
				Map<Long, WorkflowRuleContext> supportMailRule = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false);
				if (supportMailRule != null) {
					for (Long ruleId : supportMailRule.keySet()) {
						WorkflowRuleContext rule = supportMailRule.get(ruleId);
						rule.setActions(ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), ruleId));
					}
				}
				setSupportMailRules(supportMailRule);
			}
		}

		return SUCCESS;
	}
	
	public String updateEmailSettings() throws Exception {
		FacilioContext context  = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EMAIL_SETTING, emailSetting);
		
		FacilioChain emailSetting = FacilioChainFactory.getUpdateEmailSettingChain();
		emailSetting.execute(context);
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	
	private ControllerContext controllerSettings;
	public ControllerContext getcontrollerSettings() {
		return controllerSettings;
	}
	public void setControllerSettings(ControllerContext controllerSettings) {
		this.controllerSettings = controllerSettings;
	}
	
	
	private EmailSettingContext emailSetting;
	public EmailSettingContext getEmailSetting() {
		return emailSetting;
	}
	public void setEmailSetting(EmailSettingContext emailSetting) {
		this.emailSetting = emailSetting;
	}
	
	private List<SupportEmailContext> supportEmails;
	public List<SupportEmailContext> getSupportEmails() {
		return supportEmails;
	}
	public void setSupportEmails(List<SupportEmailContext> supportEmails) {
		this.supportEmails = supportEmails;
	}

	public String supportEmailSetting() throws Exception {
		setSetup(SetupLayout.getEditEmailSettingLayout());
		System.out.println(supportEmailId);
		if(supportEmailId != -1) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, supportEmailId);
			
			FacilioChain getSupportEmail = FacilioChainFactory.getSupportEmailChain();
			getSupportEmail.execute(context);
			
			setSupportEmail((SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL));
		}
		orgDomain = AccountUtil.getCurrentOrg().getDomain();
		
		return SUCCESS;
	}
	
	public String addSupportEmailSetting() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
		
		FacilioChain addSupportEmail = FacilioChainFactory.getAddSupportEmailChain();
		addSupportEmail.execute(context);
		
		supportEmailId = supportEmail.getId();
		return SUCCESS;
	}
	
	public String addControllerSettings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_SETTINGS, controllerSettings);
		
		FacilioChain addcontrollerSettings = FacilioChainFactory.getAddControllerChain();
		addcontrollerSettings.execute(context);
		
		controllerSettingsId = controllerSettings.getId();
		return SUCCESS;	
	}
	
	public String editControllerSettings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_SETTINGS, controllerSettings);
		
		FacilioChain editControllerSettings = TransactionChainFactory.getEditControllerChain();
		editControllerSettings.execute(context);
		
		controllerSettingsId = controllerSettings.getId();
		return SUCCESS; 
	}

	public String updateSupportEmailSetting() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
		System.out.println("........ update" + supportEmail);
		FacilioChain updateSupportEmail = FacilioChainFactory.getUpdateSupportEmailChain();
		updateSupportEmail.execute(context);
		
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	
	public String deleteSupportEmailSetting() throws Exception {
		
		System.out.println(supportEmailId);
		if(supportEmailId != -1) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, supportEmailId);
			
			FacilioChain deleteSupportEmail = FacilioChainFactory.getDeleteSupportEmailChain();
			deleteSupportEmail.execute(context);
			
			result = (String) context.get(FacilioConstants.ContextNames.RESULT);
			return SUCCESS;
		}
		
		return SUCCESS;
	}
	
	private String orgDomain;
	public String getOrgDomain() {
		return orgDomain;
	}
	
	private long controllerSettingsId = -1;
	public long getcontrollerSettingsId() {
		return controllerSettingsId;
	}
	public void setcontrollerSettingsId(long controllerSettingsId) {
		this.controllerSettingsId = controllerSettingsId;
	}
	
	
	private long supportEmailId = -1;
	public long getSupportEmailId() {
		return supportEmailId;
	}
	public void setSupportEmailId(long supportEmailId) {
		this.supportEmailId = supportEmailId;
	}
	
	private SupportEmailContext supportEmail;
	public SupportEmailContext getSupportEmail() {
		return supportEmail;
	}
	public void setSupportEmail(SupportEmailContext supportEmail) {
		this.supportEmail = supportEmail;
	}
	public SupportEmailContext getSupportEmailContext() {
		return supportEmail;
	}
	public void setSupportEmailContext(SupportEmailContext supportEmail) {
		this.supportEmail = supportEmail;
	}
	public String showSubscriptions() throws Exception {
		
		return SUCCESS;
	}
	
	private WorkflowRuleContext workflowRule;
	public WorkflowRuleContext getWorkflowRule(){
		return workflowRule;
	}
	
	public void setWorkflowRule(WorkflowRuleContext workflowRule) {
		this.workflowRule = workflowRule;
	}
	
	public String updateCalendarColor() throws Exception {
		TicketAPI.setCalendarColor(calendarColor);
		
		return SUCCESS;
	}
	
	private CalendarColorContext calendarColor;
	public CalendarColorContext getCalendarColor() {
		return calendarColor;
	}
	public void setCalendarColor(CalendarColorContext calendarColor) {
		this.calendarColor = calendarColor;
	}

	public Map<Long, WorkflowRuleContext> getSupportMailRules() {
		return supportMailRules;
	}

	public void setSupportMailRules(Map<Long, WorkflowRuleContext> supportMailRules) {
		this.supportMailRules = supportMailRules;
	}

	private Map<Long, WorkflowRuleContext> supportMailRules;

	// exposes api for testing
	public String fetchMailImap() throws Exception {
		List<SupportEmailContext> imapEmails = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getImapsEmailsOfOrg(AccountUtil.getCurrentOrg().getOrgId()));
		if (CollectionUtils.isNotEmpty(imapEmails)) {
			for (SupportEmailContext imapMail : imapEmails) {
				long latestUID = imapMail.getLatestMessageUID();
				try ( ImapsClient mailService = new ImapsClient(imapMail)){
					if (latestUID > 0) {
						// fetch mail which is greater than messageUID
						mailService.getMessageGtUID(latestUID);
					} else {
						// fetch today's Mail n days
						mailService.getNDaysMails(1);
					}
				}

			}
		}
		return  SUCCESS;
	}

	private HttpServletRequest httpServletRequest;
	@Override
	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}
}
