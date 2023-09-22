package com.facilio.fields.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveLicenseDisabledFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);

        if(CollectionUtils.isNotEmpty(fields)) {

            Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = (Map<AccountUtil.FeatureLicense, List<String>>) context.get(FacilioConstants.FieldsConfig.LICENSE_BASED_FIELDS_MAP);
            if (!module.isStateFlowEnabled()) {
                fields.removeIf(field -> field.getName().equals("moduleState"));
            }

            if (MapUtils.isNotEmpty(licenseBasedFieldsMap)) {
                for (Map.Entry<AccountUtil.FeatureLicense, List<String>> entry : licenseBasedFieldsMap.entrySet()) {
                    if (!AccountUtil.isFeatureEnabled(entry.getKey())) {
                        if(CollectionUtils.isNotEmpty(entry.getValue())) {
                            fields.removeIf(field -> entry.getValue().contains(field.getName()));
                        }
                    }
                }
            }

        }
        return false;
    }

}
