package com.facilio.agentv2.opcxmlda;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpcXmlDaController extends Controller {

    private String url;
    private String userName;
    private String password;

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.OPC_XML_DA_CONTROLLER_MODULE_NAME;
    private String identifier;

    private static final Logger LOGGER = LogManager.getLogger(OpcXmlDaController.class.getName());


    public OpcXmlDaController(long agentId, long orgId) throws Exception {
        new OpcXmlDaController(agentId,orgId,null);
    }

    public OpcXmlDaController(long agentId, long orgId, String identifier) throws Exception {
        super(agentId, orgId);
        processIdentifier(identifier);
        setControllerType(FacilioControllerType.OPC_XML_DA.asInt());
    }

    public OpcXmlDaController() {
    }

    public String getModuleName() { return ASSETCATEGORY; }



    public String getUrl() { return url; }
    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }


    public static OpcXmlDaController getOpcXmlDaControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if( containsValueCheck(AgentConstants.IDENTIFIER,controllerMap) ){
            OpcXmlDaController controller = new OpcXmlDaController(agentId,orgId, (String) controllerMap.get(AgentConstants.IDENTIFIER));
            return (OpcXmlDaController) controller.getControllerFromJSON(controllerMap);
        }
        if( containsValueCheck(AgentConstants.USER_NAME,controllerMap) && containsValueCheck(AgentConstants.URL,controllerMap) && containsValueCheck(AgentConstants.PASSWORD,controllerMap)){
            OpcXmlDaController controller = new OpcXmlDaController(agentId,orgId);
            controller.setUserName(String.valueOf(controllerMap.get(AgentConstants.USER_NAME)));
            controller.setUrl(String.valueOf(controllerMap.get(AgentConstants.URL)));
            controller.setPassword(String.valueOf(controllerMap.get(AgentConstants.PASSWORD)));
            return (OpcXmlDaController) controller.getControllerFromJSON(controllerMap);
        }
        throw  new Exception(" Mandatory fields like "+AgentConstants.USER_NAME+","+AgentConstants.URL+","+AgentConstants.PASSWORD+" OR "+AgentConstants.IDENTIFIER+" might be missing from input parameter -> "+controllerMap);
    }

    public void processIdentifier(String identifier) throws Exception {
        if( ( identifier != null ) && ( ! identifier.isEmpty() ) ){
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if( (uniques.length == 2) && ( (FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.OPC_XML_DA) ) )
            {
                setUrl(uniques[1]);
                this.identifier = identifier;
            }else {
                throw  new Exception(" Exceprion while processing identifier -- length or Type didnt match ");
            }

        }else {
            throw new Exception("Exception Occurred, identifier can't be null or empty ->"+identifier);
        }
    }

    @Override
    public String makeIdentifier() throws Exception {
        if( (identifier != null ) && ( ! identifier.isEmpty() ) ){
            return identifier;
        }
        if( isNotNull(url)  ){
            identifier = FacilioControllerType.OPC_XML_DA.asInt()+IDENTIFIER_SEPERATOR+url;
            return identifier;
        }
        throw new Exception("Exception occurred , parameters for identifier not set yet");
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
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        processIdentifier(identifier);
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.USER_NAME),getUserName(), StringOperators.IS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL),getUrl(),StringOperators.IS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PASSWORD),getPassword(),StringOperators.IS));
        return conditions;
    }
}
