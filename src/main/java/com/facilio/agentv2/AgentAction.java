package com.facilio.agentv2;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.actions.AdminAction;
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
            LOGGER.info(" listing agents");
            FacilioContext context = new FacilioContext();
            constructListContext(context);
            List<Map<String, Object>> agentListData = AgentApiV2.getAgentListData(false);
            // AgentApiV2.listFacilioAgents(context);
            long offLineAgents = 0;
            Set<Long> siteCount = new HashSet<>();
            for (Map<String, Object> agentListDatum : agentListData) {
                LOGGER.info(" agent datum " + agentListDatum);
                if (agentListDatum.containsKey(AgentConstants.CONNECTED)) {
                    if (agentListDatum.get(AgentConstants.CONNECTED) == null) {
                        LOGGER.info(" agent offline null");
                        offLineAgents++;
                        continue;
                    }
                    if (!(boolean) agentListDatum.get(AgentConstants.CONNECTED)) {
                        LOGGER.info(" agent offline 0");
                        offLineAgents++;
                    }
                } else {
                    offLineAgents++;
                }
            }
           /* for (FacilioAgent agent : agents) {
                jsonArray.add(agent.toJSON());
                if( ! agent.getConnected()){
                    offLineAgents++;
                    siteCount.add(agent.getSiteId());
                }
            }*/
            setResult(AgentConstants.SITE_COUNT, siteCount.size());
            setResult(AgentConstants.TOTAL_COUNT, agentListData.size());
            setResult(AgentConstants.ACTIVE_COUNT, agentListData.size() - offLineAgents);
            setResult(AgentConstants.DATA, agentListData);
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent list", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.RESULT, ERROR);
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
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agentCount->", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.RESULT, ERROR);
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
            //List<Point> points = new ArrayList<>();
            List<Point> pointsData = new ArrayList<>();
            FacilioContext context = constructListContext(new FacilioContext());
            if ((controllerId != null) && (controllerId > 0) && (controllerType != null) && (controllerType > 0) && (deviceId == null)) {
                GetPointRequest getPointRequest = new GetPointRequest()
                        .withControllerId(controllerId)
                        .ofType(FacilioControllerType.valueOf(controllerType))
                        .pagination(context);
                pointsData = getPointRequest.getPoints();
                LOGGER.info(" getting controller points");
            } else if ((deviceId != null) && (deviceId > 0) && (controllerType != null) && (controllerType > 0)) {
                LOGGER.info(" getting device points");
                GetPointRequest getPointRequest = new GetPointRequest()
                        .withDeviceId(deviceId)
                        .ofType(FacilioControllerType.valueOf(controllerType))
                        .pagination(context);
                pointsData = getPointRequest.getPoints();
            } else {
                if ((controllerId == null) && (deviceId == null)) {
                    LOGGER.info(" getting all points");
                    GetPointRequest getPointRequest = new GetPointRequest()
                            .pagination(context);
                    pointsData = getPointRequest.getPoints();
                }
            }
            if (!pointsData.isEmpty()) {
                for (Point point : pointsData) {
                    JSONObject object = new JSONObject();
                    object.putAll(point.toJSON());
                    object.put(AgentConstants.POINT, point.getChildJSON());
                    pointData.add(object);
                }
            }
            setResult(AgentConstants.DATA, pointData);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting points", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
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
                setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(-1, FacilioControllerType.valueOf(getControllerType())));
            }
            //DEVICE POINT COUNT
            else {
                setResult(AgentConstants.DATA, FieldDeviceApi.getDeviceCount());
            }
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agentDevices count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }


    public String PointsCount() {
        LOGGER.info(" getting points ");
        try {
            long count = 0;
            if ((controllerId != null) && (controllerId > 0) && (deviceId == null)) {
                LOGGER.info(" contid ");
                count = PointsAPI.getPointsCount(getControllerId(), -1);
            } else if ((deviceId != null) && (deviceId > 0) && (controllerId != null)) {
                LOGGER.info(" device id  ");
                count = PointsAPI.getPointsCount(-1, deviceId);
            } else {
                LOGGER.info(" no point id");
                count = PointsAPI.getPointsCount(-1, -1);
            }

            setResult(AgentConstants.DATA, count);
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting all point for agent->" + controllerId + " -", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
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

    public String getControllerUsingIdentifier() {
        try {
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .withAgentId(agentId)
                    .withControllerProperties(childJson, FacilioControllerType.valueOf(getControllerType()));
            Controller controller = getControllerRequest.getController();
            JSONObject jsonObject = new JSONObject();
            if (controller != null) {
                jsonObject.putAll(controller.toJSON());
                jsonObject.put(AgentConstants.CHILDJSON, controller.getChildJSON());
            }
            setResult(AgentConstants.DATA, jsonObject);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller", e);
            setResult(AgentConstants.RESULT, new JSONObject());
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getControllerUsingId() {
        try {
            Controller controllers = ControllerApiV2.getControllerFromDb(getControllerId());
            setResult(AgentConstants.DATA, controllers.toJSON());
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            LOGGER.info(" Exception occurred while getting controller data", e);
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

    public String createPolicy() {
        try {
            LOGGER.info(" calling create policy ");
            setResult(AgentConstants.DATA, AwsUtil.createIotPolicy(getName(), AccountUtil.getCurrentOrg().getDomain(), "facilio"));
            //AwsUtil.addAwsIotClient();
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info(" Exception while creating policy for " + getName() + " ", e);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getAgentFilter() {
        try {
            List<Map<String, Object>> agentFilter = AgentApiV2.getAgentFilter();
            setResult(AgentConstants.DATA, agentFilter);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent filter ", e);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getOverview() {
        try {
            setResult(AgentConstants.DATA, AgentUtilV2.getOverview());
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting overview");
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getConfiguredPoints() {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterConfigurePoints();

        try {
            if ((controllerType != null) && (controllerType > -1)) {
                getPointRequest.ofType(FacilioControllerType.valueOf(controllerType));
            }
            if ((deviceId != null)&&(deviceId > 0)) {
                getPointRequest.withDeviceId(deviceId);
            } else if ((controllerId != null) && (controllerId > 0)) {
                getPointRequest.withControllerId(controllerId);
            }
            getPointRequest.pagination(constructListContext(new FacilioContext()));
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getSubscribedPoints() {
        LOGGER.info(" get subscribed points ");
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterSubsctibedPoints();
        try {
            if ((controllerType != null) && (controllerType > -1)) {
                getPointRequest.ofType(FacilioControllerType.valueOf(controllerType));
            }
            if ((deviceId != null)&&(deviceId > 0)) {
                getPointRequest.withDeviceId(deviceId);
            } else if ((controllerId != null) && (controllerId > 0)) {
                getPointRequest.withControllerId(controllerId);
            }
            getPointRequest.pagination(constructListContext(new FacilioContext()));
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getUnconfiguredPoints() {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterUnConfigurePoints();
        try {
            if ((controllerType != null) && (controllerType > -1)) {
                getPointRequest.ofType(FacilioControllerType.valueOf(controllerType));
            }
            if ((deviceId != null)&&(deviceId > 0)) {
                getPointRequest.withDeviceId(deviceId);
            } else if ((controllerId != null) && (controllerId > 0)) {
                getPointRequest.withControllerId(controllerId);
            }
            getPointRequest.pagination(constructListContext(new FacilioContext()));
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getCommissionedPoints() {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterCommissionedPoints();
        try {
            if ((controllerType != null) && (controllerType > -1)) {
                getPointRequest.ofType(FacilioControllerType.valueOf(controllerType));
            }
            if ((deviceId != null)&&(deviceId > 0)) {
                getPointRequest.withDeviceId(deviceId);
            } else if ((controllerId != null) && (controllerId > 0)) {
                getPointRequest.withControllerId(controllerId);
            }
            getPointRequest.pagination(constructListContext(new FacilioContext()));
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getAlertsPoints(){
        try{
            if( AccountUtil.getCurrentOrg()!= null ) {
                JSONArray alertsPoints = AdminAction.getAlertsPointsData(AccountUtil.getCurrentOrg().getDomain());
                setResult(AgentConstants.DATA, alertsPoints);
                setResponseCode(HttpURLConnection.HTTP_OK);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting alert points ", e);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String downloadCertificate(){
        try{
            Organization currentOrg = AccountUtil.getCurrentOrg();
            if(currentOrg != null){
                String downloadCertificateLink = DownloadCertFile.downloadCertificate(currentOrg.getDomain(), "facilio");
                setResult(AgentConstants.DATA,downloadCertificateLink);
                ok();
            }else {
                LOGGER.info("Exception occurred, account cant be null");
                internalError();
            }
        }catch (Exception e){
            internalError();
            LOGGER.info("Exception while getting download cert link",e);
        }
        return SUCCESS;
    }


    public List<Long> getRecordIds() { return recordIds; }

    public void setRecordIds(List<Long> recordIds) { this.recordIds = recordIds; }

    private List<Long> recordIds;
    public String migrateControllers(){
        try {
            SqliteBridge.migrateAndAddControllers(getAgentId(),getRecordIds());
            setResult(AgentConstants.RESULT,SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while migrating controllers ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    //__________________________________________________
    // general utilities


}
