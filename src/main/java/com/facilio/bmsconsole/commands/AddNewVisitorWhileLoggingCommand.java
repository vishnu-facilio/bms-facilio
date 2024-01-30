package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddNewVisitorWhileLoggingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLogs = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLogs)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<Long, VisitorSettingsContext> settingsMap = VisitorManagementAPI.getVisitorSettingsForType();
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(VisitorLoggingContext vL : visitorLogs) {

				//need to change this to tenant contact context once host is changed to people lookup

				if(vL.getRequestedBy() == null || vL.getRequestedBy().getId() <= 0) {
					vL.setRequestedBy(AccountUtil.getCurrentUser());
				}
				else {
					if(vL.getHost() == null) {
						ContactsContext tenantContact = ContactsAPI.getContactsIdForUser(vL.getRequestedBy().getId());
						if(tenantContact != null){
							vL.setHost(tenantContact);
						}
					}
				}
					TenantContext tenant = PeopleAPI.getTenantForUser(vL.getRequestedBy().getOuid());
					vL.setTenant(tenant);

				if(vL.getTenant() == null && vL.getHost() != null) {
					ContactsContext host = (ContactsContext) RecordAPI.getRecord(FacilioConstants.ContextNames.CONTACT, vL.getHost().getId());
					if (host != null) {
						vL.setTenant(host.getTenant());
					}
				}


				if(vL.getVisitor() != null && vL.getVisitor().getId() > 0) {
					vL.setIsReturningVisitor(true);
					vL.getVisitor().setIsReturningVisitor(true);
				}
				else {
					vL.setIsReturningVisitor(false);
					if(vL.getVisitor() != null) {
						vL.getVisitor().setIsReturningVisitor(false);
					}
				}
				VisitorSettingsContext setting = settingsMap.get(vL.getVisitorType().getId());
				if(setting != null) {
					JSONObject hostSetting = setting.getHostSettings();
					if(!vL.isPreregistered() && hostSetting.get("requireApproval") != null && (boolean)hostSetting.get("requireApproval")) {
						vL.setIsApprovalNeeded((boolean)hostSetting.get("requireApproval"));
					}
					else {
						vL.setIsApprovalNeeded(false);
					}
					
					vL.setIsInviteApprovalNeeded(setting.getApprovalRequiredForInvite());
				}
				else {
					vL.setIsApprovalNeeded(false);
				}
				if(vL.getInvite() != null && vL.getInvite().getId() > 0) {
					vL.setIsInvited(true);
				}
				else {
					vL.setIsInvited(false);
				}
				if(vL.getAvatar() != null) {
					vL.setPhotoStatus(true);
				}
				else {
					vL.setPhotoStatus(false);
				}
				if(vL.getHost() != null && vL.getHost().getId() > 0) {
					vL.setHostStatus(true);
				}
				else {
					vL.setHostStatus(false);
				}
				if(vL.getVisitor() != null && vL.getVisitor().getId() <= 0) {
					if(!VisitorManagementAPI.checkForDuplicateVisitor(vL.getVisitor())) {
						RecordAPI.addRecord(true, Collections.singletonList(vL.getVisitor()) , module, fields);
					}
					else {
						throw new IllegalArgumentException("A Visitor Already exists with this phone number");
					}
				}
				else if(vL.getVisitor() != null && vL.getVisitor().getId() > 0) {
					VisitorContext vLVisitor = VisitorManagementAPI.getVisitor(vL.getVisitor().getId(), null);
					if(StringUtils.isNotEmpty(vL.getVisitor().getPhone())) {
						vLVisitor.setPhone(vL.getVisitor().getPhone());
					}
					if(!VisitorManagementAPI.checkForDuplicateVisitor(vLVisitor)) {
						RecordAPI.updateRecord(vL.getVisitor(), module, fields);
					}
					else {
						throw new IllegalArgumentException("A Visitor Already exists with this phone number");
					}
				}
			}
		}
		return false;
	}

}
