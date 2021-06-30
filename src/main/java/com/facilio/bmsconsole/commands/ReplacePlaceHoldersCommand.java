package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReplacePlaceHoldersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        String formattedString = (String) context.get(FacilioConstants.ContextNames.FORMATTED_STRING);
        if (StringUtils.isNotEmpty(moduleName) && id > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<FacilioField> fields = modBean.getAllFields(moduleName);
            List<FacilioField> lookupFields = fields.stream().filter(f -> f.getDataTypeEnum() == FieldType.LOOKUP).collect(Collectors.toList());

            SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                    .select(fields)
                    .module(module)
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                    .fetchSupplements(lookupFields)
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            ModuleBaseWithCustomFields moduleData = builder.fetchFirst();

            Map<String, Object> params = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
            String replacedString = StringSubstitutor.replace(formattedString, params);

            context.put(FacilioConstants.ContextNames.REPLACED_STRING, replacedString);
            context.put(FacilioConstants.ContextNames.WORK_FLOW_PARAMS, params);
            context.put(FacilioConstants.ContextNames.MODULE_DATA, moduleData);
        }
        return false;
    }
}
