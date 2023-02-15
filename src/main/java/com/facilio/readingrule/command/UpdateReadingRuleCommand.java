package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
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
        this.rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        updateAlarmDetails();
        updateNamespace();
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

    private void updateNamespace() throws Exception {
        NameSpaceContext ns = rule.getNs();
        if (ns != null) {
            Constants.getNsBean().updateNamespace(ns);
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
        for(NewReadingRuleContext newRule : newRules) {
            FacilioModule module = new FacilioModule();
            module.setModuleId(newRule.getReadingModuleId());
            module.setDisplayName(newRule.getName());
            modBean.updateModule(module);

            FacilioField field = new FacilioField();
            field.setFieldId(newRule.getReadingFieldId());
            field.setDisplayName(newRule.getName());
            modBean.updateField(field);
        }
    }
}
