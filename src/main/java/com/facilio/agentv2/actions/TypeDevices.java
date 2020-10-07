package com.facilio.agentv2.actions;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;

public class TypeDevices extends DeviceIdActions {

    private static final Logger LOGGER = LogManager.getLogger(TypeDevices.class.getName());

    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @NotNull
    @Min(value = 1,message = "controller type can't be less than 1")
    private Integer controllerType;

    /**
     * gets points for a device of a particular type.
     * @return
     */
    
    private String querySearch;
    
    public String getQuerySearch() {
		return querySearch;
	}

	public void setQuerySearch(String querySearch) {
		this.querySearch = querySearch;
	}
	private String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String listDeviceTypePoints(){
        try{
            if(checkValue(getDeviceId()) && (getControllerType()>0)){
                List<Point> points = new ArrayList<>();
                JSONArray pointsArray = new JSONArray();
                points.forEach(point -> pointsArray.add(point.toJSON()));
                setResult(AgentConstants.DATA, pointsArray);
                setResponseCode(HttpURLConnection.HTTP_OK);
            }else {
                throw new Exception(" deviceId ->"+getDeviceId());
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting device points",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String getCommissionedPointsCount(){
        try{
            long count = getPointCount(PointStatus.COMMISSIONED);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting commissioned points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getConfiguredPointsCount(){
        try{
            long count = getPointCount(PointStatus.CONFIGURED);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting configured points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getUnConfiguredPointsCount(){
        try{
            long count = getPointCount(PointStatus.UNCONFIRURED);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting Unconfigured points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getSubscribedPointsCount(){
        try{
            long count = getPointCount(PointStatus.SUBSCRIBED);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting Subscribed points count ",e);
            internalError();
        }
        return SUCCESS;
    }
    
    public String getCount() {
    	try {
    		long count = getPointCount(PointStatus.valueOf(status));
            setResult(AgentConstants.DATA,count);
            ok();
    	}catch(Exception e) {
    		LOGGER.error("Exception while getting points count ",e);
            internalError();
    	}
    	return SUCCESS;
    }
    
	private long getPointCount(PointStatus status) throws Exception {
		try {
			GetPointRequest point = new GetPointRequest();
			sanityCheck(point);
			pointFilter(status, point);
			point.ofType(FacilioControllerType.valueOf(getControllerType()));
			point.count();
			return (long) point.getPointsData().get(0).getOrDefault(AgentConstants.ID, 0L);
		} catch (Exception e) {
			throw e;
		}
	}

	private void pointFilter(PointStatus status, GetPointRequest point) throws Exception {
		switch (status) {
		case SUBSCRIBED:
			point.filterSubsctibedPoints();
			break;
		case COMMISSIONED:
			point.filterCommissionedPoints();
			break;
		case CONFIGURED:
			point.filterConfigurePoints();
			break;
		case UNCONFIRURED:
			point.filterUnConfigurePoints();
			break;
		default:
			throw new IllegalArgumentException("Point status is not satisfied");
		}
	}

	private void sanityCheck(GetPointRequest point) throws Exception {

		if(getDeviceId() != null && getDeviceId() > 0) {
			point.withDeviceId(getDeviceId());
		}
		if(StringUtils.isNotEmpty(querySearch)) {
			point.querySearch(AgentConstants.COL_NAME, querySearch);
		}
		if(getControllerType() == FacilioControllerType.BACNET_IP.asInt()) {
			Criteria criteria = new Criteria();
	    	criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getBACnetIPPointFields()).get(AgentConstants.INSTANCE_TYPE), StringUtils.join(AgentAction.getFilterInstances(),","), NumberOperators.EQUALS));
	    	point.withCriteria(criteria);
		}
	}
	
	public enum PointStatus {
		UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED
	}
}
