package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddKpiLoggerModules extends SignUpData {

    @Override
    public void addData() throws Exception {

        try {
            FacilioModule kpiModule = addKpiLoggerModule();
            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(kpiModule));
            addModuleChain.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FacilioModule resourceLoggerModule = addKpiResourceLoggerModule();
            FacilioChain addSystemModuleChain = TransactionChainFactory.addSystemModuleChain();
            addSystemModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(resourceLoggerModule));
            addSystemModuleChain.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FacilioModule addKpiLoggerModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule("kpiLogger",
                "KPI Logger",
                "KPI_Logger",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField kpiId =  FieldFactory.getDefaultField("kpi", "KPI", "KPI_ID", FieldType.LOOKUP);
        kpiId.setLookupModule(modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI));
        fields.add(kpiId);

        SystemEnumField kpiType = FieldFactory.getDefaultField("kpiType", "KPI Type", "KPI_TYPE", FieldType.SYSTEM_ENUM);
        kpiType.setEnumName("KPIType");
        fields.add(kpiType);

        SystemEnumField status = FieldFactory.getDefaultField("status", "KPI Status", "STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("KpiExecutionStatus");
        fields.add(status);

        BooleanField isSysCreated = FieldFactory.getDefaultField("isSysCreated", "Is System Created", "IS_SYS_CREATED", FieldType.BOOLEAN);
        fields.add(isSysCreated);

        NumberField startTime = FieldFactory.getDefaultField("startTime", "Interval Start Time", "START_TIME", FieldType.NUMBER);
        fields.add(startTime);

        NumberField endTime = FieldFactory.getDefaultField("endTime", "Interval End Time", "END_TIME", FieldType.NUMBER);
        fields.add(endTime);

        NumberField execStartTime = FieldFactory.getDefaultField("execStartTime", "Execution Start Time", "EXEC_START_TIME", FieldType.NUMBER);
        fields.add(execStartTime);

        NumberField execEndTime = FieldFactory.getDefaultField("execEndTime", "Execution End Time", "EXEC_END_TIME", FieldType.NUMBER);
        fields.add(execEndTime);

        NumberField resourceCount = FieldFactory.getDefaultField("resourceCount", "Resource Count", "RESOURCE_COUNT", FieldType.NUMBER);
        fields.add(resourceCount);

        NumberField successCount = FieldFactory.getDefaultField("successCount", "Success Count", "SUCCESS_COUNT", FieldType.NUMBER);
        fields.add(successCount);

        LookupField createdBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        createdBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(createdBy);

        NumberField createdTime = FieldFactory.getDefaultField("sysCreatedTime", "    Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
    }

    private FacilioModule addKpiResourceLoggerModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule("kpiResourceLogger",
                "KPI Resource Logger",
                "KPI_Resource_Logger",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField kpiId = FieldFactory.getDefaultField("kpiId", "Kpi ID", "KPI_ID", FieldType.NUMBER, true);
        fields.add(kpiId);

        LookupField parentLoggerId = FieldFactory.getDefaultField("parentLoggerId", "Parent KPI Logger ID", "PARENT_LOGGER_ID", FieldType.LOOKUP);
        parentLoggerId.setLookupModule(modBean.getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE));
        fields.add(parentLoggerId);

        NumberField resourceId = FieldFactory.getDefaultField("resourceId", "Resource ID", "RESOURCE_ID", FieldType.NUMBER);
        fields.add(resourceId);

        SystemEnumField status = FieldFactory.getDefaultField("status", "KPI Status", "STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("KpiExecutionStatus");
        fields.add(status);

        BooleanField isHistorical = FieldFactory.getDefaultField("isHistorical", "Is Historical", "IS_HISTORICAL", FieldType.BOOLEAN);
        fields.add(isHistorical);

        FacilioField message = FieldFactory.getDefaultField("message", "Message", "MESSAGE", FieldType.STRING);
        fields.add(message);

        NumberField startTime = FieldFactory.getDefaultField("startTime", "Interval Start Time", "START_TIME", FieldType.NUMBER);
        fields.add(startTime);

        NumberField endTime = FieldFactory.getDefaultField("endTime", "Interval End Time", "END_TIME", FieldType.NUMBER);
        fields.add(endTime);

        NumberField calculationStartTime = FieldFactory.getDefaultField("calculationStartTime", "Calculation Start Time", "CALCULATION_START_TIME", FieldType.NUMBER);
        fields.add(calculationStartTime);

        NumberField calculationEndTime = FieldFactory.getDefaultField("calculationEndTime", "Calculation End Time", "CALCULATION_END_TIME", FieldType.NUMBER);
        fields.add(calculationEndTime);

        LookupField createdBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        createdBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(createdBy);

        NumberField createdTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
    }

}
