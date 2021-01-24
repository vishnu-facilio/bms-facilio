package com.facilio.agentv2.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.aws.util.FacilioProperties;
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

public class GetPointRequest {
    private static final Logger LOGGER = LogManager.getLogger(GetPointRequest.class.getName());

    private static final List<FacilioField> POINT_FIELDS = FieldFactory.getPointFields();
    private static final FacilioModule POINT_MODULE = ModuleFactory.getPointModule();
    private static final  Map<String, FacilioField> POINT_MAP = FieldFactory.getAsMap(POINT_FIELDS);

    private GenericSelectRecordBuilder selectRecordBuilder = null;
    private Criteria criteria = new Criteria();
    private FacilioControllerType controllerType = null;
    private int limit = 50 ;
    private int offset = 0;
    private String orderBy;

	public GetPointRequest ofType(FacilioControllerType controllerType) throws Exception {
        if (controllerType != null) {
            selectRecordBuilder = loadBuilder(controllerType);
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
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(POINT_MODULE),controllerIds,NumberOperators.EQUALS));
        return this;
    }
    
    public GetPointRequest withLogicalControllers(long agentId) throws Exception {
    		Set<Long> controllersIds = ControllerApiV2.getControllerIds(Collections.singletonList(agentId));
		if (CollectionUtils.isNotEmpty(controllersIds)) {
			criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.LOGICAL),
					String.valueOf(true), BooleanOperators.IS));
			withControllerIds(controllersIds);
			return this;
		} else {
			throw new IllegalArgumentException("ControllersIds should not be null for getting Virtual points.");
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

    public GetPointRequest filterSubsctibedPoints() {
        criteria.addAndCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
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
    	selectRecordBuilder.select(new ArrayList<>());
    	selectRecordBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, POINT_MAP.get(AgentConstants.ID));
    	return this;
    }

    public GetPointRequest querySearch(String column,String value) {
        selectRecordBuilder.andCustomWhere("NAME OR DISPLAY_NAME LIKE ?", "%"+value+"%");
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
        List<Map<String, Object>> data = new ArrayList<>();
       
        if(selectRecordBuilder == null){
            for (FacilioControllerType controllerType : FacilioControllerType.values()) {
                try{
                    selectRecordBuilder = loadBuilder(controllerType);
                    if( (criteria.getConditions() != null) && ! criteria.getConditions().isEmpty()){
                        selectRecordBuilder.andCriteria(criteria);
                    }
                    selectRecordBuilder.limit(limit);
                    data.addAll(selectRecordBuilder.get());
                    if(FacilioProperties.isDevelopment()){
                        LOGGER.info(controllerType.asString()+"Getpoints query "+selectRecordBuilder.toString());
                    }
                }catch (Exception e){
                    LOGGER.info("Exceptio  while getting points for type "+controllerType.asString()+" "+e);
                }
            }
        }
        else {
            if( (criteria.getConditions() != null) && ! criteria.getConditions().isEmpty()){
                selectRecordBuilder.andCriteria(criteria).orderBy(orderBy)
                        .limit(limit).offset(offset);
            }
            data = selectRecordBuilder.get();
            if(FacilioProperties.isDevelopment()){
                LOGGER.info("Getpoints query "+selectRecordBuilder.toString());
            }
        }
        if( ! data.isEmpty()){
            handlePointToggleFlag(data);
        }
        return data;
    }

    private void handlePointToggleFlag(List<Map<String, Object>> points) {
        for (Map<String, Object> point : points) {
            if (containsValueCheck(AgentConstants.POINT_TYPE,point)) {
                FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number)point.get(AgentConstants.POINT_TYPE)).intValue());
                switch (controllerType){
                    case BACNET_IP:
                        PointsAPI.handleBacnetWritableSwitch(point);
                        break;
                    case MODBUS_IP:
                        PointsAPI.handleModbusIpWritableSwitch(point);
                        break;
                }
            }
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
    
    public GetPointRequest initBuilder(List<FacilioField> fields) {
		this.selectRecordBuilder = getPointBuilder();
		if (fields == null) {
			fields = new ArrayList<>(POINT_FIELDS);
		}
		this.selectRecordBuilder.select(fields);
		return this;
}
    
    private GenericSelectRecordBuilder getPointBuilder() {
    		return new GenericSelectRecordBuilder().table(POINT_MODULE.getTableName());
    }

    private GenericSelectRecordBuilder loadBuilder(FacilioControllerType controllerType) throws Exception {
        GenericSelectRecordBuilder builder = getPointBuilder();
        List<FacilioField> fields = new ArrayList<>(POINT_FIELDS);
        switch (controllerType) {
            case MODBUS_RTU:
                fields.addAll(FieldFactory.getModbusRtuPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getModbusRtuPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getModbusRtuPointModule().getTableName() + ".ID");
                return builder;

            case OPC_UA:
                fields.addAll(FieldFactory.getOPCUAPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getOPCUAPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getOPCUAPointModule().getTableName() + ".ID");
                return builder;

            case OPC_XML_DA:
                fields.addAll(FieldFactory.getOPCXmlDAPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getOPCXmlDAPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getOPCXmlDAPointModule().getTableName() + ".ID");
                return builder;

            case BACNET_IP:
                fields.addAll(FieldFactory.getBACnetIPPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getBACnetIPPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getBACnetIPPointModule().getTableName() + ".ID");
                return builder;

            case MODBUS_IP:
                fields.addAll(FieldFactory.getModbusTcpPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getModbusTcpPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getModbusTcpPointModule().getTableName() + ".ID");
                return builder;

            case MISC:
                fields.addAll(FieldFactory.getMiscPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getMiscPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getMiscPointModule().getTableName() + ".ID");
                return builder;

            case NIAGARA:
                fields.addAll(FieldFactory.getNiagaraPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getNiagaraPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getNiagaraPointModule().getTableName() + ".ID");
                return builder;
            case SYSTEM:
                fields.addAll(FieldFactory.getSystemPointFields());
                builder.select(fields).innerJoin(ModuleFactory.getSystemPointModule().getTableName())
                        .on(AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getSystemPointModule().getTableName()+".ID");
                return builder;
            case LON_WORKS:
            	fields.addAll(AgentFieldFactory.getLonWorksPointFields());
            	builder.select(fields).innerJoin(AgentModuleFactory.getLonWorksPointModule().getTableName())
            	.on(AgentConstants.POINTS_TABLE+".ID="+AgentModuleFactory.getLonWorksPointModule().getTableName()+".ID");
            	return builder;
            default:
                throw new Exception("FacilioControler type didnt match with cases " + controllerType.toString());
        }
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

    public GetPointRequest withDeviceId(long deviceId) throws Exception {
        if(deviceId > 0){
            withDeviceIds(Collections.singletonList(deviceId));
        }else {
            throw new Exception(" controller id can't be less than 1");
        }
        return this;
    }

    public GetPointRequest withDeviceIds(List<Long> deviceIds) throws Exception {
        if(CollectionUtils.isNotEmpty(deviceIds)){
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getFieldDeviceId(POINT_MODULE), deviceIds,NumberOperators.EQUALS));
        }else {
            throw new Exception(" device id can't be null");
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
}
