package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IncomingRawAlarmContext extends V3Context {
    private V3ClientContext client;
    private String message;
    private Controller controller;
    private V3SiteContext site;
    private AlarmStrategy strategy;
    private Long occurredTime;
    private Long clearedTime;
    private RawAlarmSourceType sourceType;
    private V3AssetContext asset;

    public enum RawAlarmSourceType implements FacilioStringEnum {
        SCRIPT("Script"),
        CONTROLLER("Controller"),
        EMAIL("Email"),
        SIMULATOR("Simulator"),
        SYSTEM("System");

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

    public AlarmStrategy getStrategyEnum() {
        return strategy;
    }

    public Integer getStrategy() {
        if (strategy != null) {
            return strategy.getIndex();
        }
        return null;
    }

    public void setStrategy(Integer strategy) {
        if (strategy != null) {
            this.strategy = AlarmStrategy.valueOf(strategy);
        }
    }
}
