package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderTicketStatus extends BaseModuleConfig {

    public ServiceOrderTicketStatus(){setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS);}

    @Override
    public void addData() throws Exception {
        addServiceOrderTicketStatus();
        addStatus();

    }

    private void addServiceOrderTicketStatus() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule serviceOrderTicketStatus = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS,"Service Order Ticket Status","ServiceOrder_TicketStatus", FacilioModule.ModuleType.PICK_LIST,false);
        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(serviceOrderTicketStatus,"status","Status",FacilioField.FieldDisplayType.TEXTBOX,"STATUS", FieldType.STRING,true,false,true,true));

        fields.add(new StringField(serviceOrderTicketStatus,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,false));

        fields.add(new StringField(serviceOrderTicketStatus,"color","Background Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,false,false,true,false));

        fields.add(new StringField(serviceOrderTicketStatus,"textColor","Text Color",FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR",FieldType.STRING,false,false,true,false));


        NumberField typeCode = FieldFactory.getDefaultField("typeCode", "Type", "STATUS_TYPE", FieldType.NUMBER);
        typeCode.setDefault(true);
        fields.add(typeCode);

        BooleanField recordLocked = FieldFactory.getDefaultField("recordLocked","Record Locked","RECORD_LOCKED",FieldType.BOOLEAN);
        recordLocked.setDefault(true);
        fields.add(recordLocked);

        BooleanField deleteLocked = FieldFactory.getDefaultField("deleteLocked","Delete Locked","DELETE_LOCKED",FieldType.BOOLEAN);
        deleteLocked.setDefault(true);
        fields.add(deleteLocked);

        serviceOrderTicketStatus.setFields(fields);
        modules.add(serviceOrderTicketStatus);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private void addStatus() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        InsertRecordBuilder<ServiceOrderTicketStatusContext> insertRecordBuilder = new InsertRecordBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .fields(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS));

        List<ServiceOrderTicketStatusContext> ticketStatusContextList = new ArrayList<>();

        ServiceOrderTicketStatusContext newState = new ServiceOrderTicketStatusContext();
        newState.setStatus(FacilioConstants.ServiceOrder.NEW);
        newState.setDisplayName("New");
        newState.setColor("default");
        newState.setTextColor("#ffffff");
        newState.setTypeCode(1);
        newState.setRecordLocked(false);
        newState.setDeleteLocked(false);
        ticketStatusContextList.add(newState);

        ServiceOrderTicketStatusContext scheduledState = new ServiceOrderTicketStatusContext();
        scheduledState.setStatus(FacilioConstants.ServiceOrder.SCHEDULED);
        scheduledState.setDisplayName("Scheduled");
        scheduledState.setColor("neutral");
        scheduledState.setTextColor("#ffffff");
        scheduledState.setTypeCode(1);
        scheduledState.setRecordLocked(false);
        scheduledState.setDeleteLocked(false);
        ticketStatusContextList.add(scheduledState);

        ServiceOrderTicketStatusContext inProgressState = new ServiceOrderTicketStatusContext();
        inProgressState.setStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
        inProgressState.setDisplayName("In Progress");
        inProgressState.setColor("#information");
        inProgressState.setTextColor("#ffffff");
        inProgressState.setTypeCode(1);
        inProgressState.setRecordLocked(false);
        inProgressState.setDeleteLocked(false);
        ticketStatusContextList.add(inProgressState);

        ServiceOrderTicketStatusContext completedState = new ServiceOrderTicketStatusContext();
        completedState.setStatus(FacilioConstants.ServiceOrder.COMPLETED);
        completedState.setDisplayName("Completed");
        completedState.setColor("#success");
        completedState.setTextColor("#ffffff");
        completedState.setTypeCode(2);
        completedState.setRecordLocked(false);
        completedState.setDeleteLocked(true);
        ticketStatusContextList.add(completedState);

        ServiceOrderTicketStatusContext closedState = new ServiceOrderTicketStatusContext();
        closedState.setStatus(FacilioConstants.ServiceOrder.CLOSED);
        closedState.setDisplayName("Closed");
        closedState.setColor("#success");
        closedState.setTextColor("#000000");
        closedState.setTypeCode(3);
        closedState.setRecordLocked(true);
        closedState.setDeleteLocked(true);
        ticketStatusContextList.add(closedState);

        ServiceOrderTicketStatusContext cancelledState = new ServiceOrderTicketStatusContext();
        cancelledState.setStatus(FacilioConstants.ServiceAppointment.CANCELLED);
        cancelledState.setDisplayName("Cancelled");
        cancelledState.setColor("#danger");
        cancelledState.setTextColor("#ffffff");
        cancelledState.setTypeCode(3);
        cancelledState.setRecordLocked(true);
        cancelledState.setDeleteLocked(true);
        ticketStatusContextList.add(cancelledState);

        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();
    }
}
