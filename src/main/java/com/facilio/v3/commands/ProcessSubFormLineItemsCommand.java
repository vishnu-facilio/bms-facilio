package com.facilio.v3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.SubFormContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ProcessSubFormLineItemsCommand extends FacilioCommand {

    protected FacilioField getLookupField(SubFormContext subFormContext, Map<Long, FacilioField> fieldMap, String mainModuleName) {
        long fieldId = subFormContext.getFieldId();
        if (fieldId > 0) {
            return fieldMap.get(fieldId);
        }

        Collection<FacilioField> fields = fieldMap.values();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                if (field instanceof LookupField) {
                    LookupField lookupField = (LookupField) field;
                    if (lookupField.getLookupModule().getName().equals(mainModuleName)) {
                        return lookupField;
                    }
                }
            }
        }
        return null;
    }
}
