package com.facilio.alarms.sensor.context;

import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.context.sensoralarm.SensorEventContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Setter
@Getter
@Log4j
public class SensorRuleContext extends V3Context {

    private static final long serialVersionUID = 1L;
    private static final SensorRuleType[] SENSOR_RULE_TYPES = SensorRuleType.values();

    long sensorModuleId = -1;

    long sensorFieldId = -1;

    String subject;

    List<String> sensorRuleProps;

    JSONObject rulePropInfo;

    FacilioField sensorField;

    FacilioModule sensorModule;

    long assetCategoryId = -1;

    AssetCategoryContext assetCategory;

    SensorRuleType sensorRuleType; //TODO:to del

    NameSpaceContext ns;

    List<SensorRulePropContext> sensorRuleTypes;

    Long recordModuleId;

    Long recordFieldId;

    Boolean status;

    String moduleName;

    HashMap<String, SensorRuleAlarmMeta> alarmMetaMap;

    public static void processNewSensorAlarmMeta(SensorRuleContext sensorRule, ResourceContext resource, SensorRuleType sensorRuleType, SensorEventContext sensorEvent) throws Exception {
        HashMap<String, SensorRuleAlarmMeta> metaMap = sensorRule.getAlarmMetaMap();
        String metaKey = SensorRuleUtil.getSensorAlarmMetaKey(resource.getId(), sensorRuleType.getIndex());
        if (metaMap != null) {
            SensorRuleAlarmMeta alarmMeta = metaMap.get(metaKey);
            if (Objects.isNull(alarmMeta)) {
                metaMap = new HashMap<>();
                metaMap.put(metaKey, SensorRuleUtil.constructNewAlarmMeta(-1, sensorRule, sensorRuleType, resource, false, sensorEvent.getEventMessage()));
            } else if (alarmMeta != null && alarmMeta.isClear()) {
                alarmMeta.setClear(false);
            }
        }
    }

    public FacilioField getSensorField() {
        try {
            if (sensorField == null && sensorFieldId > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                sensorField = modBean.getField(sensorFieldId);
            }
        } catch (Exception e) {
			LOGGER.error("Error in SensorRollUpAlarmContext while fetching reading fieldid : "+sensorFieldId, e);
        }
        return sensorField;
    }

    public void setSensorField(FacilioField sensorField) {
        this.sensorField = sensorField;
    }

    public NameSpaceContext getNs() {
        return ns;
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
        } else {
            this.sensorRuleType = null;
        }
    }

    public SensorRulePropContext constructSensorProp(int sensorRuleType, long ruleId, JSONObject propInfo) {
        SensorRulePropContext sensorRuleProp = new SensorRulePropContext();
        sensorRuleProp.setParentSensorRuleId(ruleId);
        sensorRuleProp.setSensorRuleType(sensorRuleType);
        sensorRuleProp.setRuleValidatorProps(propInfo);
        sensorRuleProp.setStatus(true);
        return sensorRuleProp;
    }

    @Override
    public String toString() {

        String builder = " sensorRule ID : " + getId() +
                ", readingFieldId : " + sensorFieldId +
                ", sensorRuleType : " + sensorRuleType +
                ", moduleId : " + sensorModuleId +
                ", assetCategory : " + assetCategoryId;

        return builder;
    }

}
