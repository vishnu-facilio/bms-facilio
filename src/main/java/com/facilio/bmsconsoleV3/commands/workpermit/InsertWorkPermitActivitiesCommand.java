package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.WorkPermitActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class InsertWorkPermitActivitiesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Long workPermitId = (Long) context.get(Constants.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (workPermitId != null && workPermitId > 0) {
            V3WorkPermitContext workPermit = (V3WorkPermitContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, workPermitId);
            List<UpdateChangeSet> updatedSet = Constants.getRecordChangeSets(context, workPermitId);
            if (CollectionUtils.isNotEmpty(updatedSet)) {
                FacilioField moduleStateField = modBean.getField(FacilioConstants.ContextNames.MODULE_STATE, moduleName);
                FacilioField approvalStateField = modBean.getField(FacilioConstants.ContextNames.APPROVAL_STATUS, moduleName);
                FacilioField preValidationDoneField = modBean.getField("isPreValidationDone", moduleName);
                FacilioField postValidationDoneField = modBean.getField("isPostValidationDone", moduleName);
                List<Long> updatedFieldIdsList = updatedSet.stream().map(change -> change.getFieldId()).collect(Collectors.toList());
                if (updatedFieldIdsList.contains(preValidationDoneField.getFieldId())) {
                    JSONObject info = new JSONObject();
                    CommonCommandUtil.addActivityToContext(workPermit.getId(), -1, WorkPermitActivityType.PREREQUISITES_VERIFIED, info, (FacilioContext) context);
                } else if (updatedFieldIdsList.contains(postValidationDoneField.getFieldId())) {
                    JSONObject info = new JSONObject();
                    CommonCommandUtil.addActivityToContext(workPermit.getId(), -1, WorkPermitActivityType.POST_WORK_VERIFIED, info, (FacilioContext) context);
                } else if (!((updatedFieldIdsList.contains(moduleStateField.getFieldId()) || updatedFieldIdsList.contains(approvalStateField.getFieldId())) && updatedFieldIdsList.size() == 1)) {
                    JSONObject info = new JSONObject();
                    CommonCommandUtil.addActivityToContext(workPermit.getId(), -1, CommonActivityType.UPDATE_RECORD, info, (FacilioContext) context);
                }
            }
        } else {
            List<V3WorkPermitContext> workPermitList = (List<V3WorkPermitContext>) CommandUtil.getModuleDataList(context, FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
            if (CollectionUtils.isNotEmpty((workPermitList))) {
                for (V3WorkPermitContext workPermit : workPermitList) {
                    JSONObject info = new JSONObject();
                    CommonCommandUtil.addActivityToContext(workPermit.getId(), -1, CommonActivityType.ADD_RECORD, info, (FacilioContext) context);
                }
            }
        }
        return false;
    }
}
