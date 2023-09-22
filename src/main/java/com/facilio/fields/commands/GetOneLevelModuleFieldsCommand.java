package com.facilio.fields.commands;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.util.FieldsConfigChainUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import org.apache.commons.collections4.CollectionUtils;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOneLevelModuleFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean fetchSupplementModuleFields = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, false);
        if (fetchSupplementModuleFields) {
            List<Long> defaultFieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD_IDS);
            List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
            FieldListType fieldListType = (FieldListType) context.get(FacilioConstants.FieldsConfig.FIELD_LIST_TYPE);

            List<FacilioField> lookupFields = fields.stream().filter(i -> i.getDataTypeEnum() == FieldType.LOOKUP).collect(Collectors.toList());

            List<String> lookupModuleNames = lookupFields.stream()
                    .filter(field -> !field.getName().equals("siteId") && !LookupSpecialTypeUtil.isSpecialType(((LookupField)field).getLookupModule().getName()))
                    .map(field -> ((LookupField)field).getLookupModule().getName())
                    .distinct()
                    .collect(Collectors.toList());

            Map<String, Object> supplements = new HashMap<>();
            for (String lookupModuleName : lookupModuleNames) {
                FacilioContext supplementsContext = FieldsConfigChainUtil.fetchFieldList(lookupModuleName, fieldListType, defaultFieldIds, true);

                List<Object> lookupModuleFields = (List<Object>) supplementsContext.get(FacilioConstants.ContextNames.FIELDS);
                if (CollectionUtils.isNotEmpty(lookupModuleFields)) {
                    supplements.put(lookupModuleName, lookupModuleFields);
                }
            }

            context.put(FacilioConstants.ContextNames.SUPPLEMENTS, supplements);
        }

        return false;
    }
}
