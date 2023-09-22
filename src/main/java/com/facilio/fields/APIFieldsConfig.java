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

    // note: cannot add and skip fields at the same time
    // @Module("custom_test")
    // public static Supplier<FieldConfig> customTest() {
    // return () -> new FieldConfig()
    // .sortFields()
    // .beforeFetch(new SampleBeforeFetchCommand())
    // .afterFetch(new SampleAfterFetchCommand())
    // .add(String moduleName, List<String> fieldNames)
    // .skip(String moduleName, List<String> fieldNames)
    // .done()
    // .viewFields()
    // .beforeFetch(new SampleBeforeFetchCommand())
    // .afterFetch(new SampleAfterFetchCommand())
    // .mainField(String moduleName, List<String> fieldNames)
    // .add(String moduleName, List<String> fieldNames)
    // .skip(String moduleName, List<String> fieldNames)
    // .done()
    // .advancedFilter()
    // .beforeFetch(new SampleBeforeFetchCommand())
    // .afterFetch(new SampleAfterFetchCommand())
    // .add(String moduleName, List<String> fieldNames)
    // .skip(String moduleName, List<String> fieldNames)
    // .done()
    // .build();
    // }


    private List<String> getWorkorderLicenseDisabledFields() throws Exception {
        List<String> licenseDisabledFieldNames = new ArrayList<>();
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
            licenseDisabledFieldNames.add("client");
        }
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
            licenseDisabledFieldNames.add("tenant");
        }
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VENDOR)) {
            licenseDisabledFieldNames.add("vendor");
        }
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            licenseDisabledFieldNames.add("safetyPlan");
        }
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORK_PERMIT)) {
            licenseDisabledFieldNames.add("workPermitNeeded");
            licenseDisabledFieldNames.add("workPermitIssued");
        }
        return licenseDisabledFieldNames;
    }

    @Module(value = "workorder")
    public static Supplier<FieldConfig> getWorkOrderConfig() {
        // TODO - Handle exception when both skip & add are configured
        return () -> new FieldConfig()
                .addLicenseBasedFields(AccountUtil.FeatureLicense.CLIENT, Arrays.asList("client"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.TENANTS, Arrays.asList("tenant"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.VENDOR, Arrays.asList("vendor"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.SAFETY_PLAN, Arrays.asList("safetyPlan"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.WORK_PERMIT, Arrays.asList("workPermitNeeded", "workPermitIssued"))
                .addType(FieldListType.SORTABLE)
                .add(IncludeOrExcludeFieldUtil.WORK_ORDER_FIELDS_INCLUDE)
                .done()
                .addType(FieldListType.VIEW_FIELDS)
                .addTypeSpecificFields("ticket", Arrays.asList("localId", "subject"))
//                .skip("ticket", Arrays.asList("subject", "description"))
                .add(FieldUtil.WORKORDER_VIEW_FIEDS)
                .done()
                .addType(FieldListType.ADVANCED_FILTER_FIELDS)
//                .skip("ticket", Arrays.asList("subject", "description"))
                .add(FieldFactory.Fields.WORK_ORDER_FIELDS_INCLUDE)
                .done()
                ;
    }

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
