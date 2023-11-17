package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceTaskStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceTaskStatusModule extends BaseModuleConfig {
    public ServiceTaskStatusModule(){setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS);}

    @Override
    public void addData() throws Exception {
        addServiceTaskStatus();
        addStatusValues();
    }
    private void addServiceTaskStatus()throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS, "Task Status", "Service_Task_Status", FacilioModule.ModuleType.BASE_ENTITY);
        List<FacilioField> fields = new ArrayList<>();

        FacilioField status = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        status.setRequired(true);
        status.setMainField(true);
        fields.add(status);

        fields.add(FieldFactory.getDefaultField("displayName","Display Name","DISPLAY_NAME",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("statusType","Status Type","STATUS_TYPE",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("recordLocked","Record Locked","RECORD_LOCKED",FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("deleteLocked","Delete Locked","DELETE_LOCKED",FieldType.BOOLEAN));
        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();
    }
    private void addStatusValues()throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        InsertRecordBuilder<ServiceTaskStatusContext> insertRecordBuilder = new InsertRecordBuilder<ServiceTaskStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS)
                .fields(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS));

        List<ServiceTaskStatusContext> statusList = new ArrayList<>();

        ServiceTaskStatusContext newState = new ServiceTaskStatusContext();
        newState.setName(FacilioConstants.ContextNames.ServiceTaskStatus.NEW);
        newState.setDisplayName("New");
        newState.setStatusType("default");
        newState.setRecordLocked(false);
        newState.setDeleteLocked(false);
        statusList.add(newState);

        ServiceTaskStatusContext scheduled = new ServiceTaskStatusContext();
        scheduled.setName(FacilioConstants.ContextNames.ServiceTaskStatus.SCHEDULED);
        scheduled.setDisplayName("Scheduled");
        scheduled.setStatusType("information");
        scheduled.setRecordLocked(false);
        scheduled.setDeleteLocked(false);
        statusList.add(scheduled);

        ServiceTaskStatusContext dispatched = new ServiceTaskStatusContext();
        dispatched.setName(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
        dispatched.setDisplayName("Dispatched");
        dispatched.setStatusType("information");
        dispatched.setRecordLocked(false);
        dispatched.setDeleteLocked(false);
        statusList.add(dispatched);


        ServiceTaskStatusContext inProgress = new ServiceTaskStatusContext();
        inProgress.setName(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        inProgress.setDisplayName("In Progress");
        inProgress.setStatusType("information");
        inProgress.setRecordLocked(true);
        inProgress.setDeleteLocked(true);
        statusList.add(inProgress);


        ServiceTaskStatusContext onHold = new ServiceTaskStatusContext();
        onHold.setName(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);
        onHold.setDisplayName("On Hold");
        onHold.setStatusType("warning");
        onHold.setRecordLocked(true);
        onHold.setDeleteLocked(true);
        statusList.add(onHold);

        ServiceTaskStatusContext completed = new ServiceTaskStatusContext();
        completed.setName(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        completed.setDisplayName("Completed");
        completed.setStatusType("success");
        completed.setRecordLocked(true);
        completed.setDeleteLocked(true);
        statusList.add(completed);

        ServiceTaskStatusContext cancelled = new ServiceTaskStatusContext();
        cancelled.setName(FacilioConstants.ContextNames.ServiceTaskStatus.CANCELLED);
        cancelled.setDisplayName("Cancelled");
        cancelled.setStatusType("default");
        cancelled.setRecordLocked(true);
        cancelled.setDeleteLocked(true);
        statusList.add(cancelled);

        insertRecordBuilder.addRecords(statusList);
        insertRecordBuilder.save();
    }

}
