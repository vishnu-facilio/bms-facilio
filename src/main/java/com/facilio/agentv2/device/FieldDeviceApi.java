package com.facilio.agentv2.device;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class FieldDeviceApi
{
    private static final FacilioModule MODULE = ModuleFactory.getFieldDeviceModule();
    public static final  Map<String, FacilioField> FIELD_MAP = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());

    private static final Logger LOGGER = LogManager.getLogger(FieldDeviceApi.class.getName());


    public static int deleteDevices(Collection<Long> deviceId) throws SQLException {
        LOGGER.info(" deleting devices ->"+deviceId);
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(MODULE.getTableName())
                .fields(FieldFactory.getFieldDeviceFields())
                .andCondition(CriteriaAPI.getIdCondition(deviceId, ModuleFactory.getFieldDeviceModule()));
        Map<String,Object> toUpdateMap = new HashMap<>();
        toUpdateMap.put(AgentConstants.DELETED_TIME,System.currentTimeMillis());
       return builder.update(toUpdateMap);
    }

    public static void addFieldDevices(List<Device> devices) throws Exception{
        try {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(MODULE.getTableName())
                    .fields(FieldFactory.getFieldDeviceFields())
                    .addRecords(FieldUtil.getAsMapList(devices, Device.class));
            builder.save();
            return;
        } catch (Exception e) {
            LOGGER.info(" Bulk insert failed ");
            for (Device device : devices) {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(MODULE.getTableName())
                        .fields(FieldFactory.getFieldDeviceFields());
                if (builder.insert(FieldUtil.getAsProperties(device)) < 0) {
                    LOGGER.info(" failed to insert device ->" + FieldUtil.getAsJSON(device));
                }
            }
        }
        //try multiple insert if bulk fails
    }

    public static Device getDevice(long agentId, String identifier) throws Exception {
        if (agentId > 0) {
            if ((identifier != null) && (!identifier.isEmpty())) {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(MODULE.getTableName())
                        .select(FIELD_MAP.values())
                        .andCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.IDENTIFIER),identifier.trim(), StringOperators.IS))
                        .andCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                List<Map<String, Object>> result = builder.get();
                LOGGER.info(" query "+builder.toString());
                if (result.size() == 1) {
                    Device device = FieldUtil.getAsBeanFromMap(result.get(0), Device.class);
                    if (device.getControllerProps().containsKey("type"))
                        device.setControllerType(Integer.parseInt(device.getControllerProps().get("type").toString()));
                    return device;
                } else {
                    LOGGER.info("Exception, unexpected results, only one row should be selected for device->" + identifier + " agentId->" + agentId + " rowsSelected->" + result.size());
                }
            } else {
                throw new Exception(" device name can't be null or empty");
            }
        } else {
            throw new Exception("agentId can't be less than 1");
        }
        return null;
    }

    /**
     *
     * @param agentId can be null
     * @param ids null to get all devices.
     * @return
     */
    public static List<Map<String, Object>> getDevices(Long agentId, List<Long> ids)  {
        LOGGER.info(" getting devices for "+agentId+" ids "+ids);
        try {
           GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                   .table(MODULE.getTableName())
                   .select(FieldFactory.getFieldDeviceFields());
           Criteria criteria = new Criteria();
           Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
           criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME),"NULL", CommonOperators.IS_EMPTY));
           if( (agentId != null) && (agentId > 0) ){
               criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID),String.valueOf(agentId),NumberOperators.EQUALS));
           }
           if( (ids != null) && ( ! ids.isEmpty() ) ){
               criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), StringUtils.join(ids, ","), NumberOperators.EQUALS));
           }
           List<Map<String, Object>> rows = builder.andCriteria(criteria).get();
           LOGGER.info("  query -> "+builder.toString());
           LOGGER.info(" got devices "+rows);
           return rows;
       }catch (Exception e){
            LOGGER.info(" Exception occurred ",e);
        }
        return new ArrayList<>();
    }

    public static boolean discoverPoints(List<Long> ids){
        Device device;
        Controller controller;
        LOGGER.info(" in discover points ");
        for (Map<String, Object> deviceMap : getDevices(null, ids)) {
            try{
                device = FieldUtil.getAsBeanFromMap(deviceMap, Device.class);
                controller = ControllerUtilV2.getControllerFromJSON( device.getControllerProps());
                if (controller == null) {
                    throw new Exception(" controller cant be created ");
                }
                controller.setAgentId(device.getAgentId());
                LOGGER.info(" controller formed is ->" + controller.getChildJSON());
                ControllerMessenger.discoverPoints(controller);
                return true;
            } catch (Exception e) {
                LOGGER.info("Exception while making Device bean from json -> " + deviceMap + "  ", e);
                return false;
            }
        }
        return false;
    }


    public static long getAgentDeviceCount(long agentId){
        return getDeviceCount(agentId,null);
    }
    public static  long getTypeDeviceCount(FacilioControllerType type){
        return getDeviceCount(-1,type);
    }

    public static  long getTypeDeviceCount(long agentId,FacilioControllerType type){
        return getDeviceCount(agentId,type);
    }

    private static long getDeviceCount(long agentId, FacilioControllerType type) {
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(MODULE.getTableName())
                    .select(new HashSet<>())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FIELD_MAP.get(AgentConstants.ID));
            if(agentId>0){
                builder.andCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.AGENT_ID), String.valueOf(agentId),NumberOperators.EQUALS));
            }
            if((type != null)){
                builder.andCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.CONTROLLER_TYPE), String.valueOf(type.asInt()),NumberOperators.EQUALS));
            }
            return (long) builder.get().get(0).get(AgentConstants.ID);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting FieldDevices count",e);
        }
        return -1;
    }

    public static long getDeviceCount() {
        return getDeviceCount(-1,null);
    }

    public static Map<String,Object> getDeviceData(Long deviceId) {
        List<Map<String, Object>> devices = getDevices(null, Collections.singletonList(deviceId));
        if((devices != null)&&( ! devices.isEmpty())){
            return devices.get(0);
        }
        return new HashMap<>();
    }
    public static Device getDevice(Long deviceId){
        Map<String,Object> deviceData = getDeviceData(deviceId);
        if(! deviceData.isEmpty()){
            return FieldUtil.getAsBeanFromMap(deviceData,Device.class);
        }
        return null;
    }
}
