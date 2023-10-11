package com.facilio.agentv2.point;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.modbustcp.ModbusUtils;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.AckUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PointsUtil {
    private static final Logger LOGGER = LogManager.getLogger(PointsUtil.class.getName());
    private long agentId;
    private long controllerId;

    public PointsUtil(long agentId, long controllerId) {
        this.agentId = agentId;
        this.controllerId = controllerId;
    }

    public long getAgentId() {
        return agentId;
    }


    public long getControllerId() {
        return controllerId;
    }

    public static boolean processPoints(JSONObject payload, Controller controller, FacilioAgent agent) throws Exception {
        LOGGER.info("Processing points for controller " + controller.getName());

        IotMessage iotMessage = IotMessageApiV2.getIotMessage(AckUtil.getMessageIdFromPayload(payload));

        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            long incomingCount = pointsJSON.size();
            if (incomingCount == 0) {
                throw new Exception("PointJSON can't be empty");
            }


            //getting points name
            List<String> pointName = (List<String>) pointsJSON.stream().map(x -> ((JSONObject) x).get(AgentConstants.NAME).toString()).collect(Collectors.toList());

            //getting existing points name from DB
            List<String> existingPoints = PointsAPI.getPointsFromDb(pointName, controller).stream()
                    .map(name -> name.get(AgentConstants.NAME).toString())
                    .collect(Collectors.toList());

            LOGGER.info("Existing Points count : " + existingPoints.size());
            LOGGER.info("New Points count : " + (pointName.size() - existingPoints.size()));

            List<Map<String, Object>> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.POINT_TYPE, controller.getControllerType());
                pointJSON.put(AgentConstants.CONTROLLER_ID, controller.getId());
                try {
                    Object pName = pointJSON.get(AgentConstants.NAME);
                    if (!existingPoints.contains(pName)) {
                        Point point = PointsAPI.getPointFromJSON(pointJSON);
                        if (!pointJSON.containsKey(AgentConstants.DISPLAY_NAME) && pointJSON.containsKey(AgentConstants.NAME)) {
                            point.setDisplayName(pointJSON.get(AgentConstants.NAME).toString());
                        }
                        point.setCreatedTime(System.currentTimeMillis());
                        setPointWritable(pointJSON, point);
                        if (point != null) {
                            point.setControllerId(controller.getId());
                            point.setAgentId(controller.getAgentId());
                            point.setDeviceName(controller.getName());
                            int agentType = agent.getAgentType();
                            if (iotMessage.getCommand() == FacilioCommand.ADD_POINTS.asInt() || agentType == AgentType.CUSTOM.getKey() || agentType == AgentType.REST.getKey() || agentType == AgentType.CLOUD.getKey() || agentType == AgentType.MQTT.getKey()) {
                                point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                            }
                            if (controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt() ||
                                    controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt() ||
                                    (controller.getControllerType() == FacilioControllerType.RDM.asInt() && ((RdmControllerContext) controller).getIsTdb())) {
                                if (agentType == AgentType.FACILIO.getKey() || agent.getAgentTypeEnum().isAgentService()) {
                                    point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                                }
                            }
                            if (pointJSON.containsKey(AgentConstants.STATE_TEXT_ENUMS)) {
                                JSONObject stateTextEnums = (JSONObject) pointJSON.get(AgentConstants.STATE_TEXT_ENUMS);
                                point.setStates(stateTextEnums);
                            }
                            Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());

                            points.add(pointMap);

                        }
                    } else {
                        LOGGER.info("Point already exists : " + pName);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while getting point", e);
                }
            }

            if (points.size() == 0) {
                throw new Exception("Points to add can't be empty");
            }
            FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.CONTROLLER, controller);
            context.put(AgentConstants.AGENT, agent);
            context.put(AgentConstants.POINTS, points);
            addPointsChain.setContext(context);
            addPointsChain.execute();
        } else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> " + payload);
        }
        return true;
    }

    private static void setPointWritable(JSONObject pointJSON, Point point) {

        if (point.getControllerType() == FacilioControllerType.BACNET_IP) {
            BacnetIpPointContext bacnetIpPoint = (BacnetIpPointContext) point;
            if (BACNetUtil.InstanceType.valueOf(bacnetIpPoint.getInstanceType()).isWritable()) {
                point.setWritable(true);
                point.setAgentWritable(true);
            }
        }
        if (point.getControllerType() == FacilioControllerType.MODBUS_IP) {
            ModbusTcpPointContext modbusTcpPoint = (ModbusTcpPointContext) point;
            if (ModbusUtils.RegisterType.valueOf(Math.toIntExact(modbusTcpPoint.getRegisterType())).isWritable()) {
                point.setWritable(true);
                point.setAgentWritable(true);
            }
        }
        // TODO Auto-generated method stub
        if (pointJSON.containsKey(AgentConstants.WRITABLE)) {
            Boolean value = Boolean.parseBoolean(pointJSON.get(AgentConstants.WRITABLE).toString());
            if (value != null && value) {
                point.setAgentWritable(value);
            } else {
                point.setAgentWritable(false);
            }

        }
    }

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    //BULK INSERT
    public static void addPoints(Controller controller, List<Map<String, Object>> points) throws Exception {
        FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
        addPoints(controllerType, points);
    }

    public static void addPoints(FacilioControllerType controllerType, List<Map<String, Object>> points) throws Exception {
        FacilioModule pointModule = AgentConstants.getPointModule();
        List<FacilioField> fields = PointsAPI.getChildPointFields(controllerType);
        if (pointModule == null) {
            DBUtil.insertValuesWithJoin(PointsAPI.getPointModule(controllerType), fields, FieldUtil.getAsMapList(points, PointsAPI.getPointType(controllerType)));
        } else {
            FacilioModule childPointModule = PointsAPI.getPointModule(controllerType);
            InsertRecordBuilder builder = new InsertRecordBuilder<>()
                    .table(childPointModule.getTableName())
                    .fields(fields)
                    .module(childPointModule)
                    .addRecordProps(points);
            builder.save();

        }
    }

    public static ReadingDataMeta getRDM(Point point) {
        ReadingDataMeta meta = new ReadingDataMeta();
        meta.setResourceId(point.getResourceId());
        meta.setFieldId(point.getFieldId());

        meta.setInputType(ReadingDataMeta.ReadingInputType.CONTROLLER_MAPPED);
        if (point.isWritable()) {
            meta.setReadingType(ReadingDataMeta.ReadingType.WRITE);
        }
        meta.setValue("-1");
        return meta;
    }

    public static Pair<Long, Long> getCategoryAndParentId(long parentFieldId, String fieldValue) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = getAssetFields(module);
        V3Context parent = getParentUsingFieldId(module, fields, parentFieldId, fieldValue);
        if (parent == null) {
            LOGGER.info("Auto Commission :: Parent is not found with field value of " + fieldValue + ", field id " + parentFieldId);
            return null;
        }
        V3AssetContext assetContext = (V3AssetContext) parent;
        Pair<Long, Long> pair = new ImmutablePair<>(assetContext.getCategory().getId(), assetContext.getId());
        return pair;
    }

    private static V3Context getParentUsingFieldId(FacilioModule module, List<FacilioField> fields, long fieldId, String fieldValue) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField(fieldId, module.getModuleId());
        Class beanClass = getBeanClass(module);

        SelectRecordsBuilder<V3Context> selectBuilder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .beanClass(beanClass)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(field, fieldValue,  StringOperators.IS));

        List<V3Context> v3Contexts = selectBuilder.get();
        if(v3Contexts != null && !v3Contexts.isEmpty()) {
            return v3Contexts.get(0);
        }
        return null;
    }

    private static Class getBeanClass(FacilioModule module) throws Exception {
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        return beanClass;
    }

    private static List<FacilioField> getAssetFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("category", "CATEGORY", module, FieldType.LOOKUP));
        fields.add(FieldFactory.getIdField(module));
        return fields;
    }
}
