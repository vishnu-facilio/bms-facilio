package com.facilio.readingkpi;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.*;
import com.facilio.readingkpi.context.HistoricalLoggerContext;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class ReadingKpiAPI {
    public static ReadingKPIContext getReadingKpi(Long recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        return CollectionUtils.isNotEmpty(readingKpis) ? readingKpis.get(0) : null;
    }

    /**
     * @return return List of independent Kpis, dependent Kpis and DirectedGraph(s) of the dependent kpis
     */
    public static Map<String, Object> getActiveScheduledKpisOfFrequencyType(List<Integer> scheduleTypes) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getModuleFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField frequencyField = fieldMap.get("frequency");
        FacilioField kpiType = fieldMap.get("kpiType");
        FacilioField status = fieldMap.get("status");

        SelectRecordsBuilder<ReadingKPIContext> selectRecordsBuilder = new SelectRecordsBuilder<ReadingKPIContext>()
                .module(module)
                .beanClass(ReadingKPIContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(kpiType, String.valueOf(KPIType.SCHEDULED.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(frequencyField, StringUtils.join(scheduleTypes, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(status, String.valueOf(true), BooleanOperators.IS));


        List<ReadingKPIContext> kpis = setNamespaceAndMatchedResources(selectRecordsBuilder.get());
        if (CollectionUtils.isEmpty(kpis)) {
            return new HashMap<>();
        }
        Map<Long, List<Long>> dependentKpiIdFieldIdsMap = segregateDependentKpis(kpis);

        List<ReadingKPIContext> independentKpis = new ArrayList<>();
        List<ReadingKPIContext> dependentKpis = new ArrayList<>();

        for (ReadingKPIContext kpi : kpis) {
            if (!dependentKpiIdFieldIdsMap.keySet().contains(kpi.getId())) {
                independentKpis.add(kpi);
            } else {
                dependentKpis.add(kpi);
            }
        }

        List<Graph<Long, DefaultEdge>> dependencyGraphs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dependentKpis)) {
            dependencyGraphs = getGraphsOfDependentKpis(dependentKpiIdFieldIdsMap);
        }
        LOGGER.info("valid frequency types: " + scheduleTypes + " , qry : " + selectRecordsBuilder);
        Map<String, Object> mapOfIndepAndDepKpisWithGraph = new HashMap<>();
        mapOfIndepAndDepKpisWithGraph.put("independentKpis", independentKpis);
        mapOfIndepAndDepKpisWithGraph.put("dependentKpis", dependentKpis);
        mapOfIndepAndDepKpisWithGraph.put("dependencyGraphs", dependencyGraphs);
        return mapOfIndepAndDepKpisWithGraph;

    }

    /**
     * @return returns a map of kpi ID vs dependent kpi IDs
     * @implNote A dependent kpi is identified by checking for its reading field id in the namespace fields of other given kpis
     */
    private static Map<Long, List<Long>> segregateDependentKpis(@NonNull List<ReadingKPIContext> kpis) { //returns dependent kpis map

        Map<Long, Long> readingFieldIdKpiIdMap = kpis.stream().collect(Collectors.toMap(x -> x.getReadingFieldId(), x -> x.getId()));

        // the following map gives the dependent kpiIds(value) for a given kpi(key)
        Map<Long, List<Long>> kpiIdDepKpiIdsMap = kpis.stream()
                .collect(
                        Collectors.toMap(
                                x -> x.getId(),
                                x -> getParentKpiIds(
                                        x.getNs().getFields().stream().map(y -> y.getFieldId()).collect(Collectors.toList()),//nsFieldIds
                                        readingFieldIdKpiIdMap
                                )
                        )
                );


        kpiIdDepKpiIdsMap.entrySet().removeIf(x -> isIndependentKpi(x.getKey(), kpiIdDepKpiIdsMap));
        return kpiIdDepKpiIdsMap;
    }

    /**
     * @return returns the readingFieldIds of kpis alone from ns fields of given kpi (filters out normal readings, like active power b for example)
     */
    private static List<Long> getParentKpiIds(List<Long> nsFieldIds, Map<Long, Long> readingFieldIdKpiIdMap) {
        List<Long> fieldIdsOfKpis = new ArrayList<>(readingFieldIdKpiIdMap.keySet());
        nsFieldIds.stream().filter(x -> fieldIdsOfKpis.contains(x)).collect(Collectors.toList());
        return nsFieldIds.stream().map(x -> readingFieldIdKpiIdMap.get(x)).collect(Collectors.toList());
    }

    /**
     * @implNote Single degree source KPIs and Independent KPIs do not have any dependent KPIs in the final kpiIdVsDepKpiIdsMap.
     * This function differentiates these two by iterating through each kpi in the map and checks if its field id exists
     * in other kpi's dependent kpi id list.
     */
    private static boolean isIndependentKpi(Long kpiId, Map<Long, List<Long>> kpiIdDepKpiIdsMap) {
        // to differentiate independent and single degree kpis
        List<Long> depIdList = kpiIdDepKpiIdsMap.get(kpiId).stream().filter(x -> x != null).collect(Collectors.toList()); // removes null-only lists
        if (CollectionUtils.isEmpty(depIdList)) {
            for (Map.Entry<Long, List<Long>> entry : kpiIdDepKpiIdsMap.entrySet()) {
                if (entry.getKey() != kpiId) {
                    if (entry.getValue().contains(kpiId)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static List<Graph<Long, DefaultEdge>> getGraphsOfDependentKpis(Map<Long, List<Long>> kpis) {
        Graph<Long, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        List<Long> nonSingleDegreeKpis = new ArrayList<>();

        for (Map.Entry<Long, List<Long>> entry : kpis.entrySet()) {
            Long id = entry.getKey();
            directedGraph.addVertex(id);
            List<Long> depIdList = entry.getValue().stream().filter(x -> x != null).collect(Collectors.toList()); // removes null-only entries
            if (CollectionUtils.isNotEmpty(depIdList)) {
                nonSingleDegreeKpis.add(id);
            }
        }
        for (Long id : nonSingleDegreeKpis) {
            for (Long sourceId : kpis.get(id)) {
                if (sourceId != null) {
                    directedGraph.addEdge(sourceId, id);
                }
            }
        }

        ConnectivityInspector ci = new ConnectivityInspector(directedGraph);
        List<Set<Long>> vertexSets = ci.connectedSets();

        List<Graph<Long, DefaultEdge>> independentGraphs = new ArrayList<>();
        for (Set<Long> vertexSet : vertexSets) {
            Graph<Long, DefaultEdge> subgraph = new AsSubgraph(directedGraph, vertexSet);
            independentGraphs.add(subgraph);
        }
        return independentGraphs;
    }

    public static List<Set<Long>> getOrderOfExecution(Graph<Long, DefaultEdge> graph) {
        // list of sets and not a flat list to implement parallel exec of a single set, later
        List<Set<Long>> orderOfExecution = new ArrayList<>();

        Set<Long> sourceVertices = new HashSet<>();
        Set<Long> targetVertices = new HashSet<>();
        for (Long vertex : graph.vertexSet()) {
            if (graph.inDegreeOf(vertex) == 0) {
                sourceVertices.add(vertex);
            }
            if (graph.outDegreeOf(vertex) == 0) {
                targetVertices.add(vertex);
            }
        }
        orderOfExecution.add(sourceVertices);
        Set<Long> nMinusOnethIteration = new HashSet<>(sourceVertices);
        while (CollectionUtils.isNotEmpty(nMinusOnethIteration)) {
            Set<Long> possibleNextSet = new HashSet<>();
            for (Long vertex : nMinusOnethIteration) {
                for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
                    possibleNextSet.add(graph.getEdgeTarget(edge));
                }
            }
            if (CollectionUtils.isEmpty(possibleNextSet)) {
                break;
            }
            nMinusOnethIteration = possibleNextSet;
            orderOfExecution.add(nMinusOnethIteration);

        }

        return orderOfExecution;
    }

    public static List<ReadingKPIContext> setNamespaceAndMatchedResources(@NonNull List<ReadingKPIContext> kpis) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (ReadingKPIContext kpi : kpis) {
            kpi.setNs(NamespaceAPI.getNameSpaceByRuleId(kpi.getId(), NSType.KPI_RULE));
            kpi.setReadingField(modBean.getField(kpi.getReadingFieldId()));
            FacilioModule module = modBean.getModule(kpi.getReadingModuleId());
            kpi.setReadingModule(module);

            kpi.setMatchedResourcesIds(NamespaceAPI.getMatchedResources(kpi.getNs(), kpi));
        }
        return kpis;
    }

    public static ScheduleInfo getSchedule(NamespaceFrequency frequency) {
        ScheduleInfo schedule;
        List<Integer> values;
        switch (frequency) {
            case ONE_DAY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
                return schedule;
            case WEEKLY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.WEEKLY);
                values = new ArrayList<>();
                values.add(DateTimeUtil.getWeekFields().getFirstDayOfWeek().getValue());
                schedule.setValues(values);
                return schedule;
            case MONTHLY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_DAY);
                values = new ArrayList<>();
                values.add(1);
                schedule.setValues(values);
                return schedule;
            case QUARTERLY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
                schedule.setYearlyDayValue(1);
                values = new ArrayList<>();
                values.add(1);
                values.add(4);
                values.add(7);
                values.add(10);
                schedule.setValues(values);
                return schedule;
            case HALF_YEARLY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
                schedule.setYearlyDayValue(1);
                values = new ArrayList<>();
                values.add(1);
                values.add(7);
                schedule.setValues(values);
                return schedule;
            case ANNUALLY:
                schedule = new ScheduleInfo();
                schedule.addTime("00:00");
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
                schedule.setYearlyDayValue(1);
                values = new ArrayList<>();
                values.add(1);
                schedule.setValues(values);
                return schedule;
            default:
                schedule = new ScheduleInfo();
                if(frequency.getIndex() >= NamespaceFrequency.ONE_HOUR.getIndex()) {
                    for (int i = 0; i < 24; i+=frequency.getDivisor()) {
                        LocalTime time = LocalTime.of(i, 00);
                        schedule.addTime(time);
                    }
                } else {
                    for (int i = 0; i < 24; i++) {
                        for(int j=0; j<60;j+=frequency.getDivisor()) {
                            LocalTime time = LocalTime.of(i, j);
                            schedule.addTime(time);
                        }
                    }
                }
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
                return schedule;
        }
    }

    public static List<ReadingContext> calculateReadingKpi(long resourceId, ReadingKPIContext kpi, List<DateRange> intervals, Boolean isHistorical) throws Exception {
        String fieldName = kpi.getReadingField().getName();
        NameSpaceContext ns = kpi.getNs();
        WorkflowContext workflow = ns.getWorkflowContext();

        if (CollectionUtils.isNotEmpty(intervals)) {
            List<ReadingContext> readings = new ArrayList<>();
            for (DateRange interval : intervals) {
                long iStartTime = interval.getStartTime();
                long iEndTime = interval.getEndTime();
                ReadingKpiAPI.insertLog(kpi.getId(), resourceId, iStartTime, iEndTime, isHistorical);
                try {
                    long startTime = System.currentTimeMillis();
                    Double kpiResult = fetchReadingsAndEvaluateKpi(resourceId, interval, ns.getFields(), workflow.getWorkflowV2String());
                    if (kpiResult != null) {
                        ReadingContext reading = new ReadingContext();
                        reading.setParentId(resourceId);
                        reading.addReading(fieldName, kpiResult);
                        reading.addReading("startTime", iStartTime);
                        reading.setTtime(iEndTime);
                        readings.add(reading);
                        ReadingKpiAPI.updateLog(kpi.getId(), resourceId, HistoricalLoggerContext.LoggerStatus.SUCCESS.getIndex(), iStartTime, System.currentTimeMillis());
                    } else {
                        LOGGER.info(" One of the fields does not have data, for resource" + resourceId);
                        ReadingKpiAPI.updateLog(kpi.getId(), resourceId, HistoricalLoggerContext.LoggerStatus.FAILED.getIndex(), iStartTime, System.currentTimeMillis(), "One of the fields does not have data, Evaluation returned null");
                    }
                    long timeTaken = System.currentTimeMillis() - startTime;
                    LOGGER.info("Time taken for evaluation of kpi " + kpi.getId() + " for resource " + resourceId + " for interval " + interval + " is " + timeTaken);

                } catch (SQLException e) {
                    ReadingKpiAPI.updateLog(kpi.getId(), resourceId, HistoricalLoggerContext.LoggerStatus.FAILED.getIndex(), iStartTime, System.currentTimeMillis(), e.getMessage());
                    LOGGER.error("calculateReadingKpi failed by SQLException. resource id : " + resourceId + " , field name : " + fieldName + ", workflow : " + workflow.getId(), e);
                } catch (Exception e) {
                    ReadingKpiAPI.updateLog(kpi.getId(), resourceId, HistoricalLoggerContext.LoggerStatus.FAILED.getIndex(), iStartTime, System.currentTimeMillis(), e.getMessage());
                    LOGGER.error("calculateReadingKpi failed. resource id : " + resourceId + " , field name : " + fieldName + ", intervals : " + intervals + ", workflow : " + workflow.getId(), e);
                    if (e.getMessage() == null || !(e.getMessage().contains("Division by zero") || e.getMessage().contains("Division undefined") || e.getMessage().contains("/ by zero"))) {
                        CommonCommandUtil.emailException("ReadingKpiAPI", "KPI calculation failed for : " + fieldName + " between " + iStartTime + " and " + iEndTime, e);
                    }
                }
            }
            return readings;
        }
        return null;
    }

    private static Double fetchReadingsAndEvaluateKpi(Long resourceId, DateRange interval, List<NameSpaceField> nsFields, String script) throws Exception {
        List<Object> scriptParams = new ArrayList<>();
        for (NameSpaceField field : nsFields) {
            Object reading = fetchAggregatedReading(field.getFieldId(), resourceId, interval.getStartTime(), interval.getEndTime(), field.getAggregation());
            if (reading != null) {
                scriptParams.add(reading);
            } else {
                LOGGER.info("Variable " + field.getVarName() + "with fieldID " + field.getField() + " field does not have data, for resource" + resourceId);
                return null;
            }
        }
        ScriptContext result = ScriptUtil.executeScript(script, scriptParams);
        return Double.parseDouble(result.getReturnValue().toString());
    }

    private static Object fetchAggregatedReading(Long fieldId, Long resourceId, Long startTime, Long endTime, AggregationType aggregationType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField readingField = modBean.getField(fieldId);
        FacilioModule readingsModule = modBean.getModule(readingField.getModuleId());
        Map<String, FacilioField> readingsModuleFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(readingsModule.getName()));
        FacilioField resultField = FieldFactory.getField("reading", readingField.getColumnName(), FieldType.NUMBER);
        FacilioField resourceIdField = readingsModuleFieldsMap.get("parentId");

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(readingsModule.getTableName())
                .select(new HashSet<>())
                .andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), NumberOperators.EQUALS));

        if (aggregationType != AggregationType.LATEST) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime + "," + endTime, DateOperators.BETWEEN));
        }
        List<Map<String, Object>> props = applyAggregate(selectRecordBuilder, aggregationType, resultField).get();
        return CollectionUtils.isNotEmpty(props) ? props.get(0).get("reading") : null;
    }

    private static GenericSelectRecordBuilder applyAggregate(GenericSelectRecordBuilder selectRecordBuilder, AggregationType aggregationType, FacilioField aggrField) throws Exception {
        switch (aggregationType) {
            case FIRST:
                return selectRecordBuilder.select(Arrays.asList(aggrField)).orderBy("TTIME ASC")
                        .limit(1);
            case LAST:
            case LATEST:
                return selectRecordBuilder.select(Arrays.asList(aggrField)).orderBy("TTIME DESC")
                        .limit(1);
            case SUM:
                return selectRecordBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM, aggrField);
            case MAX:
                return selectRecordBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.MAX, aggrField);
            case MIN:
                return selectRecordBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, aggrField);
            case AVG:
                return selectRecordBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.AVERAGE, aggrField);
            case COUNT:
                return selectRecordBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, aggrField);
            case DISTINCT_COUNT:
                return selectRecordBuilder.select(Arrays.asList(FieldFactory.getField("reading", "COUNT(DISTINCT " + aggrField.getColumnName() + ")", FieldType.NUMBER)));

        }
        return null;
    }

    public static List<Integer> getFrequencyTypesToBeFetched() {
        List<Integer> types = new ArrayList<>();
        ZonedDateTime zdt = DateTimeUtil.getDateTime();

        if (zdt.getHour() == 0) {
            if (zdt.getDayOfWeek() == DateTimeUtil.getWeekFields().getFirstDayOfWeek()) {
                types.add(NamespaceFrequency.WEEKLY.getIndex());
            }

            if (zdt.getDayOfMonth() == 1) {
                types.add(NamespaceFrequency.MONTHLY.getIndex());

                if (zdt.getMonth() == Month.JANUARY) {
                    types.add(NamespaceFrequency.QUARTERLY.getIndex());
                    types.add(NamespaceFrequency.HALF_YEARLY.getIndex());
                    types.add(NamespaceFrequency.ANNUALLY.getIndex());
                } else if (zdt.getMonth() == Month.JULY) {
                    types.add(NamespaceFrequency.QUARTERLY.getIndex());
                    types.add(NamespaceFrequency.HALF_YEARLY.getIndex());
                } else if (zdt.getMonth() == Month.APRIL || zdt.getMonth() == Month.OCTOBER) {
                    types.add(NamespaceFrequency.QUARTERLY.getIndex());
                }
            }
        }
        return types;
    }

    public static void insertLog(Long kpiId, Long resourceId, Long intervalStartTime, Long intervalEndTime, Boolean isHistorical) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule historyLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);
        HistoricalLoggerContext historicalLoggerContext = new HistoricalLoggerContext();
        historicalLoggerContext.setKpiId(kpiId);
        historicalLoggerContext.setResourceId(resourceId);
        historicalLoggerContext.setStartTime(intervalStartTime);
        historicalLoggerContext.setEndTime(intervalEndTime);
        historicalLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
        historicalLoggerContext.setCreatedTime(System.currentTimeMillis());
        historicalLoggerContext.setCalculationStartTime(System.currentTimeMillis());
        historicalLoggerContext.setStatus(HistoricalLoggerContext.LoggerStatus.IN_PROGRESS.getIndex());
        historicalLoggerContext.setIsHistorical(isHistorical);
        Map<String, Object> props = FieldUtil.getAsProperties(historicalLoggerContext);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(historyLoggerModule.getTableName())
                .fields(fields);

        insertRecordBuilder.insert(props);
    }

    public static void updateLog(Long kpiId, Long resourceId, Integer status, Long intervalStartTime, Long calcEndTime) throws Exception {
        updateLog(kpiId, resourceId, status, intervalStartTime, calcEndTime, null);
    }


    public static void updateLog(Long kpiId, Long resourceId, Integer status, Long intervalStartTime, Long calcEndTime, String message) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule historyLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        HistoricalLoggerContext historicalLoggerContext = new HistoricalLoggerContext();

        historicalLoggerContext.setCalculationEndTime(calcEndTime);
        historicalLoggerContext.setStatus(status);
        if (StringUtils.isNotEmpty(message)) {
            historicalLoggerContext.setMessage(message);
        }
        Map<String, Object> props = FieldUtil.getAsProperties(historicalLoggerContext);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(historyLoggerModule.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("kpiId"), String.valueOf(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), String.valueOf(intervalStartTime), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(HistoricalLoggerContext.LoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS));


        updateRecordBuilder.update(props);
    }

//    public static void deleteDuplicateLog(Long kpiId, Long resourceId, Long intervalStartTime) throws Exception {
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule historyLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);
//        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.SCHEDULED_KPI_LOGGER_MODULE);
//
//        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
//        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
//                .table(historyLoggerModule.getTableName())
//                .andCondition(CriteriaAPI.getCondition(fieldMap.get("kpiId"), String.valueOf(kpiId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), String.valueOf(resourceId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), String.valueOf(intervalStartTime), NumberOperators.EQUALS));
//        deleteRecordBuilder.delete();
//    }

    public static long getNextJobIdForCategoryEval() throws Exception {
        FacilioModule module = ModuleFactory.getJobsModule();
        List<FacilioField> fields = FieldFactory.getJobFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField jobIdField = fieldMap.get("jobId");
        FacilioField jobNameField = fieldMap.get("jobName");

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(jobIdField))
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(jobNameField, FacilioConstants.ReadingKpi.READING_KPI_JOB_NAME, StringOperators.IS))
                .orderBy("JOBID DESC")
                .limit(1);

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            long jobId = (Long) props.get(0).get("jobId");
            return jobId + 1;
        }
        return 1l;
    }

//    public static List<ReadingKPIContext> fetchKpiAndFamily(Long kpiId) throws Exception {
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
//        FacilioField readingKpiIdField = FieldFactory.getIdField("id", "ID", readingKpiModule);
//
//        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//                .table(readingKpiModule.getTableName())
//                .innerJoin("Namespace")
//                .on(readingKpiIdField.getCompleteColumnName() + "= Namespace.PARENT_RULE_ID")
//                .innerJoin("Namespace_Fields")
//                .on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
//                .andCondition(CriteriaAPI.getCondition(readingKpiIdField, Arrays.asList(kpiId), NumberOperators.EQUALS));
//
//    }

}
