package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.actions.RestAgentPushAction;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


public class AgentApiKeyInterceptor implements Interceptor {
    private static final Logger LOGGER = LogManager.getLogger(AgentApiKeyInterceptor.class.getName());

    private String agent;

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public void destroy() {
        LOGGER.info("destroyed");
    }

    @Override
    public void init() {
        LOGGER.info("initiated");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        LOGGER.info("Interceptor called");
        HttpServletRequest request = ServletActionContext.getRequest();
        String apiKey = request.getHeader("x-api-key");
        if (apiKey != null) {
            if (checkApiKey(apiKey, actionInvocation)) {

                actionInvocation.invoke();
                return "success";
            }
        }
        return "invalid api";
    }

    private boolean checkApiKey(String apiKey, ActionInvocation actionInvocation) throws Exception {
        Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getInboundConnectionsFields());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().table(ModuleFactory.getInboundConnectionsModule().getTableName())
                .select(fields.values())
                .andCondition(CriteriaAPI.getCondition(fields.get(AgentConstants.API_KEY), apiKey, StringOperators.IS));
        List<Map<String, Object>> res = selectRecordBuilder.get();
        long orgId = (long) res.get(0).get(AgentConstants.ORGID);
        String sender = (String) res.get(0).get("sender");
        AccountUtil.setCurrentAccount(orgId);
        RestAgentPushAction action = (RestAgentPushAction) actionInvocation.getAction();
        action.setSender(sender);
        return res.size() == 1;
    }
}
