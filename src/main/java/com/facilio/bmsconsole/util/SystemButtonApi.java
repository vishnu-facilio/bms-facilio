package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemButtonApi {

    public static List<WorkflowRuleContext> getSystemButtons(FacilioModule module, CustomButtonRuleContext.PositionType... positionTypes) throws Exception{
        if(positionTypes.length == 0){
            throw new IllegalArgumentException("Position Type cannot be null");
        }
        List<Integer> positionTypeInts = new ArrayList<>();
        for (CustomButtonRuleContext.PositionType positionType : positionTypes) {
            positionTypeInts.add(positionType.getIndex());
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE","positionType", StringUtils.join(positionTypeInts,","),
                NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId",String.valueOf(module.getModuleId()),NumberOperators.EQUALS));

        List<WorkflowRuleContext> systemButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                criteria,null,null, SystemButtonRuleContext.class);
        systemButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtons,SystemButtonRuleContext.class),true,true);

        List<WorkflowRuleContext> systemButtonList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(systemButtons)) {
            systemButtonList.addAll(systemButtons.stream().filter(button -> !((SystemButtonRuleContext)button).isPermissionRequired()).collect(Collectors.toList()));
            for (WorkflowRuleContext systembutton : systemButtons) {
                if (((SystemButtonRuleContext) systembutton).isPermissionRequired()) {
                    String permissionAction = ((SystemButtonRuleContext) systembutton).getPermission();
                    WebTabContext webTab = AccountUtil.getCurrentTab();
                    long tabId = webTab != null ? webTab.getId() : -1;
                    boolean hasPermission = WebTabUtil.currentUserHasPermission(tabId, permissionAction);
                    if (hasPermission) {
                        systemButtonList.add(systembutton);
                    }
                }
            }
        }

        return systemButtonList;
    }

    public static List<WorkflowRuleContext> getExecutableSystemButtons(List<WorkflowRuleContext> systemButtons, String moduleName, ModuleBaseWithCustomFields record, Context context)throws Exception{
        List<WorkflowRuleContext> availableSystemButtons = null;
        if(CollectionUtils.isNotEmpty(systemButtons)){
            availableSystemButtons = new ArrayList<>();
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
            Iterator<WorkflowRuleContext> iterator = systemButtons.iterator();;
            while (iterator.hasNext()){
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (evaluate) {
                    availableSystemButtons.add(workflowRule);
                }
            }
        }

        return availableSystemButtons;

    }

    public static WorkflowRuleContext getSystemButton(FacilioModule module, String identifier) throws Exception{
        if (identifier == null){
            return null;
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId",String.valueOf(module.getModuleId()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("IDENTIFIER","identifier",identifier, StringOperators.IS));

        List<WorkflowRuleContext> systemButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                criteria,null,null, SystemButtonRuleContext.class);

        systemButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtons,SystemButtonRuleContext.class),true,true);

        WorkflowRuleContext systemButton = CollectionUtils.isNotEmpty(systemButtons) ? systemButtons.get(0) : null;
        return systemButton;
    }

    public static void addSystemButton(String moduleName, SystemButtonRuleContext rule) throws Exception {
        if(rule == null){
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        rule.setRuleType(WorkflowRuleContext.RuleType.SYSTEM_BUTTON);
        rule.setActivityType(EventType.CUSTOM_BUTTON);
        String identifier = rule.getIdentifier();
        identifier = identifier.replaceAll("\\s+","");
        rule.setIdentifier(identifier);

        if (module != null){
            rule.setModule(module);
        }

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();;
        FacilioContext workflowContext = chain.getContext();
        workflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
        chain.execute();
    }
}
