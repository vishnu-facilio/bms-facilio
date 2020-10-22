package com.facilio.agentv2.point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class AddPointCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddPointCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Point point = (Point) context.get(FacilioConstants.ContextNames.RECORD);
        point.setCreatedTime(System.currentTimeMillis());
        JSONObject toInsertMap = point.getPointJSON();
        addPoint(ModuleFactory.getPointModule(), FieldFactory.getPointFields(),toInsertMap);
        if(toInsertMap.containsKey(AgentConstants.ID)){
            Long pointId = (Long) toInsertMap.get(AgentConstants.ID);
            LOGGER.info(" point inserted and pointId is " + pointId);
            if(pointId > 0){
                point.setId(pointId);
                long childPointId = addChildPoint(point);
                LOGGER.info(" child point id " + childPointId);
                return !(childPointId > 0);
            }
        }
        return false;
    }

    private long addChildPoint(Point point) throws Exception {
        JSONObject toInsertMap = point.getChildJSON();
        return addPoint(PointsAPI.getPointModule(point.getControllerType()), PointsAPI.getChildPointFields(point.getControllerType()), toInsertMap);
        /*switch (point.getControllerType()){
            case MODBUS_IP:
                return addPoint(ModuleFactory.getModbusTcpPointModule(),FieldFactory.getModbusTcpPointFields(),toInsertMap) > 0;
            case NIAGARA:
                return addPoint(ModuleFactory.getNiagaraPointModule(),FieldFactory.getNiagaraPointFields(),toInsertMap) > 0;
            case BACNET_IP:
                return addPoint(ModuleFactory.getBACnetIPPointModule(),FieldFactory.getBACnetIPPointFields(),toInsertMap) > 0;
            case OPC_XML_DA:
                return addPoint(ModuleFactory.getOPCXmlDAPointModule(),FieldFactory.getOPCXmlDAPointFields(),toInsertMap) > 0;
            case OPC_UA:
                return addPoint(ModuleFactory.getOPCUAPointModule(),FieldFactory.getOPCUAPointFields(),toInsertMap) > 0;
            case MODBUS_RTU:
                return addPoint(ModuleFactory.getModbusRtuPointModule(),FieldFactory.getModbusRtuPointFields(),toInsertMap) > 0;
            case KNX:
                throw new Exception(" KNK point not implemented");
            case LON_WORKS:
                throw new Exception(" LON_WORKS point not implemented");
            case MISC:
                return addPoint(ModuleFactory.getMiscPointModule(), FieldFactory.getMiscPointFields(),toInsertMap) > 0;
            case BACNET_MSTP:
                throw new Exception(" BACNET_MSTP point not implemented");
        }
        return false;*/
    }

    private long addPoint( FacilioModule module, List<FacilioField> fields,Map<String,Object> toInsertMap)throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(module.getTableName()).fields(fields);
        return builder.insert(toInsertMap);
    }
}
