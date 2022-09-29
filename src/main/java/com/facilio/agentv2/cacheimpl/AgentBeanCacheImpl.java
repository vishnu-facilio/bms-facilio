package com.facilio.agentv2.cacheimpl;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 *  Cache Logic:
 *
 *  Read -> First get objects from cache if it's not exists in cache then get it from DB and set it in the cache and return the same.
 *
 *  Update, Delete -> Do call the necessary DB methods and then clear it from cache
 *
 */

public class AgentBeanCacheImpl extends AgentBeanImpl implements AgentBean{

    private static final Logger LOGGER = LogManager.getLogger(AgentBeanCacheImpl.class.getName());

    public FacilioAgent getAgent(String agentName) throws Exception {
        FacilioCache<String, Object> agentCache = LRUCache.getAgentCache();
        String key = CacheUtil.AGENT_KEY(AccountUtil.getCurrentOrg().getOrgId(), agentName);
        FacilioAgent agent = (FacilioAgent) agentCache.get(key);

        if (agent == null) {
            // Cache Miss
            agent = super.getAgent(agentName);
            agentCache.put(key, agent);

            LOGGER.info("getAgent result from DB for agent: "+ agentName);
        }
        // Cache Hit - getAgent results From Cache
        return agent;
    }


    public boolean deleteAgent(List<Long> ids) throws Exception {
        boolean isDeleted= false;
        for (Long id : ids) {
            String agentName = super.getAgents(Collections.singleton(id)).get(0).getName();
            isDeleted = super.deleteAgent(Collections.singletonList(id));
            if (isDeleted) {
                LOGGER.info("Agent Deleted: "+ agentName);
                dropAgentFromCache(agentName);
            }
        }
        return isDeleted;
    }

    public boolean editAgent(FacilioAgent agent, JSONObject jsonObject, boolean updateLastDataReceivedTime) throws Exception {
        boolean isEdited = super.editAgent(agent, jsonObject, updateLastDataReceivedTime);
        LOGGER.info("Edited Agent: "+ agent.getName());
        // Droping Cache only if LastDataReceivedTime is not updating && So LastDataReceivedTime Updating only in db not in cache
        if (isEdited && !updateLastDataReceivedTime) {
            dropAgentFromCache(agent.getName());
        }
        return isEdited;
    }

    public void dropAgentFromCache(String agentName) {
        FacilioCache<String, Object> cache = LRUCache.getAgentCache();
        if (cache.get(CacheUtil.AGENT_KEY(AccountUtil.getCurrentOrg().getOrgId(), agentName)) != null) {
            cache.remove(CacheUtil.AGENT_KEY(AccountUtil.getCurrentOrg().getOrgId(), agentName));
            LOGGER.info("Invalidated Agent Cache for " + agentName);
        }
    }

}