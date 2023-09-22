package com.facilio.fields.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class FilterFieldsByAccessType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        FacilioField.AccessType accessType = (FacilioField.AccessType) context.get(FacilioConstants.FieldsConfig.ACCESS_TYPE);

        if(CollectionUtils.isNotEmpty(fields) && accessType != null) {
            long accessTypeVal = accessType.getVal();
            fields = fields.stream()
                    .filter(field -> field.getAccessType() <= 0 || field.getAccessType() == accessTypeVal)
                    .collect(Collectors.toList());

            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }
        return false;
    }
}
