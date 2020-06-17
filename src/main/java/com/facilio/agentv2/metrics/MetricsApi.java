package com.facilio.agentv2.metrics;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsApi {
    private static final Logger LOGGER = LogManager.getLogger(MetricsApi.class.getName());
    public static final String MODULE_NAME = FacilioConstants.ContextNames.AGENT_METRICS_MODULE;

    public static boolean logMetrics(FacilioAgent agent, JSONObject payload) throws Exception {
        Map<String, Object> metrics = getMetrics(agent, payload);
        if (metrics.isEmpty()) {
            addMetrics(agent, payload);
        } else {
            updateMetrics(agent, payload, metrics);
        }
        return false;
    }

    public static List<Map<String, Object>> getMetrics(long agentId,FacilioContext context) throws Exception {
        List<FacilioField> fields = FieldFactory.getAgentMetricV2Fields();
        FacilioModule metricsV2Module = ModuleFactory.getAgentMetricsV2Module();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
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
                .table(metricsV2Module.getTableName())
                .select(fieldMap.values())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsV2Module), String.valueOf(agentId), NumberOperators.EQUALS))
                .orderBy("CREATED_TIME DESC");
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        }else {
            selectRecordBuilder.limit(50);
        }
        
        if (fetchCount) {
        	selectRecordBuilder.select(new ArrayList<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(metricsV2Module));
        }
        List<Map<String, Object>> data = selectRecordBuilder.get();
        return data;
    }

    public static List<AgentMetrics> getMetrics() throws Exception {
        List<FacilioField> fields = FieldFactory.getAgentMetricV2Fields();
        FacilioModule metricsV2Module = ModuleFactory.getAgentMetricsV2Module();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(MODULE_NAME);
        List<FacilioField> allFields = modBean.getModuleFields(module.getName());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(allFields)
                .orderBy(AgentConstants.CREATED_TIME + " DESC");
        return FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), AgentMetrics.class);
    }

    private static Map<String, Object> getMetrics(FacilioAgent agent, JSONObject payload) throws Exception {
        List<FacilioField> fields = FieldFactory.getAgentMetricV2Fields();
        FacilioModule metricsV2Module = ModuleFactory.getAgentMetricsV2Module();
        if (agent != null) {
            if ((agent.getId() > 0)) {
                if (payload != null) {
                    if (containsValueCheck(AgentConstants.PUBLISH_TYPE, payload)) {

                        int payloadInt = getPublishType(payload).asInt();
                        long createdTime = getCreatedTime(agent);
                        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                                .table(metricsV2Module.getTableName())
                                .select(FieldFactory.getAgentMetricV2Fields())
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsV2Module), String.valueOf(agent.getId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentPublishTypeField(metricsV2Module), String.valueOf(payloadInt), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(metricsV2Module), String.valueOf(createdTime), NumberOperators.EQUALS));
                        List<Map<String, Object>> result = selectRecordBuilder.get();
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
        return baseTime;
    }

    private static boolean updateMetrics(FacilioAgent agent, JSONObject payload, Map<String, Object> metrics) throws Exception {
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
                        toUpdate.put(AgentConstants.LAST_UPDATED_TIME, System.currentTimeMillis());
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
        if (agent != null) {
            if ((agent.getId() > 0)) {
                if (payload != null) {
                    if (containsValueCheck(AgentConstants.PUBLISH_TYPE, payload)) {

                        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentAccount().getOrg().getOrgId());
                        AgentMetrics metrics = new AgentMetrics();
                        metrics.setAgentId(agent.getId());
                        metrics.setPublishType(getPublishType(payload).asInt());
                        metrics.setNumberOfMessages(1);
                        metrics.setCreatedTime(getCreatedTime(agent));
                        metrics.setSize(payload.toString().length());
                        metrics.setSiteId(agent.getSiteId());
                        metrics.setLastUpdatedTime(System.currentTimeMillis());
                        return bean.addMetrics(metrics);
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
        List<FacilioField> fields = FieldFactory.getAgentMetricV2Fields();
        FacilioModule metricsV2Module = ModuleFactory.getAgentMetricsV2Module();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(metricsV2Module.getTableName())
                .select(FieldFactory.getAgentMetricV2Fields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(metricsV2Module), String.valueOf(createdTime), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsV2Module), String.valueOf(agentId), NumberOperators.EQUALS));
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

    public static boolean insertMetrics(AgentMetrics metrics) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(MODULE_NAME);
        List<FacilioField> allFields = modBean.getModuleFields(module.getName());
        InsertRecordBuilder<AgentMetrics> insertRecordBuilder = new InsertRecordBuilder<AgentMetrics>()
                .fields(allFields)
                .module(module);
        return insertRecordBuilder.insert(metrics) > 0;
    }

    public static boolean updateMetrics(Map<String, Object> toUpdate, long metricsId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(MODULE_NAME);
        List<FacilioField> allFields = modBean.getModuleFields(module.getName());
        UpdateRecordBuilder<AgentMetrics> updateRecordBuilder = new UpdateRecordBuilder<AgentMetrics>()
                .fields(allFields)
                .module(module)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(metricsId), NumberOperators.EQUALS));
        return updateRecordBuilder.updateViaMap(toUpdate) > 0;
    }

    public static JSONObject getMetricsGraphData(Long agentId) {
        List<FacilioField> fields = FieldFactory.getAgentMetricV2Fields();
        FacilioModule metricsV2Module = ModuleFactory.getAgentMetricsV2Module();
        JSONObject obj = new JSONObject();
        obj.put("chartType", "line");

        JSONObject xField = new JSONObject();
        xField.put("aggr", 0);
        xField.put("fieldName", AgentConstants.CREATED_TIME);
        obj.put("xField", xField);

        org.json.simple.JSONArray yFields = new JSONArray();
        JSONObject yField = new JSONObject();
        yField.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
        yField.put("fieldName", AgentConstants.NUMBER_OF_MSGS);
        yFields.add(yField);
        JSONObject yField1 = new JSONObject();
        yField1.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
        yField1.put("fieldName", AgentConstants.SIZE);
        yFields.add(yField1);

        obj.put("yField", yFields);
        obj.put("isMultipleMetric",true);

        JSONObject groupBy = new JSONObject();
        groupBy.put("fieldName", null);
        obj.put("groupBy", groupBy);

        obj.put("dateOperator", DateOperators.TODAY.getOperatorId());
        obj.put("dateOperatorValue", null);
        if(agentId != null){
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsV2Module), String.valueOf(agentId), NumberOperators.EQUALS));
            obj.put("criteria", criteria);
        }
        else {
            obj.put("criteria", null);
        }
        return obj;
    }


    }

