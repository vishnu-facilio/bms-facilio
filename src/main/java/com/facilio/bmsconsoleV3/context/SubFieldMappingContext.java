package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.FieldMappingContext;
import com.facilio.bmsconsole.modulemapping.ModuleMappingBaseInfoContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SubFieldMappingContext extends ModuleMappingBaseInfoContext {
    private long parentId = -1L;
    private String sourceField;
    private String targetField;
    private List<String> targetFieldNames = new ArrayList<>();
    private long sourceFieldId;
    private long targetFieldId;


    @JsonIgnore
    private FieldMappingContext fieldMappingContext;

    public FieldMappingContext enumFieldMappingDone() {
        return this.fieldMappingContext;
    }

    public SubFieldMappingContext() {
    }

    public SubFieldMappingContext(String sourceField, String... targetFields) {
        this.sourceField = sourceField;
        this.targetFieldNames.addAll(Arrays.asList(targetFields));

    }
}
