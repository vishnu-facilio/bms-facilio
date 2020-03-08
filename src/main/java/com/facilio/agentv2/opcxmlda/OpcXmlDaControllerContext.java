package com.facilio.agentv2.opcxmlda;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpcXmlDaControllerContext extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.OPC_XML_DA_CONTROLLER_MODULE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(OpcXmlDaControllerContext.class.getName());

    @JsonInclude
    private String url;

    @JsonInclude
    private String userName;

    @JsonInclude
    private String password;

    private String identifier;



    public OpcXmlDaControllerContext(long agentId, long orgId) throws Exception {
        super(agentId,orgId);
    }


    public OpcXmlDaControllerContext() {
        setControllerType(FacilioControllerType.OPC_XML_DA.asInt());
    }

    public String getModuleName() {
        return ASSETCATEGORY;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.ID,getId());
        object.put(AgentConstants.URL,getUrl());
        object.put(AgentConstants.USER_NAME,getUserName());
        object.put(AgentConstants.PASSWORD,getPassword());
        return object;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        if(userName == null){
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.USER_NAME),"NULL", CommonOperators.IS_EMPTY));
        }else {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.USER_NAME),getUserName(), StringOperators.IS));
        }
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL),getUrl(),StringOperators.IS));
        if(password == null){
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PASSWORD),"NULL", CommonOperators.IS_EMPTY));
        }else {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PASSWORD),getPassword(),StringOperators.IS));
        }
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return url+IDENTIFIER_SEPERATER+userName+IDENTIFIER_SEPERATER+password;
    }
}
