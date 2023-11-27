package com.facilio.fsm.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fms.message.Message;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.servicePlannedMaintenance.ExecutorBase;
import com.facilio.fsm.servicePlannedMaintenance.NightlyExecutor;
import com.facilio.fsm.servicePlannedMaintenance.ScheduleExecutor;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.jcraft.jsch.Logger;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.ScheduleOperation.NIGHTLY;
import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.ScheduleOperation.RE_INIT;

@Log4j
public class ServicePlannedMaintenanceAPI {
    public enum ScheduleOperation implements FacilioStringEnum {
        RE_INIT (ScheduleExecutor.class),
        NIGHTLY (NightlyExecutor.class);

        private Class<? extends ExecutorBase> executorClass;
        private <E extends ExecutorBase> ScheduleOperation (Class<E> executorClass) {
            this.executorClass = executorClass;
        }
        public ExecutorBase getExecutorClass() throws Exception {
            return executorClass.getConstructor().newInstance();
        }
    }
    public static void scheduleServicePM(Long servicePMId){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("servicePMId", servicePMId);
        message.put("operation", RE_INIT.getValue());

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("service_pm/" + servicePMId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    public static void runNightlyScheduler(Long servicePMId){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("servicePMId", servicePMId);
        message.put("operation", NIGHTLY.getValue());

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("service_pm/" + servicePMId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    public static ServiceOrderTicketStatusContext getServiceOrderStatus(String status) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        SelectRecordsBuilder<ServiceOrderTicketStatusContext> builder = new SelectRecordsBuilder<ServiceOrderTicketStatusContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS)
                .beanClass(ServiceOrderTicketStatusContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TICKET_STATUS))
                .andCondition(CriteriaAPI.getCondition("status", "STATUS", status, StringOperators.IS));
        return builder.fetchFirst();
    }
    public static void deleteServiceOrdersInUpcomingState(Long servicePMId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);
        DeleteRecordBuilder<ServiceOrderContext> serviceOrderDeleteRecordBuilder = new DeleteRecordBuilder<ServiceOrderContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("servicePlannedMaintenance"), String.valueOf(servicePMId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(serviceOrderStatus.getId()),NumberOperators.EQUALS));
       int deletedCount = serviceOrderDeleteRecordBuilder.markAsDelete();
        LOGGER.info( deletedCount + "pre created service orders deleted");
    }
    public static Long getNextRun(Long servicePMId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);
        SelectRecordsBuilder<ServiceOrderContext> recordsBuilder = new SelectRecordsBuilder<ServiceOrderContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServiceOrderContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PM","servicePlannedMaintenance",String.valueOf(servicePMId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("MODULE_STATE","moduleState","", CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition("STATUS_ID","status", String.valueOf(serviceOrderStatus.getId()),NumberOperators.EQUALS))
                .orderBy("SO_CREATED_TIME ASC")
                .limit(1);
        ServiceOrderContext serviceOrder = recordsBuilder.fetchFirst();
        if(serviceOrder!=null && serviceOrder.getCreatedTime() !=null){
            return serviceOrder.getCreatedTime();
        }
        return null;
    }
    public static Long getLastRun(Long servicePMId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.NEW);
        SelectRecordsBuilder<ServiceOrderContext> recordsBuilder = new SelectRecordsBuilder<ServiceOrderContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServiceOrderContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PM","servicePlannedMaintenance",String.valueOf(servicePMId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS_ID","status", String.valueOf(serviceOrderStatus.getId()),NumberOperators.EQUALS))
                .orderBy("SO_CREATED_TIME DESC")
                .limit(1);
        ServiceOrderContext serviceOrder = recordsBuilder.fetchFirst();
        if(serviceOrder.getCreatedTime() !=null){
            return serviceOrder.getCreatedTime();
        }
        return null;
    }
    public static List<ServiceOrderContext> getSubsequentServiceOrders(Long servicePMId,Long preferredStartTime)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);
        SelectRecordsBuilder<ServiceOrderContext> recordsBuilder = new SelectRecordsBuilder<ServiceOrderContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServiceOrderContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PM","servicePlannedMaintenance",String.valueOf(servicePMId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS_ID","status", String.valueOf(serviceOrderStatus.getId()),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("PREFERRED_START_TIME","preferredStartTime", String.valueOf(preferredStartTime),NumberOperators.GREATER_THAN));
        List<ServiceOrderContext> serviceOrders = recordsBuilder.get();
        return serviceOrders;
    }
    public static ServicePMTriggerContext getTriggerForTemplate(Long servicePMTemplateId)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String triggerModuleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER;
        List<FacilioField> fields = modBean.getAllFields(triggerModuleName);

        SelectRecordsBuilder<ServicePMTriggerContext> builder = new SelectRecordsBuilder<ServicePMTriggerContext>()
                .moduleName(triggerModuleName)
                .select(fields)
                .beanClass(ServicePMTriggerContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PM_TEMPLATE", "servicePMTemplate", String.valueOf(servicePMTemplateId), NumberOperators.EQUALS));
        ServicePMTriggerContext trigger = builder.fetchFirst();
        return trigger;
    }
}
