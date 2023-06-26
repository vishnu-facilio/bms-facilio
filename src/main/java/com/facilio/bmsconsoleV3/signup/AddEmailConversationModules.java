package com.facilio.bmsconsoleV3.signup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.oci.util.OCIUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.util.V3Util;

public class AddEmailConversationModules extends SignUpData {
	
	public void addData() throws Exception {
		
		FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "getVerifiedEmailAddressFromAWS", 50, 300, "facilio");
		
		Organization org = AccountUtil.getCurrentOrg();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<EmailFromAddress> defaultFromEmails = new ArrayList<>();
		
		EmailFromAddress from1 = new EmailFromAddress();
		from1.setName("defaultReplyMail");
		from1.setDisplayName("Notification");
		from1.setEmail(EmailClient.getNoReplyFromEmail());
		from1.setActiveStatus(true);
		from1.setVerificationStatus(true);
		from1.setSourceType(EmailFromAddress.SourceType.NOTIFICATION.getIndex());
		from1.setCreationType(EmailFromAddress.CreationType.DEFAULT.getIndex());
		
		defaultFromEmails.add(from1);
		
		EmailFromAddress from2 = new EmailFromAddress();
		from2.setName("defaultSupportMail");
		from2.setDisplayName("Support");
		from2.setEmail(EmailClient.getFromEmail("support"));
		from2.setActiveStatus(true);
		from2.setVerificationStatus(true);
		from2.setSourceType(EmailFromAddress.SourceType.SUPPORT.getIndex());
		from2.setCreationType(EmailFromAddress.CreationType.DEFAULT.getIndex());
		
		defaultFromEmails.add(from2);

		
		InsertRecordBuilder<EmailFromAddress> insert = new InsertRecordBuilder<EmailFromAddress>()
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
				.fields(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME))
				.addRecords(defaultFromEmails);
		
		insert.save();

		//adding from address for oracle env
		if("oci".equals(FacilioProperties.getEmailClient())) {
			List<String> defaultEmailAddress = defaultFromEmails.stream().map(row -> row.getEmail()).collect(Collectors.toList());
			defaultEmailAddress.add(EmailClient.getFromEmail("issues"));
			defaultEmailAddress.add(EmailClient.getFromEmail("mlerror"));
			defaultEmailAddress.add(EmailClient.getFromEmail("report"));
			defaultEmailAddress.add(EmailClient.getFromEmail("support"));
			defaultEmailAddress.add(EmailClient.getFromEmail("noreply"));
			defaultEmailAddress.add(EmailClient.getFromEmail("error"));
			for(String fromAddr : defaultEmailAddress) {
				OCIUtil.getOracleEmailClient().addSender(fromAddr);
			}
		}
		
		//addScoping(modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));
	}

	public void addScoping(FacilioModule module) throws Exception {
		
		long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        
        ScopingConfigContext scoping = new ScopingConfigContext();
        Criteria criteria = new Criteria();
        
        Condition condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
        condition.setModuleName(module.getName());
        
        Condition condition1 = CriteriaAPI.getCondition("siteId", null, CommonOperators.IS_EMPTY);
        condition1.setModuleName(module.getName());
        
        criteria.addAndCondition(condition);
        criteria.addOrCondition(condition1);
        
        scoping.setScopingId(applicationScopingId);
        scoping.setModuleId(module.getModuleId());
        scoping.setCriteria(criteria);
        
        ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
	}
}
