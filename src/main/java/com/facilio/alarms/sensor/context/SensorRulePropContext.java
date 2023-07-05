package com.facilio.alarms.sensor.context;

import com.facilio.alarms.sensor.SensorRuleType;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;


@Setter
@Getter
public class SensorRulePropContext {

    private static final SensorRuleType[] SENSOR_RULE_TYPES = SensorRuleType.values();
    Long id;
    SensorRuleType sensorRuleType;
    long parentSensorRuleId=-1;
    HashMap<Long, SensorRuleAlarmMeta> alarmMetaMap;
    Boolean status;
    JSONObject ruleValidatorProps;
    String subject;

    boolean isInterval;

    public void setIsInterval(){
        if(ruleValidatorProps!=null){
            this.isInterval= ruleValidatorProps.containsKey("timeInterval");
        }
    }

    public boolean getIsInterval(){
        return this.isInterval;
    }

    public JSONObject  getRuleValidatorProps(){
        return ruleValidatorProps;
    }
    public void setRuleValidatorProps(JSONObject jsonStr){
        this.ruleValidatorProps=jsonStr;
        this.setIsInterval();
    }

    public SensorRuleType getSensorRuleTypeEnum() {
        return sensorRuleType;
    }

    public int getSensorRuleType() {
        if (sensorRuleType != null) {
            return sensorRuleType.getIndex();
        }
        return -1;
    }
    public void setSensorRuleType(SensorRuleType sensorRuleType) {
        this.sensorRuleType = sensorRuleType;
    }

    public void setSensorRuleType(int sensorRuleType) {
        if (sensorRuleType > 0) {
            this.sensorRuleType = SENSOR_RULE_TYPES[sensorRuleType - 1];
            this.setSubject(this.sensorRuleType.getValueString());
        } else {
            this.sensorRuleType = null;
        }
    }
    public String getRulePropStr() {
        if(ruleValidatorProps != null) {
            return ruleValidatorProps.toJSONString();
        }
        return null;
    }
    public void setRulePropStr(String jsonStr) throws ParseException {
        if(jsonStr != null) {
            JSONParser parser = new JSONParser();
            ruleValidatorProps = (JSONObject) parser.parse(jsonStr);
        }
    }
}
