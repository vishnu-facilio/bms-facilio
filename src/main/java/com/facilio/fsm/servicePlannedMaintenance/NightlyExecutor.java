package com.facilio.fsm.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getServiceOrderStatus;

public class NightlyExecutor extends ExecutorBase {
    @Override
    protected ServiceOrderTicketStatusContext getStatus(Context context) throws Exception {
        return getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);
    }
    @Override
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {
        ServicePMTriggerContext trigger = (ServicePMTriggerContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Long currentTime = System.currentTimeMillis();
        Long generatedUpto = getGeneratedUpto(trigger);

        if(generatedUpto!=null && generatedUpto>0) {
            Long endDate = computeEndTimeUsingTriggerType(trigger.getScheduleInfo(), currentTime);
            if(generatedUpto < endDate) {
                List<DateRange> times = trigger.getScheduleInfo().getTimeIntervals(generatedUpto, endDate);
                List<Long> nextExecutionTimes = new ArrayList<>();
                for(DateRange time : times) {
                    long createdTime = time.getEndTime()+1;
                    nextExecutionTimes.add(createdTime);
                }
                return nextExecutionTimes;
            }
        }
        return null;
    }

    public Long getGeneratedUpto(ServicePMTriggerContext trigger)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
        FacilioField createdTime = fieldMap.get("createdTime");

        SelectRecordsBuilder<ServiceOrderContext> recordsBuilder = new SelectRecordsBuilder<ServiceOrderContext>()
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER)
                .beanClass(ServiceOrderContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PM_TRIGGER", "servicePMTrigger", String.valueOf(trigger.getId()), NumberOperators.EQUALS))
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.MAX,  createdTime);

        ServiceOrderContext serviceOrder = recordsBuilder.fetchFirst();
        if(serviceOrder!=null){
            return serviceOrder.getCreatedTime();
        }
        return null;
    }
    @Override
    protected Long getComputedNextExecutionTime(Long nextExecutionTime, ServicePlannedMaintenanceContext plannedMaintenance) {
        Long computedCreatedTime = plannedMaintenance.getLeadTime()!=null ? nextExecutionTime - (plannedMaintenance.getLeadTime() * 1000) : nextExecutionTime;
        return computedCreatedTime;
    }
    @Override
    protected Boolean canProceedWithCreatedTime(Long createdTime){			// nightly should process all including old ones
        return true;
    }
}
