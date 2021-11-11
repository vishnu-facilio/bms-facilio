package com.facilio.qa.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerContext;
import com.facilio.bmsconsoleV3.util.SurveyAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class FetchRelatedItemsForSurveyTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<SurveyTemplateContext> surveys = Constants.getRecordList((FacilioContext) context);

		if (CollectionUtils.isNotEmpty(surveys)) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<Long> surveyTemplateIds = surveys.stream().map(SurveyTemplateContext::getId).collect(Collectors.toList());

			Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER));

			List<SurveyTriggerContext> triggers = SurveyAPI.getSurveyTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), surveyTemplateIds, NumberOperators.EQUALS), true);

			if (triggers != null) {
				Map<Long, List<SurveyTriggerContext>> triggerMap = triggers.stream().collect(Collectors.groupingBy(SurveyTriggerContext::getParentId));

				surveys.forEach((survey) -> {
					survey.setTriggers(triggerMap.get(survey.getId()));
					
				});
				
				fillLastTriggeredTime(surveys);
			}
		}
		
		return false;
	}

	private void fillLastTriggeredTime(List<SurveyTemplateContext> surveys) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Survey.SURVEY_RESPONSE);
		
		Map<String, FacilioField> filedMap = FieldFactory.getAsMap(fields);
		
		for(SurveyTemplateContext survey : surveys) {
			SelectRecordsBuilder<SurveyResponseContext> select = new SelectRecordsBuilder<SurveyResponseContext>() 
					.moduleName(FacilioConstants.Survey.SURVEY_RESPONSE)
					.select(Collections.singletonList(filedMap.get("createdTime")))
					.aggregate(NumberAggregateOperator.MAX, filedMap.get("createdTime"))
					.beanClass(SurveyResponseContext.class)
					.andCondition(CriteriaAPI.getCondition(filedMap.get("parent"), survey.getId()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(filedMap.get("status"), SurveyResponseContext.Status.OPEN.getIndex()+"", NumberOperators.EQUALS))
					.groupBy("CREATED_TIME");
					
			List<Map<String, Object>> props = select.getAsProps();
			
			if(props != null && !props.isEmpty()) {
				survey.setDatum("lastTriggeredTime", props.get(0).get("createdTime"));
			}
		}
		
	}

}
