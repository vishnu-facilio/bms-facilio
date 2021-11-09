package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerContext;
import com.facilio.bmsconsoleV3.util.SurveyAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class DeleteSurveyTriggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<SurveyTemplateContext> surveys = Constants.getRecordList((FacilioContext) context);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> surveyIds = surveys.stream()
			.map(SurveyTemplateContext::getId)
			.collect(Collectors.toList())
			;
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		DeleteRecordBuilder<SurveyTriggerContext> deleteBuilder = new DeleteRecordBuilder<SurveyTriggerContext>()
				.module(modBean.getModule(FacilioConstants.Survey.SURVEY_TRIGGER))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), surveyIds, NumberOperators.EQUALS));
		deleteBuilder.delete();
		
		SurveyAPI.deleteScheduledPreOpenSurveys(surveyIds);
		
		
		
		return false;
	}

}
