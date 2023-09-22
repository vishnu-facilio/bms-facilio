package com.facilio.fields.fieldBuilder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.fields.context.FieldListType;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class FieldConfig {
    private final Map<String, FieldListHandler> fieldListTypeHandlerMap = new HashMap<>();
    private final List<String> excludeFields = new ArrayList<>();
    private final Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = new HashMap<>();

    public FieldListHandler addType(FieldListType fieldListType) {
        String fieldListTypeName = fieldListType.getName();
        if(fieldListTypeHandlerMap.containsKey(fieldListTypeName)) {
            return fieldListTypeHandlerMap.get(fieldListTypeName);
        } else {
            fieldListTypeHandlerMap.put(fieldListTypeName, new FieldListHandler(this));
            return fieldListTypeHandlerMap.get(fieldListTypeName);
        }
    }

    /**
     * fields in excludeFields are not allowed to fetch in any of the fieldListType in the defined module
     * @param fieldNames list of fieldNames which should not be fetched for the module
     * @return FieldConfig
     * @throws Exception if exclude fields are configuired after the fieldConfigurations
     */
    public FieldConfig exclude(List<String> fieldNames) throws Exception {
        FacilioUtil.throwIllegalArgumentException(FacilioProperties.isDevelopment() && MapUtils.isNotEmpty(fieldListTypeHandlerMap), "Exclude fields should be defined before adding fieldType configurations");
        if(CollectionUtils.isNotEmpty(fieldNames)) {
            this.excludeFields.addAll(fieldNames);
        }
        return this;
    }

    /**
     * values in licenseBasedFields are allowed to be in the list only if the license(key) is enabled
     * @param license license to be enabled to display the fields
     * @param fieldNames list of fields that belongs to the license
     * @return FieldConfig
     */
    public FieldConfig addLicenseBasedFields(AccountUtil.FeatureLicense license, List<String> fieldNames) {
        FacilioUtil.throwIllegalArgumentException(FacilioProperties.isDevelopment() && MapUtils.isNotEmpty(fieldListTypeHandlerMap), "Exclude fields should be defined before adding fieldType configurations");
        if(license != null && CollectionUtils.isNotEmpty(fieldNames)) {
            licenseBasedFieldsMap.put(license, fieldNames);
        }
        return this;
    }

}
