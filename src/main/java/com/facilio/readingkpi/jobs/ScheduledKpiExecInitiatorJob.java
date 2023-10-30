package com.facilio.readingkpi.jobs;

import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class ScheduledKpiExecInitiatorJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            List<Integer> types = ReadingKpiAPI.getFrequencyTypesToBeFetched();
            if(CollectionUtils.isEmpty(types)){
                return;
            }
            List<ReadingKPIContext> kpis = ReadingKpiAPI.getActiveScheduledKpisOfFrequencyType(types);
            Map<NamespaceFrequency, List<ReadingKPIContext>> freqVsKpi = kpis.stream().collect(Collectors.groupingBy(ReadingKPIContext::getFrequencyEnum));

            LOGGER.info("Frequencies to be fetched for Scheduled Kpi Calculation : " + types);
            for (Map.Entry<NamespaceFrequency, List<ReadingKPIContext>> entry : freqVsKpi.entrySet()) {
                NamespaceFrequency freq = entry.getKey();
                List<ReadingKPIContext> kpiList = entry.getValue();
                ReadingKpiAPI.beginKpiHistorical(kpiList, ReadingKpiAPI.getStartTimeForHistoricalCalculation(freq), DateTimeUtil.getCurrenTime(), null, false, true);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred in ScheduledKpiExecInitiatorJob ", e);
        }
    }
}
