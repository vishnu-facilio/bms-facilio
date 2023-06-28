package com.facilio.permission.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.TypeItem.GroupingType;
import com.facilio.permission.context.module.FieldPermissionSet;
import com.facilio.permission.context.module.RelatedListPermissionSet;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.permission.handlers.group.FieldPermissionSetHandler;
import com.facilio.permission.handlers.group.PermissionSetGroupHandler;
import com.facilio.permission.handlers.group.RelatedRecordsPermissionSetHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PermissionSetType implements Serializable {
    private long id;
    private long orgId;
    private long permissionSetId;

    @Getter
    public enum Type implements FacilioStringEnum {
        RELATED_LIST(
                "Related List",
                GroupingType.MODULE,
                PermissionSetModuleFactory.getRelatedListPermissionSetModule(),
                PermissionSetFieldFactory.getRelatedListPermissionSetFields(),
                RelatedListPermissionSet.class,
                new RelatedRecordsPermissionSetHandler(),
                Arrays.asList("moduleId","relatedModuleId","relatedFieldId"),
                Arrays.asList(PermissionFieldEnum.RELATED_LIST_READ_PERMISSION),
                Arrays.asList("moduleName","relatedModuleName","relatedFieldName")
        ),
        FIELD_LIST(
                "Field List",
                GroupingType.MODULE,
                PermissionSetModuleFactory.getFieldPermissionSetModule(),
                PermissionSetFieldFactory.getFieldPermissionSetFields(),
                FieldPermissionSet.class,
                new FieldPermissionSetHandler(),
                Arrays.asList("moduleId","fieldId"),
                Arrays.asList(PermissionFieldEnum.FIELD_READ_PERMISSION),
                Arrays.asList("moduleName","fieldName")
        );

        Type(@NonNull String displayName, @NonNull GroupingType groupingType, @NonNull FacilioModule module, @NonNull List<FacilioField> fields, @NonNull Class setClass, @NonNull PermissionSetGroupHandler handler, @NonNull List<String> queryFields, @NonNull List<PermissionFieldEnum> permissionFieldEnumList, @NonNull List<String> requiredHTTPParams) {
            this.groupingType = groupingType;
            this.displayName = displayName;
            this.module = module;
            this.fields = fields;
            this.setClass = setClass;
            this.handler = handler;
            this.queryFields = queryFields;
            this.permissionFieldEnumList = permissionFieldEnumList;
            this.requiredHTTPParams = requiredHTTPParams;
        }
        GroupingType groupingType;
        String displayName;
        FacilioModule module;
        List<FacilioField> fields;
        Class setClass;
        PermissionSetGroupHandler handler;
        List<String> queryFields;
        List<PermissionFieldEnum> permissionFieldEnumList;
        List<String> requiredHTTPParams;
    }
}