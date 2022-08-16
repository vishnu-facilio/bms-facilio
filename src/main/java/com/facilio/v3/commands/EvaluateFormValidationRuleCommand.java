package com.facilio.v3.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class EvaluateFormValidationRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        List<Long> formIds = new ArrayList<>();
        for(ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            if(record.getFormId() > 0) {
                formIds.add(record.getFormId());
            }
        }

        if(CollectionUtils.isEmpty(formIds)) {
            return false;
        }

        Map<Long, List<ValidationContext>> formVsRuleMap= new HashMap<>();
        Set<Long> formIdsSet = formIds.stream().collect(Collectors.toSet());
        for(Long formId : formIdsSet) {
            List<ValidationContext> validations = ValidationRulesAPI.getValidationsByParentId(formId, ModuleFactory.getFormValidationRuleModule(), null, null);
            if(CollectionUtils.isNotEmpty(validations)) {
                formVsRuleMap.put(formId, validations);
            }
        }

        for(ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            if(record.getFormId() > 0 && formVsRuleMap.containsKey(record.getFormId())) {
                ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(record.getFormId()));
            }
        }

        return false;
    }

}
