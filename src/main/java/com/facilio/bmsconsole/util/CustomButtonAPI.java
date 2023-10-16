package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CustomButtonAPI {
    public static List<WorkflowRuleContext> getCustomButtons(FacilioModule module, CustomButtonRuleContext.PositionType... positionTypes) throws Exception {
        if (positionTypes.length == 0) {
            throw new IllegalArgumentException("Position types should be given");
        }

        List<Integer> positionTypeInts = new ArrayList<>();
        for (CustomButtonRuleContext.PositionType positionType : positionTypes) {
            positionTypeInts.add(positionType.getIndex());
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE", "positionType",
                StringUtils.join(positionTypeInts, ","), NumberOperators.EQUALS));

        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        Criteria appCriteria = new Criteria();
        appCriteria.addAndCondition(CriteriaAPI.getCondition("APP_ID","appId","NULL",CommonOperators.IS_EMPTY));
        ApplicationContext app = AccountUtil.getCurrentApp();
        if (app != null) {
            appCriteria.addOrCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(app.getId()), NumberOperators.EQUALS));
        }
        criteria.andCriteria(appCriteria);

        String joinCondition = "CustomButton_App_Rel.CUSTOM_BUTTON_ID = " + ModuleFactory.getCustomButtonRuleModule().getTableName() + ".ID";
        List<WorkflowRuleContext> customButtons = WorkflowRuleAPI.getExtendedButtonRules(ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields(),
                ModuleFactory.getCustomButtonAppRelModule(),FieldFactory.getCustomButtonAppRelFields(),joinCondition,criteria, null,null,CustomButtonRuleContext.class);

        customButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(FieldUtil.getAsMapList(customButtons, CustomButtonRuleContext.class), CustomButtonRuleContext.class), true, true);
        return customButtons;
    }

    public static List<WorkflowRuleContext> getExecutableCustomButtons(List<WorkflowRuleContext> customButtons, String moduleName, ModuleBaseWithCustomFields record, Context context) throws Exception {
        List<WorkflowRuleContext> availableButtons = null;
        if (CollectionUtils.isNotEmpty(customButtons)) {
            availableButtons = new ArrayList<>();
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());

            Iterator<WorkflowRuleContext> iterator = customButtons.iterator();
            while (iterator.hasNext()) {
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (evaluate) {
                    availableButtons.add(workflowRule);
                }
            }
        }
        return availableButtons;
    }



    public static boolean isCustomButtonAppRelListForId(long customButtonId) throws Exception{

        if (customButtonId == -1l){
            return false;
        }
        boolean isPresent = false;

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(ModuleFactory.getCustomButtonAppRelModule().getTableName())
                .select(FieldFactory.getCustomButtonAppRelFields())
                .andCondition(CriteriaAPI.getCondition("CUSTOM_BUTTON_ID","customButtonId", String.valueOf(customButtonId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("APP_ID","appId", String.valueOf(0),NumberOperators.GREATER_THAN));

        List<Map<String, Object>> customButtonAppRelList = builder.get();

        if (customButtonAppRelList.size() > 0 ){
            isPresent = true;
        }

        return isPresent;
    }


    public static void deleteCustomButtonAppRelList(long customButtonId) throws Exception{

        if (customButtonId == -1l){
            return;
        }

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getCustomButtonAppRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("CUSTOM_BUTTON_ID","customButtonId", String.valueOf(customButtonId),NumberOperators.EQUALS));

        builder.delete();

    }
}
