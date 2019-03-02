package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class ScheduleWOStatusChange extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(ScheduleWOStatusChange.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            TicketStatusContext status = TicketAPI.getStatus("preopen");
            long maxTime = System.currentTimeMillis()+(30*60*1000);

            SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
            selectRecordsBuilder.select(fields)
                    .module(module)
                    .beanClass(WorkOrderContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(maxTime), NumberOperators.LESS_THAN_EQUAL))
                    .andCustomWhere("WorkOrders.PM_ID IS NOT NULL");
            List<WorkOrderContext> wos = selectRecordsBuilder.get();

            if (wos == null || wos.isEmpty()) {
                return;
            }

            for (WorkOrderContext wo : wos) {
                FacilioTimer.scheduleOneTimeJob(wo.getId(), "OpenScheduledWO", wo.getScheduledStart()/1000, "priority");
            }

            updateJobStatus(wos);
        } catch (Exception e) {
            CommonCommandUtil.emailException("ScheduleWOStatusChange", ""+jc.getJobId(), e);
            LOGGER.error("PM Execution failed: ", e);
            throw e;
        }
    }

    private void updateJobStatus(List<WorkOrderContext> wos) throws Exception {
        if (wos == null || wos.isEmpty()) {
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<Long> woIds = wos.stream().map(WorkOrderContext::getId).collect(Collectors.toList());
        wos.stream().forEach(w -> w.setJobStatus(WorkOrderContext.JobsStatus.SCHEDULED));
        UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.fields(Arrays.asList(fieldMap.get("jobStatus")))
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(woIds, module))
                .update(wos.get(0));
    }
}
