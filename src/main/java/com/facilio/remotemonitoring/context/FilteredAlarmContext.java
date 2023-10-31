package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilteredAlarmContext extends V3Context {
    private String message;
    private AlarmCategoryContext alarmCategory;
    private AlarmTypeContext alarmType;
    private V3ClientContext client;
    private V3SiteContext site;
    private Controller controller;
    private Long occurredTime;
    private Long clearedTime;
    private RawAlarmContext alarm;
    private FlaggedEventRuleContext flaggedAlarmProcess;
    private FlaggedEventContext flaggedAlarm;
    private AlarmFilterRuleContext alarmCorrelationRule;
    private V3AssetContext asset;
}