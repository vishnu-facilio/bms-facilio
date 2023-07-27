package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class ServiceOrderAPI {

    private static Logger LOGGER = LogManager.getLogger(ServiceOrderAPI.class.getName());

    public static List<ServiceAppointmentContext> getServiceAppointmentByServiceOrder(long serviceOrderId) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);
        SelectRecordsBuilder<ServiceAppointmentContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
        selectAppointmentsBuilder.select(serviceAppointmentFields)
                .module(serviceAppointmentModule)
                .beanClass(ServiceAppointmentContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderId), StringOperators.IS));
        return selectAppointmentsBuilder.get();
    }

    public static List<ServiceTaskContext> getServiceTasksByServiceOrder(long serviceOrderId) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceTaskFields);
        SelectRecordsBuilder<ServiceTaskContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
        selectAppointmentsBuilder.select(serviceTaskFields)
                .module(serviceTaskModule)
                .beanClass(ServiceTaskContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderId), StringOperators.IS));
        return selectAppointmentsBuilder.get();
    }

    public static void updateServiceOrder(ServiceOrderContext so) throws Exception
    {
        Long soId = so.getId();
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, soId, FieldUtil.getAsJSON(so), null, null, null, null, null,null, null, null);
    }
    public static ServiceOrderContext getServiceOrder(long soId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        ServiceOrderContext serviceOrder = V3RecordAPI.getRecord(serviceOrderModule.getName(),soId,ServiceOrderContext.class);
        if (serviceOrder != null ) {
            return serviceOrder;
        }
        return null;
    }
}
