package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
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
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.RESOURCE_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ASSET_CATEGORY_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.FIELD_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.POINT_TYPE), String.valueOf(getControllerType()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(getDeviceId()), NumberOperators.EQUALS));
            
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(ModuleFactory.getPointModule().getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID))
                    .andCriteria(criteria);
            if (StringUtils.isNotEmpty(querySearch)) {
            	selectRecordBuilder.andCustomWhere("NAME = ? OR NAME LIKE ?", querySearch, "%"+querySearch + "%");
    		}
            List<Map<String, Object>> result = selectRecordBuilder.get();
            LOGGER.info(" query "+selectRecordBuilder.toString());
            LOGGER.info(" count is "+result.get(0));
            long count = (long) result.get(0).get(AgentConstants.ID);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting conf points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getConfiguredPointsCount(){
        try{
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.POINT_TYPE), String.valueOf(getControllerType()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(getDeviceId()), NumberOperators.EQUALS));
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(ModuleFactory.getPointModule().getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID))
                    .andCriteria(criteria);
            if (StringUtils.isNotEmpty(querySearch)) {
            	selectRecordBuilder.andCustomWhere("NAME = ? OR NAME LIKE ?", querySearch, "%"+querySearch + "%");
    		}
            List<Map<String, Object>> result = selectRecordBuilder.get();
            LOGGER.info(" query "+selectRecordBuilder.toString());
            LOGGER.info(" count is "+result.get(0));
            long count = (long) result.get(0).get(AgentConstants.ID);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting conf points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getUnConfiguredPointsCount(){
        try{
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.NOT_EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.POINT_TYPE), String.valueOf(getControllerType()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(getDeviceId()), NumberOperators.EQUALS));
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(ModuleFactory.getPointModule().getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID))
                    .andCriteria(criteria);
            if (StringUtils.isNotEmpty(querySearch)) {
            	selectRecordBuilder.andCustomWhere("NAME = ? OR NAME LIKE ?", querySearch, "%"+querySearch + "%");
    		}
            List<Map<String, Object>> result = selectRecordBuilder.get();
            LOGGER.info(" query "+selectRecordBuilder.toString());
            LOGGER.info(" count is "+result.get(0));
            long count = (long) result.get(0).get(AgentConstants.ID);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting conf points count ",e);
            internalError();
        }
        return SUCCESS;
    }

    public String getSubscribedPointsCount(){
        try{
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.POINT_TYPE), String.valueOf(getControllerType()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(getDeviceId()), NumberOperators.EQUALS));
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(ModuleFactory.getPointModule().getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID))
                    .andCriteria(criteria);
            if (StringUtils.isNotEmpty(querySearch)) {
            	selectRecordBuilder.andCustomWhere("NAME = ? OR NAME LIKE ?", querySearch, "%"+querySearch + "%");
    		}
            List<Map<String, Object>> result = selectRecordBuilder.get();
            LOGGER.info(" query "+selectRecordBuilder.toString());
            LOGGER.info(" count is "+result.get(0));
            long count = (long) result.get(0).get(AgentConstants.ID);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting conf points count ",e);
            internalError();
        }
        return SUCCESS;
    }

}
