package com.facilio.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.*;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.KpiListContainer;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
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
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ReadingKpiAPI {
    public static ReadingKPIContext getReadingKpi(Long recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        return CollectionUtils.isNotEmpty(readingKpis) ? readingKpis.get(0) : null;
    }

    public static Double getCurrentValueOfKpi(Long kpiId, Long resourceId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ReadingKPIContext> builder = new SelectRecordsBuilder<ReadingKPIContext>()
                .select(Arrays.asList(fieldMap.get("readingFieldId")))
                .module(kpiModule)
                .beanClass(ReadingKPIContext.class)
                .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(true), BooleanOperators.IS));

        List<ReadingKPIContext> kpis = builder.get();
        if (CollectionUtils.isEmpty(kpis)) {
            throw new Exception("Invalid kpi ID");
        }
        for(ReadingKPIContext kpi: kpis){
            ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, modBean.getField(kpi.getReadingFieldId()));
            return (Double) rdm.getValue();
        }
        return null;
    }

    public static List<ReadingKPIContext> getReadingKpi(List<Long> recordIds) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, recordIds);
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        return CollectionUtils.isNotEmpty(readingKpis) ? readingKpis : null;
    }

    /**
     * @return return List of independent Kpis, dependent Kpis and DirectedGraph(s) of the dependent kpis
     */
    public static KpiListContainer getActiveScheduledKpisOfFrequencyType(Integer scheduleType) throws Exception {
        return getActiveScheduledKpisOfFrequencyType(Collections.singletonList(scheduleType));
    }

    public static KpiListContainer getActiveScheduledKpisOfFrequencyType(List<Integer> scheduleTypes) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getModuleFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ReadingKPIContext> selectRecordsBuilder = new SelectRecordsBuilder<ReadingKPIContext>()
                .module(module)
                .beanClass(ReadingKPIContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("kpiType"), String.valueOf(KPIType.SCHEDULED.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), StringUtils.join(scheduleTypes, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(true), BooleanOperators.IS));


        List<ReadingKPIContext> kpis = setNamespaceAndMatchedResources(selectRecordsBuilder.get());
        if (CollectionUtils.isEmpty(kpis)) {
            return null;
        }
        LOGGER.info("Active KPI Ids: " + kpis.stream().map(x->x.getId()).collect(Collectors.toList()));

        return getIndepAndDepKpisWithGraph(kpis);

    }

    public static KpiListContainer getIndepAndDepKpisWithGraph(List<ReadingKPIContext> kpis) {
        Map<Long, List<Long>> dependentKpiIdFieldIdsMap = segregateDependentKpis(kpis);

        List<ReadingKPIContext> independentKpis = new ArrayList<>();
        List<ReadingKPIContext> dependentKpis = new ArrayList<>();

        for (ReadingKPIContext kpi : kpis) {
            if (!dependentKpiIdFieldIdsMap.containsKey(kpi.getId())) {
                independentKpis.add(kpi);
            } else {
                dependentKpis.add(kpi);
            }
        }

        List<Graph<Long, DefaultEdge>> dependencyGraphs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dependentKpis)) {
            dependencyGraphs = getGraphsOfDependentKpis(dependentKpiIdFieldIdsMap);
        }
        return new KpiListContainer(independentKpis, dependentKpis, dependencyGraphs);
    }

    /**
     * @return returns a map of kpi ID vs dependent kpi IDs
     * @implNote A dependent kpi is identified by checking for its reading field id in the namespace fields of other given kpis
     */
    public static Map<Long, List<Long>> segregateDependentKpis(@NonNull List<ReadingKPIContext> kpis) { //returns dependent kpis map

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
        return nsFieldIds.stream().filter(x -> fieldIdsOfKpis.contains(x)).map(x -> readingFieldIdKpiIdMap.get(x)).collect(Collectors.toList());
    }

    /**
     * @implNote Single degree source KPIs and Independent KPIs do not have any dependent KPIs in the final kpiIdVsDepKpiIdsMap.
     * This function differentiates these two by iterating through each kpi in the map and checks if its field id exists
     * in other kpi's dependent kpi id list.
     */
    private static boolean isIndependentKpi(Long kpiId, Map<Long, List<Long>> kpiIdDepKpiIdsMap) {
        // to differentiate independent and single degree kpis
        List<Long> depIdList = kpiIdDepKpiIdsMap.get(kpiId).stream().filter(Objects::nonNull).collect(Collectors.toList()); // removes null-only lists
        if (CollectionUtils.isEmpty(depIdList)) {
            for (Map.Entry<Long, List<Long>> entry : kpiIdDepKpiIdsMap.entrySet()) {
                if (!Objects.equals(entry.getKey(), kpiId)) {
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

        ConnectivityInspector<Long, DefaultEdge> ci = new ConnectivityInspector<>(directedGraph);
        List<Set<Long>> vertexSets = ci.connectedSets();

        List<Graph<Long, DefaultEdge>> independentGraphs = new ArrayList<>();
        for (Set<Long> vertexSet : vertexSets) {
            Graph<Long, DefaultEdge> subgraph = new AsSubgraph<>(directedGraph, vertexSet);
            independentGraphs.add(subgraph);
        }
        return independentGraphs;
    }

    /**
     * @param listContainer this pojo is obtained from getIndepAndDepKpisWithGraph function
     * @return returns a list of kpis, flattened from graphs
     */
    public static List<ReadingKPIContext> getFlattenedListOfKpis(KpiListContainer listContainer) {

        if (listContainer == null) {
            LOGGER.info("No Active Kpis");
            return new ArrayList<>();
        }
        List<ReadingKPIContext> independentKpis = listContainer.getIndependentKpis();
        List<ReadingKPIContext> dependentKpis = listContainer.getDependentKpis();
        List<Graph<Long, DefaultEdge>> dependencyGraphs = listContainer.getDependencyGraphs();


        List<Long> indepKpiIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(independentKpis)) {
            indepKpiIds = independentKpis.stream().map(x -> x.getId()).collect(Collectors.toList());
        }
        List<Long> depKpiIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dependentKpis)) {
            depKpiIds = dependentKpis.stream().map(x -> x.getId()).collect(Collectors.toList());
        }
        LOGGER.info("Independent Kpi Ids: " + indepKpiIds + " Dependent Kpi Ids: " + depKpiIds);
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
        orderedReadingKpis.stream().filter(readingKPIContext -> readingKPIContext != null);

        return orderedReadingKpis;
    }

    public static List<Set<Long>> getOrderOfExecution(Graph<Long, DefaultEdge> graph) {
        // list of sets and not a flat list to implement parallel exec of a single set, later
        List<Set<Long>> orderOfExecution = new ArrayList<>();

        Set<Long> sourceVertices = new HashSet<>();
        for (Long vertex : graph.vertexSet()) {
            if (graph.inDegreeOf(vertex) == 0) {
                sourceVertices.add(vertex);
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
                if (frequency.getIndex() >= NamespaceFrequency.ONE_HOUR.getIndex()) {
                    for (int i = 0; i < 24; i += frequency.getDivisor()) {
                        LocalTime time = LocalTime.of(i, 00);
                        schedule.addTime(time);
                    }
                } else {
                    for (int i = 0; i < 24; i++) {
                        for (int j = 0; j < 60; j += frequency.getDivisor()) {
                            LocalTime time = LocalTime.of(i, j);
                            schedule.addTime(time);
                        }
                    }
                }
                schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
                return schedule;
        }
    }

    public static List<ReadingContext> calculateReadingKpi(long resourceId, ReadingKPIContext kpi, List<DateRange> intervals, Long currentExecStartTime) throws Exception {
        String fieldName = kpi.getReadingField().getName();
        NameSpaceContext ns = kpi.getNs();
        WorkflowContext workflow = ns.getWorkflowContext();

        List<ReadingContext> readings = new ArrayList<>();
        JSONObject logMsg = new JSONObject();
        for (DateRange interval : intervals) {
            long iStartTime = interval.getStartTime();
            long iEndTime = interval.getEndTime();
            try {
                long startTime = System.currentTimeMillis();
                Double kpiResult = fetchReadingsAndEvaluateKpi(resourceId, interval, ns.getFields(), workflow.getWorkflowV2String());
                if (kpiResult != null) {
                    ReadingContext reading = new ReadingContext();
                    reading.setParentId(resourceId);
                    reading.addReading(fieldName, kpiResult);
                    reading.addReading("startTime", iStartTime);
                    reading.setTtime(iStartTime);
                    readings.add(reading);
                } else {
                    LOGGER.info(" One of the fields does not have data, for resource" + resourceId);
                    logMsg.put(iStartTime, "One of the fields does not have data, Evaluation returned null");
                }
                long timeTaken = System.currentTimeMillis() - startTime;
                LOGGER.info("Time taken for evaluation of kpi " + kpi.getId() + " for resource " + resourceId + " for interval " + interval + " is " + timeTaken);

            } catch (SQLException e) {
                ReadingKpiLoggerAPI.updateResourceLog(kpi.getId(), resourceId, KpiResourceLoggerContext.KpiLoggerStatus.FAILED.getIndex(), currentExecStartTime, iEndTime, logMsg.toJSONString());
                LOGGER.error("calculateReadingKpi failed by SQLException. resource id : " + resourceId + " , field name : " + fieldName + ", workflow : " + workflow.getId(), e);
                break;
            } catch (Exception e) {
                logMsg.put(iStartTime, e.getMessage());
                ReadingKpiLoggerAPI.updateResourceLog(kpi.getId(), resourceId, KpiResourceLoggerContext.KpiLoggerStatus.FAILED.getIndex(), currentExecStartTime, iEndTime, logMsg.toJSONString());
                LOGGER.error("calculateReadingKpi failed. resource id : " + resourceId + " , field name : " + fieldName + ", intervals : " + intervals + ", workflow : " + workflow.getId(), e);
                break;
            }
        }
        ReadingKpiLoggerAPI.updateResourceLog(kpi.getId(), resourceId, KpiResourceLoggerContext.KpiLoggerStatus.SUCCESS.getIndex(), currentExecStartTime, System.currentTimeMillis(), logMsg.toJSONString());
        return readings;
    }


    private static Double fetchReadingsAndEvaluateKpi(Long resourceId, DateRange interval, List<NameSpaceField> nsFields, String script) throws Exception {
        List<Object> scriptParams = new ArrayList<>();
        for (NameSpaceField field : nsFields) {
            Double reading = fetchAggregatedReading(field.getFieldId(), resourceId, interval.getStartTime(), interval.getEndTime(), field.getAggregation());
            if (reading != null) {
                scriptParams.add(reading);

                Map<String, Object> sysPropMap = new HashMap<>();
                sysPropMap.put("resourceId", resourceId);
                sysPropMap.put("ttime", interval.getStartTime());
                scriptParams.add(sysPropMap);

            } else {
                LOGGER.info("Variable " + field.getVarName() + "with fieldID " + field.getField() + " field does not have data, for resource" + resourceId);
                return null;
            }
        }
        ScriptContext result = ScriptUtil.executeScript(script, scriptParams);
        return Double.parseDouble(result.getReturnValue().toString());
    }

    private static Double fetchAggregatedReading(Long fieldId, Long resourceId, Long startTime, Long endTime, AggregationType aggregationType) throws Exception {
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
        List<Map<String, Object>> props = Objects.requireNonNull(applyAggregate(selectRecordBuilder, aggregationType, resultField)).get();
        return CollectionUtils.isNotEmpty(props) ? (Double) props.get(0).get("reading") : null;
    }

    private static GenericSelectRecordBuilder applyAggregate(GenericSelectRecordBuilder selectRecordBuilder, AggregationType aggregationType, FacilioField aggrField) throws Exception {
        switch (aggregationType) {
            case FIRST:
                return selectRecordBuilder.select(Collections.singletonList(aggrField)).orderBy("TTIME ASC")
                        .limit(1);
            case LAST:
            case LATEST:
                return selectRecordBuilder.select(Collections.singletonList(aggrField)).orderBy("TTIME DESC")
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
                return selectRecordBuilder.select(Collections.singletonList(FieldFactory.getField("reading", "COUNT(DISTINCT " + aggrField.getColumnName() + ")", FieldType.NUMBER)));

        }
        return null;
    }

    public static List<Integer> getFrequencyTypesToBeFetched() {
        List<Integer> types = new ArrayList<>();
        ZonedDateTime zdt = DateTimeUtil.getDateTime();

        if (zdt.getHour() == 0) {
            types.add(NamespaceFrequency.ONE_DAY.getIndex());
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

    public static Map<Long, Long> getReadingFieldIdVsKpiId() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        FacilioField fieldIdField = FieldFactory.getNumberField("readingFieldId", "READING_FIELD_ID", readingKpiModule);
        FacilioField kpiTypeField = FieldFactory.getNumberField("kpiTYpe", "KPI_TYPE", readingKpiModule);

        SelectRecordsBuilder<ReadingKPIContext> selectRecordsBuilder = new SelectRecordsBuilder<ReadingKPIContext>()
                .module(readingKpiModule)
                .beanClass(ReadingKPIContext.class)
                .select(Arrays.asList(fieldIdField))
                .andCondition(CriteriaAPI.getCondition(kpiTypeField, String.valueOf(KPIType.SCHEDULED.getIndex()), NumberOperators.EQUALS));

        return selectRecordsBuilder.get().stream().collect(Collectors.toMap(ReadingKPIContext::getReadingFieldId, ReadingKPIContext::getId));
    }

    public static List<Long> getFirstCircleRelation(Long key, Map<Long, Long> readingKpiIdVsFieldIds) throws Exception {
        FacilioModule namespaceModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
        Map<String, FacilioField> namespaceFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());

        Map<String, FacilioField> namespaceFieldsFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFieldFields());

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(namespaceModule.getTableName())
                .select(Collections.singletonList(namespaceFieldsFieldMap.get("fieldId")))
                .innerJoin("Namespace_Fields")
                .on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(namespaceFieldMap.get("parentRuleId"), Collections.singletonList(key), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(namespaceFieldMap.get("type"), String.valueOf(NSType.KPI_RULE.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(namespaceFieldsFieldMap.get("fieldId"), readingKpiIdVsFieldIds.keySet(), NumberOperators.EQUALS));


        List<Map<String, Object>> props = selectRecordBuilder.get();
        List<Long> firstCircle = new ArrayList<>();
        for (Map<String, Object> prop : props) {
            firstCircle.add(readingKpiIdVsFieldIds.get((Long) prop.get("fieldId")));
        }
        return firstCircle;
    }

    public static List<Long> fetchKpiFamily(Long kpiId) throws Exception {
        List<List<Long>> list = new ArrayList<>();
        List<Long> innerList = new ArrayList<>();
        innerList.add(kpiId);
        list.add(innerList);
        List<List<Long>> kpiIds = ReadingKpiAPI.fetchKpiFamily(kpiId, list, ReadingKpiAPI.getReadingFieldIdVsKpiId());
        List<Long> flatList = new ArrayList<>();
        kpiIds.forEach(flatList::addAll);
        return flatList;
    }

    public static List<List<Long>> fetchKpiFamily(Long key, List<List<Long>> kpis, Map<Long, Long> readingKpiIdVsFieldIds) throws Exception {
        List<Long> firstCircle = getFirstCircleRelation(key, readingKpiIdVsFieldIds);
        if (CollectionUtils.isNotEmpty(firstCircle)) {
            kpis.add(firstCircle);
            for (Long nextKey : firstCircle) {
                fetchKpiFamily(nextKey, kpis, readingKpiIdVsFieldIds);
            }
        }
        return kpis;
    }

}
