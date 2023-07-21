package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceAppointmentTicketStatus extends BaseModuleConfig {
    public ServiceAppointmentTicketStatus(){setModuleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS);}

    @Override
    public void addData() throws Exception {
        addServiceAppointmentTicketStatus();
        addStatus();

    }
    private void addServiceAppointmentTicketStatus() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule serviceAppointmentTicketStatus = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS,"Service Appointment Ticket Status","ServiceAppointment_TicketStatus", FacilioModule.ModuleType.PICK_LIST,false);
        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(serviceAppointmentTicketStatus,"status","Status",FacilioField.FieldDisplayType.TEXTBOX,"STATUS", FieldType.STRING,true,false,true,true));

        fields.add(new StringField(serviceAppointmentTicketStatus,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"color","Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,false,false,true,false));

        NumberField typeCode = FieldFactory.getDefaultField("typeCode", "Type", "STATUS_TYPE", FieldType.NUMBER);
        typeCode.setDefault(true);
        fields.add(typeCode);

        BooleanField recordLocked = FieldFactory.getDefaultField("recordLocked","Record Locked","RECORD_LOCKED",FieldType.BOOLEAN);
        recordLocked.setDefault(true);
        fields.add(recordLocked);

        serviceAppointmentTicketStatus.setFields(fields);
        modules.add(serviceAppointmentTicketStatus);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private void addStatus() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        InsertRecordBuilder<ServiceAppointmentTicketStatusContext> insertRecordBuilder = new InsertRecordBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .fields(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS));

        List<ServiceAppointmentTicketStatusContext> ticketStatusContextList = new ArrayList<>();
        ServiceAppointmentTicketStatusContext newState = new ServiceAppointmentTicketStatusContext();
        newState.setStatus("new");
        newState.setDisplayName("New");
        newState.setColor("#0492AE");
        newState.setTypeCode(1);
        newState.setRecordLocked(false);
        ticketStatusContextList.add(newState);

        ServiceAppointmentTicketStatusContext scheduledState = new ServiceAppointmentTicketStatusContext();

        scheduledState.setStatus("scheduled");
        scheduledState.setDisplayName("Scheduled");
        scheduledState.setColor("#51049F");
        scheduledState.setTypeCode(1);
        scheduledState.setRecordLocked(false);
        ticketStatusContextList.add(scheduledState);

        ServiceAppointmentTicketStatusContext dispatchedState = new ServiceAppointmentTicketStatusContext();

        dispatchedState.setStatus("dispatched");
        dispatchedState.setDisplayName("Dispatched");
        dispatchedState.setColor("#C70566");
        dispatchedState.setTypeCode(1);
        dispatchedState.setRecordLocked(false);
        ticketStatusContextList.add(dispatchedState);

        ServiceAppointmentTicketStatusContext inProgressState = new ServiceAppointmentTicketStatusContext();

        inProgressState.setStatus("inProgress");
        inProgressState.setDisplayName("In Progress");
        inProgressState.setColor("#F7BA02");
        inProgressState.setTypeCode(2);
        inProgressState.setRecordLocked(false);
        ticketStatusContextList.add(inProgressState);

        ServiceAppointmentTicketStatusContext completedState = new ServiceAppointmentTicketStatusContext();

        completedState.setStatus("completed");
        completedState.setDisplayName("Completed");
        completedState.setColor("#058545");
        completedState.setTypeCode(3);
        completedState.setRecordLocked(false);
        ticketStatusContextList.add(completedState);

        ServiceAppointmentTicketStatusContext cancelledState = new ServiceAppointmentTicketStatusContext();

        cancelledState.setStatus("cancelled");
        cancelledState.setDisplayName("Cancelled");
        cancelledState.setColor("#D12806");
        cancelledState.setTypeCode(3);
        cancelledState.setRecordLocked(false);
        ticketStatusContextList.add(cancelledState);


        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();


    }


}
