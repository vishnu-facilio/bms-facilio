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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;


public class AgentDownloadInterceptor implements Interceptor {

    private static final Logger LOGGER = LogManager.getLogger(AgentDownloadInterceptor.class.getName());

    JSONObject errorStatus = (JSONObject) new JSONParser().parse(" { \"contentType\": \"text/plain\", \"status\": \"500\" }");

    public AgentDownloadInterceptor() throws ParseException {
    }
    public List<Map<String, Object>> getAgentVersionLogRow(String auth_key) throws Exception {

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentVersionLogModule().getTableName())
                .select(FieldFactory.getAgentVersionLogFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(ModuleFactory.getAgentVersionLogModule()),auth_key,StringOperators.IS));
        List<Map<String, Object>> row = selectRecordBuilder.get();
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
        LOGGER.info("Destroy called");
    }

    @Override
    public void init() {
        LOGGER.info("Init Called");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        LOGGER.info("Intercept called ");
        HttpParameters parameters = ActionContext.getContext().getParameters();
        String auth_key = parameters.get("token").toString();
        LOGGER.info("Token : "+ auth_key);
        List<Map<String, Object>> res = getAgentVersionLogRow(auth_key);
        LOGGER.info("Res : "+res);

        if(res.size()!=0) {
            if (res.get(0).containsKey(AgentConstants.CREATED_TIME)) {
                long createdTime = (long) res.get(0).get(AgentConstants.CREATED_TIME);
                long cMillis = System.currentTimeMillis();
                LOGGER.info("DIFF:" +(cMillis - createdTime));
                if ((System.currentTimeMillis() - createdTime) < 3_600_000L) {
                    actionInvocation.invoke();
                    return "success";
                } else {
                    return "expired";
                }
            } else {
                return "error";
            }
        }else return "invaliedToken";
    }
}
