package com.facilio.bmsconsoleV3.context.workordersurvey;

import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
@ToString(callSuper=true, includeFieldNames=true)
@JsonTypeInfo (use = JsonTypeInfo.Id.NONE)
public class WorkOrderSurveyTemplateContext extends SurveyTemplateContext {

	@Override
	public WorkOrderSurveyResponseContext constructResponse(){

		return (WorkOrderSurveyResponseContext) super.constructResponse();
	}

	@Override
	protected WorkOrderSurveyResponseContext newResponseObject(){

		WorkOrderSurveyResponseContext surveyResponse = new WorkOrderSurveyResponseContext();

		surveyResponse.setData(this.getData());

		return surveyResponse;
	}
}
