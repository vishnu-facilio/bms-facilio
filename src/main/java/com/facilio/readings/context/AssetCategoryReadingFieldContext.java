package com.facilio.readings.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AssetCategoryReadingFieldContext implements Serializable {
    private Long id;
    private Long orgId;
    private Long categoryModuleId;
    private Long fieldId;
    private Boolean status;
    public boolean isActive() {
        if(status == null) {
            return false;
        }
        return status;
    }
}