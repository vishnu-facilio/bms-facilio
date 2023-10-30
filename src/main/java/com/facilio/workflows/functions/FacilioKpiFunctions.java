package com.facilio.workflows.functions;

import com.facilio.connected.CommonConnectedUtil;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateRange;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.readingkpi.ReadingKpiAPI.*;

@Log4j
@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.READING_KPI)
public class FacilioKpiFunctions {
    private static final long HISTORY_EXEC_LIMIT = 500000;

    public Object getResult(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
        Integer kpiType = ((Long) objects[0]).intValue();
        if (!Objects.equals(kpiType, KPIType.DYNAMIC.getIndex())) {
            throw new FunctionParamException("Only Dynamic Kpis are supported");
        }
        Long kpiId = (Long) objects[1];
        List<Long> parentIds = Collections.singletonList((Long) objects[2]);
        Long startTime = (Long) objects[3];
        Long endTime = (Long) objects[4];

        ReadingKPIContext dynamicKpi = getReadingKpi(kpiId);
        Double kpiValue = null;
        JSONObject cardValue = new JSONObject();
        cardValue.put("unit", dynamicKpi.getUnitLabel());
        cardValue.put("dataType", "DECIMAL");
        List<Map<String, Object>> result = ReadingKpiAPI.getResultForDynamicKpi(parentIds, new DateRange(startTime, endTime), null, dynamicKpi.getNs()).get(objects[2]);
        if (!result.isEmpty() && result.get(0) != null) {
            kpiValue = (Double) result.get(0).get("result");
        }
        cardValue.put("value", kpiValue);
        return cardValue;
    }

    public String getDependentKpis(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
        validateParameterDataTypes(objects, 2, Arrays.asList(
                Long.class,
                ArrayList.class

        ));
        Long kpiId = (Long) objects[0];
        ReadingKPIContext kpi = getReadingKpi(kpiId);

        List<Long> assetIds = CollectionUtils.isNotEmpty((List<Long>) objects[1]) ? (List<Long>) objects[1] : NamespaceAPI.getMatchedResources(kpi.getNs());

        DirectedAcyclicGraph<Long, DefaultEdge> graph = CommonConnectedUtil.fetchConRuleFamilyUpwardGraph(kpiId, NSType.KPI_RULE, assetIds);
        return "Dependent kpis : " + graph.vertexSet() + "\n Graph: " + graph.edgeSet();
    }

    public String runHistory(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
        validateParameterDataTypes(objects, 5, Arrays.asList(
                        Long.class,
                        ArrayList.class,
                        Long.class,
                        Long.class,
                        Boolean.class
                )
        );

        Long kpiId = (Long) objects[0];
        ReadingKPIContext kpi = getReadingKpi(kpiId);
        List<Long> assetIds = CollectionUtils.isNotEmpty((List<Long>) objects[1]) ? (List<Long>) objects[1] : NamespaceAPI.getMatchedResources(kpi.getNs());
        Long startTime = (Long) objects[2];
        Long endTime = (Long) objects[3];
        boolean executeDependencies = (Boolean) objects[4];

        sanitizeAndValidatePayload(kpi, startTime, endTime, assetIds);


        if (CollectionUtils.isNotEmpty(assetIds)) {
            StringBuilder s = new StringBuilder("Historical KPI Calculation will be executed for KPIS: ");

            if (Objects.requireNonNull(kpi.getKpiTypeEnum()) == KPIType.LIVE) {
                if (executeDependencies) {
                    DirectedAcyclicGraph<Long, DefaultEdge> graph = CommonConnectedUtil.fetchConRuleFamilyUpwardGraph(kpiId, NSType.KPI_RULE, assetIds);
                    s.append(graph.vertexSet()).append(" for Assets ").append(assetIds).append("\n Graph: ").append(graph.edgeSet());
                }
                beginKpiHistorical(Collections.singletonList(kpi), startTime, endTime, assetIds, executeDependencies, false);
            }
            return s.toString();
        }
        return "All chosen assets have histories, in progress, with overlapping intervals";
    }

    private void validateParameterDataTypes(Object[] objects, int noOfArgs, List<Class> classes) throws FunctionParamException {
        if (objects.length != noOfArgs) {
            throw new FunctionParamException("Insufficient Arguments passed to runHistory function");
        }
        objects[1] = objects[1] == null ? new ArrayList<>() : objects[1];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new FunctionParamException("Null arguments cannot be passed");
            }
            if (!classes.get(i).isInstance(objects[i])) {
                throw new FunctionParamException("Invalid Argument Type");
            }
        }
    }


    private void sanitizeAndValidatePayload(ReadingKPIContext kpi, long startTime, long endTime, List<Long> assetIds) throws Exception {
        if (kpi.getKpiTypeEnum() != KPIType.LIVE) {
            throw new FunctionParamException("History cannot be run for Scheduled or RealTime Kpis");
        }
        if (startTime == -1 || endTime == -1) {
            throw new FunctionParamException("Invalid start and end times");
        }
        if (startTime >= endTime) {
            throw new FunctionParamException("Start Time must be less than End Time");
        }
        if (endTime - startTime > NamespaceFrequency.ONE_DAY.getMs()) {
            throw new FunctionParamException("Historical range cannot be greater than one day");
        }

        //sanitize
        removeAssetsWithHistoryInProgress(kpi.getId(), assetIds, startTime, endTime);

        Long execInterval = kpi.getFrequencyEnum().getMs();

        if (getNoOfExecutions(startTime, endTime, execInterval, assetIds.size()) > HISTORY_EXEC_LIMIT) {
            throw new FunctionParamException("Number Of Executions exceeds safe limit");
        }
    }

    private void removeAssetsWithHistoryInProgress(Long kpiId, List<Long> assetIds, Long startTime, Long endTime) throws Exception {
        List<KpiResourceLoggerContext> resourcesInProgress = logsInProgressForResource(kpiId, assetIds);
        List<Long> idsOfResourcesInProgress = resourcesInProgress.stream()
                .filter(resLog -> inProgressRangeIsInvalid(startTime, endTime, resLog.getStartTime(), resLog.getEndTime()))
                .map(KpiResourceLoggerContext::getResourceId)
                .collect(Collectors.toList());
        LOGGER.info("In progress assets with overlapping intervals: " + idsOfResourcesInProgress);
        assetIds.removeAll(idsOfResourcesInProgress);
        LOGGER.info("History, being run for assets " + assetIds);
    }

    // only those assets whose in-progress intervals don't have any overlap with the given range are permitted
    private boolean inProgressRangeIsInvalid(Long startTime, Long endTime, Long resStartTime, Long resEndTime) {
        if (resStartTime > endTime || resEndTime < startTime) {
            return false;
        }
        return true;
    }

    private long getNoOfExecutions(Long startTime, Long endTime, Long execInterval, int assetCount) {
        return assetCount * ((endTime - startTime) / execInterval);
    }
}
