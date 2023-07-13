package com.facilio.bmsconsoleV3.context.workordersurvey;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
@JsonTypeInfo (use = JsonTypeInfo.Id.NONE)
public class WorkOrderSurveyResponseContext extends SurveyResponseContext {

	public WorkOrderSurveyResponseContext(Long id){
		super(id);
	}
}
