package com.facilio.agentv2.metrics;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsApi {
    private static final Logger LOGGER = LogManager.getLogger(MetricsApi.class.getName());

    private static final FacilioModule MODULE = ModuleFactory.getAgentMetricsV2Module();
    private static final List<FacilioField> FIELDS = FieldFactory.getAgentMetricV2Fields();
    public static final String MODULE_NAME = FacilioConstants.ContextNames.AGENT_METRICS_MODULE;

    public static boolean logMetrics(FacilioAgent agent, JSONObject payload) throws Exception {
        LOGGER.info(" marking metrics ");
        Map<String, Object> metrics = getMetrics(agent, payload);
        if (metrics.isEmpty()) {
            addMetrics(agent, payload);
        } else {
            updateMetrics(agent, payload, metrics);
        }
        LOGGER.info(" done marking metrics ");
        return false;
    }


    private static List<Map<String, Object>> getMetrics(long agentId) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FIELDS);
        if (fieldMap.containsKey(AgentConstants.AGENT_ID)) {
            fieldMap.remove(AgentConstants.AGENT_ID);
        }
        if (fieldMap.containsKey(AgentConstants.ID)) {

            fieldMap.remove(AgentConstants.ID);
        }
        if (fieldMap.containsKey(AgentConstants.SITE_ID)) {

            fieldMap.remove(AgentConstants.SITE_ID);
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(fieldMap.values())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS))
                .orderBy(AgentConstants.CREATED_TIME + " DESC");
        return selectRecordBuilder.get();
    }

    public static List<AgentMetrics> getMetrics() throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FIELDS);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(MODULE_NAME);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(allFields)
                .orderBy(AgentConstants.CREATED_TIME + " DESC");
        return FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), AgentMetrics.class);
    }

    private static Map<String, Object> getMetrics(FacilioAgent agent, JSONObject payload) throws Exception {
        LOGGER.info(" getting metrics for agent->" + agent.getId());
        if (agent != null) {
            if ((agent.getId() > 0)) {
                if (payload != null) {
                    if (containsValueCheck(AgentConstants.PUBLISH_TYPE, payload)) {

                        int payloadInt = getPublishType(payload).asInt();
                        long createdTime = getCreatedTime(agent);
                        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                                .table(MODULE.getTableName())
                                .select(FieldFactory.getAgentMetricV2Fields())
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(MODULE), String.valueOf(agent.getId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentPublishTypeField(MODULE), String.valueOf(payloadInt), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(MODULE), String.valueOf(createdTime), NumberOperators.EQUALS));
                        List<Map<String, Object>> result = selectRecordBuilder.get();
                        LOGGER.info(" query to get metrics " + selectRecordBuilder.toString());
                        if (!result.isEmpty()) {
                            if (result.size() == 1) {
                                return result.get(0);
                            } else {
                                throw new Exception(" unexcepted result ");
                            }
                        } else {
                            return new HashMap<>();
                        }
                    } else {
                        throw new Exception(" payload is missing key publishtype ");
                    }
                } else {
                    throw new Exception(" payload can't be null");
                }
            } else {
                throw new Exception(" agentId cant be less than 1");
            }
        } else {
            throw new Exception(" agent cant be null");
        }
    }

    private static long getCreatedTime(FacilioAgent agent) {
        long dataInterval = agent.getInterval() ;
        if (dataInterval < 5) {
            dataInterval = 15;
        }
        long timeStamp = System.currentTimeMillis();
       /* if (containsValueCheck(AgentConstants.TIMESTAMP, payload)) {
            timeStamp = (long) payload.get(AgentConstants.TIMESTAMP);
        }*/
        long baseTime = getBaseTime(timeStamp, (dataInterval* 60000));
        LOGGER.info(timeStamp + " - - " + baseTime);
        return baseTime;
    }

    private static boolean updateMetrics(FacilioAgent agent, JSONObject payload, Map<String, Object> metrics) throws Exception {
        LOGGER.info(" updating agent metrics " + agent.getId() + " for metrics " + metrics);
        if (agent != null) {
            if ((agent.getId() > 0)) {
                if (payload != null) {
                    if (containsValueCheck(AgentConstants.PUBLISH_TYPE, payload)) {

                        Map<String, Object> toUpdate = new HashMap<>();
                        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentAccount().getOrg().getOrgId());
                        long numberOfMsgs = (long) metrics.get(AgentConstants.NUMBER_OF_MSGS);
                        toUpdate.put(AgentConstants.NUMBER_OF_MSGS, numberOfMsgs + 1);
                        long size = (long) metrics.get(AgentConstants.SIZE);
                        toUpdate.put(AgentConstants.SIZE, size + payload.toString().length());
                        toUpdate.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
                        return bean.updateMetrics(toUpdate, (Long) metrics.get(AgentConstants.ID));

                    } else {
                        throw new Exception(" payload is missing key publishtype ");
                    }
                } else {
                    throw new Exception(" payload can't be null");
                }
            } else {
                throw new Exception(" agentId cant be less than 1");
            }
        } else {
            throw new Exception(" agent cant be null");
        }
    }

    private static boolean addMetrics(FacilioAgent agent, JSONObject payload) throws Exception {
        LOGGER.info(" adding metrics ");
        if (agent != null) {
            if ((agent.getId() > 0)) {
                if (payload != null) {
                    if (containsValueCheck(AgentConstants.PUBLISH_TYPE, payload)) {

                        Map<String, Object> toInsertMap = new HashMap<>();
                        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentAccount().getOrg().getOrgId());
                        toInsertMap.put(AgentConstants.AGENT_ID, agent.getId());
                        toInsertMap.put(AgentConstants.PUBLISH_TYPE, getPublishType(payload).asInt());
                        toInsertMap.put(AgentConstants.NUMBER_OF_MSGS, 1);
                        toInsertMap.put(AgentConstants.CREATED_TIME, getCreatedTime(agent));
                        toInsertMap.put(AgentConstants.SIZE, payload.toString().length());
                        toInsertMap.put(AgentConstants.SITE_ID, agent.getSiteId());
                        toInsertMap.put(AgentConstants.LAST_MODIFIED_TIME, toInsertMap.get(AgentConstants.CREATED_TIME));
                        return bean.addMetrics(toInsertMap);
                    } else {
                        throw new Exception(" payload is missing key publishtype ");
                    }
                } else {
                    throw new Exception(" payload can't be null");
                }
            } else {
                throw new Exception(" agentId cant be less than 1");
            }
        } else {
            throw new Exception(" agent cant be null");
        }
    }

    private static PublishType getPublishType(JSONObject payload) {
        return PublishType.valueOf(((Number) payload.get(AgentConstants.PUBLISH_TYPE)).intValue());
    }


    private static List<Map<String, Object>> getMetrics(long createdTime, long agentId, int Pub) throws Exception {
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FieldFactory.getAgentMetricV2Fields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(MODULE), String.valueOf(createdTime), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        List<Map<String, Object>> result = genericSelectRecordBuilder.get();
        if (!result.isEmpty()) {
            return result;
        }
        return new ArrayList();
    }


    private static long getBaseTime(long timeStamp, long dataInterval) {
        long rem = timeStamp % dataInterval;
        if (rem != 0) {
            return timeStamp - rem;
        }
        LOGGER.info(" created time is " + timeStamp);
        return timeStamp;
    }


    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean checkValue(Long value) {
        return (value != null) && (value > 0);
    }

    public static boolean containsValueCheck(String key, Map<String, Object> map) {
        if (notNull(key) && notNull(map) && map.containsKey(key) && (map.get(key) != null)) {
            return true;
        }
        return false;
    }

    public static boolean insertMetrics(Map<String, Object> toInsertMap) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FIELDS);
        return insertRecordBuilder.insert(toInsertMap) > 0;
    }

    public static boolean updateMetrics(Map<String, Object> toUpdate, long metricsId) throws SQLException {
        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FieldFactory.getAgentMetricV2Fields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(MODULE), String.valueOf(metricsId), NumberOperators.EQUALS));
        return genericUpdateRecordBuilder.update(toUpdate) > 0;
    }

    public static JSONObject getMetricsGraphData() {
        JSONObject obj = new JSONObject();
        /*obj.put("chartType", "line");

        JSONObject xField = new JSONObject();
        xField.put("aggr", BmsAggregateOperators.DateAggregateOperator.HOURSOFDAY);
        xField.put("fieldName", AgentConstants.CREATED_TIME);
        obj.put("xField", xField);

        JSONObject yField = new JSONObject();
        yField.put("aggr", BmsAggregateOperators.NumberAggregateOperator.);
        yField.put("fieldName", yFieldName);
        obj.put("yField", yField);

        JSONObject groupBy = new JSONObject();
        groupBy.put("fieldName", groupByFieldName);
        obj.put("groupBy", groupBy);

        obj.put("dateOperator", dateOperator.getOperatorId());
        obj.put("dateOperatorValue", dateOperatorValue);
        obj.put("criteria", criteria);
        return null;*/
        return obj;
    }
}
