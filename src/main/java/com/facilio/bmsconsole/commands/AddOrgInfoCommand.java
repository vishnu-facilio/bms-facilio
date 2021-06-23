package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;

public class AddOrgInfoCommand extends FacilioCommand {

        @Override
        public boolean executeCommand(Context context) throws Exception {
            JSONObject signupInfo = (JSONObject) context.get(FacilioConstants.ContextNames.SIGNUP_INFO);
			boolean facilioAuth = (Boolean) signupInfo.get("isFacilioAuth");
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(AccountConstants.getOrgInfoModule().getTableName())
                    .fields(AccountConstants.getOrgInfoFields());

            Map<String, Object> properties = new HashMap<>();
            properties.put("orgId", context.get("orgId"));
            properties.put("name", "FACILIOAUTH");
            properties.put("value", facilioAuth);
            insertRecordBuilder.addRecord(properties);
            insertRecordBuilder.save();
            
        	AccountUtil.getRoleBean().createSuperdminRoles((long)context.get("orgId"));
        	
            return false;
        }

}
