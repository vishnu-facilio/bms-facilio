package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modulemapping.ModuleMappingBaseInfoContext;
import com.facilio.bmsconsoleV3.context.SubFieldMappingContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

@Getter
@Setter
public class ModuleMappingContext extends ModuleMappingBaseInfoContext {
    private String name;
    private String displayName;
    private String sourceModule;
    private String targetModule;
    private boolean isCustom;
    private boolean isDefault;
    private Long parentId;

    private List<FieldMappingContext> fieldMappingList;

    private List<SubModuleMappingContext> subModuleMappingList;

    public ModuleMappingContext() {
    }

    public ModuleMappingContext(String name, String displayName, String targetModule, boolean isDefault) {
        this.name = name;
        this.displayName = displayName;
        this.targetModule = targetModule;
        this.isDefault = isDefault;
    }

    @JsonIgnore
    private ModuleMappings moduleMappings;

    public ModuleMappings subModuleMapping() {
        return this.moduleMappings;
    }

    public ModuleMappings moduleMappingDone() {
        return this.moduleMappings;
    }

    public ModuleMappingContext addFieldMapping(String sourceField, String targetField) {

        FieldMappingContext fieldMappingContext = new FieldMappingContext(sourceField, targetField);

        if (this.fieldMappingList == null) {
            this.fieldMappingList = new ArrayList<>(Arrays.asList(fieldMappingContext));
        } else {
            this.fieldMappingList.add(fieldMappingContext);
        }

        fieldMappingContext.setModuleMappingContext(this);

        return this;
    }

    public SubModuleMappingContext addSubModuleMapping(String sourceSubModule, String targetSubModule, String sourceContextName, String targetContextName) {
        SubModuleMappingContext subModuleMappingContext = new SubModuleMappingContext(sourceSubModule, targetSubModule, sourceContextName, targetContextName);

        if (CollectionUtils.isEmpty(this.subModuleMappingList)) {
            this.subModuleMappingList = new ArrayList<>(Arrays.asList(subModuleMappingContext));
        } else {
            this.subModuleMappingList.add(subModuleMappingContext);
        }

        subModuleMappingContext.setModuleMappingContext(this);

        return subModuleMappingContext;

    }

    // Below method is for EnumFieldMapping
    public ModuleMappingContext addFieldMapping(String sourceField, String targetField, MultiFieldMapping multiFieldMapping) {

        FieldMappingContext fieldMappingContext = new FieldMappingContext(sourceField, targetField);

        List<SubFieldMappingContext> subFieldMappingContextList = new ArrayList<>();

        Map<String, List<String>> sourceAndTargetMap = multiFieldMapping.getSourceAndTargetMap();

        if (MapUtils.isNotEmpty(sourceAndTargetMap)) {
            for (String field : sourceAndTargetMap.keySet()) {
                if (field != null && sourceAndTargetMap.get(field) != null) {
                    List<String> targetValues = sourceAndTargetMap.get(field);

                    // Remove duplicates from the targetValues list
                    List<String> distinctTargetValues = new ArrayList<>(new HashSet<>(targetValues));

                    SubFieldMappingContext subFieldMappingContext = new SubFieldMappingContext(field, distinctTargetValues.toArray(new String[0]));
                    subFieldMappingContextList.add(subFieldMappingContext);

                }
            }
        }
        fieldMappingContext.setSubFieldMappings(subFieldMappingContextList);


        if (this.fieldMappingList == null) {
            this.fieldMappingList = new ArrayList<>(Arrays.asList(fieldMappingContext));
        } else {
            this.fieldMappingList.add(fieldMappingContext);
        }

        fieldMappingContext.setModuleMappingContext(this);

        return this;
    }


}
