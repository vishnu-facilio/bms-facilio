package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.servicePlannedMaintenance.ExecutorBase;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI.ScheduleOperation;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Log4j
public class GenerateServiceOrdersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long servicePMId = (Long) context.get("servicePMId");
        ScheduleOperation operation = (ScheduleOperation) context.get("operation");
        if(servicePMId!=null && operation!=null){
            ModuleBean modBean = Constants.getModBean();
            Collection<SupplementRecord> lookupFields = new ArrayList<>();
            String moduleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE;
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            lookupFields.add((LookupField) fieldMap.get("servicePMTrigger"));
            List<ServicePlannedMaintenanceContext> servicePlannedMaintenanceList = V3RecordAPI.getRecordsListWithSupplements(moduleName, Collections.singletonList(servicePMId),ServicePlannedMaintenanceContext.class,lookupFields);
            if(CollectionUtils.isNotEmpty(servicePlannedMaintenanceList)){
                ServicePlannedMaintenanceContext servicePlannedMaintenance= servicePlannedMaintenanceList.get(0);
                ExecutorBase scheduleExecutor = operation.getExecutorClass();
                scheduleExecutor.deletePreOpenServiceOrders(servicePlannedMaintenance.getId());

                context.put(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,servicePlannedMaintenance);
                context.put(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER,servicePlannedMaintenance.getServicePMTrigger());
                ExecutorBase executorBase = operation.getExecutorClass();
                executorBase.execute(context);
                LOGGER.info("Generated Service Orders for pm - " + servicePlannedMaintenance.getId());
            }
        }
        return false;
    }
}
