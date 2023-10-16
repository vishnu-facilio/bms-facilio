package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateReadingRuleCommand extends FacilioCommand {
    NewReadingRuleContext rule;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.rule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);

        updateAlarmDetails();
        updateNamespaceStatus();
        updateReadingModuleName(context);

        return false;
    }

    private void updateAlarmDetails() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
        if (rule.getAlarmDetails() != null) {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getRuleAlarmDetailsFields())
                    .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rule.getAlarmDetails().getId()), NumberOperators.EQUALS));
            updateBuilder.update(FieldUtil.getAsProperties(rule.getAlarmDetails()));
        }
    }

    private void updateNamespaceStatus() throws Exception {
        List<NSType> nsTypeList = new ArrayList<>();
        nsTypeList.add(NSType.READING_RULE);
        nsTypeList.add(NSType.FAULT_IMPACT_RULE);
        Constants.getNsBean().updateNsStatus(rule.getId(), rule.getStatus(), nsTypeList);
    }

    private void updateReadingModuleName(Context context) throws Exception {
        List<NewReadingRuleContext> newRules = (List<NewReadingRuleContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ReadingRules.NEW_READING_RULE));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (NewReadingRuleContext newRule : newRules) {
            FacilioModule module = new FacilioModule();
            module.setModuleId(newRule.getReadingModuleId());
            module.setDisplayName(newRule.getName());
            modBean.updateModule(module);

            List<FacilioField> moduleFields = modBean.getAllFields(modBean.getModule(newRule.getReadingModuleId()).getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(moduleFields);
            List<FacilioField> fields = new ArrayList<>();
            fields.add(fieldMap.get(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_RESULT));
            fields.add(fieldMap.get(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_COST_IMPACT));
            fields.add(fieldMap.get(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_ENERGY_IMPACT));

            for (FacilioField field : fields) {
                FacilioField facilioField = new FacilioField();
                facilioField.setFieldId(field.getFieldId());
                switch (field.getName()) {
                    case "ruleResult":
                        facilioField.setDisplayName(newRule.getName());
                        break;
                    case "costImpact":
                        facilioField.setDisplayName(newRule.getName() + " - Cost Impact");
                        break;
                    case "energyImpact":
                        facilioField.setDisplayName(newRule.getName() + " - Energy Impact");
                        break;
                }
                modBean.updateField(facilioField);
            }
        }
    }
}