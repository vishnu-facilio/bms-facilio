package com.facilio.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.*;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.*;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.readingkpi.context.*;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.storm.InstructionType;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import lombok.NonNull;
import lombok.SneakyThrows;
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
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.util.BmsJobUtil.scheduleOneTimeJobWithProps;

import static com.facilio.connected.CommonConnectedUtil.getDependencyMapForConnectedRules;
import static com.facilio.connected.CommonConnectedUtil.postConRuleHistoryInstructionToStorm;

@Log4j
public class ReadingKpiAPI {

    public static List<ReadingKPIContext> getAllActiveKpis(Map<String, Object> paramsMap, Criteria userCriteria) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        String search = null;
        int page = 0, perPage = 50;
        String orderBy = null, orderType = null;
        if (paramsMap != null) {
            page = (int) paramsMap.get("page");
            perPage = (int) paramsMap.get("perPage");
            search = (String) paramsMap.get("search");
            if (paramsMap.containsKey("orderBy")) {
                orderBy = (String) paramsMap.get("orderBy");
                orderType = (String) paramsMap.get("orderType");
            }
        }
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ReadingKpi.READING_KPI));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
        criteria.andCriteria(userCriteria);

        FacilioContext fetch = V3Util.fetchList(moduleName, true, null, null, false, null, orderBy, orderType, search, page, perPage, true, null, criteria, null);
        Map<String, Object> readingKpiContexts = (Map<String, Object>) fetch.get(Constants.RECORD_MAP);

        return (List<ReadingKPIContext>) readingKpiContexts.get(moduleName);
    }

    public static List<Map<String, Object>> getListOfAssetCategoriesOfAllKpis() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule kpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ReadingKpi.READING_KPI);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        FacilioModule assetCategoryModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
        List<FacilioField> assetCategoryFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY);
        Map<String, FacilioField> assetCategoryFieldMap = FieldFactory.getAsMap(assetCategoryFields);
        FacilioField assetCatField = FieldFactory.getNumberField("assetCategory", "DISTINCT ReadingKPI.ASSET_CATEGORY", kpiModule);
        assetCatField.setModule(null);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(kpiModule.getTableName())
                .select(Arrays.asList(assetCatField, assetCategoryFieldMap.get("name")))
                .innerJoin(assetCategoryModule.getTableName())
                .on(fieldMap.get("assetCategory").getCompleteColumnName() + " = " + FieldFactory.getIdField(assetCategoryModule).getCompleteColumnName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(true), BooleanOperators.IS));
        List<Map<String, Object>> props = builder.get();

        if (CollectionUtils.isEmpty(props)) {
            return new ArrayList<>();
        }
        return props;
    }

    public static List<ReadingKPIContext> getKpisOfCategory(Long categoryId) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ReadingKpi.READING_KPI));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("assetCategory"), Collections.singleton(categoryId), NumberOperators.EQUALS));
        return getAllActiveKpis(null, criteria);
    }

    public static ReadingKPIContext getReadingKpi(Long recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        if (CollectionUtils.isNotEmpty(readingKpis)) {
            return readingKpis.get(0);
        }
        throw new IllegalArgumentException("Invalid Kpi Id");
    }


    public static List<ReadingKPIContext> getReadingKpi(List<Long> recordIds) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, recordIds);
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        return CollectionUtils.isNotEmpty(readingKpis) ? readingKpis : null;
    }

    public static long adjustTtime(Long ttime, Long interval) {
        ZoneId zoneId = DBConf.getInstance().getCurrentZoneId();
        int seconds = (int) (interval / 1000);
        ZonedDateTime zdt = DateTimeUtil.getDateTime(zoneId, ttime);
        zdt = zdt.truncatedTo(new SecondsChronoUnit(seconds));
        return DateTimeUtil.getMillis(zdt, true);
    }

    // only id, fieldId and ns will be set in the context, this is used in storm exec, to form dependency graph
    public static List<IConnectedRule> getReadingKpis(Map<Long, Long> kpiIdVsNsId) throws Exception {
        Map<Long, Long> kpiIdVsFieldId = getKpiIdVsReadingFieldId(new ArrayList<>(kpiIdVsNsId.keySet()));
        List<IConnectedRule> kpis = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : kpiIdVsNsId.entrySet()) {
            NameSpaceCacheContext ns = Constants.getNsBean().getNamespace(entry.getValue());
            Long readingFieldId = kpiIdVsFieldId.get(entry.getKey());
            kpis.add(new ReadingKPIContext(entry.getKey(), readingFieldId, ns));
        }
        return kpis;
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
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(true), BooleanOperators.IS));

        List<ReadingKPIContext> kpis = builder.get();
        if (CollectionUtils.isEmpty(kpis)) {
            throw new Exception("Invalid kpi ID");
        }
        for (ReadingKPIContext kpi : kpis) {
            ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, modBean.getField(kpi.getReadingFieldId()));
            return (Double) rdm.getValue();
        }
        return null;
    }

    // Scheduled KPI Util

    public static List<ReadingKPIContext> getActiveScheduledKpisOfFrequencyType(Integer scheduleType) throws Exception {
        return getActiveScheduledKpisOfFrequencyType(Collections.singletonList(scheduleType));
    }

    public static List<ReadingKPIContext> getActiveScheduledKpisOfFrequencyType(List<Integer> scheduleTypes) throws Exception {
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
            return new ArrayList<>();
        }
        return kpis;
    }

    /**
     * @return return List of independent Kpis, dependent Kpis and DirectedGraph(s) of the dependent kpis
     */
    public static KpiListContainer getActiveScheduledKpisOfFrequencyTypeWithGraph(Integer scheduleType) throws Exception {
        return getActiveScheduledKpisOfFrequencyTypeWithGraph(Collections.singletonList(scheduleType));
    }

    public static KpiListContainer getActiveScheduledKpisOfFrequencyTypeWithGraph(List<Integer> scheduleTypes) throws Exception {
        List<ReadingKPIContext> kpis = getActiveScheduledKpisOfFrequencyType(scheduleTypes);
        return getIndepAndDepKpisWithGraph(kpis);
    }


    public static KpiListContainer getIndepAndDepKpisWithGraph(List<ReadingKPIContext> kpis) {
        Map<Long, List<Long>> kpiIdVsDepKpiIds = getDependencyMapForConnectedRules(kpis);

        List<ReadingKPIContext> independentKpis = new ArrayList<>();
        List<ReadingKPIContext> dependentKpis = new ArrayList<>();

        for (ReadingKPIContext kpi : kpis) {
            if (!kpiIdVsDepKpiIds.containsKey(kpi.getId())) {
                independentKpis.add(kpi);
            } else {
                dependentKpis.add(kpi);
            }
        }

        List<Graph<Long, DefaultEdge>> dependencyGraphs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dependentKpis)) {
            dependencyGraphs = getGraphsOfDependentKpis(kpiIdVsDepKpiIds);
        }
        return new KpiListContainer(independentKpis, dependentKpis, dependencyGraphs);
    }

    public static List<Graph<Long, DefaultEdge>> getGraphsOfDependentKpis(Map<Long, List<Long>> dependencyMap) {
        Graph<Long, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        List<Long> dependentKpis = new ArrayList<>(); // kpis that aren't sources

        // first add vertices for each kpi and list all dep kpis
        for (Map.Entry<Long, List<Long>> entry : dependencyMap.entrySet()) {
            Long id = entry.getKey();
            directedGraph.addVertex(id);
            List<Long> depIdList = entry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList()); // removes null-only entries
            if (CollectionUtils.isNotEmpty(depIdList)) {
                dependentKpis.add(id);
            }
        }
        // for every dep kpi add an edge with its dependency
        for (Long id : dependentKpis) {
            for (Long sourceId : dependencyMap.get(id)) {
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
            orderOfExecution.addAll(CommonConnectedUtil.getOrderOfExecution(graph));
        }
        List<Long> flattenedOrder = new ArrayList<>();
        orderOfExecution.forEach(flattenedOrder::addAll);

        List<ReadingKPIContext> orderedReadingKpis = new ArrayList<>(independentKpis);
        Map<Long, ReadingKPIContext> dependentKpisMap = dependentKpis.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));
        for (Long id : flattenedOrder) {
            orderedReadingKpis.add(dependentKpisMap.get(id));
        }
        orderedReadingKpis.stream().filter(readingKPIContext -> readingKPIContext != null);

        return orderedReadingKpis;
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
            Long resId = field.getResourceId() != null ? field.getResourceId() : resourceId;
            Double reading = fetchAggregatedReading(field.getFieldId(), resId, interval.getStartTime(), interval.getEndTime(), field.getAggregation());
            if (reading != null) {
                scriptParams.add(reading);
                LOGGER.info("nsId: " + field.getNsId() + " resourceId: " + resourceId + " variable: " + field.getVarName() + " value: " + reading);
            } else {
                LOGGER.info("Variable " + field.getVarName() + " with fieldID " + field.getField().getId() + " field does not have data, for resource" + resId);
                return null;
            }
        }
        Map<String, Object> sysPropMap = new HashMap<>();
        sysPropMap.put("resourceId", resourceId);
        sysPropMap.put("ttime", interval.getStartTime());
        scriptParams.add(sysPropMap);
        LOGGER.info("Script Params : " + scriptParams + " for resource " + resourceId);
        ScriptContext result = ScriptUtil.executeScript(script, scriptParams);
        return Double.parseDouble(result.getReturnValue().toString());
    }

    private static Double fetchAggregatedReading(Long fieldId, Long resourceId, Long startTime, Long endTime, AggregationType aggregationType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField readingField = modBean.getField(fieldId);
        FacilioModule readingsModule = modBean.getModule(readingField.getModuleId());
        Map<String, FacilioField> readingsModuleFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(readingsModule.getName()));
        FacilioField resultField = FieldFactory.getField("reading", readingField.getColumnName(), FieldType.NUMBER);
        FacilioField moduleIdField = FieldFactory.getField("moduleId", "MODULEID", FieldType.NUMBER);
        FacilioField resourceIdField = readingsModuleFieldsMap.get("parentId");

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(readingsModule.getTableName())
                .select(new HashSet<>())
                .andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(readingsModule.getModuleId()), NumberOperators.EQUALS));

        if (aggregationType != AggregationType.LATEST) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime + "," + endTime, DateOperators.BETWEEN));
        }

        List<Map<String, Object>> props = Objects.requireNonNull(applyAggregate(selectRecordBuilder, aggregationType, resultField)).get();
        LOGGER.info("select query of fieldId : " + fieldId + " for resource: " + resourceId + " qry: " + selectRecordBuilder);
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

    // Dynamic KPI Util

    public static List<KpiContextWrapper> getDynamicKpisForAsset(Long assetId) throws Exception {
        AssetContext assetInfo = AssetsAPI.getAssetInfo(assetId);
        AssetCategoryContext category = assetInfo.getCategory();
        return getDynamicKpisOfCategory(category.getId()).stream()
                .filter(dynKpi -> getMatchedResources(dynKpi.getNs(), category.getId()).contains(assetId))
                .map(dynKpi -> new KpiContextWrapper(dynKpi.getId(), dynKpi.getName()))
                .collect(Collectors.toList());
    }

    private static List<Long> getMatchedResources(NameSpaceContext ns, Long categoryId) {
        try {
            return CollectionUtils.isEmpty(ns.getIncludedAssetIds()) ? NamespaceAPI.getMatchedResources(ns, categoryId) : ns.getIncludedAssetIds();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ReadingKPIContext> getDynamicKpisOfCategory(Long categoryId) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ReadingKpi.READING_KPI));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("assetCategory"), Collections.singleton(categoryId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("kpiType"), String.valueOf(KPIType.DYNAMIC.getIndex()), NumberOperators.EQUALS));
        return getAllActiveKpis(null, criteria);
    }


    public static boolean getCategoryIfAssetInBuildings(ReadingKPIContext kpi, List<Long> buildingIds, Map<Long, String> dynamicKpisAssetCategories) {
        if (dynamicKpisAssetCategories.containsKey(kpi.getAssetCategory().getId())) {
            return false;
        }
        List<Long> includedAssetIds = kpi.getNs().getIncludedAssetIds();
        if (org.apache.commons.collections.CollectionUtils.isEmpty(includedAssetIds)) {
            return true;
        }
        try {
            return org.apache.commons.collections.CollectionUtils.isNotEmpty(getAssetsIfInBuilding(buildingIds, includedAssetIds));
        } catch (Exception e) {
            LOGGER.error("Error while fetching assets from included assets", e);
            return false;
        }
    }

    private static Set<Long> getAssetsIfInBuilding(List<Long> buildingIds, List<Long> includedAssetIds) throws Exception {
        FacilioModule assetModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.ASSET));
        SelectRecordsBuilder<AssetContext> builder = new SelectRecordsBuilder<AssetContext>()
                .moduleName(FacilioConstants.ContextNames.ASSET)
                .beanClass(AssetContext.class)
                .select(Collections.singletonList(FieldFactory.getIdField(assetModule)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("space"), buildingIds, BuildingOperator.BUILDING_IS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(assetModule), includedAssetIds, NumberOperators.EQUALS));

        return builder.get().stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
    }


    public static Map<Long, List<Map<String, Object>>> getResultForDynamicKpi(List<Long> parentIds, DateRange dateRange, AggregateOperator aggr, NameSpaceContext ns) throws Exception {

        if (CollectionUtils.isEmpty(parentIds)) {
            // when dimension is chosen as assets, parentIds can be null. Not yet supported from ui
//            parentIds = AssetsAPI.getAssetIdsFromBaseSpaceIds(Collections.singletonList(buildingId));
            throw new IllegalArgumentException("Parent Ids cannot be null");
        }
        // data collection
        // < res Id < ttime, <varName, [value]> > >
        Map<Long, TreeMap<Long, Map<String, List<Double>>>> readingsMap = fetchReadings(parentIds, dateRange, aggr, ns);

        // execution
        // < resId, [<ttime, resultObj>] >
        Map<Long, List<Map<String, Object>>> finalResultProps = calculateDynamicKpi(parentIds, ns, readingsMap);
        return finalResultProps;
    }

    private static Map<Long, TreeMap<Long, Map<String, List<Double>>>> fetchReadings(List<Long> parentIds, DateRange dateRange, AggregateOperator aggr, NameSpaceContext ns) throws Exception {
        Map<Long, TreeMap<Long, Map<String, List<Double>>>> readingsMap = new HashMap<>();
        for (Long parentId : parentIds) {
            // < ttime, <varName, [values]> > - list of values cause, in related case same variable has all the related assets' values
            TreeMap<Long, Map<String, List<Double>>> resReadingsMap = new TreeMap<>();
            List<NameSpaceField> flattenedNsFields = resolveRelatedFieldsAndSetResourceId(ns.getFields(), parentId);
            LOGGER.info("Dynamic KPI : Flattened Ns Fields : " + flattenedNsFields + " for resource : " + parentId + " for kpi : " + ns.getParentRuleId());
            for (NameSpaceField nsField : flattenedNsFields) {
                fetchReadingsForNsField(dateRange, aggr, resReadingsMap, nsField);
            }
            readingsMap.put(parentId, resReadingsMap);
        }
        LOGGER.info("Dynamic KPI : readingsMap: " + readingsMap);
        return readingsMap;
    }

    public static List<NameSpaceField> resolveRelatedFieldsAndSetResourceId(List<NameSpaceField> fields, Long resourceId) throws Exception {
        List<NameSpaceField> flattenedNsFields = new ArrayList<>();
        for (NameSpaceField fld : fields) {
            switch (fld.getNsFieldType()) {
                case RELATED_READING:
                    boolean isValidRelatedField = resolveRelatedFieldsAndAddToList(resourceId, flattenedNsFields, fld);
                    if (!isValidRelatedField) return new ArrayList<>();
                    break;
                case ASSET_READING:
                    if (fld.getResourceId() == null) {
                        NameSpaceField nsField = getNsFieldClone(resourceId, fld);
                        flattenedNsFields.add(nsField);
                    } else {
                        flattenedNsFields.add(fld);
                    }
                    break;
                case SPACE_READING:
                    flattenedNsFields.add(fld);
                    break;
            }
        }
        return flattenedNsFields;
    }

    private static boolean resolveRelatedFieldsAndAddToList(Long resourceId, List<NameSpaceField> flattenedNsFields, NameSpaceField fld) throws Exception {
        NamespaceFieldRelated relatedInfo = fld.getRelatedInfo();
        List<Long> relatedResIds = fetchRelatedResourceIds(relatedInfo.getRelMapId(), resourceId, relatedInfo.getCriteria());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(relatedResIds)) {
            for (Long resId : relatedResIds) {
                NameSpaceField nsField = getNsFieldClone(resId, fld);
                nsField.setParentResourceId(resourceId);
                flattenedNsFields.add(nsField);
            }
            return true;
        } else {
            // if relatedResIds are zero then this related nsField will never be populated so,
            // this execution can be skipped (Note this case is possible when,
            // exec flow comes here from PRIMARY or DIRECT) or via historicalExecutor initiation
            LOGGER.debug("For nsId: " + fld.getNsId() + " the resource " + resourceId + " doesn't have any related resource, returning empty fields list");
            return false;
        }
    }

    public static List<Long> fetchRelatedResourceIds(Long relMapId, Long resourceId, Criteria criteria) throws Exception {
        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relMapId);
        JSONObject data = (JSONObject) RelationUtil.getRecordsWithRelationship(relMapId, resourceId, 1, 500, criteria).get("data");
        List frmObjs = (List) data.get(relationMapping.getFromModule().getName());
        List<Long> resIds = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(frmObjs)) {
            for (Object frmObj : frmObjs) {
                Map obj = (Map) frmObj;
                resIds.add((Long) obj.get("id"));
            }
        }
        return resIds;
    }

    private static void fetchReadingsForNsField(DateRange dateRange, AggregateOperator aggr, TreeMap<Long, Map<String, List<Double>>> readingsMap, NameSpaceField nsField) throws Exception {
        FacilioField field = nsField.getField();

        ModuleBean modBean = Constants.getModBean();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();

        FacilioModule module = modBean.getModule(field.getModuleId());
        List<FacilioField> allFields = modBean.getAllFields(field.getModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        fieldMap.put("id", FieldFactory.getIdField(module));

        List<FacilioField> selectFields = getSelectFields(aggr, nsField, field, fieldMap);
        String groupBy = getGroupByString(aggr, fieldMap);
        selectRecordBuilder
                .table(module.getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), Collections.singleton(nsField.getResourceId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(field.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(field, CommonOperators.IS_NOT_EMPTY))
                .groupBy(groupBy)
                .limit(20000);
        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            populateReadingsMapFromProps(aggr, readingsMap, nsField.getVarName(), field, props);
        }
    }

    // returns aggregated result field and ttime field
    private static List<FacilioField> getSelectFields(AggregateOperator aggr, NameSpaceField nsField, FacilioField field, Map<String, FacilioField> fieldMap) throws Exception {
        List<FacilioField> selectFields = new ArrayList<>();

        FacilioField aggrField = aggr == BmsAggregateOperators.CommonAggregateOperator.ACTUAL
                ? field  // high res
                : getAggregatedField(nsField.getAggregationType(), field); // normal and dashbaord

        FacilioField ttimeField = aggr instanceof BmsAggregateOperators.DateAggregateOperator
                ? ((BmsAggregateOperators.DateAggregateOperator) aggr).getTimestampField(fieldMap.get("ttime"))  // normal
                : fieldMap.get("ttime"); // for high res and dashboard cases

        selectFields.add(ttimeField);
        selectFields.add(aggrField);
        return selectFields;
    }

    private static FacilioField getAggregatedField(AggregationType aggregationType, FacilioField field) throws Exception {
        FacilioField aggrField = getAggrOperatorForAggrType(aggregationType).getSelectField(field);
        aggrField.setColumnName("ROUND(" + aggrField.getCompleteColumnName() + ",3)");
        return aggrField;
    }

    private static BmsAggregateOperators.NumberAggregateOperator getAggrOperatorForAggrType(AggregationType aggregationType) {
        switch (aggregationType) {
            case SUM:
                return BmsAggregateOperators.NumberAggregateOperator.SUM;
            case MAX:
                return BmsAggregateOperators.NumberAggregateOperator.MAX;
            case MIN:
                return BmsAggregateOperators.NumberAggregateOperator.MIN;
            default:
                return BmsAggregateOperators.NumberAggregateOperator.AVERAGE;
        }
    }

    private static String getGroupByString(AggregateOperator aggr, Map<String, FacilioField> fieldMap) throws Exception {
        if (aggr instanceof BmsAggregateOperators.DateAggregateOperator) { // normal
            FacilioField groupBy = aggr.getSelectField(fieldMap.get("ttime"));
            adjustGroupByOptionBasedOnDayOfWeek(aggr, groupBy);
            return groupBy.getCompleteColumnName();
        } else if (aggr == null) { // dashboard
            return fieldMap.get("parentId").getCompleteColumnName();
        }
        return null; // high res
    }

    private static void adjustGroupByOptionBasedOnDayOfWeek(AggregateOperator aggr, FacilioField groupBy) {
        // edge case handled in reports (FetchReportDataCommand:1336), copied here
        if (aggr == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR) {
            DayOfWeek dayOfWeek = DateTimeUtil.getWeekFields().getFirstDayOfWeek();
            if (dayOfWeek == DayOfWeek.MONDAY) {
                String expr_col = groupBy.getColumnName();
                if (expr_col != null) {
                    expr_col = expr_col.replace("%V", "%v");
                    groupBy.setColumnName(expr_col);
                }
            }
        }
    }

    private static void populateReadingsMapFromProps(AggregateOperator aggr, TreeMap<Long, Map<String, List<Double>>> readingsMap, String varName, FacilioField field, List<Map<String, Object>> props) {
        for (Map<String, Object> prop : props) {
            Long ttime = (Long) prop.get("ttime");
            long adjustedTtime = ttime; // high res
            if (aggr == null) { // for dashboard (time aggr is sent as null), and ttime doesn't matter, since only one value is expected, so ttime is dummy assigned 0
                adjustedTtime = 0L;
            } else if (aggr instanceof BmsAggregateOperators.DateAggregateOperator) { // some time aggr
                adjustedTtime = ((BmsAggregateOperators.DateAggregateOperator) aggr).getAdjustedTimestamp(ttime);
            }

            // create new map or add to existing map
            readingsMap.putIfAbsent(adjustedTtime, new HashMap<>());
            Map<String, List<Double>> varNameVsValue = readingsMap.get(adjustedTtime);


            //create new List of values or add to existing List of values
            varNameVsValue.putIfAbsent(varName, new ArrayList<>());
            List<Double> values = varNameVsValue.get(varName);

            Double readingValue = (Double) prop.get(field.getName());
            values.add(readingValue);

            readingsMap.put(adjustedTtime, varNameVsValue);
        }
    }

    private static Map<Long, List<Map<String, Object>>> calculateDynamicKpi(List<Long> parentIds, NameSpaceContext ns, Map<Long, TreeMap<Long, Map<String, List<Double>>>> readingsMap) throws Exception {
        Map<Long, List<Map<String, Object>>> finalResultProps = new HashMap<>();

        String script = ns.getWorkflowContext().getWorkflowV2String();
        for (Long parentId : parentIds) {

            List<Map<String, Object>> resourceResultProps = new ArrayList<>();
            TreeMap<Long, Map<String, List<Double>>> resReadingsMap = readingsMap.get(parentId);
            for (Map.Entry<Long, Map<String, List<Double>>> entry : resReadingsMap.entrySet()) {

                groupByVarNameAndGetVarNameVsValueMap(ns, entry.getValue())
                        .map(varNameVsValueMap -> executeScriptAndGetResultProp(script, entry.getKey(), varNameVsValueMap))
                        .map(resultProp -> {
                            resourceResultProps.add(resultProp);
                            return null;
                        });
            }
            finalResultProps.put(parentId, resourceResultProps);
        }
        LOGGER.info("Dynamic KPI : Final Result Props : " + finalResultProps);
        return finalResultProps;
    }

    private static Optional<Map<String, Object>> groupByVarNameAndGetVarNameVsValueMap(NameSpaceContext ns, Map<String, List<Double>> varNameVsValues) {
        Map<String, Object> groupedVarNameVsValueMap = new HashMap<>();

        for (NameSpaceField fld : ns.getFields()) {
            List<Double> values = varNameVsValues.get(fld.getVarName());
            if (values == null) {
                return Optional.empty();
            }
            NamespaceFieldRelated relatedInfo = fld.getRelatedInfo();
            AggregationType relAggregationType = AggregationType.valueOf(relatedInfo != null && relatedInfo.getRelAggregationTypeInt() != null
                    ? relatedInfo.getRelAggregationTypeInt()
                    : AggregationType.AVG.getIndex());

            Double value = getValueForAggregation(relAggregationType != null ? relAggregationType : AggregationType.AVG, values);
            groupedVarNameVsValueMap.put(fld.getVarName(), value);
        }
        return Optional.of(groupedVarNameVsValueMap);
    }

    @SneakyThrows
    private static Map<String, Object> executeScriptAndGetResultProp(String script, Long ttime, Map<String, Object> groupedVarNameVsValueMap) {
        ScriptContext scriptContext = ScriptUtil.executeScript(script, groupedVarNameVsValueMap);

        Object returnValue = scriptContext.getReturnValue();
        if (returnValue == null) {
            return null;
        }
        Map<String, Object> resultProp = new HashMap<>();
        resultProp.put("ttime", ttime);
        resultProp.put("result", returnValue);
        return resultProp;

    }

    private static Double getValueForAggregation(AggregationType aggregationType, List<Double> values) {
        switch (aggregationType) {
            case SUM:
                return values.stream().mapToDouble(d -> d).sum();
            case MAX:
                return values.stream().mapToDouble(d -> d).max().orElse(0.0);
            case MIN:
                return values.stream().mapToDouble(d -> d).min().orElse(0.0);
            default:
                return values.stream().mapToDouble(d -> d).average().orElse(0.0);
        }
    }

    private static NameSpaceField getNsFieldClone(Long resourceId, NameSpaceField fld) throws CloneNotSupportedException {
        NameSpaceField nsField = (NameSpaceField) fld.clone();
        nsField.setResourceId(resourceId);
        return nsField;
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

    public static Map<Long, Long> getKpiIdVsReadingFieldId(List<Long> kpiIds) throws Exception {
        return getReadingFieldIdVsKpiId(kpiIds).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static Map<Long, Long> getReadingFieldIdVsKpiId(List<Long> kpiIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingKpiModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI);
        FacilioField fieldIdField = FieldFactory.getNumberField("readingFieldId", "READING_FIELD_ID", readingKpiModule);
        FacilioField kpiTypeField = FieldFactory.getNumberField("kpiType", "KPI_TYPE", readingKpiModule);
        SelectRecordsBuilder<ReadingKPIContext> selectRecordsBuilder = new SelectRecordsBuilder<ReadingKPIContext>()
                .module(readingKpiModule)
                .beanClass(ReadingKPIContext.class)
                .select(Collections.singletonList(fieldIdField))
                .andCondition(CriteriaAPI.getCondition(kpiTypeField, String.valueOf(3), NumberOperators.NOT_EQUALS));
        if (CollectionUtils.isNotEmpty(kpiIds)) {
            selectRecordsBuilder.andCondition(CriteriaAPI.getIdCondition(kpiIds, readingKpiModule));
        }

        return Optional.ofNullable(selectRecordsBuilder.get())
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.toMap(ReadingKPIContext::getReadingFieldId, ReadingKPIContext::getId));
    }

    public Boolean logsInProgress(Long kpiId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule loggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE));
        SelectRecordsBuilder<KpiLoggerContext> builder = new SelectRecordsBuilder<KpiLoggerContext>()
                .select(Collections.singleton(FieldFactory.getIdField(loggerModule)))
                .module(loggerModule)
                .beanClass(KpiLoggerContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("kpi"), Collections.singleton(kpiId), NumberOperators.EQUALS));

        List<KpiLoggerContext> loggersInProgress = builder.get();
        return CollectionUtils.isNotEmpty(loggersInProgress);
    }

    public static List<KpiResourceLoggerContext> logsInProgressForResource(Long kpiId, List<Long> resourceIds) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule resourceLoggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE));
        SelectRecordsBuilder<KpiResourceLoggerContext> builder = new SelectRecordsBuilder<KpiResourceLoggerContext>()
                .select(Arrays.asList(fieldsMap.get("resourceId"), fieldsMap.get("startTime"), fieldsMap.get("endTime")))
                .module(resourceLoggerModule)
                .beanClass(KpiResourceLoggerContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("kpiId"), Collections.singleton(kpiId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("resourceId"), resourceIds, NumberOperators.EQUALS));

        List<KpiResourceLoggerContext> resLogInProgress = builder.get();
        if (CollectionUtils.isNotEmpty(resLogInProgress)) {
            return resLogInProgress;
        }
        return new ArrayList<>();
    }


    public static void beginLiveKpiHistorical(List<ReadingKPIContext> kpis, Long startTime, Long endTime, List<Long> assetIds, boolean executeDependencies) throws Exception {
        postConRuleHistoryInstructionToStorm(kpis, startTime, endTime, assetIds, executeDependencies, InstructionType.LIVE_KPI_HISTORICAL);
    }

    public static void beginSchKpiHistorical(ReadingKPIContext kpi, Long startTime, Long endTime, List<Long> assetIds) throws Exception {
        JSONObject props = new JSONObject();
        props.put(FacilioConstants.ContextNames.START_TIME, startTime);
        props.put(FacilioConstants.ContextNames.END_TIME, endTime);
        props.put(FacilioConstants.ReadingKpi.IS_HISTORICAL, true);
        props.put(FacilioConstants.ContextNames.RESOURCE_LIST, assetIds);
        props.put(FacilioConstants.ReadingKpi.READING_KPI, kpi.getId());

        Long parentLoggerId = ReadingKpiLoggerAPI.insertLog(kpi.getId(), KPIType.SCHEDULED.getIndex(), startTime, endTime, false, CollectionUtils.isNotEmpty(assetIds) ? assetIds.size() : NamespaceAPI.getMatchedResources(kpi.getNs(), kpi.getAssetCategory().getId()).size());
        props.put(FacilioConstants.ReadingKpi.PARENT_LOGGER_ID, parentLoggerId);

        scheduleOneTimeJobWithProps(ReadingKpiLoggerAPI.getNextJobId(), FacilioConstants.ReadingKpi.READING_KPI_HISTORICAL_JOB, 1, "facilio", props);
    }

}
