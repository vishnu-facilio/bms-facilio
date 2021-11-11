package com.facilio.agentv2.device;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.actions.GetPointsAction;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class FieldDeviceApi {

    private static final Logger LOGGER = LogManager.getLogger(FieldDeviceApi.class.getName());

//    public static Device getDevice(long agentId, String identifier) throws Exception {
//        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
//        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
//        if (agentId > 0) {
//            if ((identifier != null) && (!identifier.isEmpty())) {
//                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//                        .table(fieldDeviceModule.getTableName())
//                        .select(fieldMap.values())
//                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.IDENTIFIER), identifier.trim(), StringOperators.IS))
//                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
//                List<Map<String, Object>> result = builder.get();
//                if (result.size() == 1) {
//                    Device device = FieldUtil.getAsBeanFromMap(result.get(0), Device.class);
//                    if (device.getControllerProps().containsKey("type"))
//                        device.setControllerType(Integer.parseInt(device.getControllerProps().get("type").toString()));
//                    return device;
//                } else {
//                    LOGGER.info("Exception, unexpected results, only one row should be selected for device->" + identifier + " agentId->" + agentId + " rowsSelected->" + result.size());
//                }
//            } else {
//                throw new Exception(" device name can't be null or empty");
//            }
//        } else {
//            throw new Exception("agentId can't be less than 1");
//        }
//        return null;
//    }
//
//    public static List<Map<String, Object>> getDevices(List<Long> deviceIds) {
//        try {
//            FacilioContext context = new FacilioContext();
//            context.put(AgentConstants.RECORD_IDS, deviceIds);
//            return getDeviceData(context);
//        } catch (Exception e) {
//            LOGGER.info(" Exception occurred ", e);
//        }
//        return new ArrayList<>();
//    }

    public static Controller getControllers(List<Long> controllerId,Long agentId) throws Exception {
        FacilioModule module = ModuleFactory.getNewControllerModule();
        FacilioModule resourceModule = ModuleFactory.getResourceModule();
        List<FacilioField> fields = new ArrayList<FacilioField>();
        fields.add(FieldFactory.getNameField(resourceModule));
        fields.addAll(FieldFactory.getControllersField());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName()+".ID = "+module.getTableName()+".ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(resourceModule), "NULL", CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getIdCondition(controllerId,module));
                if(agentId != null){
                    builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module),String.valueOf(agentId),NumberOperators.EQUALS));
                }

        Map<String, Object> props = builder.fetchFirst();
        if(props != null && !props.isEmpty()) {
        	FacilioControllerType ctype = FacilioControllerType.valueOf((Integer)props.get(AgentConstants.CONTROLLER_TYPE));
            Controller controller = ControllerApiV2.makeControllerFromMap(getControllerProps(ctype, controllerId),ctype);
            controller.setAgentId((long)props.get(AgentConstants.AGENT_ID));
            controller.setName((String)props.get(AgentConstants.NAME));
            controller.setId((Long)props.get(AgentConstants.ID));
            return controller;
        }
        return null;
    }

    private static Map<String,Object> getControllerProps(FacilioControllerType type,List<Long> controllerId) throws Exception{
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	String moduleName = ControllerApiV2.getControllerModuleName(type);
    	FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = new ArrayList<FacilioField>();
    	 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                 .table(module.getTableName())
                 .andCondition(CriteriaAPI.getIdCondition(controllerId,module));
        if(type != null && type.asInt() == FacilioControllerType.MODBUS_RTU.asInt()){
            fields.addAll(FieldFactory.getRtuNetworkFields());
            fields.addAll(modBean.getModuleFields(module.getName()));
            builder.select(fields).innerJoin(ModuleFactory.getRtuNetworkModule().getTableName())
                    .on(module.getTableName() + ".NETWORK_ID = " + ModuleFactory.getRtuNetworkModule().getTableName() + ".ID");
        }else {
            builder.select(modBean.getModuleFields(moduleName));
        }
		return  builder.fetchFirst();
    }

    public static boolean discoverPoint(List<Long> controllerId) throws Exception {
        try {
            Controller controller = getControllers(controllerId,null);
            Objects.requireNonNull(controller,"Controller doesn't exist");
            ControllerMessenger.discoverPoints(controller);
            return true;
        }catch (Exception e){
            LOGGER.error("Exception while discoverPoints -> ", e);
           throw e;
        }
    }


//    public static Map<String, Object> getDeviceData(Long deviceId) {
//        List<Map<String, Object>> devices = getDevices(Collections.singletonList(deviceId));
//        if ((devices != null) && (!devices.isEmpty())) {
//            return devices.get(0);
//        }
//        return new HashMap<>();
//    }

//    public static Device getDevice(Long deviceId) {
//        Map<String, Object> deviceData = getDeviceData(deviceId);
//        if (!deviceData.isEmpty()) {
//            return FieldUtil.getAsBeanFromMap(deviceData, Device.class);
//        }
//        return null;
//    }

//    public static List<Map<String, Object>> getDeviceFilterData(Long agentId) throws Exception {
//        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
//        List<FacilioField> deviceFilterFields = new ArrayList<>();
//        deviceFilterFields.add(FieldFactory.getIdField(fieldDeviceModule));
//        deviceFilterFields.add(FieldFactory.getNameField(fieldDeviceModule));
//        deviceFilterFields.add(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule));
//        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//                .table(fieldDeviceModule.getTableName())
//                .select(deviceFilterFields)
//                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(fieldDeviceModule), String.valueOf(agentId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(fieldDeviceModule), "NULL", CommonOperators.IS_EMPTY));
//
//        List<Map<String, Object>> props = selectRecordBuilder.get();
//        boolean isMiscType=false;
//		for (Map<String, Object> prop : props) {
//			Integer val = (Integer) prop.get("controllerType");
//			if (val != null && val == 0) {
//				isMiscType = true;
//				break;
//			}
//		}
//        if(isMiscType && GetPointsAction.isVirtualPointExist(agentId)) {
//        	Map<String,Object> prop = new HashMap<String, Object>();
//        	prop.put("id", 0L);
//        	prop.put("controllerType",0);
//        	prop.put("name", "Logical");
//        	props.add(prop);
//        }
//        return props;
//    }


//    public static List<Map<String,Object>> getModbusDeviceFilter(Long agentId) throws Exception {
//        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
//        List<FacilioField> deviceFilterFields = new ArrayList<>();
//        deviceFilterFields.add(FieldFactory.getIdField(fieldDeviceModule));
//        deviceFilterFields.add(FieldFactory.getNameField(fieldDeviceModule));
//        deviceFilterFields.add(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule));
//        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//                .table(fieldDeviceModule.getTableName())
//                .select(deviceFilterFields)
//                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(fieldDeviceModule), String.valueOf(agentId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(fieldDeviceModule), "NULL", CommonOperators.IS_EMPTY))
//                .andCondition(CriteriaAPI.getCondition(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule), String.valueOf(FacilioControllerType.MODBUS_IP.asInt()),NumberOperators.EQUALS));
//        return selectRecordBuilder.get();
//    }

}
