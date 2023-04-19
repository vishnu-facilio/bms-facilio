package com.facilio.agentv2.E2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class E2PointContext extends Point implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(E2PointContext.class.getName());

    private String groupName;
    private String propName;
    private long parentId;
    private ParentType parentType;
    private long appId;
    private long appIndex;
    private int propStatus;
    private int propNumber;
    private PropMode propMode;
    private PropType propType;
    private PropDataType propDataType;

    public int getPropMode() {
        if (propMode != null) {
            return propMode.getIndex();
        }
        return -1;
    }
    public PropMode getPropModeEnum(){
        return propMode;
    }

    public void setPropMode(int propMode) {
        this.propMode = PropMode.valueOf(propMode);
    }

    public int getPropType() {
        if(propType != null){
            return propType.getIndex();
        }
        return -1;
    }
    public PropType getPropTypeEnum(){
        return propType;
    }

    public void setPropType(int propType) {
        this.propType = PropType.valueOf(propType);
    }

    public int getPropDataType() {
        if(propDataType != null) {
            return propDataType.getIndex();
        }
        return -1;
    }
    public PropDataType getPropDataTypeEnum() {
        return propDataType;
    }

    public void setPropDataType(int propDataType) {
        this.propDataType = PropDataType.valueOf(propDataType);
    }

    public int getParentType() {
        if(parentType != null) {
            return parentType.getIndex();
        }
        return -1;
    }
    public ParentType getParentTypeEnum() {
        return parentType;
    }

    public void setParentType(int parentType) {
        this.parentType = ParentType.valueOf(parentType);
    }

    @Deprecated
    public E2PointContext() {
    }


    public static Point getPointFromMap( Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.GROUP_NAME, pointMap) && containsValueCheck(AgentConstants.PROP_NAME, pointMap)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(pointMap);
            E2PointContext point = FieldUtil.getAsBeanFromJson(jsonObject, E2PointContext.class);
            return point;
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.GROUP_NAME + "," + AgentConstants.PROP_NAME + " might be missing form input params -> " + pointMap);
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.E2;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject e2PointJSON = new JSONObject();
        e2PointJSON.put(AgentConstants.ID,getId());
        e2PointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        e2PointJSON.put(AgentConstants.GROUP_NAME, groupName);
        e2PointJSON.put(AgentConstants.PROP_NAME, propName);
        e2PointJSON.put(AgentConstants.PARENT_ID, parentId);
        e2PointJSON.put(AgentConstants.PARENT_TYPE, getParentType());
        e2PointJSON.put(AgentConstants.APP_ID, appId);
        e2PointJSON.put(AgentConstants.APP_INDEX, appIndex);
        e2PointJSON.put(AgentConstants.PROP_STATUS, propStatus);
        e2PointJSON.put(AgentConstants.PROP_NUMBER, propNumber);
        e2PointJSON.put(AgentConstants.PROP_MODE, getPropMode());
        e2PointJSON.put(AgentConstants.PROP_TYPE, getPropType());
        e2PointJSON.put(AgentConstants.PROP_DATA_TYPE, getPropDataType());

        return e2PointJSON;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public enum PropMode implements FacilioIntEnum {
        READ(0,"Read"),
        WRITE(1,"Write"),
        READ_OR_WRITE(2,"Read/write");

        private String name;
        private int value;
        PropMode(int value,String name) {
            this.value = value;
            this.name = name;
        }
        public static PropMode valueOf(int value) {
            if (value >= 0 && value < values().length) {
                return values()[value];
            }
            return null;
        }
        @Override
        public Integer getIndex(){return value;}

        @Override
        public String getValue(){return name;}
    }

    public enum PropType implements FacilioIntEnum {
        INPUT(0,"Input"),
        OUTPUT(8,"Output"),
        SETTING(10,"Setting")
        ;

        private String name;
        private int value;

        PropType(int value,String name) {
            this.name = name;
            this.value = value;
        }
        public static PropType valueOf(int value) {
            return TYPE_MAP.get(value);
        }

        private static final Map<Integer, PropType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

        private static Map<Integer, PropType> initTypeMap() {
            Map<Integer, PropType> typeMap = new HashMap<>();
            for (PropType type : values()) {
                typeMap.put(type.getIndex(), type);
            }
            return typeMap;
        }

        @Override
        public Integer getIndex(){return value;}

        @Override
        public String getValue(){return name;}
    }

    public enum PropDataType implements FacilioIntEnum {
        ANALOG(0,"Analog"),
        DIGITAL(1,"Digital");

        private String name;
        private int value;

        PropDataType(int value,String name) {
            this.value = value;
            this.name = name;
        }
        public static PropDataType valueOf(int value) {
            if (value >= 0 && value < values().length) {
                return values()[value];
            }
            return null;
        }
        @Override
        public Integer getIndex(){return value;}

        @Override
        public String getValue(){return name;}
    }

    public enum ParentType implements FacilioIntEnum {
        PHYSICAL_AI(1, "Physical AI"),
        PHYSICAL_DI(2, "Physical DI"),
        XM669K_DEVICE_CELL(45, "XM669K device cell"),
        EMERSON_ENERGY_METER(59, "Emerson Energy Meter"),
        GLOBAL_DATA(91, "Global Data"),
        COMMAND(92, "Command"),
        SENSOR_CONTROL_AV(94, "Sensor Control AV"),
        LOOP_SEQUENCE_CTRL(95, "Loop/Sequence Ctrl"),
        ARTC_RTU_UNIT(142, "ARTC/RTU Unit"),
        MULTIFLEX_RCB(159, "MultiFlex RCB"),
        XM_CIRCUIT(399, "XM Circuit"),
        PHYSICAL_AO(32, "Physical AO"),
        PHYSICAL_DO(33, "Physical DO"),
        XM679K_42(540, "XM679K_42"),
        HARDWARE_PULSE_OUT(36, "Hardware pulse out"),
        IPRODAC(54, "iProDAC"),
        ANALOG_COMBINERS(65, "Analog Combiners"),
        DIGITAL_COMBINERS(66, "Digital Combiners"),
        FLEXIBLE_COMBINERS(67, "Flexible Combiners"),
        EM21V72D(70, "EM21V72D"),
        LOGGING_GROUPS(73, "Logging Groups"),
        TIME_SCHEDULES(80, "Time Schedules"),
        HOLIDAYS(81, "Holidays"),
        DISCUS(82, "DISCUS"),
        HEAT_COOL_CONTROL(85, "Heat/Cool Control"),
        DEMAND_CONTROL(86, "Demand Control"),
        PULSE_ACCUMULATION(87, "Pulse Accumulation"),
        SENSOR_CONTROL_DV(96, "Sensor Control DV"),
        CONVERSION_CELL(97, "Conversion Cell"),
        LIGHTING_CONTROL(98, "Lighting Control"),
        SUCTION_CONTROL(128, "Suction Control"),
        CONDENSER_CONTROL(129, "Condenser Control"),
        CASE_CONTROL_CIRCUIT(130, "Case control circuit"),
        STANDARD_CIRCUIT(131, "Standard circuit"),
        ANTI_SWEAT_CONTROL(132, "Anti-Sweat Control"),
        AIR_HANDLERS(134, "Air Handlers (AHUs)"),
        HVAC_ZONE(135, "HVAC ZONE"),
        DEMAND_CONTROL_2(136, "Demand Control"),
        RLDS(342, "RLDS"),
        ENHANCED_SUCTION(162, "Enhanced Suction"),
        COMMAND_2(171, "Command"),
        CT_DRIVER(173, "CT DRIVER"),
        CT_M400_VFD(175, "CT M400 VFD"),
        EC2_371_REV23(192, "EC2-371 REV23"),
        XEV22D_15(216, "XEV22D_15");

        private int value;
        private String name;

        ParentType(int value, String name) {
            this.value = value;
            this.name = name;
        }
        public static ParentType valueOf(int value) {
            return TYPE_MAP.get(value);
        }

        private static final Map<Integer, ParentType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

        private static Map<Integer, ParentType> initTypeMap() {
            Map<Integer, ParentType> typeMap = new HashMap<>();
            for (ParentType type : values()) {
                typeMap.put(type.getIndex(), type);
            }
            return typeMap;
        }
        @Override
        public Integer getIndex(){return value;}

        @Override
        public String getValue(){return name;}
    }
}
