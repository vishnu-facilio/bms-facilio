package com.facilio.fields.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterFieldsByFieldType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

        List<FieldType> fieldTypesToFetch = (List<FieldType>) context.getOrDefault(FacilioConstants.FieldsConfig.FIELD_TYPES_TO_FETCH, null);
        Objects.requireNonNull(fieldTypesToFetch, "FieldType(s) to fetch should be defined");

        if(CollectionUtils.isNotEmpty(fields)) {
            List<Integer> fieldTypeIdsToFetch = fieldTypesToFetch.stream().map(FieldType::getTypeAsInt).collect(Collectors.toList());

            fields = fields.stream()
                    .filter(field -> fieldTypeIdsToFetch.contains(field.getDataType()))
                    .collect(Collectors.toList());
            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }
        return false;
    }
}
