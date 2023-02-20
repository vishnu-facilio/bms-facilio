package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleSettingContext {
    private long id;
    private long moduleId;
    private String name;
    private boolean status;
    private Object configurationDetails;
    private String configurationName;
    private String description;
    private String displayName;
    private boolean isStatusDependent;
}
