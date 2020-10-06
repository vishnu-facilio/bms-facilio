package com.facilio.bmsconsole.interceptors;

import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.kinesis.metrics.impl.MetricsHelper.SUCCESS;

public class AgentFileDownloadInterceptor implements Interceptor {
    private static final Logger LOGGER = LogManager.getLogger(AgentFileDownloadInterceptor.class.getName());

    @Override
    public void destroy() {

    }

    @Override
    public void init() {
        LOGGER.info("Agent file download interceptor initialized");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        LOGGER.info("Intercept called ");
        HttpParameters parameters = ActionContext.getContext().getParameters();
        String auth_key = parameters.get("token").toString();
        LOGGER.info("Token : " + auth_key);
        List<Map<String, Object>> res = getAgentFileLogRow(auth_key);
        LOGGER.info("Res : " + res);

        if (res.size() != 0) {
            if (res.get(0).containsKey(AgentConstants.CREATED_TIME)) {
                long createdTime = (long) res.get(0).get(AgentConstants.CREATED_TIME);
                long cMillis = System.currentTimeMillis();
                LOGGER.info("DIFF:" + (cMillis - createdTime));
                if ((System.currentTimeMillis() - createdTime) < 3_600_000L) {
                    Map<String, Parameter> params = new HashMap<>();
                    params.put("fileName", new Parameter.Request("fileName", res.get(0).get("fileName").toString()));
                    params.put("fileId", new Parameter.Request("fileId", Long.parseLong(res.get(0).get("fileId").toString())));
                    params.put("orgId", new Parameter.Request("orgId", Long.parseLong(res.get(0).get("orgId").toString())));
                    params.put("agentId", new Parameter.Request("agentId", Long.parseLong(res.get(0).get("agentId").toString())));
                    ActionContext.getContext().getParameters().appendAll(params);
                    actionInvocation.invoke();
                    return SUCCESS;
                } else {
                    return "expired";
                }
            } else {
                return "error";
            }
        } else return "invaliedToken";
    }

    private List<Map<String, Object>> getAgentFileLogRow(String auth_key) throws Exception {

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentFileLogModule().getTableName())
                .select(FieldFactory.getAgentFileLogFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(ModuleFactory.getAgentFileLogModule()), auth_key, StringOperators.IS));
        List<Map<String, Object>> row = selectRecordBuilder.get();
        return row;
    }
}
