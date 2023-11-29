package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;

import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import java.text.MessageFormat;

import java.util.List;
import java.util.Map;


public class AddFeatureLimit extends SignUpData {
    private static final String FEATURE_LIMIT_CONFIG_PATH ="conf/featureLimits.yml" ;

    @Override
    public void addData() throws Exception {

        Yaml yaml = new Yaml();
        Map<String, Object> json = null;
        try(InputStream inputStream = AddFeatureLimit.class.getClassLoader().getResourceAsStream(FEATURE_LIMIT_CONFIG_PATH);) {
            json = yaml.load(inputStream);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(MessageFormat.format("Error occurred while reading Feature License yml  file, msg : {0}",e.getMessage()), e);
        }

        if(json!=null) {
            List<Map<String, Object>> prop = (List<Map<String, Object>>) json.get(FacilioConstants.ContextNames.FEATURE_LICENSE_LIST);
            if(CollectionUtils.isNotEmpty(prop)) {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getFeatureLimitsModule().getTableName())
                        .fields(FieldFactory.getFeatureLimitsFields())
                        .addRecords(prop);
                builder.save();
            }
        }
    }
}
