package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitChecklistContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillWorkPermitChecklistCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3WorkPermitContext workPermit = (V3WorkPermitContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, id);

        if (workPermit != null && workPermit.getId() > 0) {
            List<WorkPermitChecklistContext> checklists = fetchAllChecklistForWorkPermit(workPermit.getId());
            if (CollectionUtils.isNotEmpty(checklists)) {
                checklists = refactorCheckLists(checklists);
                workPermit.setChecklist(checklists);
            }
        }

        return false;
    }

    private List<WorkPermitChecklistContext> refactorCheckLists(List<WorkPermitChecklistContext> checklists) {
        for (WorkPermitChecklistContext checklist : checklists) {
            if (checklist.getChecklist() != null) {
                WorkPermitTypeChecklistContext checklistData = checklist.getChecklist();
                checklist.setItemName(checklistData.getItem());
                checklist.setCheckListType(checklistData.getValidationTypeEnum().getValue());
                if (checklistData.getCategory() != null) {
                    checklist.setCategoryName(checklistData.getCategory().getName());
                }
            }
        }
        return checklists;
    }
    private List<WorkPermitChecklistContext> fetchAllChecklistForWorkPermit(Long workPermitId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_CHECKLIST);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_CHECKLIST);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        LookupFieldMeta checklistField = new LookupFieldMeta((LookupField) fieldsAsMap.get("checklist"));
        LookupField categoryField = (LookupField) modBean.getField("category", FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST);
        checklistField.addChildSupplement(categoryField);

        SelectRecordsBuilder<WorkPermitChecklistContext> builder = new SelectRecordsBuilder<WorkPermitChecklistContext>()
                .module(module)
                .beanClass(WorkPermitChecklistContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("workPermit"), String.valueOf(workPermitId), NumberOperators.EQUALS))
                .fetchSupplement(checklistField);

        List<WorkPermitChecklistContext> records = builder.get();

        return records;
    }
}
