package com.facilio.bmsconsoleV3.signup;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
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
		from1.setEmail("noreply@"+org.getDomain()+".facilio.com");
		from1.setActiveStatus(true);
		from1.setVerificationStatus(true);
		from1.setSourceType(EmailFromAddress.SourceType.NOTIFICATION.getIndex());
		from1.setCreationType(EmailFromAddress.CreationType.DEFAULT.getIndex());
		
		defaultFromEmails.add(from1);
		
		EmailFromAddress from2 = new EmailFromAddress();
		from2.setName("defaultSupportMail");
		from2.setDisplayName("Support");
		from2.setEmail("support@"+org.getDomain()+".facilio.com");
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
	}
}
