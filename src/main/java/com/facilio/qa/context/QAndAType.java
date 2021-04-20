package com.facilio.qa.context;

import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum QAndAType implements FacilioEnum<QAndAType> {
    INSPECTION (FacilioConstants.Inspection.INSPECTION_TEMPLATE, FacilioConstants.Inspection.INSPECTION_TEMPLATE, InspectionTemplateContext.class)
    ;

    private String templateModule, responseModule;
    private Class<? extends QAndATemplateContext> subClass;

    private static final Map<String, QAndAType> TEMPLATE_WISE_TYPE = initTemplateWiseType();
    private static Map<String, QAndAType> initTemplateWiseType () {
        Map<String, QAndAType> templateWiseType = new HashMap<>();
        for (QAndAType type : values()) {
            templateWiseType.put(type.templateModule, type);
        }
        return Collections.unmodifiableMap(templateWiseType);
    }

    public static QAndAType getQAndATypeFromTemplateModule (String moduleName) {
        return TEMPLATE_WISE_TYPE.get(moduleName);
    }

    public static QAndAType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
