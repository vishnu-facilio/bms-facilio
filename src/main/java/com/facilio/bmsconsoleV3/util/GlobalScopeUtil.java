package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.GlobalScopeBean;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsoleV3.context.GlobalScopeVariableEvaluationContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.datastructure.dag.DAG;
import com.facilio.datastructure.dag.Edge;
import com.facilio.datastructure.dag.Node;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.ValueGeneratorUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Log4j
public class GlobalScopeUtil {
    public static DAG constructAndGetGraph(long appId,int maxGraphSize) throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        List<GlobalScopeVariableContext> globalScopeVariableList = scopeBean.getAllScopeVariable(appId, -1, -1, null, false);
        return constructAndGetGraph(globalScopeVariableList,maxGraphSize,false);
    }
    public static DAG constructAndGetGraph(List<GlobalScopeVariableContext> globalScopeVariableList,int maxGraphSize,boolean checkInactive) throws Exception {
        Node defaultNode = new Node(null);
        DAG scopeGraph = new DAG(maxGraphSize); //Maximum of two levels chaining allowed in global scope
        if (CollectionUtils.isNotEmpty(globalScopeVariableList)) {
            List<Node> globalScopeNodes = new ArrayList<>();
            globalScopeNodes.add(defaultNode);
            addGlobalScopeGraphVertex(globalScopeVariableList, globalScopeNodes, scopeGraph, checkInactive);
            for (Node toNode : globalScopeNodes) {
                GlobalScopeVariableContext toNodeScope = (GlobalScopeVariableContext) toNode.retriveObject();
                if (toNodeScope != null) {
                    List<ScopeVariableModulesFields> modulesFields = toNodeScope.getScopeVariableModulesFieldsList();
                    if (CollectionUtils.isNotEmpty(modulesFields)) {
                        addScopeGraphEdges(modulesFields, globalScopeNodes, scopeGraph, defaultNode, toNode);
                    }
                }
            }
        }
        return scopeGraph;
    }

    public static void addScopeGraphEdges(List<ScopeVariableModulesFields> modulesFields, List<Node> globalScopeNodes, DAG scopeGraph, Node defaultNode, Node toNode) throws Exception {
        for (ScopeVariableModulesFields moduleField : modulesFields) {
            boolean addedEdge = false;
            for (Node fromNode : globalScopeNodes) {
                GlobalScopeVariableContext fromNodeScope = (GlobalScopeVariableContext) fromNode.retriveObject();
                if (fromNodeScope != null) {
                    FacilioModule mod = getScopeVariableModule(fromNodeScope);
                    if (mod != null && mod.getModuleId() == moduleField.getModuleId()) {
                        addedEdge = true;
                        scopeGraph.addEdge(fromNode, toNode, new Edge(moduleField));
                        break;
                    }
                }
            }
            if (!addedEdge) {
                scopeGraph.addEdge(defaultNode, toNode, new Edge(moduleField));
            }
        }
    }

    public static void addGlobalScopeGraphVertex(List<GlobalScopeVariableContext> globalScopeVariableList, List<Node> globalScopeNodes, DAG scopeGraph, boolean checkInactive) {
        for (GlobalScopeVariableContext scopeVariable : globalScopeVariableList) {
            if (scopeVariable.isActive() || checkInactive) {
                Node node = new Node(scopeVariable);
                globalScopeNodes.add(node);
                scopeGraph.addVertex(node);
            }
        }
    }

    public static Pair<List<FacilioField>, Criteria> getScopeCriteriaFromGraph(FacilioModule module, DAG graph) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria finalAllEdgeCriteria = new Criteria();
        List<FacilioField> allFields = new ArrayList<>();
        if (module != null) {
            List<Edge> edges = graph.getEdges();
            for (Edge edge : edges) {
                ScopeVariableModulesFields moduleField = (ScopeVariableModulesFields) edge.retriveObject();
                FacilioModule currentModule = modBean.getModule(moduleField.getModuleId());
                if (currentModule.getModuleId() == module.getModuleId()) {
                    FacilioField field = modBean.getField(moduleField.getFieldName(), currentModule.getName());
                    Pair<Criteria,Criteria> criterias = getScopeCriteriaForNode(edge, field, new Criteria(), new ArrayList<>(),new Criteria());
                    if (!field.getDataTypeEnum().isRelRecordField()) {
                        allFields.add(field);
                    }
                    finalAllEdgeCriteria.andCriteria(criterias.getRight());
                }
            }
        }
        Criteria selfModuleCriteria = getSelfModuleNodeCriteria(module, graph);
        if (selfModuleCriteria != null && !selfModuleCriteria.isEmpty()) {
            finalAllEdgeCriteria.andCriteria(selfModuleCriteria);
        }
        return Pair.of(allFields, finalAllEdgeCriteria);
    }

    private static Criteria getSelfModuleNodeCriteria(FacilioModule currentModule, DAG graph) throws Exception {
        Criteria criteria = null;
        List<Node> allVertices = graph.getVertices();
        if (CollectionUtils.isNotEmpty(allVertices)) {
            for (Node node : allVertices) {
                GlobalScopeVariableContext scopeVariable = (GlobalScopeVariableContext) node.retriveObject();
                if ((scopeVariable.getApplicableModuleId() != null && scopeVariable.getApplicableModuleId().equals(currentModule.getModuleId())) || (scopeVariable.getApplicableModuleName() != null && scopeVariable.getApplicableModuleName().equals(currentModule.getName()))) {
                    if (criteria == null) {
                        criteria = new Criteria();
                    }
                    criteria.andCriteria(getValueGeneratorCriteria(scopeVariable, null));
                    break;
                }
            }
        }
        return criteria;
    }

    public static Pair<Criteria,Criteria> getScopeCriteriaForNode(Edge currentEdge, FacilioField scopeField, Criteria nodeCriteria, List<FacilioField> fields,Criteria intermediateCriteria) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GlobalScopeVariableContext scopeVariable = (GlobalScopeVariableContext) currentEdge.getEndNode().retriveObject();
        List<Edge> forwardingEdges = currentEdge.getEndNode().getForwardingEdges();
        if (!scopeField.getDataTypeEnum().isRelRecordField()) {
            fields.add(scopeField);
        }
        if (CollectionUtils.isNotEmpty(forwardingEdges)) {
            Criteria c = new Criteria();
            for (Edge edge : forwardingEdges) {
                ScopeVariableModulesFields moduleField = (ScopeVariableModulesFields) edge.retriveObject();
                FacilioField field = modBean.getField(moduleField.getFieldName(), modBean.getModule(moduleField.getModuleId()).getName());
                intermediateCriteria = new Criteria();
                Pair<Criteria,Criteria> criteriaPair = getScopeCriteriaForNode(edge, field, nodeCriteria, fields, intermediateCriteria);
                c.andCriteria(criteriaPair.getRight());
            }
            intermediateCriteria.andCriteria(c);
            nodeCriteria = intermediateCriteria;
        }
        Criteria subQueryCriteria = new Criteria();
        Criteria valGenCriteria = getValueGeneratorCriteria(scopeVariable, scopeField);
        if (valGenCriteria != null) {
            subQueryCriteria.andCriteria(valGenCriteria);
        }
        if (nodeCriteria != null && !nodeCriteria.isEmpty()) {
            subQueryCriteria.andCriteria(nodeCriteria);
        }
        Condition condition = getSubQueryCondition(scopeField, subQueryCriteria);
        Criteria finalCriteria = new Criteria();
        if (condition != null) {
            finalCriteria.addAndCondition(condition);
        }
        return Pair.of(intermediateCriteria,finalCriteria);
    }

    private static Condition getSubQueryCondition(FacilioField scopeField, Criteria subQueryCriteria) {
        Condition condition = null;
        if (subQueryCriteria != null && !subQueryCriteria.isEmpty()) {
            if (scopeField instanceof MultiLookupField && isUserOrRequesterField(scopeField)) {
                condition = CriteriaAPI.getCondition(scopeField, subQueryCriteria, MultiLookupOperator.MULTI_LOOKUP_USER);
            } else if (scopeField.getName().equals("siteId")) {
                condition = CriteriaAPI.getCondition(scopeField, subQueryCriteria, SiteOperator.SITE);
            } else if (scopeField instanceof MultiLookupField) {
                condition = CriteriaAPI.getCondition(scopeField, subQueryCriteria, MultiLookupOperator.MULTI_LOOKUP);
            } else if (scopeField instanceof LookupField && isUserOrRequesterField(scopeField)) {
                condition = CriteriaAPI.getCondition(scopeField, subQueryCriteria, UserOperator.USER);
            } else if (scopeField instanceof LookupField) {
                condition = CriteriaAPI.getCondition(scopeField, subQueryCriteria, LookupOperator.LOOKUP);
            }
        }
        return condition;
    }

    private static boolean isUserOrRequesterField(FacilioField scopeField) {
        List<String> mods = Arrays.asList("users","requester");
        if(scopeField instanceof BaseLookupField) {
            BaseLookupField lookupField = (BaseLookupField) scopeField;
            if(lookupField != null && lookupField.getLookupModule() != null) {
                FacilioModule lookupModule = lookupField.getLookupModule();
                if(lookupModule != null && lookupModule.getName() != null && mods.contains(lookupModule.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static FacilioModule getScopeVariableModule(GlobalScopeVariableContext scopeVariable) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule mod = null;
        if (scopeVariable.getApplicableModuleName() != null) {
            mod = modBean.getModule(scopeVariable.getApplicableModuleName()); //special module - users
        } else if (scopeVariable.getApplicableModuleId() != null) {
            mod = modBean.getModule(scopeVariable.getApplicableModuleId());
        }
        return mod;
    }

    private static Criteria getValueGeneratorCriteria(GlobalScopeVariableContext scopeVariable, FacilioField scopeField) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria crit = null;
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        List<Long> switchValues = new ArrayList<>();
        JSONObject switchMap = getFilterRecordIdMapFromHeader();
        if (switchMap != null) {
            List<Long> ids = getFilterRecordIdFromHeader(switchMap, scopeVariable.getLinkName());
            if (CollectionUtils.isNotEmpty(ids)) {
                switchValues = ids;
            }
        }
        if (scopeVariable != null && scopeVariable.getTypeEnum() != null) {
            if (scopeVariable.getTypeEnum().getIndex() == GlobalScopeVariableContext.Type.SCOPED.getIndex()) {
                ValueGeneratorContext valGen = valGenBean.getValueGenerator(scopeVariable.getValueGeneratorId());
                if (valGen != null) {
                    ValueGenerator valueGenerator = ValueGeneratorUtil.getValueGeneratorObjectForLinkName(valGen.getLinkName());
                    if (valGen.getValueGeneratorType() != null && valGen.getValueGeneratorType() == ValueGeneratorContext.ValueGeneratorType.SUB_QUERY) {
                        if (valueGenerator != null) {
                            if (crit == null) {
                                crit = new Criteria();
                            }
                            Criteria criteria = valueGenerator.getCriteria(scopeField, null);
                            crit.andCriteria(criteria);
                        }
                    } else {
                        if (crit == null) {
                            crit = new Criteria();
                        }
                        List<Long> ids = getEvaluatedValuesForGlobalScopeVariable(scopeVariable.getLinkName());
                        if(ids != null) {
                            String val = StringUtils.join(ids, ",");
                            if(scopeVariable.getApplicableModuleName() != null && scopeVariable.getApplicableModuleName().equals("users")) {
                                crit.addAndCondition(CriteriaAPI.getCondition("ORG_USERID","orgUserId", val, NumberOperators.EQUALS));
                            } else {
                                crit.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(scopeVariable.getApplicableModuleId())), val, NumberOperators.EQUALS));
                            }
                        }
                    }
                }
            }
            if (scopeVariable.getApplicableModuleId() != null && CollectionUtils.isNotEmpty(switchValues)) {
                if (crit == null) {
                    crit = new Criteria();
                }
                crit.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(scopeVariable.getApplicableModuleId())), StringUtils.join(switchValues, ","), NumberOperators.EQUALS));
            }
        }
        return crit;
    }

    public static JSONObject getFilterRecordIdMapFromHeader() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String switchVariable = request.getHeader("X-Switch-Value");
        if (StringUtils.isNotEmpty(switchVariable)) {
            byte[] decodedBytes = Base64.getDecoder().decode(switchVariable);
            if (decodedBytes != null) {
                String decodedString = new String(decodedBytes);
                if (decodedString != null) {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(decodedString);
                    if (json != null) {
                        return json;
                    }
                }
            }
        }
        return null;
    }

    public static List<Long> getFilterRecordIdFromHeader(JSONObject switchMap, String linkName) throws Exception {
        if (switchMap != null && !switchMap.isEmpty() && switchMap.containsKey(linkName)) {
            JSONArray arrayList = (JSONArray) switchMap.get(linkName);
            if (arrayList != null && !arrayList.isEmpty()) {
                List<Long> list = new ArrayList<>();
                for (Object id : arrayList) {
                    Long recordId = Long.parseLong(String.valueOf(id));
                    list.add(recordId);
                }
                return list;
            }
        }
        return null;
    }


    private static List<Long> getEvaluatedValuesForGlobalScopeVariable(String linkName) {
        Map<String, GlobalScopeVariableEvaluationContext> variableEvaluationMap = AccountUtil.getGlobalScopeVariableValues();
        if(MapUtils.isNotEmpty(variableEvaluationMap)){
            GlobalScopeVariableEvaluationContext scopeVariableEvaluationContext = variableEvaluationMap.get(linkName);
            if(scopeVariableEvaluationContext != null) {
                List<Long> values = scopeVariableEvaluationContext.getValues();
                if(values != null) {
                    return values;
                }
            }
        }
        return null;
    }
}