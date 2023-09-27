package com.facilio.beans;

import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.datastructure.dag.DAGCache;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface GlobalScopeBean {
    public List<GlobalScopeVariableContext> getAllScopeVariable() throws Exception;
    public List<GlobalScopeVariableContext> getSwitchVariable() throws Exception;

    public List<ScopeVariableModulesFields> getScopeVariableModulesFields(Long id) throws Exception;

    public GlobalScopeVariableContext getScopeVariable(Long id) throws Exception;

    public GlobalScopeVariableContext getScopeVariable(String linkName) throws Exception;

    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception;

    public Long updateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception;

    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception;

    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception;

    public Map<String, Pair<GlobalScopeVariableContext,ValueGeneratorContext>> getAllScopeVariableAndValueGen(Long appId) throws Exception;

    public List<GlobalScopeVariableContext> getAllScopeVariable(Long appId, int page,int perPage, String searchQuery, boolean fetchDeleted) throws Exception;

    public void deleteScopeVariable(Long id,Long appId) throws Exception;

    public long getScopeVariableCount(Long appId, String searchQuery) throws Exception;

    public void setStatus(Long appId, Long scopeVariableId,boolean status) throws Exception;

    public void setSwitchStatus(Long appId,Long scopeVariableId,boolean status) throws Exception;

    public DAGCache getGlobalScopeGraph(Long appId) throws Exception;
}