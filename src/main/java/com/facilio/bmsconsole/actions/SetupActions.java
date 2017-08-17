package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.bmsconsole.context.OrgContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class SetupActions extends ActionSupport {
	
	static
	{
		System.out.println("###############Setup Action Class Loaded#############");
	}
	
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	public String mySettings() throws Exception {
		
		setSetup(SetupLayout.getPersonalSettingsLayout());
		return SUCCESS;
	}

	public String importData() throws Exception {
		
		setSetup(SetupLayout.getImportLayout());
		return SUCCESS;
	}
	public String servicePortal() throws Exception {

		SetupLayout<ServicePortalInfo> portalInfo =SetupLayout.getservicePortal();
		ServicePortalInfo spinfo = new ServicePortalInfo();
		portalInfo.setData(spinfo);
		setSetup(portalInfo);
		return SUCCESS;
	}
	public String orgSettings() throws Exception {
		
		setSetup(SetupLayout.getCompanySettingsLayout());
		
		return SUCCESS;
	}
	
	
	private OrgContext org;
	public void setOrg(OrgContext org) {
		this.org = org;
	}
	
	public OrgContext getOrg() {
		return this.org;
	}
	
	public String updateOrgSettings() throws Exception {
		org.getName();
		
	System.out.println(org.getName());
		
		return SUCCESS;
	}
	
	public String showNotificationSettings() throws Exception {
		
		setSetup(SetupLayout.getEmailNotificationsLayout());
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
