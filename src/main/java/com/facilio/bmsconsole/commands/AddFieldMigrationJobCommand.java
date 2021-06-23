package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class AddFieldMigrationJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceId = (long) context.get(FacilioConstants.ContextNames.SOURCE_ID);

        long targetId = (long) context.get(FacilioConstants.ContextNames.TARGET_ID);
        List<Long> resources = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);



        JSONObject jobProp = new JSONObject();
        jobProp.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
        jobProp.put(FacilioConstants.ContextNames.SOURCE_ID,sourceId);
        jobProp.put(FacilioConstants.ContextNames.TARGET_ID, targetId);
        jobProp.put(FacilioConstants.ContextNames.RESOURCE_LIST,resources);


        BmsJobUtil.deleteJobWithProps(targetId,"FieldMigration" );
        BmsJobUtil.scheduleOneTimeJobWithProps(targetId,  "FieldMigration", 30, "facilio", jobProp);
        return false;
    }
}
