package com.facilio.bmsconsoleV3.signup.moduleconfig;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import java.util.ArrayList;
import java.util.List;
public class AddWorkflowRuleLogModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        List<FacilioModule> moduleList=getWorkflowLogModules();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
        addModuleChain.execute();
    }
    public List<FacilioModule> getWorkflowLogModules() throws Exception{
        List<FacilioModule> modules=new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workflowRuleLogModule = new FacilioModule("workflowRuleLogs",
                "Workflow Rule Logs",
                "Workflow_Rule_Logs",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        addWorkflowRuleLogModuleFields(workflowRuleLogModule);
        modules.add(workflowRuleLogModule);
        FacilioModule addWorkflowRuleActionLogModule=new FacilioModule("workflowRuleActionLogs",
                "Workflow Rule Action Logs",
                "Workflow_Rule_Action_Logs",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        addWorkflowRuleActionLogModuleFields(addWorkflowRuleActionLogModule);
        modules.add(addWorkflowRuleActionLogModule);
        return modules;
    }
    private void addWorkflowRuleLogModuleFields(FacilioModule module) throws Exception
    {
        ModuleBean modBean=(ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fieldsList=new ArrayList<>();
        fieldsList.add(FieldFactory.getDefaultField("workflowRuleId","Workflow Rule Id","WORKFLOW_RULE_ID",FieldType.NUMBER));
        fieldsList.add(FieldFactory.getDefaultField("workflowRuleName","Workflow Rule Name","WORKFLOW_RULE_NAME",FieldType.STRING,true));
        fieldsList.add(FieldFactory.getDefaultField("recordId","Record Id","RECORD_ID",FieldType.NUMBER));
        fieldsList.add(FieldFactory.getDefaultField("recordModuleId","Record Module Id","RECORD_MODULE_ID",FieldType.NUMBER));
        fieldsList.add(FieldFactory.getDefaultField("executedOn","Executed On","EXECUTED_ON",FieldType.DATE_TIME));
        fieldsList.add(FieldFactory.getDefaultField("siteResult","Site Result","SITE_RESULT",FieldType.BOOLEAN));
        fieldsList.add(FieldFactory.getDefaultField("fieldChangeResult","Field Change Result","FIELD_CHANGE_RESULT",FieldType.BOOLEAN));
        fieldsList.add(FieldFactory.getDefaultField("miscResult","Misc Result","MISC_RESULT",FieldType.BOOLEAN));
        fieldsList.add(FieldFactory.getDefaultField("criteriaResult","Criteria Result","CRITERIA_RESULT",FieldType.BOOLEAN));
        fieldsList.add(FieldFactory.getDefaultField("workflowResult","Workflow Result","WORKFLOW_RESULT",FieldType.BOOLEAN));
        SystemEnumField workflowRuleType = FieldFactory.getDefaultField("workflowLoggableRuleType","Workflow Rule Type","WORKFLOW_RULE_TYPE",FieldType.SYSTEM_ENUM);
        workflowRuleType.setEnumName("WorkflowLoggableRuleType");
        fieldsList.add(workflowRuleType);
        SystemEnumField ruleStatus = FieldFactory.getDefaultField("ruleStatus","Rule Status","RULE_STATUS",FieldType.SYSTEM_ENUM);
        ruleStatus.setEnumName("WorkflowRuleStatus");
        fieldsList.add(ruleStatus);
        LookupField executedBy = (LookupField) FieldFactory.getDefaultField("executedBy", "Performed By", "EXECUTED_BY", FieldType.LOOKUP);
        executedBy.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.USERS));
        executedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fieldsList.add(executedBy);
        fieldsList.add(FieldFactory.getDefaultField("linkConfig","Link Config","LINK_CONFIG",FieldType.STRING));
        module.setFields(fieldsList);
    }
    private void addWorkflowRuleActionLogModuleFields(FacilioModule module)
    {
        List<FacilioField> fieldsList=new ArrayList<>();
        fieldsList.add(FieldFactory.getDefaultField("workflowRuleLogId","Workflow Rule Log Id","WORKFLOW_RULE_LOG_ID",FieldType.NUMBER,true));
        SystemEnumField actionType = FieldFactory.getDefaultField("actionType","Action Type","ACTION_TYPE",FieldType.SYSTEM_ENUM);
        actionType.setEnumName("RuleActionType");
        fieldsList.add(actionType);
        SystemEnumField actionStatus=FieldFactory.getDefaultField("actionStatus","Action Status","ACTION_STATUS",FieldType.SYSTEM_ENUM);
        actionStatus.setEnumName("RuleActionStatus");
        fieldsList.add(actionStatus);
        module.setFields(fieldsList);
    }
}