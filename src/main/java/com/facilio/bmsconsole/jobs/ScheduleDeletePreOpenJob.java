package com.facilio.bmsconsole.jobs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduleDeletePreOpenJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(ScheduleDeletePreOpenJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        long pmId = jc.getJobId();
        List<Long> pmIds = Collections.singletonList(pmId);
        PreventiveMaintenanceAPI.deleteScheduledWorkorders(pmIds);
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(pmIds, 0);

        FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
        List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(Arrays.asList(fieldMap.get("woGenerationStatus")))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), pmIds, NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectRecordBuilder.get();

        Boolean status = (Boolean) props.get(0).get("woGenerationStatus");
        if (status) {
            LOGGER.log(Level.SEVERE, "status should be 0, but instead 1 pmID: " + pmId);
        }
    }
}
