package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ServicePMNightlySchedulerCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ServicePlannedMaintenanceContext> select = new SelectRecordsBuilder<ServicePlannedMaintenanceContext>()
                .moduleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE)
                .select(fields)
                .beanClass(ServicePlannedMaintenanceContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("servicePMTrigger"), "", CommonOperators.IS_NOT_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("isPublished"), String.valueOf(true), BooleanOperators.IS));

        List<ServicePlannedMaintenanceContext> plannedMaintenanceList = select.get();
        if(CollectionUtils.isNotEmpty(plannedMaintenanceList)){
            for(ServicePlannedMaintenanceContext servicePM : plannedMaintenanceList){
                ServicePlannedMaintenanceAPI.runNightlyScheduler(servicePM.getId());
            }
        }
        return false;
    }
}
