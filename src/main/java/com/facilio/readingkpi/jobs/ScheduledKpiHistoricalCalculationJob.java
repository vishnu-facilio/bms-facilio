package com.facilio.readingkpi.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

@Log4j
public class ScheduledKpiHistoricalCalculationJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        long jobStartTime = System.currentTimeMillis();

        try {
            JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());

            List<Long> historicalLoggerAssetIds = (List<Long>) props.get(FacilioConstants.ContextNames.RESOURCE_LIST);
            FacilioChain execKpiChain = TransactionChainFactory.fetchIntervalsAndCalculateKpiChain();
            FacilioContext context = execKpiChain.getContext();
            ReadingKPIContext kpi = ReadingKpiAPI.getReadingKpi((Long) props.get(FacilioConstants.ReadingKpi.READING_KPI));
            context.put(FacilioConstants.ReadingKpi.READING_KPI, kpi);
            context.put(FacilioConstants.ContextNames.START_TIME, props.get(FacilioConstants.ContextNames.START_TIME));
            context.put(FacilioConstants.ContextNames.END_TIME, props.get(FacilioConstants.ContextNames.END_TIME));
            context.put(FacilioConstants.ReadingKpi.IS_HISTORICAL, props.get(FacilioConstants.ReadingKpi.IS_HISTORICAL));

            ReadingKpiAPI.setNamespaceAndMatchedResources(Arrays.asList(kpi));
            context.put(FacilioConstants.ContextNames.RESOURCE_LIST, historicalLoggerAssetIds == null ? kpi.getMatchedResourcesIds() : historicalLoggerAssetIds);
            execKpiChain.execute();

            BmsJobUtil.deleteJobWithProps(jc.getJobId(), jc.getJobName());
            LOGGER.info("Time taken for ScheduledKpiHistoricalCalculation job : "+(System.currentTimeMillis() - jobStartTime));

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
            BmsJobUtil.deleteJobWithProps(jc.getJobId(), jc.getJobName());
            throw e;
        }
    }
}
