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
        FacilioModule serviceAppointmentTicketStatus = new FacilioModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS,"Appointment Ticket Status","ServiceAppointment_TicketStatus", FacilioModule.ModuleType.PICK_LIST,false);
        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(serviceAppointmentTicketStatus,"status","Status",FacilioField.FieldDisplayType.TEXTBOX,"STATUS", FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"displayName","Display Name",FacilioField.FieldDisplayType.TEXTBOX,"DISPLAY_NAME",FieldType.STRING,false,false,true,true));

        fields.add(new StringField(serviceAppointmentTicketStatus,"color","Color",FacilioField.FieldDisplayType.TEXTBOX,"COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"backgroundColor","Background Color",FacilioField.FieldDisplayType.TEXTBOX,"BACKGROUND_COLOR",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"textColor","Text Color",FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR",FieldType.STRING,false,true,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"backgroundColorHover","Background Color on Hover",FacilioField.FieldDisplayType.TEXTBOX,"BACKGROUND_COLOR_HOVER",FieldType.STRING,true,false,true,false));

        fields.add(new StringField(serviceAppointmentTicketStatus,"textColorHover","Text Color on Hover",FacilioField.FieldDisplayType.TEXTBOX,"TEXT_COLOR_HOVER",FieldType.STRING,true,false,true,false));

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
        scheduledState.setBackgroundColor("var(--colors-background-neutral-grey-01-light)");
        scheduledState.setBackgroundColorHover("var(--colors-background-neutral-grey-01-darker)");
        scheduledState.setTextColor("var(--colors-text-default)");
        scheduledState.setTextColorHover("var(--colors-text-inverse-default)");
        scheduledState.setTypeCode(1);
        scheduledState.setRecordLocked(false);
        scheduledState.setDeleteLocked(false);
        ticketStatusContextList.add(scheduledState);

        ServiceAppointmentTicketStatusContext dispatchedState = new ServiceAppointmentTicketStatusContext();

        dispatchedState.setStatus(FacilioConstants.ServiceAppointment.DISPATCHED);
        dispatchedState.setDisplayName("Dispatched");
        dispatchedState.setColor("default");
        dispatchedState.setBackgroundColor("var(--colors-background-accent-violet-light)");
        dispatchedState.setBackgroundColorHover("var(--colors-background-accent-violet-dark)");
        dispatchedState.setTextColor("var(--colors-text-default)");
        dispatchedState.setTextColorHover("var(--colors-text-inverse-default)");
        dispatchedState.setTypeCode(1);
        dispatchedState.setRecordLocked(false);
        dispatchedState.setDeleteLocked(false);
        ticketStatusContextList.add(dispatchedState);

        ServiceAppointmentTicketStatusContext enRouteState = new ServiceAppointmentTicketStatusContext();
        enRouteState.setStatus(FacilioConstants.ServiceAppointment.EN_ROUTE);
        enRouteState.setDisplayName("En Route");
        enRouteState.setColor("warning");
        enRouteState.setBackgroundColor("var(--colors-background-accent-yellow-light)");
        enRouteState.setBackgroundColorHover("var(--colors-icon-accent-yellow)");
        enRouteState.setTextColor("var(--colors-text-default)");
        enRouteState.setTextColorHover("var(--colors-text-default)");
        enRouteState.setTypeCode(1);
        enRouteState.setRecordLocked(false);
        enRouteState.setDeleteLocked(true);
        ticketStatusContextList.add(enRouteState);

        ServiceAppointmentTicketStatusContext inProgressState = new ServiceAppointmentTicketStatusContext();

        inProgressState.setStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);
        inProgressState.setDisplayName("In Progress");
        inProgressState.setColor("information");
        inProgressState.setBackgroundColor("var(--colors-background-accent-blue-light)");
        inProgressState.setBackgroundColorHover("var(--colors-background-accent-blue-dark)");
        inProgressState.setTextColor("var(--colors-text-default)");
        inProgressState.setTextColorHover("var(--colors-text-inverse-default)");
        inProgressState.setTypeCode(2);
        inProgressState.setRecordLocked(false);
        inProgressState.setDeleteLocked(true);
        ticketStatusContextList.add(inProgressState);

        ServiceAppointmentTicketStatusContext completedState = new ServiceAppointmentTicketStatusContext();

        completedState.setStatus(FacilioConstants.ServiceAppointment.COMPLETED);
        completedState.setDisplayName("Completed");
        completedState.setColor("success");
        completedState.setBackgroundColor("var(--colors-background-semantic-green-light)");
        completedState.setBackgroundColorHover("var(--colors-background-semantic-green-medium)");
        completedState.setTextColor("var(--colors-text-default)");
        completedState.setTextColorHover("var(--colors-text-default)");
        completedState.setTypeCode(3);
        completedState.setRecordLocked(true);
        completedState.setDeleteLocked(true);
        ticketStatusContextList.add(completedState);

        ServiceAppointmentTicketStatusContext cancelledState = new ServiceAppointmentTicketStatusContext();

        cancelledState.setStatus(FacilioConstants.ServiceAppointment.CANCELLED);
        cancelledState.setDisplayName("Cancelled");
        cancelledState.setColor("danger");
        cancelledState.setBackgroundColor("var(--colors-background-semantic-red-subtle)");
        cancelledState.setBackgroundColorHover("var(--colors-background-semantic-red-light)");
        cancelledState.setTextColor("var(--colors-text-default)");
        cancelledState.setTextColorHover("var(--colors-text-inverse-default)");
        cancelledState.setTypeCode(3);
        cancelledState.setRecordLocked(true);
        cancelledState.setDeleteLocked(true);
        ticketStatusContextList.add(cancelledState);


        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();


    }


}
