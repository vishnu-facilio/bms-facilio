package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class UpdateContactsRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContactsContext> contactsList = (List<ContactsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(contactsList)) {
			for (ContactsContext contact : contactsList) {
				if (contact.getRequester() != null && contact.getRequester().getId() > 0) {
					ContactsAPI.updatePortalUserAccess(contact, false);
				} else {
					if (contact.isPortalAccessNeeded()) {
						long identifier = 1;
						AppDomain appDomain = null;
						if (contact.getContactTypeEnum() == ContactType.TENANT) {
							List<AppDomain> appDomains = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL,
									AccountUtil.getCurrentOrg().getOrgId());
							if (CollectionUtils.isNotEmpty(appDomains)) {
								if (appDomains.size() > 1) {
									throw new IllegalArgumentException(
											"There are multiple apps configured for this type");
								}
								appDomain = appDomains.get(0);
							}
						}
						else if (contact.getContactTypeEnum() == ContactType.VENDOR) {
							List<AppDomain> appDomains = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL,
									AccountUtil.getCurrentOrg().getOrgId());
							if (CollectionUtils.isNotEmpty(appDomains)) {
								if (appDomains.size() > 1) {
									throw new IllegalArgumentException(
											"There are multiple apps configured for this type");
								}
								appDomain = appDomains.get(0);
								identifier = appDomain.getId();
							}
						}
						else if (contact.getContactTypeEnum() == ContactType.EMPLOYEE) {
							List<AppDomain> appDomains = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL,
									AccountUtil.getCurrentOrg().getOrgId());
							if (CollectionUtils.isNotEmpty(appDomains)) {
								if (appDomains.size() > 1) {
									throw new IllegalArgumentException(
											"There are multiple apps configured for this type");
								}
								appDomain = appDomains.get(0);
							}
						}
						ContactsAPI.addUserAsRequester(contact, appDomain, identifier);
					}
				}
			}
		}
		return false;
	}

}
