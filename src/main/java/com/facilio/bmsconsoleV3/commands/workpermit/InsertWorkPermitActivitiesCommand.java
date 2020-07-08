package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitChecklistContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
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
                List<Long> updatedFieldIdsList = updatedSet.stream().map(change -> change.getFieldId()).collect(Collectors.toList());
                if (!((updatedFieldIdsList.contains(moduleStateField.getFieldId()) || updatedFieldIdsList.contains(approvalStateField.getFieldId())) && updatedFieldIdsList.size() == 1)) {
                    JSONObject info = new JSONObject();
                    CommonCommandUtil.addActivityToContext(workPermit.getId(), -1, CommonActivityType.UPDATE_RECORD, info, (FacilioContext) context);
                }
            }
            if (CollectionUtils.isNotEmpty(workPermit.getWorkpermitchecklist())) {
                FacilioModule checklistModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_CHECKLIST);
                DeleteRecordBuilder<WorkPermitChecklistContext> deleteBuilder = new DeleteRecordBuilder<WorkPermitChecklistContext>()
                        .module(checklistModule)
                        .andCondition(CriteriaAPI.getCondition("WORK_PERMIT_ID", "workPermit", String.valueOf(workPermit.getId()), NumberOperators.EQUALS));
                deleteBuilder.delete();
                V3WorkPermitContext workPermitContext = new V3WorkPermitContext();
                workPermitContext.setId(workPermit.getId());
                for (WorkPermitChecklistContext lineItem : workPermit.getWorkpermitchecklist()) {
                    lineItem.setWorkPermit(workPermitContext);
                }
                V3RecordAPI.addRecord(false, workPermit.getWorkpermitchecklist(), checklistModule, modBean.getAllFields(checklistModule.getName()));
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
