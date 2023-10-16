package com.facilio.connected;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.storm.InstructionType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.apache.commons.collections4.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.modules.FieldFactory.getField;
import static com.facilio.readingkpi.ReadingKpiAPI.getReadingKpis;
import static com.facilio.readingrule.util.NewReadingRuleAPI.getReadingRules;

public class CommonConnectedUtil {

    private static final int MAX_HOPS = 5;

    public static IConnectedRule fetchConnectedRule(Long conRuleId, NSType type) throws Exception {
        List<IConnectedRule> connectedRules = fetchConnectedRules(Collections.singleton(conRuleId), type);
        if (CollectionUtils.isEmpty(connectedRules)) {
            throw new IllegalArgumentException("Invalid Connected Rule Id");
        }
        return connectedRules.get(0);
    }

    public static List<IConnectedRule> fetchConnectedRules(Set<Long> conRuleIds, NSType type) throws Exception {
        Map<Long, Long> ruleIdVsNsId = new HashMap<>();
        for (Long id : conRuleIds) {
            NamespaceAPI.getNsIdForRuleId(id, type)
                    .map(nsId -> ruleIdVsNsId.put(id, nsId));
        }
        if (ruleIdVsNsId.isEmpty()) {
            throw new IllegalArgumentException("Invalid conRule Ids, no corresponding namespaces found");
        }
        switch (type) {
            case KPI_RULE:
                return getReadingKpis(ruleIdVsNsId);
            case READING_RULE:
                return getReadingRules(ruleIdVsNsId);
        }
        throw new IllegalArgumentException("Invalid Connected Rule Type");
    }

    /**
     * @param connectedRules - a list of connectedRules known to be dependent
     * @return the graph these dependent conRules
     */
    public static Graph<Long, DefaultEdge> getDependencyGraphForConnectedRules(List<IConnectedRule> connectedRules) {
        Map<Long, List<Long>> dependencyMap = getDependencyMapForConnectedRules(connectedRules);
        List<Graph<Long, DefaultEdge>> dependencyGraphs = getGraphsOfDependentRules(dependencyMap);

        return dependencyGraphs.get(0);
    }

    /**
     * @return a map of connected rule ID vs dependent rule IDs. A dependent rule is identified by checking for its reading field id in the namespace fields of other given connected rules
     */
    public static <T extends IConnectedRule> Map<Long, List<Long>> getDependencyMapForConnectedRules(@NonNull List<T> connectedRules) {

        Map<Long, Long> readingFieldIdVsRuleId = connectedRules.stream().collect(Collectors.toMap(IConnectedRule::getReadingFieldId, IConnectedRule::getId));

        // the following map gives the dependent conRuleIds(value) for a given conRule(key)
        Map<Long, List<Long>> ruleIdDepRuleIdsMap = connectedRules.stream()
                .collect(
                        Collectors.toMap(
                                IConnectedRule::getId,
                                connectedRule -> getParentConRuleIds(
                                        connectedRule.getNs().getFields().stream().map(NameSpaceField::getFieldId).collect(Collectors.toList()),//nsFieldIds
                                        readingFieldIdVsRuleId
                                )
                        )
                );


        ruleIdDepRuleIdsMap.entrySet().removeIf(x -> isIndependentConnectedRule(x.getKey(), ruleIdDepRuleIdsMap));
        return ruleIdDepRuleIdsMap;
    }

    /**
     * @return returns the readingFieldIds of connectedRules alone from ns fields of given rule (filters out normal readings, like active power b for example)
     */
    private static List<Long> getParentConRuleIds(List<Long> nsFieldIds, Map<Long, Long> readingFieldIdVsRuleId) {
        List<Long> fieldIdsOfConRules = new ArrayList<>(readingFieldIdVsRuleId.keySet());
        return nsFieldIds.stream().filter(fieldIdsOfConRules::contains).map(readingFieldIdVsRuleId::get).collect(Collectors.toList());
    }

    /**
     * @implNote Single degree source Rules and Independent Rules do not have any dependent Rules in the final ruleIdDepRuleIdsMap.
     * This function differentiates these two by iterating through each rule in the map and checks if its field id exists
     * in other rule's dependent rule id list.
     */
    private static boolean isIndependentConnectedRule(Long conRuleId, Map<Long, List<Long>> ruleIdDepRuleIdsMap) {
        // to differentiate independent and single degree rules
        List<Long> depIdList = ruleIdDepRuleIdsMap.get(conRuleId).stream().filter(Objects::nonNull).collect(Collectors.toList()); // removes null-only lists
        if (CollectionUtils.isEmpty(depIdList)) {
            for (Map.Entry<Long, List<Long>> entry : ruleIdDepRuleIdsMap.entrySet()) {
                if (!Objects.equals(entry.getKey(), conRuleId) && entry.getValue().contains(conRuleId)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }


    public static List<Graph<Long, DefaultEdge>> getGraphsOfDependentRules(Map<Long, List<Long>> dependencyMap) {
        Graph<Long, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        List<Long> dependentRules = new ArrayList<>(); // rules that aren't sources

        // first add vertices for each rule and list all dep rules
        for (Map.Entry<Long, List<Long>> entry : dependencyMap.entrySet()) {
            Long id = entry.getKey();
            directedGraph.addVertex(id);
            List<Long> depIdList = entry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList()); // removes null-only entries
            if (CollectionUtils.isNotEmpty(depIdList)) {
                dependentRules.add(id);
            }
        }
        // for every dep kpi add an edge with its dependency
        for (Long id : dependentRules) {
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

    public static Map<String, Object> fetchConRuleFamilyNodesAndEdges(Long connectedRuleId, NSType type, List<Long> resourceIds) throws Exception {
        int xGap = 300;
        int yGap = 300;
        DirectedAcyclicGraph<Long, DefaultEdge> graph = fetchConRuleFamilyUpwardGraph(connectedRuleId, type, resourceIds);
        List<IConnectedRule> conRules = fetchConnectedRules(graph.vertexSet(), type);
        List<Set<Long>> orderOfExecution = getOrderOfExecution(graph);
        List<Map<String, Object>> nodes = new ArrayList<>();

        for (int i = 0; i < orderOfExecution.size(); i++) {
            Set<Long> oneLevelConRules = orderOfExecution.get(i);
            int j = 0;
            for (Long conRuleId : oneLevelConRules) {
                Map<String, Object> map = new HashMap<>();
                Optional<IConnectedRule> conRuleOpt = getConRuleCtxWithId(conRules, conRuleId);
                if (!conRuleOpt.isPresent()) continue;
                IConnectedRule conRule = conRuleOpt.get();
                map.put("name", conRule.getId() + " " + conRule.getName());
                map.put("x", j * xGap);
                map.put("y", i * yGap);
                nodes.add(map);
                j++;
            }
        }
        List<Map<String, Object>> edges = new ArrayList<>();
        for (DefaultEdge edge : graph.edgeSet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("source", graph.getEdgeSource(edge));
            map.put("target", graph.getEdgeTarget(edge));
            edges.add(map);
        }
        Map<String, Object> nodesAndEdgesMap = new HashMap<>();
        nodesAndEdgesMap.put("data", nodes);
        nodesAndEdgesMap.put("links", edges);
        return nodesAndEdgesMap;
    }

    private static Optional<IConnectedRule> getConRuleCtxWithId(List<IConnectedRule> conRules, Long conRuleId) {
        return conRules.stream()
                .filter(cRule -> cRule.getId() == conRuleId)
                .findFirst();
    }

    public static DirectedAcyclicGraph<Long, DefaultEdge> fetchConRuleFamilyUpwardGraph(Long connectedRuleId, NSType type, List<Long> resourceIds) throws Exception {
        IConnectedRule conRule = fetchConnectedRules(Collections.singleton(connectedRuleId), type).get(0);
        ResourceCategory resourceCategory=conRule.getCategory();
        NameSpaceContext ns = conRule.getNs();
        Long categoryId = resourceCategory.fetchId();
        if (CollectionUtils.isEmpty(resourceIds)) {
            resourceIds = NamespaceAPI.getMatchedResources(ns, resourceCategory);
        }

        DirectedAcyclicGraph<Long, DefaultEdge> directedGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        directedGraph.addVertex(connectedRuleId);

        Map<Long, Long> conRuleIdVsFieldId = getFieldIdVsConRuleId(type).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        fetchConRuleFamilyUpwards(connectedRuleId, type, categoryIds, conRuleIdVsFieldId, directedGraph, new ArrayList<>(resourceIds), 0);
        return directedGraph;
    }

    public static Map<Long, Long> getFieldIdVsConRuleId(NSType type) throws Exception {
        return getFieldIdVsConRuleId(null, type);
    }

    public static Map<Long, Long> getFieldIdVsConRuleId(List<Long> conRuleIds, NSType type) throws Exception {
        switch (type) {
            case KPI_RULE:
                return ReadingKpiAPI.getReadingFieldIdVsKpiId(conRuleIds);
            case READING_RULE:
                return NewReadingRuleAPI.getReadingFieldIdVsRuleId(conRuleIds);
        }
        return new HashMap<>();
    }

    private static void fetchConRuleFamilyUpwards(Long conRuleId, NSType type, List<Long> categoryIds, Map<Long, Long> conRuleIdVsFieldId, DirectedAcyclicGraph<Long, DefaultEdge> directedGraph, List<Long> resourceIds, int hops) throws Exception {
        if (hops >= MAX_HOPS) return;
        Set<Long> firstCircle = getFirstCircleRelationUpwards(conRuleId, type, categoryIds, conRuleIdVsFieldId, resourceIds);
        if (CollectionUtils.isNotEmpty(firstCircle)) {
            boolean firstIter = true;
            for (Long nextConRule : firstCircle) {
                directedGraph.addVertex(nextConRule);
                directedGraph.addEdge(conRuleId, nextConRule);
                if (firstIter) {
                    hops++;
                    firstIter = false;
                }
                fetchConRuleFamilyUpwards(nextConRule, type, categoryIds, conRuleIdVsFieldId, directedGraph, resourceIds, hops);
            }
        }
    }

    public static Set<Long> getFirstCircleRelationUpwards(Long conRuleId, NSType type, List<Long> categoryIds, Map<Long, Long> conRuleIdVsFieldId, List<Long> resourceIds) throws Exception {
        FacilioModule namespaceModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
        Map<String, FacilioField> namespaceFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());

        Map<String, FacilioField> namespaceFieldsFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFieldFields());

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(namespaceModule.getTableName())
                .select(Arrays.asList(namespaceFieldMap.get("parentRuleId"), namespaceFieldMap.get("categoryId"), namespaceFieldsFieldMap.get("resourceId")))
                .innerJoin("Namespace_Fields")
                .on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(namespaceFieldMap.get("status"), String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(namespaceFieldsFieldMap.get("fieldId"), Collections.singleton(conRuleIdVsFieldId.get(conRuleId)), NumberOperators.EQUALS));


        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isEmpty(props)) {
            return new HashSet<>();
        }

        return getFirstCircleFromProps(categoryIds, type, resourceIds, props);
    }

    private static Set<Long> getFirstCircleFromProps(List<Long> categoryIds, NSType type, List<Long> resourceIds, List<Map<String, Object>> props) throws Exception {
        Set<Long> firstCircle = new OrderedHashSet<>();
        for (Map<String, Object> prop : props) {
            Long parentRuleId = (Long) prop.get("parentRuleId");
            Long catId = (Long) prop.get("categoryId");
            // if the rule is of same category, or if the main graph contains other categories (captured in categoryIds) directly add
            if (categoryIds.contains(catId)) {
                firstCircle.add(parentRuleId);
                continue;
            }
            // else check if the namespace field's resource id is in the list of resources given by user or the field
            Long resourceId = (Long) prop.get("resourceId");
            if (resourceIds.contains(resourceId)) {
                firstCircle.add(parentRuleId);
                categoryIds.add(catId);
                IConnectedRule connectedRule = fetchConnectedRule(parentRuleId, type);
                ResourceCategory resourceCategory=connectedRule.getCategory();
                resourceIds.addAll(NamespaceAPI.getMatchedResources(connectedRule.getNs(),resourceCategory));
            }
        }
        return firstCircle;
    }


//    public static DefaultDirectedGraph<Long, DefaultEdge> fetchConnectedRuleFamily(Long connectedRuleId, NSType type) throws Exception {
//        DefaultDirectedGraph<Long, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
//        directedGraph.addVertex(connectedRuleId);
//
//        IConnectedRule conRule = fetchConnectedRules(Collections.singleton(connectedRuleId), type).get(0);
//        NameSpaceContext ns = conRule.getNs();
//        Long categoryId = ns.getCategoryId();
//
//        Map<Long, Long> fieldIdVsConRuleId = getFieldIdVsConRuleId(type);
//        Map<Long, Long> conRuleIdVsFieldId = fieldIdVsConRuleId.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//        fetchConRuleFamilyDownwards(connectedRuleId, fieldIdVsConRuleId, directedGraph);
//        fetchConRuleFamilyUpwards(connectedRuleId, type, Arrays.asList(categoryId), conRuleIdVsFieldId, directedGraph, null);
//        return directedGraph;
//    }

//    public static DefaultDirectedGraph<Long, DefaultEdge> fetchConRuleFamilyDownwardGraph(Long connectedRuleId, NSType type) throws Exception {
//        DefaultDirectedGraph<Long, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
//        directedGraph.addVertex(connectedRuleId);
//
//        Map<Long, Long> fieldIdVsConRuleId = getFieldIdVsConRuleId(type);
//
//        fetchConRuleFamilyDownwards(connectedRuleId, fieldIdVsConRuleId, directedGraph);
//        return directedGraph;
//    }


//    public static void fetchConRuleFamilyDownwards(Long conRuleId, Map<Long, Long> conRuleIdVsFieldIds, Graph<Long, DefaultEdge> directedGraph) throws Exception {
//        List<Long> firstCircle = getFirstCircleRelationDownwards(conRuleId, conRuleIdVsFieldIds);
//        if (CollectionUtils.isNotEmpty(firstCircle)) {
//            for (Long nextConRule : firstCircle) {
//                directedGraph.addVertex(nextConRule);
//                directedGraph.addEdge(nextConRule, conRuleId);
//                fetchConRuleFamilyDownwards(nextConRule, conRuleIdVsFieldIds, directedGraph);
//            }
//        }
//    }


//    public static List<Long> getFirstCircleRelationDownwards(Long conRuleId, Map<Long, Long> fieldIdVsConRuleId) throws Exception {
//        FacilioModule namespaceModule = NamespaceModuleAndFieldFactory.getNamespaceModule();
//        Map<String, FacilioField> namespaceFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFields());
//
//        Map<String, FacilioField> namespaceFieldsFieldMap = FieldFactory.getAsMap(NamespaceModuleAndFieldFactory.getNamespaceFieldFields());
//
//        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//                .table(namespaceModule.getTableName())
//                .select(Collections.singletonList(namespaceFieldsFieldMap.get("fieldId")))
//                .innerJoin("Namespace_Fields")
//                .on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
//                .andCondition(CriteriaAPI.getCondition(namespaceFieldMap.get("parentRuleId"), Collections.singletonList(conRuleId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(namespaceFieldMap.get("status"), String.valueOf(true), BooleanOperators.IS))
//                .andCondition(CriteriaAPI.getCondition(namespaceFieldsFieldMap.get("fieldId"), fieldIdVsConRuleId.keySet(), NumberOperators.EQUALS));
//
//
//        List<Map<String, Object>> props = selectRecordBuilder.get();
//        List<Long> firstCircle = new ArrayList<>();
//        for (Map<String, Object> prop : props) {
//            firstCircle.add(fieldIdVsConRuleId.get((Long) prop.get("fieldId")));
//        }
//        return firstCircle;
//    }

    public static List<Set<Long>> getOrderOfExecution(Graph<Long, DefaultEdge> graph) {
        // list of sets and not a flat list to implement parallel exec of a single set, later
        List<Set<Long>> orderOfExecution = new ArrayList<>();

        Set<Long> sourceVertices = getSourceVertices(graph);
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

    public static Set<Long> getSourceVertices(Graph<Long, DefaultEdge> graph) {
        return graph.vertexSet().stream().filter(vertex -> graph.inDegreeOf(vertex) == 0).collect(Collectors.toSet());
    }

    public static <T extends IConnectedRule> void postConRuleHistoryInstructionToStorm(List<T> conRules, long startTime, long endTime, List<Long> assets, boolean executeDependencies, InstructionType instructionType) throws Exception {
        FacilioChain runStormHistorical = TransactionChainFactory.initiateStormInstructionExecChain();
        FacilioContext historicalContext = runStormHistorical.getContext();
        historicalContext.put("type", instructionType.getIndex());

        JSONObject instructionData = new JSONObject();
        instructionData.put("conRuleIds", conRules.stream().map(IConnectedRule::getId).collect(Collectors.toSet()));
        instructionData.put("startTime", startTime);
        instructionData.put("endTime", endTime);
        instructionData.put("assetIds", assets);
        instructionData.put("executeDependencies", executeDependencies);

        Map<Long, Long> conRuleIdVsParentLoggerId = new HashMap<>();
        for (IConnectedRule conRule : conRules) {
            instructionData.put("assetCategoryId", conRule.getCategory().fetchId());
            long parentLoggerId = conRule.insertLog(startTime, endTime, CollectionUtils.isNotEmpty(assets) ? assets.size() : NamespaceAPI.getMatchedResources(conRule.getNs(), conRule.getCategory()).size(), false);
            conRuleIdVsParentLoggerId.put(conRule.getId(), parentLoggerId);
        }
        instructionData.put("conRuleIdVsParentLoggerId", conRuleIdVsParentLoggerId);

        historicalContext.put("data", instructionData);
        runStormHistorical.execute();
    }

    public static V3Context getCategory(ResourceType type, Long categoryId) throws Exception {
        if(categoryId == null) {
            return null;
            //TODO: hack, need to remove this return check. added for backward compatibility
            //throw new Exception("Category id cannot be null");
        }
        FacilioContext resultCtx;
        switch (type) {
            case ASSET_CATEGORY:
                resultCtx = V3Util.getSummary(FacilioConstants.ContextNames.ASSET_CATEGORY, Lists.newArrayList(categoryId));
                List<V3AssetCategoryContext> assetCategoriesCtx = Constants.getRecordListFromContext(resultCtx, FacilioConstants.ContextNames.ASSET_CATEGORY);
                return CollectionUtils.isNotEmpty(assetCategoriesCtx) ? assetCategoriesCtx.get(0) : null;
            case METER_CATEGORY:
                resultCtx = V3Util.getSummary(FacilioConstants.Meter.UTILITY_TYPE, Lists.newArrayList(categoryId));
                List<V3UtilityTypeContext> utilityCtxs = Constants.getRecordListFromContext(resultCtx, FacilioConstants.Meter.UTILITY_TYPE);
                return CollectionUtils.isNotEmpty(utilityCtxs) ? utilityCtxs.get(0) : null;
            case SITE:
                throw new Exception("Not supported yet");
        }
        return null;
    }

    public static List<Long> getResourceIdsBasedOnCategory(ResourceType resourceType, Long categoryId) throws Exception {
        switch (resourceType) {
            case ASSET_CATEGORY:
                List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(categoryId);
                return assets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
            case METER_CATEGORY:
                List<V3MeterContext> meters = MetersAPI.getMeterListOfUtilityType(categoryId);
                return meters.stream().map(meter -> meter.getId()).collect(Collectors.toList());
            case SITE:
                throw new Exception("Not supported yet");
        }
        return new ArrayList<>();
    }

    public static List<Long> getResourcesBasedOnType(int resourceType, List<Long> assetIds) throws Exception {
        ResourceType resourceTypeEnum=ResourceType.valueOf(resourceType);
        return getResourcesBasedOnType(resourceTypeEnum,assetIds);
    }
    public static List<Long> getResourcesBasedOnType(ResourceType resourceType, List<Long> assetIds) throws Exception {
        switch (resourceType) {
            case ASSET_CATEGORY:
                List<AssetContext> assetInfo = AssetsAPI.getAssetInfo(assetIds);
                return assetInfo.stream().map(m -> m.getId()).collect(Collectors.toList());
            case METER_CATEGORY:
                List<V3MeterContext> meterInfo = MetersAPI.getMeters(assetIds);
                return meterInfo.stream().map(m -> m.getId()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static Map<String, Object> getConnectedData(NameSpaceContext ns) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        NSType type = ns.getTypeEnum();
        FacilioModule module = modBean.getModule(type.getModuleName());

        List<FacilioField> fields = getFields(type, module);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(ns.getParentRuleId(), module));

        if(type.equals(NSType.FAULT_IMPACT_RULE)){
            selectRecordBuilder.innerJoin(ModuleFactory.getNewReadingRuleModule().getTableName()).on(ModuleFactory.getNewReadingRuleModule().getTableName()+".IMPACT_ID = "+module.getTableName()+".ID");
        }

        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            return props.get(0);
        }
        return null;
    }

    private static List<FacilioField> getFields(NSType type, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = new ArrayList<FacilioField>() {{
            add(getField("status", "STATUS", FieldType.BOOLEAN));
        }};
        switch (type) {
            case FAULT_IMPACT_RULE:
                fields.add(getField("categoryId", "ASSET_CATEGORY", FieldType.NUMBER));
                break;
            case VIRTUAL_METER:
                fields.add(getField("categoryId", "UTILITY_TYPE_ID", FieldType.NUMBER));
                break;
            default:
                fields.add(getField("categoryId", "CATEGORY_ID", FieldType.NUMBER));
                fields.add(getField("resourceType", "RESOURCE_TYPE", FieldType.NUMBER));
        }
        return fields;
    }
}
