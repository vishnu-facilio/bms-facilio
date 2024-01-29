package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddReadingKpiLogsModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        createReadingKpiLogsModule();
        createKpiLogsChildModules();
        addJobForCleanup();
    }

    private void addJobForCleanup() throws Exception {
        FacilioTimer.schedulePeriodicJob(AccountUtil.getCurrentOrg().getId(), "ReadingKpiLogsCleanUp", 5260000, 5260000, "facilio");
    }

    private void createReadingKpiLogsModule() throws Exception {
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(composeReadingKpiLogsModule()));
        addModuleChain.execute();
    }
    private void createKpiLogsChildModules() throws Exception {
        FacilioModule readingKpiLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingKpi.READING_KPI_LOGS_MODULE);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(composeKpiScriptLogsModule()));
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, readingKpiLogsModule.getName());
        addModuleChain.execute();

        FacilioModule kpiScriptLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingKpi.KPI_SCRIPT_LOGS_MODULE);
        persistScriptLogsForRule(readingKpiLogsModule, kpiScriptLogsModule);
    }
    private void persistScriptLogsForRule(FacilioModule readingKpiLogsModule, FacilioModule kpiScriptLogsModule) throws Exception {
        LargeTextField scriptLogs = FieldFactory.getDefaultField("scriptLogs", "Script Logs", null, FieldType.LARGE_TEXT);
        scriptLogs.setModule(readingKpiLogsModule);
        scriptLogs.setRelModuleId(kpiScriptLogsModule.getModuleId());
        Constants.getModBean().addField(scriptLogs);
    }
    private FacilioModule composeReadingKpiLogsModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.ReadingKpi.READING_KPI_LOGS_MODULE,
                "Reading KPI Logs",
                "ReadingKPI_Logs",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        List<FacilioField> fields = new ArrayList<>();

        LookupField kpi = (LookupField) FieldFactory.getDefaultField("kpi", "KPI", "KPI_ID", FieldType.LOOKUP);
        kpi.setSpecialType(FacilioConstants.ReadingKpi.READING_KPI);
        fields.add(kpi);

        FacilioField kpiExecTime = (FacilioField) FieldFactory.getDefaultField("kpiExecTime", "Time of Execution", "KPI_EXEC_TIME", FieldType.DATE_TIME);
        fields.add(kpiExecTime);

        FacilioField resourceName = (FacilioField) FieldFactory.getDefaultField("resourceName", "Resource Name", "RESOURCE_NAME", FieldType.STRING);
        fields.add(resourceName);

        FacilioField kpiResult = (FacilioField) FieldFactory.getDefaultField("kpiResult", "KPI Result", "KPI_RESULT", FieldType.DECIMAL);
        fields.add(kpiResult);

        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
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
    private FacilioModule composeKpiScriptLogsModule() throws Exception {
        FacilioModule kpiLogsModule = Constants.getModBean().getModule(FacilioConstants.ReadingKpi.READING_KPI_LOGS_MODULE);

        FacilioModule module = new FacilioModule(FacilioConstants.ReadingKpi.KPI_SCRIPT_LOGS_MODULE,
                "KPI Script Logs",
                "KPI_Script_Logs",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(kpiLogsModule);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));
        module.setFields(fields);
        return module;
    }
}
