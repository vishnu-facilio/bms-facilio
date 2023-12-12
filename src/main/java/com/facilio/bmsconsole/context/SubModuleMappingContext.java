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
public class SubModuleMappingContext extends ModuleMappingBaseInfoContext {

    private String sourceModuleName;
    private String targetModuleName;
    private String sourceContextName;
    private String targetContextName;

    private Long sourceModuleId;
    private Long targetModuleId;

    private Long parentId;
    private Long extendsId;


    private List<FieldMappingContext> fieldMappingList;
    private List<SubModuleMappingContext> subModuleMappingList;

    @JsonIgnore
    private ModuleMappingContext moduleMappingContext;
    private SubModuleMappingContext subModuleMappingContext;

    public SubModuleMappingContext() {

    }

    public ModuleMappingContext subModuleMappingDone() {
        return this.moduleMappingContext;
    }

    public SubModuleMappingContext innerSubModuleMappingdone() {
        return this.subModuleMappingContext;
    }

    public SubModuleMappingContext(String sourceModuleName, String targetModuleName, String sourceContextName, String targetContextName) {
        this.sourceModuleName = sourceModuleName;
        this.targetModuleName = targetModuleName;
        this.sourceContextName = sourceContextName;
        this.targetContextName = targetContextName;
    }

    public SubModuleMappingContext addInnerSubModuleMapping(String sourceSubModule, String targetSubModule, String sourceContextName, String targetContextName) {
        SubModuleMappingContext subModuleMappingContext = new SubModuleMappingContext(sourceSubModule, targetSubModule, sourceContextName, targetContextName);

        if (CollectionUtils.isEmpty(this.subModuleMappingList)) {
            this.subModuleMappingList = new ArrayList<>(Arrays.asList(subModuleMappingContext));
        } else {
            this.subModuleMappingList.add(subModuleMappingContext);
        }

        subModuleMappingContext.setSubModuleMappingContext(this);

        return subModuleMappingContext;

    }

    public SubModuleMappingContext addFieldMapping(String sourceField, String targetField) {

        FieldMappingContext fieldMappingContext = new FieldMappingContext(sourceField, targetField);

        if (this.fieldMappingList == null) {
            this.fieldMappingList = new ArrayList<>(Arrays.asList(fieldMappingContext));
        } else {
            this.fieldMappingList.add(fieldMappingContext);
        }

        return this;
    }


    public SubModuleMappingContext addFieldMapping(String sourceField, String targetField, MultiFieldMapping multiFieldMapping) {

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


        return this;
    }


}
