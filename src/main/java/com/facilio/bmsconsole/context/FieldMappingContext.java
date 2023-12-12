package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modulemapping.ModuleMappingBaseInfoContext;
import com.facilio.bmsconsoleV3.context.SubFieldMappingContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class FieldMappingContext extends ModuleMappingBaseInfoContext {
    private Long parentId;
    private Long subModuleMappingId;
    private String sourceField;
    private String targetField;
    private Long sourceFieldId;
    private Long targetFieldId;

    private List<SubFieldMappingContext> subFieldMappings;

    @JsonIgnore
    private ModuleMappingContext moduleMappingContext;

    public ModuleMappingContext fieldMappingDone() {
        return this.moduleMappingContext;
    }


    public FieldMappingContext() {
    }

    public FieldMappingContext(String sourceField, String targetField) {
        this.sourceField = sourceField;
        this.targetField = targetField;
    }


}
