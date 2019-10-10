package com.facilio.agentnew.point;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import com.facilio.agentnew.bacnet.BacnetIpPoint;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.modbusrtu.ModbusRtuPoint;
import com.facilio.agentnew.modbustcp.ModbusTcpPoint;
import com.facilio.agentnew.niagara.NiagaraPoint;
import com.facilio.agentnew.opcua.OpcUaPoint;
import com.facilio.agentnew.opcxmlda.OpcXmlDaPoint;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointsUtil
{

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


    public boolean processPoints(JSONObject payload, Controller controller){

        LOGGER.info(" processing points ");
        String identifier;
        try {
            identifier = controller.makeIdentifier();
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
            return false;
        }
        if(containsValueCheck(AgentConstants.DATA,payload)){
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            List<Point> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.DEVICE_NAME,identifier);
                pointJSON.put(AgentConstants.CONTROLLER_ID,controller.getId());
                try {
                    Point point = getPointFromJSON(pointJSON);
                    if(point != null){
                        points.add(point);
                        LOGGER.info(" point generated " + point.toJSON());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info(" points processed "+points.size());
            for (Point point : points) {
                LOGGER.info("point to add to db point -> " + point.toJSON());
                LOGGER.info("point add status -> "+addPoint(point));;
            }
        }
        return false;
    }

        private Point getPointFromJSON(Map<String, Object> payload) throws Exception {
        LOGGER.info("iamcvijay point to json -> "+payload);
        if(containsValueCheck(AgentConstants.POINT_TYPE,payload)) {
            FacilioControllerType controllerType;
            controllerType = FacilioControllerType.valueOf(JsonUtil.getInt(payload.get(AgentConstants.POINT_TYPE)));
            LOGGER.info(" controller type for point is " + controllerType.asString());
            if(payload.containsKey(AgentConstants.PSEUDO) ){
                Object object;
                boolean pseudo;
                if(JsonUtil.getBoolean(payload.get(AgentConstants.PSEUDO))){
                    controllerType = FacilioControllerType.MISC;
                }
            }
            switch (controllerType){
                case MODBUS_RTU:
                    return ModbusRtuPoint.getPointFromMap(agentId,controllerId,payload);
                case MODBUS_IP:
                    return ModbusTcpPoint.getPointFromMap(agentId,controllerId,payload);
                case OPC_UA:
                    return OpcUaPoint.getPointFromMap(agentId,controllerId,payload);
                case OPC_XML_DA:
                    return OpcXmlDaPoint.getPointFromMap(agentId,controllerId,payload);
                case BACNET_IP:
                    return BacnetIpPoint.getPointFromMap(agentId,controllerId,payload);
                case NIAGARA:
                    return NiagaraPoint.getPointFromMap(agentId,controllerId,payload);
                case MISC:
                    return MiscPoint.getPointFromMap(agentId,controllerId,payload);
                default:
                    throw new Exception("no implementation for "+controllerType.asString());
            }
        }
        throw new Exception("Point Type missing not defined -> "+payload);
    }


    public List<Point> getAllPoints(){
        List<Point> allPoints = new ArrayList<>();
        LOGGER.info(" controller types are " + FacilioControllerType.values());
        for (FacilioControllerType type : FacilioControllerType.values()) {
            LOGGER.info("getting points for " + type.asString());
            List<Point> pointList = getAllPoints(type);
            LOGGER.info("points obtained is " + pointList);
            if( (pointList != null) && ( ! pointList.isEmpty()) ){
                LOGGER.info("adding it to all points ");
                    allPoints.addAll(pointList);
            }
            LOGGER.info(" all points now is " + allPoints);
        }
        LOGGER.info("returning all points ");
        return allPoints;
    }

    private List<Point> getAllPoints(FacilioControllerType controllerType){
        List<Point> pointList = new ArrayList<>();
        ModuleCRUDBean bean = null;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.POINTS_TABLE);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getPointFields()).get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId), NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
            List<FacilioField> fields = FieldFactory.getPointFields();
            switch (controllerType){
                case BACNET_MSTP:
                    throw  new Exception("Implementation for BACNET_MSTP, not found ");
                case MODBUS_RTU:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusRtuPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getModbusRtuPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getModbusRtuPointFields());
                    break;
                case OPC_UA:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCUAPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getOPCUAPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getOPCUAPointFields());
                    break;
                case OPC_XML_DA:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCXmlDAPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getOPCXmlDAPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getOPCXmlDAPointFields());
                    break;
                case BACNET_IP:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getBACnetIPPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getBACnetIPPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getBACnetIPPointFields());
                    break;
                case MODBUS_IP:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusTcpPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getModbusTcpPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getModbusTcpPointFields());
                    break;
                case MISC:
                    LOGGER.info("adding misc inner join ");
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getMiscPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getMiscPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getMiscPointFields());
                    break;
                case NIAGARA:
                    context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getNiagaraPointModule().getTableName());
                    context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getNiagaraPointModule().getTableName()+".ID");
                    fields.addAll(FieldFactory.getNiagaraPointFields());
                    break;
                default:
                    LOGGER.info(" No implementation for " + controllerType.asString());
                    return new ArrayList<>();
            }
            context.put(FacilioConstants.ContextNames.FIELDS,fields);
            List<Map<String, Object>> points = bean.getRows(context);
            for (Map<String, Object> point : points) {
                pointList.add(getPointFromJSON(point));
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return pointList;
    }


    private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }




    private static boolean addPoint(Point point) {
        FacilioChain addPointChain = FacilioChainFactory.getAddPointChain();
        if (point != null) {
            Map<String, Object> toInsertMap = point.getPointJSON();
            LOGGER.info(" point as map " + toInsertMap);
            LOGGER.info(" faciliochainFActory.getContext() " + addPointChain.getContext());
            // if point.isPseudo -> point.setControllerType(Misc);
            addPointChain.getContext().put(FacilioConstants.ContextNames.RECORD,point);
            try {
                return addPointChain.execute();
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return false;
    }





}
