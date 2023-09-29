package com.facilio.fields.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class RemoveCustomLookupFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

        if(CollectionUtils.isNotEmpty(fields)) {
            fields.removeIf(field->!field.isDefault()
                    && (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataType() == FieldType.LOOKUP.getTypeAsInt()));
            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }
        return false;
    }
}
