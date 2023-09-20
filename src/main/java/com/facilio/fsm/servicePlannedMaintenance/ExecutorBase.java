package com.facilio.fsm.servicePlannedMaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public abstract class ExecutorBase implements Executor {
    @Override
    public List<ServiceOrderContext> execute(Context context) throws Exception{
        List<ServiceOrderContext> serviceOrderList = new ArrayList<>();
        ServicePlannedMaintenanceContext plannedMaintenance = (ServicePlannedMaintenanceContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        ServicePMTriggerContext servicePMTrigger = (ServicePMTriggerContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        if(plannedMaintenance !=null) {
            List<Long> nextExecutionTimes = getNextExecutionTimes(context);
            if (nextExecutionTimes != null) {
                LOGGER.info("Next Execution Times - " + nextExecutionTimes.toString());
            }
            ServiceOrderTicketStatusContext status = getStatus(context);
            if (CollectionUtils.isNotEmpty(nextExecutionTimes)) {
//                context.put(FacilioConstants.ContextNames.LAST_EXECUTION_TIME, nextExecutionTimes.get(nextExecutionTimes.size() - 1));

                for (long nextExecutionTime : nextExecutionTimes) {
                    Long computedCreatedTime = getComputedNextExecutionTime(nextExecutionTime, plannedMaintenance);
                    LOGGER.info("Computed Created Time - " + computedCreatedTime);

                    if (!canProceedWithCreatedTime(computedCreatedTime)) {
                        continue;
                    }
                    //Cloning workorder
                    Map<String, Object> plannedMaintenanceMap = FieldUtil.getAsProperties(plannedMaintenance);
                    ServiceOrderContext serviceOrder = FieldUtil.getAsBeanFromMap(plannedMaintenanceMap, ServiceOrderContext.class);

                    if(plannedMaintenance.getSite()==null) {
                        LOGGER.error("Site is required to create Service Order for service PM - " + plannedMaintenance.getId());
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Site is required to create Service Order");
                    }
                    if(plannedMaintenance.getPmType()==null) {
                        LOGGER.error("Resource Type is required for service PM - " + plannedMaintenance.getId());
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Resource Type is required");
                    }
                    // to check if this gets already set
                    if(plannedMaintenance.getPmTypeEnum().equals(ServicePlannedMaintenanceContext.PMType.ASSET) && plannedMaintenance.getAsset()!=null) {
                        serviceOrder.setAsset(plannedMaintenance.getAsset());
                    }else if(plannedMaintenance.getPmTypeEnum().equals(ServicePlannedMaintenanceContext.PMType.SPACE) && plannedMaintenance.getSpace()!=null) {
                        serviceOrder.setSpace(plannedMaintenance.getSpace());
                    }
                    if(plannedMaintenance.getEstimatedDuration()!=null){
                        Long preferredEndTime = nextExecutionTime + plannedMaintenance.getEstimatedDuration() * 1000;
                        serviceOrder.setPreferredEndTime(preferredEndTime);
                    }
                    serviceOrder.setServicePMTrigger(servicePMTrigger);
                    serviceOrder.setSite(plannedMaintenance.getSite());
                    serviceOrder.setPreferredStartTime(nextExecutionTime);
                    serviceOrder.setCreatedTime(computedCreatedTime);
                    serviceOrder.setServicePlannedMaintenance(plannedMaintenance);
                    serviceOrder.setSourceType(ServiceOrderContext.ServiceOrderSourceType.PLANNED);
                    serviceOrder.setStatus(status);
                    serviceOrder.setModuleState(null);
                    serviceOrder.setStateFlowId(-1);

                    serviceOrderList.add(serviceOrder);
                }
            }
        }
        context.put(FacilioConstants.ServicePlannedMaintenance.GENERATED_SERVICE_ORDERS, serviceOrderList);
        return serviceOrderList;
    }
    protected abstract List<Long> getNextExecutionTimes(Context context) throws Exception;
    protected abstract ServiceOrderTicketStatusContext getStatus(Context context) throws Exception;
    protected abstract Long getComputedNextExecutionTime(Long nextExecutionTime, ServicePlannedMaintenanceContext plannedMaintenance) throws Exception;
    protected abstract Boolean canProceedWithCreatedTime(Long createdTime) throws Exception;
    public long computeEndTimeUsingTriggerType(ScheduleInfo scheduleInfo, long cutOffTime) {	// returning milliseconds
        ZonedDateTime dateTime = DateTimeUtil.getDateTime(cutOffTime, false);
        ZonedDateTime zonedEnd = dateTime.plusDays(15);
        if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DAILY) {
            zonedEnd = dateTime.plusDays(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.WEEKLY) {
            zonedEnd = dateTime.plusWeeks(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.MONTHLY_DAY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.MONTHLY_WEEK) {
            zonedEnd = dateTime.plusMonths(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.QUARTERLY_WEEK
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.QUARTERLY_DAY) {
            zonedEnd = dateTime.plusYears(5);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.HALF_YEARLY_DAY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.HALF_YEARLY_WEEK) {
            zonedEnd = dateTime.plusYears(5);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.YEARLY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.YEARLY_WEEK) {
            zonedEnd = dateTime.plusYears(5);
        }
        return zonedEnd.toEpochSecond()*1000;
    }
    public void deletePreOpenServiceOrders(Long servicePMId) throws Exception {

    }
}
