package com.facilio.readingkpi.commands;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.readingkpi.context.KpiListContainer;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.time.DateTimeUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j
public class ExecuteSchKpiOfACategoryCommand extends FacilioCommand implements PostTransactionCommand {
    private Integer scheduleType;

    public ExecuteSchKpiOfACategoryCommand(@NonNull Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // note while executing kpis of a freq type, it's assumed that all the dependent lower order kpis
        // are calculated during the previous iteration, so only the current freq type is fetched
        KpiListContainer kpisMap = ReadingKpiAPI.getActiveScheduledKpisOfFrequencyTypeWithGraph(this.scheduleType);
        List<ReadingKPIContext> orderedReadingKpis = ReadingKpiAPI.getFlattenedListOfKpis(kpisMap);

        if (CollectionUtils.isEmpty(orderedReadingKpis)) {
            return true;
        }

        List<Long> calculatedFieldIds = new ArrayList<>();
        long endTime = DateTimeUtil.getHourStartTime();
        long jobStartTime = System.currentTimeMillis();
        try {
            Iterator<ReadingKPIContext> it = orderedReadingKpis.iterator();
            while (it.hasNext()) {
                ReadingKPIContext kpi = it.next();
                if (kpi != null) {
                    try {
                        FacilioChain execKpiChain = TransactionChainFactory.fetchIntervalsAndCalculateKpiChain();
                        FacilioContext ctx = execKpiChain.getContext();
                        ctx.put(FacilioConstants.ContextNames.RESOURCE_LIST, kpi.getMatchedResourcesIds());
                        ctx.put(FacilioConstants.ContextNames.END_TIME, endTime);
                        ctx.put(FacilioConstants.ReadingKpi.READING_KPI, kpi);
                        execKpiChain.execute();
                    } catch (Exception e) {
                        LOGGER.error("Exception occurred: Schedule kpi calculator job failed. kpi : " + kpi, e);
                    }
                    calculatedFieldIds.add(kpi.getReadingFieldId());
                }
                it.remove();
            }

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
            CommonCommandUtil.emailException("ExecuteSchKpiOfACategoryCommand", "Execution of scheduled kpi failed for type " + this.scheduleType, e);
        } finally {
            LOGGER.info("Time taken for calculating all kpis : " + (System.currentTimeMillis() - jobStartTime));
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        List<Integer> types = ReadingKpiAPI.getFrequencyTypesToBeFetched();
        long nextJobId = ReadingKpiLoggerAPI.getNextJobId();
        if (CollectionUtils.isNotEmpty(types) && types.get(types.size() - 1) != this.scheduleType) {
            Integer nextSchedule = this.scheduleType + 1;
            JSONObject props = new JSONObject();
            props.put("scheduleType", nextSchedule);
            BmsJobUtil.scheduleOneTimeJobWithProps(nextJobId, FacilioConstants.ReadingKpi.READING_KPI_JOB_NAME, 1, "facilio", props);
        }
        return false;
    }
}
