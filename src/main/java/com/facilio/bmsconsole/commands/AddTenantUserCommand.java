package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddTenantUserCommand implements Command {
	
	
	public boolean execute(Context context) throws Exception {
		//TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		User user = (User)context.get(FacilioConstants.ContextNames.USER);
		Long tenantId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		long orgid = AccountUtil.getCurrentOrg().getOrgId();
		user.setOrgId(orgid);
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		try {
			AccountUtil.getUserBean().inviteRequester(orgid, user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    
		Map<String, Object> prop = new HashMap<>();
		
		prop.put("tenantId", tenantId);
		prop.put("ouid", user.getId());
		prop.put("orgId", user.getOrgId());
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getTenantsuserModule().getTableName());
		insert.fields(FieldFactory.getTenantsUserFields());
		insert.addRecord(prop);

		insert.save(); 
		
		return false;
		
		
		
	}

}
