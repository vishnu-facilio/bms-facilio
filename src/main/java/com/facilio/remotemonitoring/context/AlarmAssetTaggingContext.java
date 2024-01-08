package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmAssetTaggingContext extends V3Context {
    private V3ClientContext client;
    private AlarmDefinitionContext alarmDefinition;
    private Controller controller;
    private V3AssetContext asset;
    private Boolean suspendAlarms;
}