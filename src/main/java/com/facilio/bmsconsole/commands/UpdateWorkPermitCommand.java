package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkPermitActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UpdateWorkPermitCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        WorkPermitContext workpermit = (WorkPermitContext) context.getOrDefault(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, null);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        if (workpermit != null) {
            RecordAPI.updateRecord(workpermit,module, Arrays.asList(fieldsMap.get("isPreValidationDone"), fieldsMap.get("isPostValidationDone")));
        }
        if (workpermit.isPreValidationDone()) {
            JSONObject info = new JSONObject();
            CommonCommandUtil.addActivityToContext(workpermit.getId(), -1, WorkPermitActivityType.PREREQUISITES_VERIFIED, info, (FacilioContext) context);
        } else if (workpermit.isPostValidationDone()) {
            JSONObject info = new JSONObject();
            CommonCommandUtil.addActivityToContext(workpermit.getId(), -1, WorkPermitActivityType.POST_WORK_VERIFIED, info, (FacilioContext) context);
        }

            return false;
    }
}
