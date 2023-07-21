package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ServiceAppointmentUtil {
    public static void validateDispatch(Long appointmentId, Map<String, Object> mapping) throws Exception{

    }
    public static ServiceAppointmentTicketStatusContext getStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .andCustomWhere("STATUS = ?", status)
                .orderBy("ID");

        List<ServiceAppointmentTicketStatusContext> statuses = builder.get();
        if (CollectionUtils.isNotEmpty(statuses)) {
            return statuses.get(0);
        }
        return null;
    }

    public static List<ServiceAppointmentTicketStatusContext> getStatusOfStatusType(ServiceAppointmentTicketStatusContext.StatusType statusType) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SelectRecordsBuilder<ServiceAppointmentTicketStatusContext> builder = new SelectRecordsBuilder<ServiceAppointmentTicketStatusContext>()
                .moduleName(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS)
                .beanClass(ServiceAppointmentTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS))
                .andCustomWhere("STATUS_TYPE = ?", statusType.getIndex())
                .orderBy("ID");
        List<ServiceAppointmentTicketStatusContext> statuses = builder.get();
        return statuses;
    }

}
