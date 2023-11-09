package com.facilio.agentv2.cacheimpl;


import com.facilio.agentv2.FacilioAgent;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import org.json.simple.JSONObject;
import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface AgentBean {
    public List<FacilioAgent> getAgents(Collection<Long> ids) throws Exception;

    public List<FacilioAgent> getAgents(Collection<Long> ids, boolean fetchOnlyName) throws Exception;
    
    public List<FacilioAgent> getAgents(Criteria criteria) throws Exception;

    public Map<Long, FacilioAgent> getAgentMap(Collection<Long> ids) throws Exception;

    public Map<Long, FacilioAgent> getAgentMap(Collection<Long> ids, boolean fetchOnlyName) throws Exception;

    public FacilioAgent getAgent(Long agentId) throws Exception;

    public long addAgent(FacilioAgent agent) throws Exception;

    public boolean editAgent(FacilioAgent agent, JSONObject jsonObject, boolean updateLastDataReceivedTime) throws Exception;

    public boolean updateAgent(FacilioAgent agent) throws Exception;

    public void updateAgentLastDataReceivedTime(FacilioAgent agent);

    public JSONObject getAgentCountDetails();

    public Condition getDeletedTimeNullCondition(FacilioModule module);

    public boolean deleteAgent(List<Long> ids) throws Exception;

    public List<Map<String, Object>> getAgentListData(boolean fetchDeleted, String querySearch,JSONObject pagination,List<Long> defaultIds,Criteria filterCriteria) throws Exception;

    public long getAgentCount(String querySearch,Criteria filterCriteria);

    public List<Map<String,Object>> getAgentFilter() throws Exception;

    public FacilioAgent getAgent(String agentName) throws Exception;

    public void scheduleRestJob(FacilioAgent agent) throws Exception;

    public void scheduleJob(FacilioAgent agent, String jobName) throws Exception;
}
