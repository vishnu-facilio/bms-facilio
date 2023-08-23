package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.CustomButtonAppRelContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetCustomButtonAppCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WorkflowRuleContext> customButtonList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
        long ruleId = (long) context.get(FacilioConstants.ContextNames.ID);

        if (CollectionUtils.isEmpty(customButtonList)){
            return false;
        }

        CustomButtonRuleContext customButton = (CustomButtonRuleContext) customButtonList.get(0);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getCustomButtonAppRelModule().getTableName())
                .select(FieldFactory.getCustomButtonAppRelFields())
                .andCondition(CriteriaAPI.getCondition("CUSTOM_BUTTON_ID","customButtonId", String.valueOf(ruleId), NumberOperators.EQUALS));

        List<Map<String,Object>> customButtonAppPropList = builder.get();
        List<CustomButtonAppRelContext> customButtomAppList = customButtonAppPropList != null ? FieldUtil.getAsBeanListFromMapList(customButtonAppPropList, CustomButtonAppRelContext.class) : null;
        customButton.setCustomButtonAppRel(customButtomAppList);

        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE,customButton);

        return false;
    }
}
