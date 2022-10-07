package com.facilio.readingkpi.jobs;

import java.util.*;

import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.readingkpi.ReadingKpiAPI;
import lombok.extern.log4j.Log4j;

import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

@Log4j
public class ScheduledKpiExecInitiatorJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.info("JOB ID for ScheduledReadingKpiCalculatorJob " + jc.getJobId());
        try {
            List<Integer> types = ReadingKpiAPI.getFrequencyTypesToBeFetched();
            if (CollectionUtils.isNotEmpty(types)) {
                LOGGER.info("Frequencies to be fetched for Scheduled Kpi Calculation : " + types);
                JSONObject props = new JSONObject();
                props.put(FacilioConstants.ReadingKpi.SCHEDULE_TYPE, types.get(0));
                BmsJobUtil.scheduleOneTimeJobWithProps(ReadingKpiAPI.getNextJobIdForCategoryEval(), FacilioConstants.ReadingKpi.READING_KPI_JOB_NAME, 1, "facilio", props);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }
}
