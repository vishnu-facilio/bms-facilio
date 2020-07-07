package com.facilio.bmsconsole.interceptors;


import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.Operator;
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
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import static com.amazonaws.services.kinesis.metrics.impl.MetricsHelper.SUCCESS;

public class AgentInboundConnectionInterceptor implements Interceptor {
    private static final Logger LOGGER = LogManager.getLogger(AgentInboundConnectionInterceptor.class.getName());

    @Override
    public void destroy() {
        LOGGER.info("Interceptor destroyed");
    }

    @Override
    public void init() {
        LOGGER.info("Interceptor initialized");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        LOGGER.info("Interceptor called");
        HttpServletRequest request = ServletActionContext.getRequest();
        String apiKey = request.getHeader("x-api-key");
        if(apiKey!=null){
            if (checkApiKey(apiKey)){
                actionInvocation.invoke();
                return SUCCESS;
            }
        }
        return "invalid api";
    }

    private boolean checkApiKey(String apiKey) throws Exception {
        Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getInboundConnectionsFields());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().table(ModuleFactory.getInboundConnectionsModule().getTableName())
                .select(fields.values())
                .andCondition(CriteriaAPI.getCondition(fields.get("apiKey"),apiKey,StringOperators.IS));
        List<Map<String, Object>> res = selectRecordBuilder.get();
        return res.size()==1;
    }
}
