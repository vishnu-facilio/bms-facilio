package com.facilio.qa.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;

import com.facilio.plannedmaintenance.ExecutorBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QAndAType implements FacilioIntEnum {
    INSPECTION ("Inspection",FacilioConstants.Inspection.INSPECTION_TEMPLATE, InspectionTemplateContext.class, FacilioConstants.Inspection.INSPECTION_RESPONSE, InspectionResponseContext.class),
    INDUCTION ("Induction",FacilioConstants.Induction.INDUCTION_TEMPLATE, InductionTemplateContext.class, FacilioConstants.Induction.INDUCTION_RESPONSE, InductionResponseContext.class),
    SURVEY ("Survey",FacilioConstants.Survey.SURVEY_TEMPLATE, SurveyTemplateContext.class, FacilioConstants.Survey.SURVEY_RESPONSE, SurveyResponseContext.class),
    ;

	String name;
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return this.name;
	}
    private String templateModule;
    private Class<? extends QAndATemplateContext> templateClass;
    private String responseModule;
    private Class<? extends ResponseContext> responseClass;

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

    public QAndATemplateContext getQAndATemplateClass() throws Exception {
        return templateClass.getConstructor().newInstance();
    }
}
