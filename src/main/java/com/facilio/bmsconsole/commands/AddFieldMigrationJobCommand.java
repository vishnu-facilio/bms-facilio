package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

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
