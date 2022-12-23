package com.facilio.readingkpi.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class ExecuteScheduledReadingKpiJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        Long jobStartTime = System.currentTimeMillis();
        LOGGER.info("job Start Time " + jobStartTime );
        try {
            JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
            Long scheduleType = (Long) props.get(FacilioConstants.ReadingKpi.SCHEDULE_TYPE);
            FacilioChain execKpiChain = TransactionChainFactory.executeScheduleKpi(scheduleType.intValue());
            execKpiChain.execute();
            BmsJobUtil.deleteJobWithProps(jc.getJobId(), jc.getJobName());
        } catch (Exception e) {
            LOGGER.info("Execution of scheduled kpi failed", e);
            CommonCommandUtil.emailException("ExecuteScheduledReadingKpiJob", "Execution of scheduled kpi failed", e);
        }
        LOGGER.info("Time taken for job execution " + (System.currentTimeMillis() - jobStartTime));
    }

    @Override
    public void handleTimeOut() {
        LOGGER.info("Scheduled Kpi calculator timed out!");
        super.handleTimeOut();
    }
}
