package com.facilio.readingkpi;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
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
import com.facilio.readingkpi.readingslist.AssetDataFetcher;
import com.facilio.readingkpi.readingslist.KpiAnalyticsDataFetcher;
import com.facilio.readingkpi.readingslist.MeterDataFetcher;
import com.facilio.readingkpi.readingslist.ReadingKPIDataFetcher;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.service.FacilioService;
import com.facilio.storm.InstructionType;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.json.simple.JSONObject;

import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facilio.connected.CommonConnectedUtil.getDependencyMapForConnectedRules;
import static com.facilio.connected.CommonConnectedUtil.postConRuleHistoryInstructionToStorm;
import static com.facilio.modules.BaseFieldFactory.getIdField;

@Log4j
public class ReadingKpiAPI {

    public static List<ReadingKPIContext> getAllActiveKpis() throws Exception {
        return getAllActiveKpis(null, null);
    }

    public static List<ReadingKPIContext> getAllActiveKpis(Map<String, Object> paramsMap, Criteria userCriteria) throws Exception {
        FacilioContext context = getAllActiveKpisWithMeta(paramsMap, userCriteria);
        Map<String, Object> readingKpiMap = (Map<String, Object>) context.get(Constants.RECORD_MAP);
        return (List<ReadingKPIContext>) readingKpiMap.get(FacilioConstants.ReadingKpi.READING_KPI);
    }

    public static List<ReadingKPIContext> getAllKpisWithMeta() throws Exception {
        return getAllKpisWithMeta(null, null);
    }

    public static List<ReadingKPIContext> getAllKpisWithMeta(Map<String, Object> paramsMap, Criteria userCriteria) throws Exception {
        FacilioContext context = getAllKpisWithMeta(paramsMap, userCriteria, false);
        Map<String, Object> readingKpiMap = (Map<String, Object>) context.get(Constants.RECORD_MAP);
        return (List<ReadingKPIContext>) readingKpiMap.get(FacilioConstants.ReadingKpi.READING_KPI);
    }

    public static FacilioContext getAllActiveKpisWithMeta(Map<String, Object> paramsMap, Criteria userCriteria) throws Exception {
        return getAllKpisWithMeta(paramsMap, userCriteria, true);
    }

    public static FacilioContext getAllKpisWithMeta(Map<String, Object> paramsMap, Criteria userCriteria, boolean fetchOnlyActive) throws Exception {
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
        if (fetchOnlyActive) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
        }
        criteria.andCriteria(userCriteria);

        return V3Util.fetchList(moduleName, true, null, null, false, null, orderBy, orderType, search, page, perPage, true, null, criteria, null);
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

    public static List<ReadingKPIContext> getKpisOfCategory(Long categoryId, ResourceType resourceType) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ReadingKpi.READING_KPI));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("categoryId"), Collections.singleton(categoryId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("resourceType"), String.valueOf(resourceType.getIndex()), NumberOperators.EQUALS));
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
        orderedReadingKpis.stream().filter(Objects::nonNull);

        return orderedReadingKpis;
    }


    public static List<ReadingKPIContext> setNamespaceAndMatchedResources(@NonNull List<ReadingKPIContext> kpis) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (ReadingKPIContext kpi : kpis) {
            kpi.setNs(NamespaceAPI.getNameSpaceByRuleId(kpi.getId(), NSType.KPI_RULE));
            kpi.setReadingField(modBean.getField(kpi.getReadingFieldId()));
            FacilioModule module = modBean.getModule(kpi.getReadingModuleId());
            kpi.setReadingModule(module);
            setCategory(kpi);
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
                        LocalTime time = LocalTime.of(i, 0);
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


    // Dynamic KPI Util

    public static List<KpiContextWrapper> getDynamicKpisForResource(Long resourceId) throws Exception {
        AssetContext assetInfo = AssetsAPI.getAssetInfo(resourceId);
        AssetCategoryContext category = assetInfo.getCategory();
        return getDynamicKpisOfCategory(category.getId()).stream()
                .filter(dynKpi -> getMatchedResourcesWrapper(dynKpi.getNs()).contains(resourceId))
                .map(dynKpi -> new KpiContextWrapper(dynKpi.getId(), dynKpi.getName()))
                .collect(Collectors.toList());
    }

    private static List<Long> getMatchedResourcesWrapper(NameSpaceContext ns) {
        try {
            return NamespaceAPI.getMatchedResources(ns);
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
                case METER_READING:
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
        String groupBy = getGroupByString(aggr, nsField.getAggregationType(), module, fieldMap);
        selectRecordBuilder
                .table(module.getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(field.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), Collections.singleton(nsField.getResourceId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(field, CommonOperators.IS_NOT_EMPTY))
                .groupBy(groupBy)
                .limit(20000);

        List<Map<String, Object>> props = null;
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE))
        {
            try {
                props = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.CLICKHOUSE,
                        () -> selectRecordBuilder.get());
            }catch (Exception e){
                LOGGER.error("Error while execution dynamic kpi--- " , e);
            }
        }
        else
        {
            props = selectRecordBuilder.get();
        }
        LOGGER.debug("SELECT BUILDER EXECUTED FOR DYNAMICK KPI" + selectRecordBuilder);
        if (CollectionUtils.isNotEmpty(props)) {
            populateReadingsMapFromProps(aggr, readingsMap, nsField.getVarName(), field, props);
        }
    }

    // returns aggregated result field and ttime field
    private static List<FacilioField> getSelectFields(AggregateOperator aggr, NameSpaceField nsField, FacilioField field, Map<String, FacilioField> fieldMap) throws Exception {
        List<FacilioField> selectFields = new ArrayList<>();

        FacilioField aggrField = aggr == BmsAggregateOperators.CommonAggregateOperator.ACTUAL && nsField.getAggregationType() != AggregationType.COUNT
                ? field  // high res
                : getAggregatedField(nsField.getAggregationType(), field); // normal and dashboard

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

    private static AggregateOperator getAggrOperatorForAggrType(AggregationType aggregationType) {
        switch (aggregationType) {
            case SUM:
                return BmsAggregateOperators.NumberAggregateOperator.SUM;
            case MAX:
                return BmsAggregateOperators.NumberAggregateOperator.MAX;
            case MIN:
                return BmsAggregateOperators.NumberAggregateOperator.MIN;
            case COUNT:
                return BmsAggregateOperators.CommonAggregateOperator.COUNT;
            default:
                return BmsAggregateOperators.NumberAggregateOperator.AVERAGE;
        }
    }

    private static String getGroupByString(AggregateOperator aggr, AggregationType nsAggrType, FacilioModule module, Map<String, FacilioField> fieldMap) throws Exception {
        if (aggr instanceof BmsAggregateOperators.DateAggregateOperator) { // normal
            FacilioField groupBy = aggr.getSelectField(fieldMap.get("ttime"));
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE))
            {
                groupBy = BmsAggregateOperators.getCHAggregateOperator(aggr.getValue()).getSelectField(fieldMap.get("ttime")).clone();
            }
            FacilioField groupByField = adjustGroupByOptionBasedOnDayOfWeek(aggr , fieldMap.get("ttime"));
            if(groupByField != null){
                groupBy = groupByField.clone();
            }
            return groupBy.getCompleteColumnName();
        } else if (aggr == null) { // dashboard
            return fieldMap.get("parentId").getCompleteColumnName();
        } else if (nsAggrType == AggregationType.COUNT) {
            return FieldFactory.getIdField(module).getCompleteColumnName();
        }
        return null; // high res
    }

    private static FacilioField adjustGroupByOptionBasedOnDayOfWeek(AggregateOperator aggr, FacilioField xField) throws Exception{
        // edge case handled in reports (FetchReportDataCommand:1336), copied here
        if (aggr == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR) {
            DayOfWeek dayOfWeek = DateTimeUtil.getWeekFields().getFirstDayOfWeek();
            if (dayOfWeek == DayOfWeek.MONDAY) {
                return AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE) ? BmsAggregateOperators.CHDateAggregateOperator.MONDAY_START_WEEKLY.getSelectField(xField).clone() : BmsAggregateOperators.DateAggregateOperator.MONDAY_START_WEEKANDYEAR.getSelectField(xField).clone();
            }
        }
        return null;
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

            Double readingValue = Double.parseDouble(String.valueOf(prop.get(field.getName())));
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

    public static void beginKpiHistorical(List<ReadingKPIContext> kpis, long startTime, long endTime, List<Long> assetIds, boolean executeDependencies, boolean isSysCreated) throws Exception {
        postConRuleHistoryInstructionToStorm(kpis, startTime, endTime, assetIds, executeDependencies, InstructionType.LIVE_KPI_HISTORICAL, isSysCreated);

    }

    public static void setCategory(ReadingKPIContext kpi) throws Exception {
        ResourceType type = kpi.getResourceTypeEnum();
        V3Context category = CommonConnectedUtil.getCategory(type, kpi.getCategoryId());
        if (category != null) {
            kpi.setCategory(new ResourceCategory<>(type, category));
        }
    }

    public static void addFilterToBuilder(Context context, Map<String, FacilioField> fieldsMap, GenericSelectRecordBuilder builder) {
        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH_QUERY);
        if (fieldsMap != null && StringUtils.isNotEmpty(searchText)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), searchText, StringOperators.CONTAINS));
        }

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null) {
            builder.andCriteria(filterCriteria);
        }
    }

    public static GenericSelectRecordBuilder getCountBuilder(GenericSelectRecordBuilder builder, FacilioModule module) throws Exception {
        FacilioField selectDistinctField = getSelectDistinctIdField(module);
        builder.select(new HashSet<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, selectDistinctField);
        return builder;
    }

    public static GenericSelectRecordBuilder getDataBuilder(Context context, FacilioModule module, Map<String, FacilioField> fieldsMap, GenericSelectRecordBuilder builder) {
        return getDataBuilder(context, module, fieldsMap, builder, null);
    }

    public static GenericSelectRecordBuilder getDataBuilder(Context context, FacilioModule module, Map<String, FacilioField> fieldsMap, GenericSelectRecordBuilder builder, List<FacilioField> additionalSelectFields) {
        FacilioField selectDistinctField = getSelectDistinctIdField(module);
        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(selectDistinctField);
        selectFields.add(fieldsMap.get("name"));
        if (CollectionUtils.isNotEmpty(additionalSelectFields)) {
            selectFields.addAll(additionalSelectFields);
        }
        builder.select(selectFields);
        return addPaginationPropsToBuilder(context, builder);
    }

    private static FacilioField getSelectDistinctIdField(FacilioModule module) {
        FacilioField idField = FieldFactory.getIdField(module);
        FacilioField selectDistinctField = idField.clone();
        selectDistinctField.setColumnName("DISTINCT(" + idField.getCompleteColumnName() + ")");
        selectDistinctField.setModule(null);
        return selectDistinctField;
    }

    public static GenericSelectRecordBuilder addPaginationPropsToBuilder(Context context, GenericSelectRecordBuilder builder) {
        Integer page = (Integer) context.get(FacilioConstants.ContextNames.PAGE);
        Integer perPage = (Integer) context.get(FacilioConstants.ContextNames.PER_PAGE);

        if (perPage != null) {
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            builder.offset(offset);
            builder.limit(perPage);
        }
        return builder;
    }

    public static long getStartTimeForHistoricalCalculation(NamespaceFrequency freq) {
        long currentTime = DateTimeUtil.getCurrenTime() - 10 * 60 * 1000; // trigger happens at 00:00, we need prev interval, so 10 mins behind
        switch (freq) {
            case ONE_DAY:
                return DateTimeUtil.getDayStartTimeOf(currentTime);
            case WEEKLY:
                return DateTimeUtil.getWeekStartTimeOf(currentTime);
            case MONTHLY:
                return DateTimeUtil.getMonthStartTimeOf(currentTime);
            case QUARTERLY:
                return DateTimeUtil.getQuarterStartTimeOf(currentTime);
            case HALF_YEARLY:
                return DateTimeUtil.getYearStartTime(-1);
            case ANNUALLY:
                return DateTimeUtil.getYearStartTimeOf(currentTime);
            default:
                throw new IllegalArgumentException("Invalid Scheduled Freq");
        }
    }

    public static KpiAnalyticsDataFetcher getKpiAnalyticsDataFetcher(String moduleName, Context context) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        switch (moduleName) {
            case FacilioConstants.ReadingKpi.READING_KPI:
                return new ReadingKPIDataFetcher(module, context, Collections.singletonList(FieldFactory.getNumberField("kpiType", "KPI_TYPE", module)));
            case FacilioConstants.ContextNames.ASSET:
                return new AssetDataFetcher(module, context, new ArrayList<>());
            case FacilioConstants.Meter.METER:
                return new MeterDataFetcher(module, context, new ArrayList<>());
            default:
                throw new IllegalArgumentException("Unsupported Module");
        }
    }

    public static List<Map<String, Object>> getMatchedResourcesOfAllKpis(Long categoryId, ResourceType resourceType) throws Exception {
        FacilioModule nsModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
        FacilioModule nsInclModule = NamespaceModuleAndFieldFactory.getNamespaceInclusionModule();
        Map<String, FacilioField> nsFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());
        Map<String, FacilioField> nsInclFieldsMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceInclusionFields());

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(getIdField(nsModule));
        selectFields.add(nsInclFieldsMap.get("resourceId"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(nsModule.getTableName())
                .select(selectFields)
                .leftJoin(nsInclModule.getTableName())
                .on(nsModule.getTableName() + ".ID=" + nsInclModule.getTableName() + ".NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("type"), String.valueOf(NSType.KPI_RULE.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("resourceType"), String.valueOf(resourceType.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("categoryId"), String.valueOf(categoryId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(nsFieldsMap.get("status"), String.valueOf(1), NumberOperators.EQUALS));

        return builder.get();
    }

    public static Set<Long> getInclResIdsFromProps(List<Map<String, Object>> maps) {
        Set<Long> inclResIds = new HashSet<>();
        Map<Long, Set<Long>> nsIdVsResIds = new HashMap<>();
        for (Map<String, Object> map : maps) {
            Long resId = (Long) map.get("resourceId");
            if(resId == null){
                return new HashSet<>();
            }
            Long nsId = (Long) map.get("id");
            if(nsIdVsResIds.containsKey(nsId)){
                Set<Long> resIds = nsIdVsResIds.get(nsId);
                resIds.add(resId);
            } else {
                Set<Long> resIds = new HashSet<>();
                resIds.add(resId);
                nsIdVsResIds.put(nsId, resIds);
            }
        }
        return inclResIds;
    }
}
