package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class GetCountForWorkflowRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Integer ruleType = (Integer) context.get(FacilioConstants.ContextNames.RULE_TYPE);

        if (StringUtils.isEmpty(moduleName) && ruleType <= 0){
            return false;
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        if (module == null){
            throw new IllegalArgumentException("Module cannot be null");
        }

        List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField ruleTypeField = fieldMap.get("ruleType");
        FacilioField moduleIdField = fieldMap.get("moduleId");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getCountField())
                .table(ModuleFactory.getWorkflowRuleModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(ruleTypeField, String.valueOf(ruleType), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(moduleIdField,String.valueOf(module.getModuleId()),NumberOperators.EQUALS));

        Map<String, Object> workflowRuleMap = builder.fetchFirst();
        long count = MapUtils.isNotEmpty(workflowRuleMap) ? (long) workflowRuleMap.get("count") : 0;

        context.put(FacilioConstants.ContextNames.COUNT,count);
        return false;
    }
}
