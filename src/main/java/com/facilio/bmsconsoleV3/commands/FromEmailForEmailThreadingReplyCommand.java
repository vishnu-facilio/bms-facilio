package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;

public class FromEmailForEmailThreadingReplyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		
		EmailFromAddress fromAddress = null;
		
		if(recordId != null && moduleId != null) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Long orgID = AccountUtil.getCurrentOrg().getId();
			
			ModuleBaseWithCustomFields record = V3RecordAPI.getRecord(modBean.getModule(moduleId).getName(), recordId);
			
			EmailToModuleDataContext emailToModuleData = MailMessageUtil.getEmailToModuleContext(recordId, moduleId);
			
			if(emailToModuleData != null) {
				Long supportEmailIds = emailToModuleData.getParentId();
				
				SupportEmailContext parentSupportMailContext = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getSupportEmailFromId(orgID, supportEmailIds));
				
				if(parentSupportMailContext != null) {
					 
					fromAddress = MailMessageUtil.getEmailFromAddress(parentSupportMailContext.getActualEmail(), true);
				}
			}
			if(fromAddress == null) {
				
				if(record.getSiteId() > 0) {
					
					SupportEmailContext siteSupportMailContext = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getSupportEmailsOfSite(orgID,record.getSiteId()));
					
					if(siteSupportMailContext != null) {
						 
						fromAddress = MailMessageUtil.getEmailFromAddress(siteSupportMailContext.getActualEmail(), true);
					}
				}
			}
			
			if(fromAddress == null) {
				fromAddress = MailMessageUtil.getDefaultEmailFromAddress(EmailFromAddress.SourceType.SUPPORT);
			}
			
			
			Criteria criteria = new Criteria();
			
			List<FacilioField> emailFromAddressField = modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);
			
			
			if(fromAddress != null) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("email"), fromAddress.getEmail(), StringOperators.ISN_T));
			}
			if(record.getSiteId() > 0) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("siteId"), record.getSiteId()+"", NumberOperators.EQUALS));
			}
			
			List<EmailFromAddress> emailAddressList = MailMessageUtil.getEmailFromAddress(criteria);
			
			if(fromAddress != null) {
				emailAddressList.add(0, fromAddress);
			}
			context.put(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME, emailAddressList);
			
		}
		// TODO Auto-generated method stub
		return false;
	}

}
