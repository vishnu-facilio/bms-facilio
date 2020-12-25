package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.List;

public class IdsAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(IdsAction.class.getName());

    public List<Long> getRecordIds() { return recordIds; }

    public void setRecordIds(List<Long> recordIds) { this.recordIds = recordIds; }

    @NotNull
    private List<Long> recordIds;



    public String deleteControllers(){
        try {
            List<Long> controllerIds = getRecordIds();
            if (ControllerApiV2.deleteControllerApi(controllerIds)) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
            } else {
                setResult(AgentConstants.RESULT, ERROR);
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }catch (Exception e){
            LOGGER.info("Exception while deleting controller"+e.getMessage());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }


    public String discoverDevicePoints(){
        try{
            LOGGER.info(" discovering points for device " + getRecordIds());
            List<Long> deviceIds = getRecordIds();
            if( !deviceIds.isEmpty() ){
                if(FieldDeviceApi.discoverPoint(deviceIds)){
                    setResult(AgentConstants.RESULT,SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                }
            }else {
                setResult(AgentConstants.RESULT,ERROR);
                setResult(AgentConstants.EXCEPTION," Ids can't be empty ");
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while discovering points "+e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        setResult(AgentConstants.RESULT,ERROR);
        return SUCCESS;
    }




    public String deleteAgent() { //TODO test
        try {
            List<Long> agentIds = getRecordIds();
            if (!agentIds.isEmpty()) {
                boolean isdeleted = AgentApiV2.deleteAgent(agentIds);
                if (isdeleted) {
                    setResult(AgentConstants.RESULT, SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                }
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
                setResult(AgentConstants.RESULT, ERROR);
                return SUCCESS;
            } else {
                setResult(AgentConstants.EXCEPTION, "agentIds can't be empty");
                setResult(AgentConstants.RESULT, ERROR);
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                LOGGER.info("Exception occurred while deleting agent and agentIds can't be empty");
            }
        }catch (Exception e){
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            LOGGER.info("Exception while agent delete->"+recordIds+"  ",e);
        }
        return SUCCESS;
    }

    public String shutDownAgent() {
        try {
            List<Long> ids = getRecordIds();
            LOGGER.info(" shutting down agent" + ids);
            if ((ids != null) && (!ids.isEmpty())) {
                for (Long id : ids) {
                    AgentMessenger.shutDown(id);
                }
                setResult(AgentConstants.RESULT, SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
                return SUCCESS;
            }else {
                setResult(AgentConstants.EXCEPTION, "AgentIds empty");
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurr3ed while shutdown agent command");
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    } //TODO to test





    public String deleteDevice(){
        try{
            if(FieldDeviceApi.deleteDevices(getRecordIds())>0){
                setResult(AgentConstants.RESULT,SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agentDevices count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return SUCCESS;
    }

    public String makeWritable() {
        try {
            PointsAPI.makePoinsWritable(getRecordIds());
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while editing point writable", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return SUCCESS;
    }

    public String disableWritable() {
        try {
            PointsAPI.disablePoinsWritable(getRecordIds());
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while editing point writable", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return SUCCESS;
    }

}
