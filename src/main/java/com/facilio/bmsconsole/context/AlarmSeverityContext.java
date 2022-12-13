package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
public class AlarmSeverityContext extends ModuleBaseWithCustomFields {

    private static final long serialVersionUID = 1L;

    private String severity;

    private int cardinality = -1;

    private String displayName;

    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : severity;
    }

    private Boolean isDefault;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDeleted() {
        if (isDefault != null) {
            return isDefault.booleanValue();
        }
        return false;
    }

    private String color;


    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof AlarmSeverityContext)) {
            return ((AlarmSeverityContext) obj).getId() == getId();
        }
        return false;
    }
}
