package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class WorkPermitFillChecklistForCategoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkPermitTypeChecklistCategoryContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (WorkPermitTypeChecklistCategoryContext categoryContext : list) {
                List<WorkPermitTypeChecklistContext> checklists = fetchAllChecklistforCategory(categoryContext.getId());
                if (CollectionUtils.isNotEmpty(checklists)) {
                    categoryContext.setChecklist(checklists);
                }
            }

        }

        return false;
    }

    private List<WorkPermitTypeChecklistContext> fetchAllChecklistforCategory(Long categoryId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<WorkPermitTypeChecklistContext> builder = new SelectRecordsBuilder<WorkPermitTypeChecklistContext>()
                .module(module)
                .beanClass(WorkPermitTypeChecklistContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("category"), String.valueOf(categoryId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("deleted"), CommonOperators.IS_EMPTY));

        List<WorkPermitTypeChecklistContext> records = builder.get();

        return records;
    }
}
