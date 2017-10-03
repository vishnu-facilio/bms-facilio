package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.bmsconsole.context.OrgContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class SetupActions<T> extends ActionSupport {
	
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
	
		setOrg(OrgApi.getOrgContext());
		
		return SUCCESS;
	}
	
	
	private OrgContext org;
	public void setOrg(OrgContext org) {
		this.org = org;
	}
	
	public OrgContext getOrg() {
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
		
		if (getOrgPhoto() != null) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			long fileId = fs.addFile(getOrgPhotoFileName(), getOrgPhoto(), getOrgPhotoContentType());
			org.setLogoId(fileId);
		}
		
	OrgApi.updateOrgsettings(org, null);
		
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
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			this.org = OrgApi.getOrgContext();
			fileInfo = fs.getFileInfo(this.org.getLogoId());
			if (fileInfo != null) {
				downloadStream = fs.readFile(this.org.getLogoId());
			}
			else {
				return ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	
	
	public String showNotificationSettings() throws Exception {
		
		setSetup(SetupLayout.getEmailNotificationsLayout());
		
		//OrgContext org = OrgApi.getOrgContext();
		List<WorkflowRuleContext> rules= WorkflowAPI.getWorkflowRules(OrgInfo.getCurrentOrgInfo().getOrgid());
		ActionContext.getContext().getValueStack().set("emailNotifications", rules);
		return SUCCESS;
	}
	
	public String showEmailSettings() throws Exception {
		FacilioContext context  = new FacilioContext();
		Chain emailSetting = FacilioChainFactory.getEmailSettingChain();
		emailSetting.execute(context);
		
		setSetup(SetupLayout.getEmailSettingsLayout());
		setEmailSetting((EmailSettingContext) context.get(FacilioConstants.ContextNames.EMAIL_SETTING));
		setSupportEmails((List<SupportEmailContext>) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST));
		return SUCCESS;
	}
	
	public String updateEmailSettings() throws Exception {
		FacilioContext context  = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EMAIL_SETTING, emailSetting);
		
		Chain emailSetting = FacilioChainFactory.getUpdateEmailSettingChain();
		emailSetting.execute(context);
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
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
			
			Chain getSupportEmail = FacilioChainFactory.getSupportEmailChain();
			getSupportEmail.execute(context);
			
			setSupportEmail((SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL));
		}
		orgDomain = OrgInfo.getCurrentOrgInfo().getOrgDomain();
		
		return SUCCESS;
	}
	
	public String addSupportEmailSetting() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
		
		Chain addSupportEmail = FacilioChainFactory.getAddSupportEmailChain();
		addSupportEmail.execute(context);
		
		supportEmailId = supportEmail.getId();
		return SUCCESS;
	}
	
	public String updateSupportEmailSetting() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
		
		Chain updateSupportEmail = FacilioChainFactory.getUpdateSupportEmailChain();
		updateSupportEmail.execute(context);
		
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	public String deleteSupportEmailSetting() throws Exception {
		
		System.out.println(supportEmailId);
		if(supportEmailId != -1) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, supportEmailId);
			
			Chain deleteSupportEmail = FacilioChainFactory.getDeleteSupportEmailChain();
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
	
	public String showSubscriptions() throws Exception {
		
		return SUCCESS;
	}

	
	
}
