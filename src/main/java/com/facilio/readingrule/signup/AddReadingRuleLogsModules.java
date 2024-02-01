package com.facilio.readingrule.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddReadingRuleLogsModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        createReadingRuleLogsModule();
        createRuleLogsChildModules();
        addJobForCleanup();
    }

    private void addJobForCleanup() throws Exception {
        ScheduleInfo schedule = CommonConnectedUtil.getScheduleForLogsCleanup();
        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "ReadingRuleLogsCleanUp", System.currentTimeMillis(), schedule, "facilio");
    }

    private void createReadingRuleLogsModule() throws Exception {
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(composeReadingRuleLogsModule()));
        addModuleChain.execute();
    }
    private void createRuleLogsChildModules() throws Exception {
        FacilioModule readingRuleLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingRules.READING_RULE_LOGS_MODULE);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(composeRuleScriptLogsModule()));
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, readingRuleLogsModule.getName());
        addModuleChain.execute();

        FacilioModule ruleScriptLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingRules.RULE_SCRIPT_LOGS_MODULE);
        persistScriptLogsForRule(ruleScriptLogsModule, readingRuleLogsModule);
    }
    private void persistScriptLogsForRule(FacilioModule ruleScriptLogsModule, FacilioModule readingRuleLogsModule) throws Exception {
        LargeTextField scriptLogs = FieldFactory.getDefaultField("scriptLogs", "Script Logs", null, FieldType.LARGE_TEXT);
        scriptLogs.setModule(readingRuleLogsModule);
        scriptLogs.setRelModuleId(ruleScriptLogsModule.getModuleId());
        Constants.getModBean().addField(scriptLogs);
    }
    private FacilioModule composeReadingRuleLogsModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.READING_RULE_LOGS_MODULE,
                "Reading Rule Logs",
                "ReadingRule_Logs",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        List<FacilioField> fields = new ArrayList<>();


        LookupField rule = (LookupField) FieldFactory.getDefaultField("rule", "Rule", "RULE_ID", FieldType.LOOKUP);
        rule.setSpecialType(FacilioConstants.ReadingRules.NEW_READING_RULE);
        fields.add(rule);

        FacilioField ruleExecTime = (FacilioField) FieldFactory.getDefaultField("ruleExecTime", "Time of Execution", "RULE_EXEC_TIME", FieldType.DATE_TIME);
        fields.add(ruleExecTime);

        FacilioField resourceName = (FacilioField) FieldFactory.getDefaultField("resourceName", "Resource Name", "RESOURCE_NAME", FieldType.STRING);
        fields.add(resourceName);

        BooleanField ruleResult = (BooleanField) FieldFactory.getDefaultField("ruleResult", "Rule Result", "RULE_RESULT", FieldType.BOOLEAN);
        fields.add(ruleResult);

        FacilioField costImpact = (FacilioField) FieldFactory.getDefaultField("costImpact", "Cost Impact", "COST_IMPACT", FieldType.DECIMAL);
        fields.add(costImpact);

        FacilioField energyImpact = (FacilioField) FieldFactory.getDefaultField("energyImpact", "Energy Impact", "ENERGY_IMPACT", FieldType.DECIMAL);
        fields.add(energyImpact);

        BooleanField isImpactLog = (BooleanField) FieldFactory.getDefaultField("isImpactLog", "Impact Log", "IMPACT_LOG", FieldType.BOOLEAN);
        fields.add(isImpactLog);

        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status","Status","STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("ExecutionStatus");
        fields.add(status);

        NumberField duration = (NumberField) FieldFactory.getDefaultField("duration", "Duration", "DURATION", FieldType.NUMBER);
        fields.add(duration);

        FacilioField sysCreatedTime = (FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME);
        fields.add(sysCreatedTime);

        FacilioField varInfo = (FacilioField) FieldFactory.getDefaultField("varInfo", "Variable Info", "VAR_INFO", FieldType.BIG_STRING);
        fields.add(varInfo);

        FacilioField errorCode = (FacilioField) FieldFactory.getDefaultField("errorCode", "Error Code", "ERROR_CODE", FieldType.STRING);
        fields.add(errorCode);

        module.setFields(fields);
        return module;
    }
    private FacilioModule composeRuleScriptLogsModule() throws Exception {
        FacilioModule ruleLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingRules.READING_RULE_LOGS_MODULE);

        FacilioModule module = new FacilioModule(FacilioConstants.ReadingRules.RULE_SCRIPT_LOGS_MODULE,
                "Rule Script Logs",
                "Rule_Script_Logs",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(ruleLogsModule);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));
        module.setFields(fields);
        return module;
    }
}
