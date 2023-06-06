package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetFormFieldUsageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formFieldId = (long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
        String fieldName = null;
        List<String> formFieldUsage = new ArrayList<>();

        FormField formField = FormsAPI.getFormFieldFromId(formFieldId);

        context.put(FacilioConstants.ContextNames.FORM_FIELD,FieldUtil.getAsProperties(formField));
        if (formField == null) {
            throw new IllegalArgumentException("Field not found");
        }

        fieldName = formField.getDisplayName();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFormRuleTriggerFieldFields())
                .table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", String.valueOf(formFieldId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        String ruleName = null;

        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext((Long) prop.get("ruleId"));
                ruleName = formRuleContext.getName();
                formFieldUsage.add(fieldName +" field is used as trigger field in " +ruleName+" rule");
            }
        }

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFormRuleActionFieldsFields());

        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormRuleActionModule().getTableName())
                .select(FieldFactory.getFormRuleActionFields()).leftJoin(ModuleFactory.getFormRuleActionFieldModule().getTableName())
                .on(ModuleFactory.getFormRuleActionFieldModule().getTableName() + ".FORM_RULE_ACTION_ID=" + ModuleFactory.getFormRuleActionModule().getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("formFieldId"), String.valueOf(formFieldId), NumberOperators.EQUALS));

        List<Map<String, Object>> gets = genericSelectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(gets)) {
            for (Map<String, Object> prop : gets) {
                FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext((Long) prop.get("formRuleId"));
                formFieldUsage.add(fieldName +" field is used as action field in "+formRuleContext.getName() +" rule");
            }
        }

        if(CollectionUtils.isNotEmpty(formFieldUsage)){
            context.put(FacilioConstants.FormContextNames.FORM_RULE_USAGE,formFieldUsage);
            return true;
        }

        return false;
    }
}
