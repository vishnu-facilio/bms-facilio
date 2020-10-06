package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    
	private long getPointCount(PointStatus status) throws Exception {
		try {
			GetPointRequest point = new GetPointRequest();
			point.ofType(FacilioControllerType.valueOf(getControllerType()));
			point.withDeviceId(getDeviceId());
			point.count();
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
			if(StringUtils.isNotEmpty(querySearch)) {
				point.querySearch(AgentConstants.COL_NAME, querySearch);
			}
			List<Map<String, Object>> result = point.getPointsData();
			return (long) result.get(0).get(AgentConstants.ID);
		} catch (Exception e) {
			throw e;
		}
	}

	public enum PointStatus {
		UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED
	}
}
