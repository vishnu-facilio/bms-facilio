package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.aws.util.AwsUtil;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.util.*;


public class AgentAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentAction.class.getName());

   /* public String createPolicy(){
            CreateKeysAndCertificateResult cert = AwsUtil.signUpIotToKinesis(AccountUtil.getCurrentOrg().getDomain(), AccountUtil.getCurrentOrg().getDomain(), "facilio");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cert arn",cert.getCertificateArn());
            jsonObject.put("cert id",cert.getCertificateId());
            jsonObject.put("cert cert pem",cert.getCertificatePem());
            setResult("Data",jsonObject);

        return SUCCESS;
    }*/

    public String listAgents() {
        try {
            FacilioContext context = new FacilioContext();
            constructListContext(context);
            List<FacilioAgent> agents = AgentApiV2.listFacilioAgents(context);
            JSONArray jsonArray = new JSONArray();
            long offLineAgents = 0;
            Set<Long> siteCount = new HashSet<>();
            for (FacilioAgent agent : agents) {
                jsonArray.add(agent.toJSON());
                if( ! agent.getConnectionStatus()){
                    offLineAgents++;
                    siteCount.add(agent.getSiteId());
                }
            }
            setResult(AgentConstants.SITE_COUNT,siteCount.size());
            setResult(AgentConstants.TOTAL_COUNT,agents.size());
            setResult(AgentConstants.ACTIVE_COUNT,agents.size()-offLineAgents);
            setResult(AgentConstants.DATA, jsonArray);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.RESULT, SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception while getting agent list",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }

    public String download() {
        //TODO not yet implemented
        setResult(SUCCESS, " no implementation");
        return SUCCESS;
    }


    public String getAgentCount() {
        try {
            setResult(AgentConstants.DATA, AgentApiV2.getAgentCount());
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception while getting agentCount->",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }


    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    private Long agentId;

    public String getControllerCount() {
        try {
            setResult(AgentConstants.RESULT, SUCCESS);
            if( (getAgentId() == null) || getAgentId() < 1){
                LOGGER.info(" getting org controller count ");
                setResult(AgentConstants.DATA, ControllerApiV2.getCountForOrg());
            }else {
                LOGGER.info(" getting agent controller count ");
                setResult(AgentConstants.DATA, ControllerApiV2.getCountForAgent(agentId));
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting total controller count ");
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    private Long controllerId;
    private Long deviceId;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    private Integer controllerType;

    public String listPoints() {
        JSONArray pointData = new JSONArray();
        try {
            List<Point> points = new ArrayList<>();
            FacilioContext context = constructListContext(new FacilioContext());
            if((controllerId != null) && (controllerId > 0) && (controllerType != null) && (controllerType > 0) && (deviceId == null)){
                LOGGER.info(" getting controller points");
                points = PointsAPI.getControllerPoints(FacilioControllerType.valueOf(controllerType), getControllerId(),context);
            }
            else if((deviceId != null) && (deviceId>0) && (controllerType != null) && (controllerType > 0)){
                LOGGER.info(" getting device points");
               points =  PointsAPI.getDevicePoints(getDeviceId(), getControllerType(),context);
            }else {
                if((controllerId == null)&&(deviceId == null)){
                    LOGGER.info(" getting all points");
                    points = PointsAPI.getAllPoints(null,-1,context);
                }
            }
            LOGGER.info(" in device action " + points);
            if (!points.isEmpty()) {
                for (Point point : points) {
                    JSONObject object = new JSONObject();
                    object.putAll(point.toJSON());
                    object.put(AgentConstants.CHILDJSON, point.getChildJSON());
                    pointData.add(object);
                }
            }
            setResult(AgentConstants.DATA, pointData);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting points", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        return SUCCESS;
    }

    public String deleteController(){
        try{

        }catch (Exception e){
            LOGGER.info("Exception while deleting controller",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }



    public String countDevices() {
        try {
            if ((agentId != null) && (agentId > 0)) {
                //TYPE AND AGENT ID
                if ((controllerType != null) && (controllerType > 0)) {
                    setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
                }
                // AGENT ID ALONE
                else {
                    setResult(AgentConstants.DATA, FieldDeviceApi.getAgentDeviceCount(getAgentId()));
                }
            }
            // TYPE ALONE
            else if ((controllerType != null) && (controllerType > 0)) {
                setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
            }
            //DEVICE POINT COUNT
            else {
                setResult(AgentConstants.DATA, FieldDeviceApi.getDeviceCount());
            }
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agentDevices count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }


    public String PointsCount(){
        LOGGER.info(" getting points ");
        try{
            long count = 0;
            if((controllerId != null) && (controllerId > 0) && (deviceId == null)){
                LOGGER.info(" contid ");
                count = PointsAPI.getPointsCount( getControllerId(), -1);
            }else if((deviceId != null) && (deviceId > 0) && (controllerId != null)){
                    LOGGER.info(" device id  ");
                    count = PointsAPI.getPointsCount(-1, deviceId);
            }else {
                LOGGER.info(" no point id");
                count = PointsAPI.getPointsCount(-1,-1);
            }

            setResult(AgentConstants.DATA,count);
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting all point for agent->"+controllerId+" -",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }

    public JSONObject getChildJson() {
        return childJson;
    }

    public void setChildJson(JSONObject childJson) {
        this.childJson = childJson;
    }

    private JSONObject childJson;
    public String getControllerUsingIdentifier(){
        try {
            Controller controller = ControllerApiV2.getControllerFromDb(getChildJson(), getAgentId(), FacilioControllerType.valueOf(getControllerType()));
            JSONObject jsonObject = new JSONObject();
            if (controller != null) {
                jsonObject.putAll(controller.toJSON());
                jsonObject.put(AgentConstants.CHILDJSON, controller.getChildJSON());
            }
            setResult(AgentConstants.DATA, jsonObject);
        }catch (Exception e){
            LOGGER.info("Exception while getting controller",e);
            setResult(AgentConstants.RESULT,new JSONObject());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getControllerUsingId(){
        try{
            List<Map<String, Object>> controllers = ControllerApiV2.getControllerData(getControllerId(), FacilioControllerType.valueOf(getControllerType()));
                  setResult(AgentConstants.DATA,controllers);
                    } catch (Exception e) {
            LOGGER.info(" Exception occurred while getting controller data",e);
        }
        return SUCCESS;
    }

    public String getOverview(){
        try{
            setResult(AgentConstants.DATA,AgentUtilV2.getOverview());
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting overview");
            setResult(AgentConstants.RESULT,new JSONObject());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    public String createPolicy(){
        try{
            LOGGER.info(" calling create policy ");
            setResult(AgentConstants.DATA,AwsUtil.createIotPolicy(getName(), AccountUtil.getCurrentOrg().getDomain(),"facilio"));
            //AwsUtil.addAwsIotClient();
        }catch (Exception e){
            LOGGER.info(" Exception while creating policy for "+getName()+" ",e);
        }
        return SUCCESS;
    }


    //__________________________________________________
    // general utilities



}
