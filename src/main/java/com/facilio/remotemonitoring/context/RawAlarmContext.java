package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RawAlarmContext extends V3Context {
    private V3ClientContext client;
    private String message;
    private AlarmCategoryContext alarmCategory;
    private AlarmTypeContext alarmType;
    private AlarmDefinitionContext alarmDefinition;
    private Controller controller;
    private V3SiteContext site;
    private Boolean filtered;
    private Boolean processed;
    private AlarmApproach alarmApproach;
    private Long occurredTime;
    private Long clearedTime;
    private Long filterRuleCriteriaId;
    private RawAlarmSourceType sourceType;
    private V3AssetContext asset;
    private RawAlarmContext parentAlarm;
    private List<Long> relatedAlarmIds;
    public enum RawAlarmSourceType implements FacilioStringEnum {
        SCRIPT("Script"),
        CONTROLLER("Controller"),
        EMAIL("Email"),
        SIMULATOR("Simulator"),
        SYSTEM("System"),
        ROLLUP("Roll Up");

        @Getter
        @Setter
        private String displayName;

        @Override
        public String getValue() {
            return displayName;
        }

        RawAlarmSourceType(String displayName) {
            this.displayName = displayName;
        }
    }

    public boolean isFiltered() {
        if (filtered == null) {
            return false;
        }
        return filtered;
    }

    public AlarmApproach getAlarmApproachEnum() {
        return alarmApproach;
    }

    public Integer getAlarmApproach() {
        if (alarmApproach != null) {
            return alarmApproach.getIndex();
        }
        return null;
    }

    public void setAlarmApproach(Integer alarmApproach) {
        if (alarmApproach != null) {
            this.alarmApproach = AlarmApproach.valueOf(alarmApproach);
        }
    }
}
