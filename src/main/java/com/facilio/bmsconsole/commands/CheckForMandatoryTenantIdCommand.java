package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import io.jsonwebtoken.lang.Collections;

public class CheckForMandatoryTenantIdCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TenantContactContext> tenantContacts = (List<TenantContactContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(tenantContacts)) {
			for(TenantContactContext tc : tenantContacts) {
				tc.setPeopleType(PeopleType.TENANT_CONTACT);
				if(tc.getTenant() == null || tc.getTenant().getId() <=0 ) {
					throw new IllegalArgumentException("Tenant Contact must have a tenant id associated");
				}
			}
		}
		return false;
	}

}
