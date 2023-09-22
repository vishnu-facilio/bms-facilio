package com.facilio.fields;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.fieldBuilder.FieldConfig;
import com.facilio.fields.util.IncludeOrExcludeFieldUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.modules.FieldUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Config
public class APIFieldsConfig {

    /**
     *
     *
    @Module(value = FacilioConstants.FieldsConfig.CUSTOM)
    public static Supplier<FieldConfig> getCustomModuleConfig() {
        return () ->  new FieldConfig()
                .exclude(Arrays.asList("createdTime", "modifiedTime"))
                                -- fields to exclude in module level irrespective of fieldListType
                .addLicenseBasedFields(AccountUtil.FeatureLicense.CLIENT, Arrays.asList("client"))
                                -- fields to show/remove based on license should be added with license as key and list of fieldNames
                .addType(FieldListType.SORTABLE)
                                -- specify fieldListType to config
                .add(Arrays.asList("createdBy", "modifiedBy"))
//                .skip(Arrays.asList("createdBy", "modifiedBy"))
                                -- either add or skip fields should be defined, if misconfigured(add will be in priority)
                .addConfigSpecificFields(FacilioConstants.FieldsConfig.FIXED_FIELD_NAMES, Arrays.asList("name"))
                                -- add fieldListType specific fields which will be avialable in context as configSpecificFields
                .done()
                                -- returns the FieldConfig

                .addType(FieldListType.VIEW_FIELDS)
                                -- add another config for fieldListType
                .add()
//                .skip()
                .addTypeSpecificFields()
                .done()
    }
     */


    @Module(value = FacilioConstants.FieldsConfig.CUSTOM)
    public static Supplier<FieldConfig> getCustomModuleConfig() {
        return () ->  new FieldConfig()

                .addType(FieldListType.SORTABLE)
                .done()

                .addType(FieldListType.VIEW_FIELDS)
                .done()

                .addType(FieldListType.ADVANCED_FILTER_FIELDS)
                .done()

                .addType(FieldListType.PAGE_BUILDER_CRITERIA_FIELDS)
                .done();
    }
}
