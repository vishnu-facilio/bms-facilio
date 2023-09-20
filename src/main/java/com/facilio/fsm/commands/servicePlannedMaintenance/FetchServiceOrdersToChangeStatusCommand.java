package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getServiceOrderStatus;

@Log4j
public class FetchServiceOrdersToChangeStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // Fetches service orders that needs to be changed from upcoming to new
        Long maxTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);

        SelectRecordsBuilder<ServiceOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<ServiceOrderContext>();
        selectRecordsBuilder.select(fields)
                .module(module)
                .beanClass(ServiceOrderContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition("STATUS_ID","status", String.valueOf(serviceOrderStatus.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(maxTime), NumberOperators.LESS_THAN))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("servicePlannedMaintenance"), CommonOperators.IS_NOT_EMPTY))
                .skipModuleCriteria();

        List<ServiceOrderContext> serviceOrders = selectRecordsBuilder.get();

        if (CollectionUtils.isNotEmpty(serviceOrders)) {
            List<Long> serviceOrderIds = serviceOrders.stream().map(ServiceOrderContext::getId).collect(Collectors.toList());

            LOGGER.info("Service Orders size: " + serviceOrderIds.size() + ". Service Order IDs = " + serviceOrderIds);
        }
        else {
            LOGGER.info("NO SO's available for this endTime -- "+maxTime);
            return true;
        }

        context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST, serviceOrders);
        return false;
    }
}
