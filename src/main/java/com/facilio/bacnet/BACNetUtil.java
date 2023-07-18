package com.facilio.bacnet;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.agentv2.AgentConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BACNetUtil {
    private static final List<Integer> FILTER_INSTANCES = new ArrayList<>();

    static {
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_INPUT.ordinal());//0
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_OUTPUT.ordinal());//1
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_VALUE.ordinal());//2
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_INPUT.ordinal());//3
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_OUTPUT.ordinal());//4
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_VALUE.ordinal());//5
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.CALENDAR.ordinal());//6
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.COMMAND.ordinal());//7
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_INPUT.ordinal());//13
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_OUTPUT.ordinal());
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_VALUE.ordinal());//19
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.ACCUMULATOR.ordinal());//23
        FILTER_INSTANCES.add(BACNetUtil.InstanceType.PULSE_CONVERTER.ordinal());//24
    }

    private static final String FILTER_JOIN = StringUtils.join(FILTER_INSTANCES, ",");

    public static List<Integer> getBacnetFilterInstanceTypes(){
        return FILTER_INSTANCES;
    }

    public static Criteria getBacnetInstanceTypeCriteria() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule bacnetPointModule = moduleBean.getModule(AgentConstants.BACNET_IP_POINT_MODULE);
        Map<String, FacilioField> BACNET_POINT_MAP = bacnetPointModule != null ? FieldFactory.getAsMap(moduleBean.getAllFields(AgentConstants.BACNET_IP_POINT_MODULE)) : FieldFactory.getAsMap(FieldFactory.getBACnetIPPointFields(true));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(BACNET_POINT_MAP.get(AgentConstants.INSTANCE_TYPE),
                BACNetUtil.FILTER_JOIN, NumberOperators.EQUALS));
        return criteria;
    }

    public enum InstanceType implements FacilioIntEnum {
        ANALOG_INPUT(0, "Analog input", false, false),
        ANALOG_OUTPUT(1, "Analog output", true, false),
        ANALOG_VALUE(2, "Analog value", true, false),
        BINARY_INPUT(3, "Binary input", false, false),
        BINARY_OUTPUT(4, "Binary output", true, false),
        BINARY_VALUE(5, "Binary value", true, false),
        CALENDAR(6, "Calendar", false, false),
        COMMAND(7, "Command", false, false),
        DEVICE(8, "Device", false, false),
        EVENT_ENROLLMENT(9, "Event enrollment", false, false),
        FILE(10, "File", false, false),
        GROUP(11, "Group", false, false),
        LOOP(12, "Loop", false, false),
        MULTI_STATE_INPUT(13, "Multi state input", false, true),
        MULTI_STATE_OUTPUT(14, "Multi state output", true, true),
        NOTIFICATION_CLASS(15, "Notification", false, false),
        PROGRAM(16, "Program", false, false),
        SCHEDULE(17, "Schedule", false, false),
        AVERAGING(18, "Average", false, false),
        MULTI_STATE_VALUE(19, "Multi state value", true, true),
        TREND_LOG(20, "Trend log", false, false),
        LIFE_SAFETY_POINT(21, "Life safety point", false, false),
        LIFE_SAFETY_ZONE(22, "Life safety zone", false, false),
        ACCUMULATOR(23, "Accumulator", false, false),
        PULSE_CONVERTER(24, "Pulse converter", false, false),
        EVENT_LOG(25, "Event log", false, false),
        GLOBAL_GROUP(26, "Global group", false, false),
        TREND_LOG_MULTIPLE(27, "Trend log multiple", false, false),
        LOAD_CONTROL(28, "Load control", false, false),
        STRUCTURED_VIEW(29, "Structure view", false, false),
        ACCESS_DOOR(30, "Access door", false, false),
        TIMER(31, "Timer", false, false),
        ACCESS_CREDENTIAL(32, "Access credential", false, false),
        ACCESS_POINT(33, "Access point", false, false),
        ACCESS_RIGHTS(34, "Access rights", false, false),
        ACCESS_USER(35, "Access user", false, false),
        ACCESS_ZONE(36, "Access zone", false, false),
        CREDENTIAL_DATA_INPUT(37, "Credential data input", false, false),
        NETWORK_SECURITY(38, "Network security", false, false),
        BIT_STRING_VALUE(39, "Bit string value", false, false),
        CHARACTER_STRING_VALUE(40, "Character string value", false, false),
        DATE_PATTERN_VALUE(41, "Data pattern value", false, false),
        DATE_VALUE(42, "Data value", false, false),
        DATETIME_PATTERN_VALUE(43, "Datetime pattern value", false, false),
        DATETIME_VALUE(44, "Datetime value", false, false),
        INTEGER_VALUE(45, "Integer value", false, false),
        LARGE_ANALOG_VALUE(46, "Large analog value", false, false),
        OCTET_STRING_VALUE(47, "Octet string value", false, false),
        POSITIVE_INTEGER_VALUE(48, "Positive integer value", false, false),
        TIME_PATTERN_VALUE(49, "Time pattern value", false, false),
        TIME_VALUE(50, "Time value", false, false),
        NOTIFICATION_FORWARDER(51, "Notification forwarder", false, false),
        ALERT_ENROLLMENT(52, "Alert enrollment", false, false),
        CHANNEL(53, "Channel", false, false),
        LIGHTING_OUTPUT(54, "Lighting output", true, false),
        BINARY_LIGHTING_OUTPUT(55, "Binary lighting output", true, false),
        NETWORK_PORT(56, "Network port", false, false),
        ELEVATOR_GROUP(57, "Elevator group", false, false),
        ESCALATOR(58, "Escalator", false, false),
        LIFT(59, "Lift", false, false);

        private final int value;
        private final String name;
        private final boolean writable;
        private final boolean multiState;

        InstanceType(int value, String name, boolean writable, boolean multiState) {
            this.value = value;
            this.name = name;
            this.writable = writable;
            this.multiState = multiState;
        }

        public String getName() {
            return name;
        }

        public boolean isWritable() {
            return writable;
        }

        public boolean isMultiState() {
            return multiState;
        }

        public static InstanceType valueOf(int value) {
            if (value >= 0 && value < values().length) {
                return values()[value];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return value;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
