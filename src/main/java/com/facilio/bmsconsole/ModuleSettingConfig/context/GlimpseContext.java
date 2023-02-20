package com.facilio.bmsconsole.ModuleSettingConfig.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GlimpseContext {

    private long glimpseId;
    private long orgId;
    private long modifiedTime;
    private long moduleId;
    private List<GlimpseFieldContext> configurationFields;
    private boolean active;
    private long appId;
}
