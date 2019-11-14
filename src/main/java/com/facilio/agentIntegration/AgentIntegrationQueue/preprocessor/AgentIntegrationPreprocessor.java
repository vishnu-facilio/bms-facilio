package com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public interface AgentIntegrationPreprocessor {
    public abstract JSONObject preProcess(Object o) throws Exception;
}
