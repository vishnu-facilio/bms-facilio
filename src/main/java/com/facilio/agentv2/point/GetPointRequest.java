package com.facilio.agentv2.point;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetPointRequest {
    private static final Logger LOGGER = LogManager.getLogger(GetPointRequest.class.getName());

    private List<FacilioField> fields = FieldFactory.getPointFields();
    private final FacilioModule pointModule = ModuleFactory.getPointModule();
    private GenericSelectRecordBuilder selectRecordBuilder = null;
    private Criteria criteria = new Criteria();
    private FacilioControllerType controllerType = null;
    private int limit = 150 ;
    private int offset = 0;

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
            this.criteria.addAndConditions(new ArrayList<>(criteria.getConditions().values()));
            this.criteria = criteria;
            return this;
        } else {
            throw new Exception(" criteria cant be null ");
        }
    }

    public GetPointRequest withControllerId(long controllerId) throws Exception {
        if (controllerId > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controllerId), NumberOperators.EQUALS));
            return this;
        } else {
            throw new Exception(" controller id cane be less than 1");
        }
    }

    public GetPointRequest filterConfigurePoints() {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
        return this;
    }

    public GetPointRequest filterUnConfigurePoints() {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(3), NumberOperators.NOT_EQUALS));
        return this;
    }

    public GetPointRequest filterUnSubscribePoints() {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.NOT_EQUALS));
        return this;
    }

    public GetPointRequest filterSubsctibedPoints() {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.SUBSCRIBE_STATUS), String.valueOf(3), NumberOperators.EQUALS));
        return this;
    }

    public GetPointRequest forController(long controllerId) throws Exception {
        if(controllerId > 0){
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controllerId),NumberOperators.EQUALS));
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
            criteria.addAndCondition(CriteriaAPI.getIdCondition(ids,pointModule));
        }else {
            throw new Exception(" controller id can't be less than 1");
        }
        return this;
    }

    public GetPointRequest filterCommissionedPoints() {
        Map<String, FacilioField> asMap = FieldFactory.getAsMap(fields);
        criteria.addAndCondition(CriteriaAPI.getCondition(asMap.get(AgentConstants.RESOURCE_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(asMap.get(AgentConstants.ASSET_CATEGORY_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(asMap.get(AgentConstants.FIELD_ID), "NULL", CommonOperators.IS_NOT_EMPTY));
        return this;
    }

    public List<Point> getPoints() throws Exception {
        return PointsAPI.getPointFromRows(getPointsData());
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
                    data.addAll(selectRecordBuilder.get());
                    LOGGER.info(controllerType.asString()+" Query "+selectRecordBuilder.toString());
                }catch (Exception e){
                    LOGGER.info("Exceptio  while getting points for type "+controllerType.asString()+" "+e);
                }
            }
        }
        else {
            if( (criteria.getConditions() != null) && ! criteria.getConditions().isEmpty()){
                selectRecordBuilder.andCriteria(criteria)
                        .limit(limit).offset(offset);
            }
            data = selectRecordBuilder.get();
        }
        return data;
    }

    private GenericSelectRecordBuilder loadBuilder(FacilioControllerType controllerType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(pointModule.getTableName());
        List<FacilioField> fields = new ArrayList<>(this.fields);
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
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getFieldDeviceId(pointModule), String.valueOf(deviceId),NumberOperators.EQUALS));
        }else {
            throw new Exception(" controller id can't be less than 1");
        }
        return this;
    }
}
