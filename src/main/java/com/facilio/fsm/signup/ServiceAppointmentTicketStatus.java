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

        fields.add(new StringField(serviceAppointmentTicketStatus,"status","Status",FacilioField.FieldDisplayType.TEXTBOX,"STATUS", FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,true));

        fields.add(new StringField(serviceAppointmentTicketStatus,"color","Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"backgroundColor","Background Color",FacilioField.FieldDisplayType.TEXTBOX,"BACKGROUND_COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"textColor","Text Color",FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR",FieldType.STRING,false,false,true,false));


        NumberField typeCode = FieldFactory.getDefaultField("typeCode", "Type", "STATUS_TYPE", FieldType.NUMBER);
        typeCode.setDefault(true);
        fields.add(typeCode);

        BooleanField recordLocked = FieldFactory.getDefaultField("recordLocked","Record Locked","RECORD_LOCKED",FieldType.BOOLEAN);
        recordLocked.setDefault(true);
        fields.add(recordLocked);

        BooleanField deleteLocked = FieldFactory.getDefaultField("deleteLocked","Delete Locked","DELETE_LOCKED",FieldType.BOOLEAN);
        deleteLocked.setDefault(true);
        fields.add(deleteLocked);

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

        ServiceAppointmentTicketStatusContext scheduledState = new ServiceAppointmentTicketStatusContext();

        scheduledState.setStatus(FacilioConstants.ServiceAppointment.SCHEDULED);
        scheduledState.setDisplayName("Scheduled");
        scheduledState.setColor("default");
        scheduledState.setBackgroundColor("#51049F");
        scheduledState.setTextColor("#ffffff");
        scheduledState.setTypeCode(1);
        scheduledState.setRecordLocked(false);
        scheduledState.setDeleteLocked(false);
        ticketStatusContextList.add(scheduledState);

        ServiceAppointmentTicketStatusContext dispatchedState = new ServiceAppointmentTicketStatusContext();

        dispatchedState.setStatus(FacilioConstants.ServiceAppointment.DISPATCHED);
        dispatchedState.setDisplayName("Dispatched");
        dispatchedState.setColor("default");
        dispatchedState.setBackgroundColor("#C70566");
        dispatchedState.setTextColor("#ffffff");
        dispatchedState.setTypeCode(1);
        dispatchedState.setRecordLocked(false);
        scheduledState.setDeleteLocked(false);
        ticketStatusContextList.add(dispatchedState);

        ServiceAppointmentTicketStatusContext enRouteState = new ServiceAppointmentTicketStatusContext();
        enRouteState.setStatus(FacilioConstants.ServiceAppointment.EN_ROUTE);
        enRouteState.setDisplayName("En Route");
        enRouteState.setColor("warning");
        enRouteState.setBackgroundColor("#0492AE");
        enRouteState.setTextColor("#ffffff");
        enRouteState.setTypeCode(1);
        enRouteState.setRecordLocked(false);
        scheduledState.setDeleteLocked(true);
        ticketStatusContextList.add(enRouteState);

        ServiceAppointmentTicketStatusContext inProgressState = new ServiceAppointmentTicketStatusContext();

        inProgressState.setStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);
        inProgressState.setDisplayName("In Progress");
        inProgressState.setBackgroundColor("#F7BA02");
        inProgressState.setColor("information");
        inProgressState.setTextColor("#000000");
        inProgressState.setTypeCode(2);
        inProgressState.setRecordLocked(false);
        scheduledState.setDeleteLocked(true);
        ticketStatusContextList.add(inProgressState);

        ServiceAppointmentTicketStatusContext completedState = new ServiceAppointmentTicketStatusContext();

        completedState.setStatus(FacilioConstants.ServiceAppointment.COMPLETED);
        completedState.setDisplayName("Completed");
        completedState.setColor("success");
        completedState.setBackgroundColor("#058545");
        completedState.setTextColor("#ffffff");
        completedState.setTypeCode(3);
        completedState.setRecordLocked(true);
        scheduledState.setDeleteLocked(true);
        ticketStatusContextList.add(completedState);

        ServiceAppointmentTicketStatusContext cancelledState = new ServiceAppointmentTicketStatusContext();

        cancelledState.setStatus(FacilioConstants.ServiceAppointment.CANCELLED);
        cancelledState.setDisplayName("Cancelled");
        cancelledState.setColor("danger");
        cancelledState.setBackgroundColor("#D12806");
        cancelledState.setTextColor("#ffffff");
        cancelledState.setTypeCode(3);
        cancelledState.setRecordLocked(true);
        scheduledState.setDeleteLocked(true);
        ticketStatusContextList.add(cancelledState);


        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();


    }


}
