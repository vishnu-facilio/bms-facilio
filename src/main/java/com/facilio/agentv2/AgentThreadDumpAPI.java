package com.facilio.agentv2;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class AgentThreadDumpAPI {

    private static final Logger LOGGER = LogManager.getLogger(AgentThreadDumpAPI.class.getName());

    public static void processThreadDump(JSONObject payload, FacilioAgent agent) throws Exception {
        Objects.requireNonNull(payload, "payload null");
        Objects.requireNonNull(agent, "agent null");
        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray threadDump = (JSONArray) payload.get(AgentConstants.DATA);
            long fileId = addDumpToFileStore(agent, threadDump);
            makeEntry(fileId, agent);
        }
    }

    public static long addDumpToFileStore(FacilioAgent agent, JSONArray threadDump) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(currentOrg, "current org null");
        FileStore fileStore = FacilioFactory.getFileStoreFromOrg(currentOrg.getOrgId());
        long currentTimeMillis = System.currentTimeMillis();
        String fileName = AgentConstants.THREAD_DUMP + "_" + currentOrg.getDomain() + "_" + agent.getId() + "_" + currentTimeMillis;
        return fileStore.addFile(fileName, threadDump.toJSONString(), "text/plain");
    }

    public static void makeEntry(long fileId, FacilioAgent agent) throws Exception {
        FacilioModule module = ModuleFactory.getAgentThreadDumpModule();
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(AgentConstants.FILE_ID, fileId);
        toInsert.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        toInsert.put(AgentConstants.AGENT_ID, agent.getId());
        List<FacilioField> agentThreadDumpFields = FieldFactory.getAgentThreadDumpFields();
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(agentThreadDumpFields);
        insertRecordBuilder.insert(toInsert);
    }

    public static List<Map<String, Object>> getDumps(Long agentId) throws Exception {
        return getDumps(agentId, null);
    }

    public static List<Map<String, Object>> getDumps(Long agentId, FacilioContext context) throws Exception {
        FacilioModule module = ModuleFactory.getAgentThreadDumpModule();
        List<FacilioField> agentThreadDumpFields = FieldFactory.getAgentThreadDumpFields();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(agentThreadDumpFields);
        if (agentId != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        genericSelectRecordBuilder.limit(50);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            genericSelectRecordBuilder.limit(perPage).offset(offset);
        }
        LOGGER.info(" log list query " + genericSelectRecordBuilder.get());
        return genericSelectRecordBuilder.get();
    }

    public static Long count(Long agentId) throws Exception {
        FacilioModule module = ModuleFactory.getAgentThreadDumpModule();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField());
        if (agentId != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> result = genericSelectRecordBuilder.get();
        LOGGER.info(" log count query " + genericSelectRecordBuilder.get());
        return (long) result.get(0).get(AgentConstants.ID);
    }

    private static boolean containsValueCheck(String key, JSONObject jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }
}
