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
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.time.DateTimeUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ExecuteSchKpiOfACategoryCommand extends FacilioCommand implements PostTransactionCommand {
    private boolean timedOut = false;
    private Integer scheduleType;

    public ExecuteSchKpiOfACategoryCommand(@NonNull Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        int evalLim = 0;

        Map<String, Object> kpisMap = ReadingKpiAPI.getActiveScheduledKpisOfFrequencyType(Arrays.asList(this.scheduleType));
        List<ReadingKPIContext> independentKpis = (List<ReadingKPIContext>) kpisMap.get("independentKpis");
        List<ReadingKPIContext> dependentKpis = (List<ReadingKPIContext>) kpisMap.get("dependentKpis");
        List<Graph<Long, DefaultEdge>> dependencyGraphs = (List<Graph<Long, DefaultEdge>>) kpisMap.get("dependencyGraphs");

        if(CollectionUtils.isEmpty(independentKpis) && CollectionUtils.isEmpty(dependentKpis)){
            LOGGER.info("No Active Kpis");
            return true;
        }

        List<Long> indepKpiIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(independentKpis)) {
            indepKpiIds = independentKpis.stream().map(x -> x.getId()).collect(Collectors.toList());
        }
        List<Long> depKpiIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dependentKpis)) {
            depKpiIds = dependentKpis.stream().map(x -> x.getId()).collect(Collectors.toList());
        }
        LOGGER.info("Independent Kpi Ids: " + indepKpiIds + " Dependent Kpi Ids: " + depKpiIds );
        List<Set<Long>> orderOfExecution = new ArrayList<>();
        orderOfExecution.add(independentKpis.stream().map(x -> x.getId()).collect(Collectors.toSet()));
        for (Graph<Long, DefaultEdge> graph : dependencyGraphs) {
            orderOfExecution.addAll(ReadingKpiAPI.getOrderOfExecution(graph));
        }
        List<Long> flattenedOrder = new ArrayList<>();
        orderOfExecution.forEach(x -> flattenedOrder.addAll(x));

        List<ReadingKPIContext> orderedReadingKpis = new ArrayList<>(independentKpis);
        Map<Long, ReadingKPIContext> dependentKpisMap = dependentKpis.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));
        for (Long id : flattenedOrder) {
            orderedReadingKpis.add(dependentKpisMap.get(id));
        }
        orderedReadingKpis.stream().filter(readingKPIContext -> readingKPIContext != null).collect(Collectors.toList());
        List<Long> calculatedFieldIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderedReadingKpis)) {
            long endTime = DateTimeUtil.getHourStartTime();
            long jobStartTime = System.currentTimeMillis();
            try {
                while (!orderedReadingKpis.isEmpty() && !timedOut) {
                    if (++evalLim > 1000) {
                        LOGGER.info("Schedule Kpi calculator exceeded 1000 count.");
                        break;
                    }
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
                }

            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
                CommonCommandUtil.emailException("ExecuteSchKpiOfACategoryCommand", "Execution of scheduled kpi failed for type " + this.scheduleType, e);
            } finally {
                LOGGER.info("Time taken for ScheduledFormulaExecution job : " + (System.currentTimeMillis() - jobStartTime));
            }
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        List<Integer> types = ReadingKpiAPI.getFrequencyTypesToBeFetched();
        long nextJobId= ReadingKpiAPI.getNextJobIdForCategoryEval();
        if (CollectionUtils.isNotEmpty(types) && types.get(types.size() - 1) != this.scheduleType) {
            Integer nextSchedule = this.scheduleType + 1;
            JSONObject props = new JSONObject();
            props.put("scheduleType", nextSchedule);
            BmsJobUtil.scheduleOneTimeJobWithProps(nextJobId, FacilioConstants.ReadingKpi.READING_KPI_JOB_NAME, 1, "facilio", props);
        }
        BmsJobUtil.deleteJobWithProps(nextJobId-1, FacilioConstants.ReadingKpi.READING_KPI_JOB_NAME);
        return false;
    }
}
