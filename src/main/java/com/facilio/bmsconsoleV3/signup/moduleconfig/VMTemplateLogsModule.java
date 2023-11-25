package com.facilio.bmsconsoleV3.signup.moduleconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;

public class VMTemplateLogsModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        
    	FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(composeVMLogsModule()));
        addModuleChain.execute();
        
        FacilioModule kpiModule = addVMLoggerModule();
        addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(kpiModule));
        addModuleChain.execute();
        
        
        FacilioModule resourceLoggerModule = addVMResourceLoggerModule();
        FacilioChain addSystemModuleChain = TransactionChainFactory.addSystemModuleChain();
        addSystemModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(resourceLoggerModule));
        addSystemModuleChain.execute();
    }


	private FacilioModule addVMLoggerModule() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_LOGGER,
                "VM Logger",
                "KPI_Logger",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField vmReading =  FieldFactory.getDefaultField("vmReading", "VM Reading", "KPI_ID", FieldType.LOOKUP);
        vmReading.setLookupModule(modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING));
        fields.add(vmReading);

        SystemEnumField kpiType = FieldFactory.getDefaultField("kpiType", "VM Type", "KPI_TYPE", FieldType.SYSTEM_ENUM);
        kpiType.setEnumName("KPIType");
        fields.add(kpiType);

        SystemEnumField status = FieldFactory.getDefaultField("status", "VM Status", "STATUS", FieldType.SYSTEM_ENUM);
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

        NumberField createdTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
		
	}

	private FacilioModule addVMResourceLoggerModule() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_RESOURCE_LOGGER,
                "VM Resource Logger",
                "KPI_Resource_Logger",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField kpiId = FieldFactory.getDefaultField("vmId", "VM ID", "KPI_ID", FieldType.NUMBER, true);
        fields.add(kpiId);

        LookupField parentLoggerId = FieldFactory.getDefaultField("parentLoggerId", "Parent VM Logger ID", "PARENT_LOGGER_ID", FieldType.LOOKUP);
        parentLoggerId.setLookupModule(modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_LOGGER));
        fields.add(parentLoggerId);

        NumberField resourceId = FieldFactory.getDefaultField("resourceId", "Resource ID", "RESOURCE_ID", FieldType.NUMBER);
        fields.add(resourceId);

        SystemEnumField status = FieldFactory.getDefaultField("status", "VM Status", "STATUS", FieldType.SYSTEM_ENUM);
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

	private Object composeVMLogsModule() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_READING_LOGS,
                "VM Reading Logs",
                "ReadingKPI_Logs",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        List<FacilioField> fields = new ArrayList<>();

        FacilioField kpiName = (FacilioField) FieldFactory.getDefaultField("kpiName", "KPI Name", "KPI_NAME", FieldType.STRING);
        fields.add(kpiName);

        LookupField kpi = (LookupField) FieldFactory.getDefaultField("vmReading", "VM Reading", "KPI_ID", FieldType.LOOKUP);
        kpi.setLookupModule(modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING));
        fields.add(kpi);

        FacilioField kpiExecTime = (FacilioField) FieldFactory.getDefaultField("kpiExecTime", "Time of Execution", "KPI_EXEC_TIME", FieldType.DATE_TIME);
        fields.add(kpiExecTime);

        FacilioField resourceName = (FacilioField) FieldFactory.getDefaultField("resourceName", "Resource Name", "RESOURCE_NAME", FieldType.STRING);
        fields.add(resourceName);

        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE_ID", FieldType.LOOKUP);
        resource.setSpecialType(FacilioConstants.ContextNames.RESOURCE);
        fields.add(resource);

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

}
