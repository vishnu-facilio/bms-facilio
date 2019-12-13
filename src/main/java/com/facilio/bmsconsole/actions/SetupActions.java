package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.CalendarColorContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class SetupActions<T> extends ActionSupport {

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
		FacilioChain emailSetting = FacilioChainFactory.getEmailSettingChain();
		emailSetting.execute(context);
		
		setSetup(SetupLayout.getEmailSettingsLayout());
		setEmailSetting((EmailSettingContext) context.get(FacilioConstants.ContextNames.EMAIL_SETTING));
		setSupportEmails((List<SupportEmailContext>) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST));
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
	
}
