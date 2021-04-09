package com.facilio.agentv2.rdm;

import com.facilio.agent.controller.FacilioController;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data

public class RdmControllerContext extends Controller {
    private static final Logger LOGGER = LogManager.getLogger(RdmControllerContext.class.getName());

    private String device;
    private String url;
    private String username;
    private String password;

    public static String getKey(RdmControllerContext rdmControllerContext) throws URISyntaxException {
        URI uri = new URI(rdmControllerContext.getUrl());
        return rdmControllerContext.getDevice() + ":" + uri.getHost();
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.RDM_CONTROLLER_MODULE_NAME;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.ID, getId());
        object.put(AgentConstants.URL, getUrl());
        object.put(AgentConstants.DEVICE, getDevice());
        object.put(AgentConstants.USER_NAME, getUsername());
        object.put(AgentConstants.PASSWORD, getPassword());
        return object;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        FacilioField userNameField = fieldsMap.get(AgentConstants.USERNAME);
        FacilioField deviceField = fieldsMap.get(AgentConstants.DEVICE);
        FacilioField urlField = fieldsMap.get(AgentConstants.URL);
        FacilioField passwordField = fieldsMap.get(AgentConstants.PASSWORD);

        conditions.add(CriteriaAPI.getCondition(urlField, getUrl(), StringOperators.IS));
        conditions.add(CriteriaAPI.getCondition(deviceField, getDevice(), StringOperators.IS));

        LOGGER.info(" fields : " + urlField + "-----" + deviceField + "-----" + userNameField + "-----" + passwordField);
        if (username == null) {
            conditions.add(CriteriaAPI.getCondition(userNameField, "NULL", CommonOperators.IS_EMPTY));
        } else {
            conditions.add(CriteriaAPI.getCondition(userNameField, getUsername(), StringOperators.IS));
        }
        if (password == null) {
            conditions.add(CriteriaAPI.getCondition(passwordField, "NULL", CommonOperators.IS_EMPTY));
        } else {
            conditions.add(CriteriaAPI.getCondition(passwordField, getPassword(), StringOperators.IS));
        }
        return conditions;
    }

    @Override
    public String getIdentifier() throws Exception {
        return device + ":" + getHost(url);
    }

    private String getHost(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getHost();
    }

    @Override
    public int getControllerType() {
        return FacilioControllerType.RDM.asInt();
    }
}
