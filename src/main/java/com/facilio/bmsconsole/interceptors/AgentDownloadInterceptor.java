package com.facilio.bmsconsole.interceptors;

import com.facilio.agentIntegration.AgentIntegrationQueue.AgentIntegrationQueueFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.kinesis.metrics.impl.MetricsHelper.SUCCESS;

public class AgentDownloadInterceptor implements Interceptor {

    private static final Logger LOGGER = LogManager.getLogger(AgentDownloadInterceptor.class.getName());

    JSONObject errorStatus = (JSONObject) new JSONParser().parse(" { \"contentType\": \"text/plain\", \"status\": \"500\" }");

    public AgentDownloadInterceptor() throws ParseException {
    }
    public List<Map<String, Object>> getAgentVersionLogRow(String auth_key) throws Exception {

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentVersionLogModule().getTableName())
                .select(FieldFactory.getAgentVersionLogFields());
        List<Map<String, Object>> row = selectRecordBuilder.get();
        LOGGER.info("query : "+ selectRecordBuilder.toString());
        return row;
    }


    public JSONObject getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(JSONObject errorStatus) {
        this.errorStatus = errorStatus;
    }
    @Override
    public void destroy() {
        System.out.println("Destroy called");
    }

    @Override
    public void init() {
        System.out.println("Init Called");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        System.out.println("Intercept called ");
        HttpParameters parameters = ActionContext.getContext().getParameters();
        String auth_key = parameters.get("token").toString();
        long orgId = Long.parseLong(parameters.get("orgId").toString());

        System.out.println("AgentVersionLogRow->service");
        List<Map<String, Object>> res = getAgentVersionLogRow(auth_key);


        System.out.println("AgentVerisonLogEntry : "+res);
        if(res.size()!=0) {
            Map<String, Object> map = new HashMap<>();
            if (res.get(0).containsKey(AgentConstants.CREATED_TIME)) {
                long createdTime = (long) res.get(0).get(AgentConstants.CREATED_TIME);
                if (System.currentTimeMillis() - createdTime < 3_600_000L) {
                    actionInvocation.invoke();
                    return SUCCESS;
                } else {
                    return "expired";
                }
            } else {
                return "error";
            }
        }else return "error";
    }
}
