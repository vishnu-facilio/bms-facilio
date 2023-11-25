package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.modbusrtu.ModbusImportUtils;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentIdAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentIdAction.class.getName());

    private Long agentId;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String querySearch;

    public String getQuerySearch() {
        return querySearch;
    }

    public void setQuerySearch(String querySearch) {
        this.querySearch = querySearch;
    }

    public Integer controllerType;

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    private Boolean configured;


    public Boolean getConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    private Boolean type;

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    private Boolean count;

    public Boolean getCount() {
        return count;
    }

    public void setCount(Boolean count) {
        this.count = count;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private long supportEmailId = -1;

    public long getSupportEmailId() {
        return supportEmailId;
    }

    public void setSupportEmailId(long supportEmailId) {
        this.supportEmailId = supportEmailId;
    }

    @Override
    public String getFilters() {
        return filters;
    }

    @Override
    public void setFilters(String filters) {
        this.filters = filters;
    }

    private String filters;

    private String moduleName;

    public String getDeviceOrControllersData() {

        try {
            List<Map<String, Object>> data = new ArrayList<>();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.AGENT_ID, getAgentId());
            context.put(AgentConstants.SEARCH_KEY, getQuerySearch());
            context.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
            context.put(AgentConstants.TYPE, type);
            context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

            if ((count != null && count == Boolean.TRUE)) {
                long count = -1;
                context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
                if ((configured != null && configured == Boolean.TRUE)) {
                    count = AgentConstants.getControllerBean().getControllersCount(context);
                } else if (configured != null && configured == Boolean.FALSE) {
                    //data = FieldDeviceApi.getDevices(context);
                    if (CollectionUtils.isNotEmpty(data)) {
                        //count = (long) data.get(0).get(AgentConstants.ID);
                    }
                }
                setResult(AgentConstants.DATA, count);
            } else {
                if ((configured != null && configured == Boolean.TRUE)) {
                    data = AgentConstants.getControllerBean().getControllerDataForAgent(context);
                }
                setResult(AgentConstants.DATA, data);
            }

            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting Controllers ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }

        return SUCCESS;
    }

    public String getControllersData() {
        try {
            FacilioChain chain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.MODULE_NAME, moduleName);
            if (filters != null) {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(getFilters());
                context.put(FacilioConstants.ContextNames.FILTERS, json);
                ;
            }
            chain.execute();
            context.put(AgentConstants.AGENT_ID, getAgentId());
            context.put(AgentConstants.SEARCH_KEY, getQuerySearch());
            context.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
            context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

            if (count != null && count) {
                context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
                setResult(AgentConstants.DATA, AgentConstants.getControllerBean().getControllersCount(context));
            } else {
                setResult(AgentConstants.DATA, AgentConstants.getControllerBean().getControllerDataForAgent(context));
            }
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting Controllers ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getControllerCount() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.AGENT_ID, getAgentId());
            context.put(AgentConstants.SEARCH_KEY, getName());
            context.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
            setResult(AgentConstants.RESULT, SUCCESS);
            setResult(AgentConstants.DATA, AgentConstants.getControllerBean().getControllersCount(context));
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller count for agentId->" + getAgentId());
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getControllers() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.SEARCH_KEY, getName());
            context.put(AgentConstants.AGENT_ID, getAgentId());
            context.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
            context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
            List<Map<String, Object>> controllers = AgentConstants.getControllerBean().getControllerDataForAgent(context);
            setResult(AgentConstants.DATA, controllers);
            ok();
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("exception while fetching controller data fro agent " + agentId + " ", e);
        }
        return SUCCESS;
    }

    public String pingAgent() throws Exception {
        try {
            LOGGER.info(" ping agent " + getAgentId());
            AgentMessenger.pingAgent(getAgentId());
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while pingAgent", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            return SUCCESS;
        }
    }

    public String getAgentUsingId() {
        try {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(getAgentId());
            if (agent != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.DATA, agent.toJSON());
                ok();
            } else {
                setResult(AgentConstants.RESULT, ERROR);
                setResult(AgentConstants.EXCEPTION, "no such agent");
                noContent();
                return SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent ->" + agentId + "  ,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            return ERROR;
        }
        return SUCCESS;

    }

    public String getjvmStatus() {
        try {
            setResult(AgentConstants.DATA, AgentMessenger.getJVMStatus(getAgentId()));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getJVM command ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getThreadDump() {
        try {
            setResult(AgentConstants.DATA, AgentMessenger.getThreadDump(agentId));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getThreadDump command ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getSqlite() {
        try {
            long fileId = SqliteBridge.migrateAgentData(getAgentId());
            if (fileId > 0) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.FILE_ID, fileId);
                ok();
                return SUCCESS;
            } else {
                internalError();
                setResult(AgentConstants.RESULT, ERROR);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while migrating data", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String migrate() {
        try {
            SqliteBridge.migrateToNewAgent(getAgentId());
            ok();
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception occurred while migrating to new Agent " + getAgentId() + "  ", e);
        }
        return SUCCESS;
    }

    public String getAgentLogs() {
        try {
            FacilioContext context = constructListContext(new FacilioContext());

            List<Map<String, Object>> data = LogsApi.getLogs(agentId, context);
            setResult(AgentConstants.DATA, data);
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            LOGGER.info("DATA : " + data);
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception while getting agent logs for ->" + agentId + "  ", e);
        }
        return SUCCESS;
    }

    public String getLogCount() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
            List<Map<String, Object>> data = LogsApi.getLogs(agentId, context);
            if (!data.isEmpty()) {
                setResult(AgentConstants.DATA, data.get(0).get(AgentConstants.ID));
            }
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception while getting agent logs for ->" + agentId + "  ", e);
        }
        return SUCCESS;
    }


    public String getAgentMetrics() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();

            jsonObject.put(AgentConstants.ID, 1);
            jsonObject.put(AgentConstants.ORGID, 1);
            jsonObject.put(AgentConstants.AGENT_ID, getAgentId());
            jsonObject.put(AgentConstants.PUBLISH_TYPE, 1);
            jsonObject.put(AgentConstants.NUMBER_OF_MSGS, 101010);
            jsonObject.put(AgentConstants.SIZE, 808080);
            jsonObject.put(AgentConstants.LAST_MODIFIED_TIME, 978546214);
            jsonObject.put(AgentConstants.CREATED_TIME, 978545214);

            array.add(jsonObject);
            array.add(jsonObject);
            array.add(jsonObject);

            setResult(AgentConstants.DATA, array);
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
            LOGGER.info("Exception while getting agentMetrics for ->" + getAgentId());
        }
        return SUCCESS;
    }

    public String getAgentOverView() {
        try {
            setResult(AgentConstants.DATA, AgentUtilV2.getAgentOverView(getAgentId()));
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent overview ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String getIotMessages() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
            result = IotMessageApiV2.listIotMessages(agentId, context);
            setResult(AgentConstants.DATA, result);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting iot messages->" + getAgentId() + " ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getIotMessageCount() {
        try {
            long count = IotMessageApiV2.getCount(agentId);
            setResult(AgentConstants.DATA, count);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting iot message count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getPointCount() {
        try {
            setResult(AgentConstants.DATA, PointsAPI.getPointsCountData(getAgentId()));
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting point count for agent " + agentId);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getControllerFilter() {
        try {
            List<Map<String, Object>> agentControllerFilterData = AgentConstants.getControllerBean().getControllerFilterData(getAgentId(), getControllerType());
            setResult(AgentConstants.DATA, agentControllerFilterData);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            internalError();
            LOGGER.info("Excdeption occurred while getting controller filter", e);
        }
        return SUCCESS;
    }

    public String getControllerTypeFilter() {
        try {
            setResult(AgentConstants.DATA, AgentConstants.getControllerBean().getControllerTypes(getAgentId()));
            ok();
            return SUCCESS;
        } catch (Exception e) {
            internalError();
            LOGGER.info("Excdeption occurred while getting controller filter", e);
        }
        return SUCCESS;
    }

    public String getMetrics() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
            List<Map<String, Object>> metrics = MetricsApi.getMetrics(getAgentId(), context);
            ok();
            setResult(AgentConstants.DATA, metrics);
        } catch (Exception e) {
            LOGGER.info("Exception while getting metrics ", e);
        }
        return SUCCESS;
    }

    public String getMetricsCount() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
            List<Map<String, Object>> metrics = MetricsApi.getMetrics(getAgentId(), context);
            long count = 0;
            if (CollectionUtils.isNotEmpty(metrics)) {
                count = (long) metrics.get(0).get(AgentConstants.ID);
            }
            setResult(AgentConstants.DATA, count);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting metrics ", e);
        }
        return SUCCESS;
    }

    public String getPendingControllerImports() {
        try {
            List<Map<String, Object>> maps = ModbusImportUtils.getpendingControllerImports(getAgentId());
            setResult(AgentConstants.DATA, maps);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pending controller imports for agentId " + getAgentId() + " ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String pendingDeviceImportCount() {
        try {
            setResult(AgentConstants.DATA, ModbusImportUtils.getPendingControllerImportCount(agentId));
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pending point imports count for agentId " + getAgentId() + " ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getRtuNetworks() {
        try {
            setResult(AgentConstants.DATA, fetchRtuNetworks(agentId));
            ok();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting Rtu Networks. ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    private List<Map<String, Object>> fetchRtuNetworks(Long agentId) throws Exception {
        List<FacilioField> fields = FieldFactory.getRtuNetworkFields();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields).table(ModuleFactory.getRtuNetworkModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
        return builder.get();
    }

    public String getSupportEmailUsingId() throws Exception {
        try {
            if (supportEmailId != -1) {
                FacilioContext context = new FacilioContext();
                context.put(FacilioConstants.ContextNames.ID, supportEmailId);

                FacilioChain getSupportEmail = FacilioChainFactory.getSupportEmailChain();
                getSupportEmail.execute(context);

                setResult(FacilioConstants.ContextNames.SUPPORT_EMAIL, context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL));
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting Support Email", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }

        return SUCCESS;
    }

}
