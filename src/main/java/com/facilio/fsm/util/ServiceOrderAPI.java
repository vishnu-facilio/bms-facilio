package com.facilio.fsm.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

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
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) fieldMap.get("status"));
        return selectAppointmentsBuilder.get();
    }

    public static void updateServiceOrder(ServiceOrderContext so) throws Exception
    {
        JSONObject bodyParams = new JSONObject();
        if(so != null && so.getStatus() != null) {
            if(StringUtils.isNotEmpty(so.getStatus().getStatus())) {
                if (so.getStatus().getStatus().equals(FacilioConstants.ServiceOrder.COMPLETED)) {
                    bodyParams.put("completeTask", true);
                }
            }
        }
        Long soId = so.getId();
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, soId, FieldUtil.getAsJSON(so), bodyParams, null, null, null, null,null, null, null,null);
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

    public static ServiceOrderTicketStatusContext getStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<ServiceOrderTicketStatusContext> builder = new SelectRecordsBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .beanClass(ServiceOrderTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS))
                .andCustomWhere("STATUS = ?", status)
                .orderBy("ID");

        List<ServiceOrderTicketStatusContext> statuses = builder.get();
        if (CollectionUtils.isNotEmpty(statuses)) {
            return statuses.get(0);
        }
        return null;
    }
    public static ServiceTaskStatusContext getTaskStatus(String status) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<ServiceTaskStatusContext> builder = new SelectRecordsBuilder<ServiceTaskStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS)
                .beanClass(ServiceTaskStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS))
                .andCondition(CriteriaAPI.getCondition("NAME","name",status,StringOperators.IS));

        ServiceTaskStatusContext taskStatus = builder.fetchFirst();
        return taskStatus;
    }

    public static List<ServiceOrderTicketStatusContext> getStatusOfStatusType(ServiceOrderTicketStatusContext.StatusType statusType) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SelectRecordsBuilder<ServiceOrderTicketStatusContext> builder = new SelectRecordsBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .beanClass(ServiceOrderTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS))
                .andCustomWhere("STATUS_TYPE = ?", statusType.getIndex())
                .orderBy("ID");
        List<ServiceOrderTicketStatusContext> statuses = builder.get();
        return statuses;
    }

    public static List<ServiceOrderTicketStatusContext> getServiceOrderStatuses() throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        SelectRecordsBuilder<ServiceOrderTicketStatusContext> builder = new SelectRecordsBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .beanClass(ServiceOrderTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS))
                .orderBy("ID");
        List<ServiceOrderTicketStatusContext> statuses = builder.get();
        return statuses;
    }

    public static void dispatchServiceOrder(long orderId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String,FacilioField> soFieldMap = FieldFactory.getAsMap(serviceOrderFields);

        List<ServiceOrderTicketStatusContext> statuses = ServiceOrderAPI.getStatusOfStatusType(ServiceOrderTicketStatusContext.StatusType.OPEN);
        ServiceOrderTicketStatusContext dispatchedStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceAppointment.DISPATCHED);

        if(dispatchedStatus != null && CollectionUtils.isNotEmpty(statuses)) {
            List<Long> statusIds = statuses.stream().map(ServiceOrderTicketStatusContext::getId).collect(Collectors.toList());

            UpdateRecordBuilder<ServiceOrderContext> updateBuilder = new UpdateRecordBuilder<ServiceOrderContext>()
                    .module(serviceOrderModule)
                    .fields(Collections.singletonList(soFieldMap.get(FacilioConstants.ContextNames.STATUS)))
                    .andCondition(CriteriaAPI.getCondition(soFieldMap.get(FacilioConstants.ContextNames.STATUS), StringUtils.join(statusIds), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getIdCondition(orderId, serviceOrderModule));

            Map<String, Object> updateProps = new HashMap<>();
            updateProps.put("status", dispatchedStatus);
            updateBuilder.updateViaMap(updateProps);
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing work order states");
        }
    }

    public static void startServiceOrder(long orderId,long startTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String,FacilioField> soFieldMap = FieldFactory.getAsMap(serviceOrderFields);

        List<ServiceOrderTicketStatusContext> statuses = ServiceOrderAPI.getStatusOfStatusType(ServiceOrderTicketStatusContext.StatusType.OPEN);
        ServiceOrderTicketStatusContext inProgressStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceAppointment.IN_PROGRESS);

        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(soFieldMap.get(FacilioConstants.ContextNames.STATUS));
        updateFields.add(soFieldMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));

        if(inProgressStatus != null && CollectionUtils.isNotEmpty(statuses)) {
            List<Long> statusIds = statuses.stream().map(ServiceOrderTicketStatusContext::getId).collect(Collectors.toList());

            UpdateRecordBuilder<ServiceOrderContext> updateBuilder = new UpdateRecordBuilder<ServiceOrderContext>()
                    .module(serviceOrderModule)
                    .fields(updateFields)
                    .andCondition(CriteriaAPI.getCondition(soFieldMap.get(FacilioConstants.ContextNames.STATUS), StringUtils.join(statusIds), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(soFieldMap.get(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME), CommonOperators.IS_EMPTY))
                    .andCondition(CriteriaAPI.getIdCondition(orderId, serviceOrderModule));

            Map<String, Object> updateProps = new HashMap<>();
            updateProps.put(FacilioConstants.ContextNames.STATUS, inProgressStatus);
            updateProps.put(FacilioConstants.ServiceAppointment.ACTUAL_START_TIME, startTime);
            updateBuilder.updateViaMap(updateProps);
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing work order states");
        }
    }

    public static String getServiceOrderStatus(Long statusId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderTicketStatus = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS);

        FacilioField statusField = Constants.getModBean().getField("status",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS);

        SelectRecordsBuilder<ServiceOrderTicketStatusContext> builder = new SelectRecordsBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .beanClass(ServiceOrderTicketStatusContext.class)
                .select(Collections.singletonList(statusField))
                .andCondition(CriteriaAPI.getIdCondition(statusId,serviceOrderTicketStatus));


        List<ServiceOrderTicketStatusContext> statuses = builder.get();
        if(CollectionUtils.isNotEmpty(statuses)){
            for(ServiceOrderTicketStatusContext orderStatus:statuses)
            {
                String status = orderStatus.getStatus();
                return status;
            }
        }

        return null;
    }

}
