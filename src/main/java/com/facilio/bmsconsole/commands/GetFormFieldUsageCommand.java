package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

public class GetFormFieldUsageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formFieldId = (long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
        String fieldName = null;

        GenericSelectRecordBuilder formFieldsFieldIdBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormFieldsModule().getTableName())
                .select(FieldFactory.getFormFieldsFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(formFieldId), NumberOperators.EQUALS));

        Map<String, Object> props = formFieldsFieldIdBuilder.fetchFirst();
        if(MapUtils.isEmpty(props)){
            throw new IllegalArgumentException("Field not found");
        }

        fieldName = props.get("displayName").toString();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFormRuleTriggerFieldFields())
                .table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELD_ID","fieldId",String.valueOf(formFieldId), NumberOperators.EQUALS));

        Map<String, Object> prop =  selectBuilder.fetchFirst();
        String ruleName = null;
        if (MapUtils.isNotEmpty(prop)){
            FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext((Long) prop.get("ruleId"));
            ruleName = formRuleContext.getName();
            throw new FacilioException(fieldName +" field is used in " +ruleName+" rule");
        }

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFormRuleActionFieldsFields());

        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormRuleActionModule().getTableName())
                .select(FieldFactory.getFormRuleActionFields()).leftJoin(ModuleFactory.getFormRuleActionFieldModule().getTableName())
                .on(ModuleFactory.getFormRuleActionFieldModule().getTableName() + ".FORM_RULE_ACTION_ID=" + ModuleFactory.getFormRuleActionModule().getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("formFieldId"),String.valueOf(formFieldId), NumberOperators.EQUALS));

        Map<String, Object> get = genericSelectRecordBuilder.fetchFirst();

        if (MapUtils.isNotEmpty(get)) {
            FormRuleContext formRuleContext = FormRuleAPI.getFormRuleContext((Long) get.get("formRuleId"));
            throw new FacilioException(fieldName +" field is used as action field in "+formRuleContext.getName() +" rule");

        }

        return false;
    }
}
