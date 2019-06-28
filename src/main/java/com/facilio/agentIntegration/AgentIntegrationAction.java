package com.facilio.agentIntegration;


import com.facilio.agentIntegration.wattsense.WattsenseUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class    AgentIntegrationAction extends FacilioAction
{

    private String userName;
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    private String password;
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    private JSONObject userCredentials;
    public JSONObject getUserCredentials() { return userCredentials; }
    private final String USER_NAME="userName";
    private final String PASSWORD = "password";

    public String addWattsense(){
        JSONObject userCredentials = getUserCredentials();
        if((getUserName()!=null && getPassword()!=null)){
           boolean status = WattsenseUtil.authenticateAndConfigureWattsense(getUserName(), getPassword());
           if(status) {
               setResult(SUCCESS,new ArrayList<>());
               return SUCCESS;
           }
        }
        setResponseCode(400);
        setResult("message","exception occurred");
        return ERROR;
    }

    private  List<Map<String, Object>> wattsenseList;
    public List<Map<String, Object>> getWattsenseList() { return wattsenseList; }
    public void setWattsenseList(List<Map<String, Object>> wattsenseList) { this.wattsenseList = wattsenseList; }

    public String listWattsense(){
            List<Map<String, Object>> wattsenseList = new ArrayList<>();
            wattsenseList = WattsenseUtil.getWattsenseList();
            setWattsenseList(wattsenseList);
            if( ! wattsenseList.isEmpty()){
                setResult("wattsenseList",getWattsenseList());
                return SUCCESS;
            }
            setResult("ERROR",getWattsenseList());
            return SUCCESS;
    }


    private String clientId;
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    private static final Logger LOGGER = LogManager.getLogger(WattsenseUtil.class.getName());
    public String deleteWattsense(){
        String clientId = getClientId();
        if(clientId != null && getUserName() != null && getPassword() != null){
            if(WattsenseUtil.deleteIntegration(clientId,getUserName(),getPassword())){
                setResult(SUCCESS,"Integration deleted");
                return SUCCESS;
            }
        }
        setResult(ERROR,"Integration could not deleted");
        return ERROR;
    }
}
