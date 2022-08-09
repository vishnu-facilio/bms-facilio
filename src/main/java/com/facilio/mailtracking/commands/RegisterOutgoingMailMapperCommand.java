package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterOutgoingMailMapperCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        Map<String, Object> row = new HashMap<>();
        row.put("orgId", orgId);
        row.put("sysCreatedTime", System.currentTimeMillis());
        FacilioModule module = ModuleFactory.getMailMapperModule();
        List<FacilioField> fields = FieldFactory.getMailMapperFields();
        Long mapperId = OutgoingMailAPI.insert(module, fields, row);
        context.put(MailConstants.Params.ORGID, orgId);
        context.put(MailConstants.Params.MAPPER_ID, mapperId);
        return false;
    }
}
