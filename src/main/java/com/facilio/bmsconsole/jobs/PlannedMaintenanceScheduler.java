package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
//import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import java.time.Duration;
import java.util.*;

public class PlannedMaintenanceScheduler extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        SelectRecordsBuilder.BatchResult<Map<String, Object>> plannedMaintenanceBatchResult = selectPlannedMaintenanceBatches();
        while (plannedMaintenanceBatchResult.hasNext()) {
            List<Map<String, Object>> props = plannedMaintenanceBatchResult.get();
            for (Map<String, Object> prop: props) {
                long plannerId = (long) prop.get("id");
               // PlannedMaintenanceAPI.extendPlanner(plannerId, Duration.ofDays(1));
            }
        }
    }

    private SelectRecordsBuilder.BatchResult<Map<String, Object>> selectPlannedMaintenanceBatches() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        FacilioModule plannerModule = modBean.getModule(FacilioConstants.ContextNames.PMPLANNER);
        FacilioField pmStatus = modBean.getField(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, "pmStatus");
        FacilioField idField = FieldFactory.getIdField(module);
        FacilioField plannerId = FieldFactory.getIdField(plannerModule);
        plannerId.setName("plannerId");
        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(Arrays.asList(idField, plannerId));
        selectRecordsBuilder.innerJoin("PM_Planner")
                        .on("PM_Planner.PM_ID = PM_V2.ID");
        selectRecordsBuilder.module(module);
        selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(pmStatus, String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()), BooleanOperators.IS));
        return selectRecordsBuilder.getAsPropsInBatches("NULL", 5000);
    }
}
