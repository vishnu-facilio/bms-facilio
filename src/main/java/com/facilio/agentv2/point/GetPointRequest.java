package com.facilio.agentv2.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class GetPointRequest {
    private static final Logger LOGGER = LogManager.getLogger(GetPointRequest.class.getName());

    private static final List<FacilioField> POINT_FIELDS = FieldFactory.getPointFields();
    private static final FacilioModule POINT_MODULE = ModuleFactory.getPointModule();
    private static final  Map<String, FacilioField> POINT_MAP = FieldFactory.getAsMap(POINT_FIELDS);


    private static List<FacilioField> M_POINT_FIELDS ;
    private static  Map<String, FacilioField> M_POINT_MAP ;
    private static  FacilioModule M_POINT_MODULE ;

    private Criteria criteria = new Criteria();
    private FacilioControllerType controllerType = null;
    private int limit = 50 ;
    private int offset = 0;
    private String orderBy;
    private boolean fetchCount = false;

    public GetPointRequest() throws Exception {
        M_POINT_FIELDS = AgentConstants.getPointFields();
        M_POINT_MODULE = AgentConstants.getPointModule();
        M_POINT_MAP = FieldFactory.getAsMap(M_POINT_FIELDS);
    }

	public GetPointRequest ofType(FacilioControllerType controllerType) throws Exception {
        if (controllerType != null) {
        	this.controllerType = controllerType;
            return this;
        } else {
            throw new Exception(" controller type cant be null");
        }
    }


    public GetPointRequest withCriteria(Criteria criteria) throws Exception {
        if (criteria != null) {
            this.criteria.andCriteria(criteria);
            return this;
        } else {
            throw new Exception(" criteria cant be null ");
        }
    }

    public GetPointRequest withControllerId (long controllerId) throws Exception {
        return withControllerIds(Collections.singletonList(controllerId));
    }

    public GetPointRequest withControllerIds ( Collection<Long> controllerIds ) throws Exception {
        if (M_POINT_MODULE == null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(POINT_MODULE), controllerIds, NumberOperators.EQUALS));
        }
        else {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(M_POINT_MODULE), controllerIds, NumberOperators.EQUALS));

        }
        return this;
    }
    
    public GetPointRequest withAgentId ( long agentId ) throws Exception {
        if (M_POINT_MODULE == null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(POINT_MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        else {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(M_POINT_MODULE), String.valueOf(agentId), NumberOperators.EQUALS));

        }
        return this;
    }
    
    public GetPointRequest withLogicalControllers(long agentId) throws Exception {
        Set<Long> controllersIds = AgentConstants.getControllerBean().getControllerIds(Collections.singletonList(agentId));
        if (M_POINT_MODULE == null) {
            if (CollectionUtils.isNotEmpty(controllersIds)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.LOGICAL),
                        String.valueOf(true), BooleanOperators.IS));
                withControllerIds(controllersIds);
                return this;
            } else {
                throw new IllegalArgumentException("ControllersIds should not be null for getting Virtual points.");
            }
        }
        else {
            if (CollectionUtils.isNotEmpty(controllersIds)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(M_POINT_MAP.get(AgentConstants.LOGICAL),
                        String.valueOf(true), BooleanOperators.IS));
                withControllerIds(controllersIds);
                return this;
            } else {
                throw new IllegalArgumentException("ControllersIds should not be null for getting Virtual points.");
            }
        }
    }

    public GetPointRequest filterConfigurePoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
        return this;
    }

    public GetPointRequest filterUnConfigurePoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.NOT_EQUALS));
        return this;
    }

    public GetPointRequest filterUnSubscribePoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.NOT_EQUALS));
        return this;
    }

    public GetPointRequest filterSubscribedPoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
        return this;
    }
    
    public GetPointRequest filterPointsForCommissioning() {
    		Criteria filterCriteria = new Criteria(); 
    		filterCriteria.addOrCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
    		filterCriteria.addOrCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
    		criteria.andCriteria(filterCriteria);
        return this;
    }

    public GetPointRequest forController(long controllerId) throws Exception {
        if(controllerId > 0){
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(POINT_MODULE), String.valueOf(controllerId),NumberOperators.EQUALS));
        }else {
            throw new Exception(" controller id can't be less than 1");
        }
        return this;
    }

    public GetPointRequest withId(long id) throws Exception {
        return fromIds(new ArrayList<>(Arrays.asList(id)));
    }
    public GetPointRequest fromIds(List<Long> ids) throws Exception {
        if( (ids != null) && ( ! ids.isEmpty()) ){
            criteria.addAndCondition(CriteriaAPI.getIdCondition(ids,POINT_MODULE));
        }else {
            throw new Exception(" controller id can't be less than 1");
        }
        return this;
    }

    public GetPointRequest filterCommissionedPoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.FIELD_ID), CommonOperators.IS_NOT_EMPTY));
        return this;
    }
    
    public GetPointRequest filterUnMappedPoints() {
        Criteria filterCriteria= new Criteria(); 
        filterCriteria.addOrCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_EMPTY));
        filterCriteria.addOrCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.FIELD_ID), CommonOperators.IS_EMPTY));
        filterCriteria.andCriteria(filterCriteria);
        return this;
    }
    
    public GetPointRequest count() throws Exception {
    	this.fetchCount = true;
    	return this;
    }

    public List<Point> getPoints() throws Exception {
        return PointsAPI.getPointFromRows(getPointsData());
    }
    public Point getPoint() throws Exception {
        List<Point> points = PointsAPI.getPointFromRows(getPointsData());
        return points.get(0);
    }

    public List<Map<String, Object>> getPointsData() throws Exception {
    	GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
        if (M_POINT_MODULE == null){
            selectRecordBuilder.table(POINT_MODULE.getTableName());
        }
        else {
            selectRecordBuilder.table(M_POINT_MODULE.getTableName());
        }
		if (controllerType != null) {
			joinExtendedTable(selectRecordBuilder);
		}
        else if (fetchCount) {
            setCountAggregate(selectRecordBuilder);
        }
        else {
            selectRecordBuilder.select(new ArrayList<>(POINT_FIELDS));
        }
        if(criteria.getConditions() != null && !criteria.getConditions().isEmpty()){
            selectRecordBuilder.andCriteria(criteria);
        }
        if (limit > 0) {
        	selectRecordBuilder.limit(limit).offset(offset);
        }
        
        
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if( !props.isEmpty()){
        	if (fetchCount) {
        		return props;
        	}
        	
        	Map<FacilioControllerType, List<Long>> extendedIds = new HashMap<>();
        	for (Map<String, Object> prop : props) {
        		
        		FacilioControllerType pointControllerType = FacilioControllerType.valueOf(FacilioUtil.parseInt(prop.get(AgentConstants.POINT_TYPE)));
        		handlePointToggleFlag(prop, pointControllerType);
        		/* if(pointControllerType == FacilioControllerType.BACNET_IP){
                    addBACnetProperties(prop);
                } */
        		// Fetching extended data if controllerType unknown during fetch
            	if (this.controllerType == null) {
            		
            		List<Long> ids = extendedIds.get(pointControllerType);
            		if (ids == null) {
            			ids = new ArrayList<>();
            			extendedIds.put(pointControllerType, ids);
            		}
            		ids.add((long) prop.get("id"));
            	}
    		}
        	
        	if (!extendedIds.isEmpty()) {
        		Map<Long, Map<String, Object>> extendedProps = getExtendedProps(extendedIds);
        		for (Map<String, Object> prop : props) {
        			Map<String, Object> extendedProp = extendedProps.get(prop.get("id"));
        			prop.putAll(extendedProp);
        		}
        	}
            
        }
        return props;
    }
    
	private  Map<Long, Map<String, Object>> getExtendedProps(Map<FacilioControllerType, List<Long>> extendedIds) throws Exception {
		Map<Long, Map<String, Object>> extendedProps = new HashMap<>();
        for (Entry<FacilioControllerType, List<Long>> entry: extendedIds.entrySet()) {
        	FacilioControllerType pointControllerType = entry.getKey();
    		List<Long> pointIds = entry.getValue();
    		FacilioModule pointModule = PointsAPI.getPointModule(pointControllerType);
            List<Map<String, Object>> props = new ArrayList<>();
            if (M_POINT_MODULE == null) {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(pointModule.getTableName())
                        .select(PointsAPI.getChildPointFields(pointControllerType, false))
                        .andCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));

                props = builder.get();
            }
            else {
                SelectRecordsBuilder builder = new SelectRecordsBuilder()
                        .module(pointModule)
                        .table(pointModule.getTableName())
                        .select(PointsAPI.getChildPointFields(pointControllerType, false))
                        .andCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));

                props = builder.getAsProps();
            }
    		extendedProps.putAll(props.stream().collect(Collectors.toMap(prop -> (long)prop.get("id"), prop -> prop)));
        }
        return extendedProps;
	}

    private void handlePointToggleFlag(Map<String, Object> point, FacilioControllerType controllerType) {
        switch (controllerType){
            case BACNET_IP:
                PointsAPI.handleBacnetWritableSwitch(point);
                break;
            case MODBUS_IP:
                PointsAPI.handleModbusIpWritableSwitch(point);
                break;
        }
    }

    public GetPointRequest limit(int limit) {
    		this.limit = limit;
    		return this;
    }
    
    public GetPointRequest orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

    private void joinExtendedTable(GenericSelectRecordBuilder builder) throws Exception {
        
		FacilioModule extendedModule = PointsAPI.getPointModule(controllerType);
        builder.innerJoin(extendedModule.getTableName())
        	.on(AgentConstants.POINTS_TABLE + ".ID=" + extendedModule.getTableName() + ".ID");
        if (!fetchCount) {
            List<FacilioField>fields = PointsAPI.getChildPointFields(controllerType);
            if (M_POINT_MODULE != null){
                fields.add(FieldFactory.getIdField(M_POINT_MODULE));
            }
        	builder.select(fields);
        }
        else {
        	setCountAggregate(builder);
        }
    }
    
    private void setCountAggregate(GenericSelectRecordBuilder builder) throws Exception {
    	builder.select(new ArrayList<>())
    			.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, POINT_MAP.get(AgentConstants.ID));
    }

    public GetPointRequest pagination(FacilioContext context){
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null ) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
           this.offset = offset;
            this.limit = perPage;
        }
        return this;
    }

    private static boolean notNull(Object object) {
        return object != null;
    }

    private static boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    private static boolean containsValueCheck(String key, Map<String,Object> map){
        if(notNull(key) && notNull(map) && map.containsKey(key) && ( map.get(key) != null) ){
            return true;
        }
        return false;
    }

    private void addBACnetProperties(Map<String, Object> prop) throws Exception {
        if(prop.containsKey(AgentConstants.INSTANCE_TYPE) && prop.get(AgentConstants.INSTANCE_TYPE) != null){
            int pointInstanceType = (int) prop.get(AgentConstants.INSTANCE_TYPE);
            BACNetUtil.InstanceType instanceType = BACNetUtil.InstanceType.valueOf(pointInstanceType);
            if(instanceType.isMultiState()){
                List<Map<String, Object>> readingInputValueAndLabelProps = ReadingsAPI.getReadingInputValues(Collections.singletonList((Long) prop.get(AgentConstants.ID)));
                JSONObject inputValueVsLabel = new JSONObject();
                for (Map<String, Object> readingInputValueAndLabelProp : readingInputValueAndLabelProps) {
                    if(readingInputValueAndLabelProp.containsKey("inputLabel") && readingInputValueAndLabelProp.get("inputLabel") != null && readingInputValueAndLabelProp.containsKey("inputValue") && readingInputValueAndLabelProp.get("inputValue") != null){
                        inputValueVsLabel.put(readingInputValueAndLabelProp.get("inputValue").toString(), readingInputValueAndLabelProp.get("inputLabel").toString());
                    }
                }
                if(!inputValueVsLabel.isEmpty()){
                    prop.put(AgentConstants.STATES, inputValueVsLabel);
                }
            }
        }
    }
     
}
