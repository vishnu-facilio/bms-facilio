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
        ServiceAppointmentTicketStatusContext ticketStatus = new ServiceAppointmentTicketStatusContext();
        ticketStatus.setStatus("new");
        ticketStatus.setDisplayName("New");
        ticketStatus.setColor("#0492AE");
        ticketStatus.setTypeCode(1);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);

        ticketStatus.setStatus("scheduled");
        ticketStatus.setDisplayName("Scheduled");
        ticketStatus.setColor("#51049F");
        ticketStatus.setTypeCode(1);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);

        ticketStatus.setStatus("dispatched");
        ticketStatus.setDisplayName("Dispatched");
        ticketStatus.setColor("#C70566");
        ticketStatus.setTypeCode(1);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);

        ticketStatus.setStatus("inProgress");
        ticketStatus.setDisplayName("In Progress");
        ticketStatus.setColor("#F7BA02");
        ticketStatus.setTypeCode(2);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);

        ticketStatus.setStatus("completed");
        ticketStatus.setDisplayName("Completed");
        ticketStatus.setColor("#058545");
        ticketStatus.setTypeCode(3);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);

        ticketStatus.setStatus("cancelled");
        ticketStatus.setDisplayName("Cancelled");
        ticketStatus.setColor("#D12806");
        ticketStatus.setTypeCode(3);
        ticketStatus.setRecordLocked(false);
        ticketStatusContextList.add(ticketStatus);


        insertRecordBuilder.addRecords(ticketStatusContextList);
        insertRecordBuilder.save();


    }


}
