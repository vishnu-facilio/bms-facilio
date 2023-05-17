package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.bmsconsoleV3.context.DecommissionLogContext;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class AddDecommissionLogCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject logContext = (JSONObject) context.get(FacilioConstants.ContextNames.LOG_CONTEXT);

        Long resourceId = Long.parseLong(String.valueOf(logContext.get("resourceId")));
        Long decommissionTime = Long.parseLong(String.valueOf(logContext.get(FacilioConstants.ContextNames.COMMISSION_TIME)));

        DecommissionLogContext logData = DecommissionUtil.getDecommissionLogForResource(
                (String) logContext.get(FacilioConstants.ContextNames.RESOURCE_MODULENAME),
                resourceId,
                (Boolean) logContext.get(FacilioConstants.ContextNames.DECOMMISSION),
                decommissionTime,
                (String) logContext.get(FacilioConstants.ContextNames.REMARKS));

        List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.DECOMMISSION_LOG);

        InsertRecordBuilder<DecommissionLogContext> insertBuilder = new InsertRecordBuilder<DecommissionLogContext>()
                .module(Constants.getModBean().getModule(FacilioConstants.ContextNames.DECOMMISSION_LOG))
                .fields(fields)
                .addRecord(logData);
        insertBuilder.save();

        return false;
    }
}
