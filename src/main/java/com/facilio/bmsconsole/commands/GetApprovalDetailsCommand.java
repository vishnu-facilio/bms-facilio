package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetApprovalDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (recordId != null && StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module name");
            }

            List<FacilioField> fields = modBean.getAllFields(moduleName);
            List<SupplementRecord> lookupFields = new ArrayList<>();
            for (FacilioField field : fields) {
                if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    lookupFields.add((SupplementRecord) field);
                }
            }

            SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                    .module(module)
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                    .select(fields)
                    .fetchSupplements(lookupFields)
                    .andCondition(CriteriaAPI.getIdCondition(recordId, module));
            ModuleBaseWithCustomFields moduleRecord = builder.fetchFirst();
            if (moduleRecord == null) {
                throw new IllegalArgumentException("Invalid record");
            }

            if (moduleRecord.getApprovalStatus() == null || moduleRecord.getApprovalFlowId() < 0) {
                throw new IllegalArgumentException("Not in approval stage");
            }
            context.put(FacilioConstants.ContextNames.RECORD, moduleRecord);
            long approvalFlowId = moduleRecord.getApprovalFlowId();
            context.put(FacilioConstants.ContextNames.ID, approvalFlowId);
        }
        return false;
    }
}
