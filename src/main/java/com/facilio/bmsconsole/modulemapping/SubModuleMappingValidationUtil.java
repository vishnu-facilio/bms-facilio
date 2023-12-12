package com.facilio.bmsconsole.modulemapping;

import com.facilio.bmsconsole.context.FieldMappingContext;
import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.SubModuleMappingContext;

import java.util.List;

public class SubModuleMappingValidationUtil {
    public static void validateMappings(SubModuleMappingContext mapping) throws Exception {

        List<FieldMappingContext> fieldMappingsList = mapping.getFieldMappingList();

        if (fieldMappingsList == null) {
            throw new IllegalArgumentException("Sub Module mapping " + mapping.getSourceModuleName() + " - " + mapping.getTargetModuleName() + " requires at least one associated field mapping.");
        }

        for (FieldMappingContext fieldMapping : fieldMappingsList) {
            ModuleMappingValidationUtil.fieldMappingValidationForSubModule(fieldMapping, mapping);
        }


    }
}

