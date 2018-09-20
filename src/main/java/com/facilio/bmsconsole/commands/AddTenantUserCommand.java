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
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		
		long orgid = AccountUtil.getCurrentOrg().getOrgId();
		user.setOrgId(orgid);
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		try {
//			user.getId();
			AccountUtil.getUserBean().inviteRequester(orgid, user);
//			user.setOuid(user.getId());	
		}
		catch (Exception e) {
		}
		
		Map<String, Object> prop = new HashMap<>();
		
		prop.put("tenantId", tenant.getId());
		prop.put("ouid", user.getId());
		prop.put("orgId", orgid);
		
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getTenantsuserModule().getTableName());
		insert.fields(FieldFactory.getTenantsUserFields());
		insert.addRecord(prop);

		insert.save(); 
		
		return false;
		
		
		
	}

}
