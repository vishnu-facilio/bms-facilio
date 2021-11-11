package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class SurveyCheckForActiveandInActiveStateFlowCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(SurveyCheckForActiveandInActiveStateFlowCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		//changeSetMap={surveyTemplate={10=[Field ID : 3954::Old Value : 106::New Value : 105]}},
		
		List<SurveyTemplateContext> surveys = Constants.getRecordList((FacilioContext) context);
		
		Map<Long, SurveyTemplateContext> surveyMap = surveys.stream().collect(Collectors.toMap(SurveyTemplateContext::getId, Function.identity()));
		
		FacilioField moduleStateField = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Survey.SURVEY_TEMPLATE)).get("moduleState");
		
		Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
		
		if(changeSet != null) {
			
			for(Long id : changeSet.keySet()) {
				
				Boolean status = null;
				
				List<UpdateChangeSet> changeSets = changeSet.get(id);
				if(changeSets != null) {
					Map<Long, UpdateChangeSet> changeSetFieldMap = changeSets.stream().collect(Collectors.toMap(UpdateChangeSet::getFieldId, Function.identity()));
					
					if(changeSetFieldMap.containsKey(moduleStateField.getFieldId())) {
						
						UpdateChangeSet stateChangeSet = changeSetFieldMap.get(moduleStateField.getFieldId());
						
						Long stateId = (Long)stateChangeSet.getNewValue();
						
						FacilioStatus state = StateFlowRulesAPI.getStateContext(stateId);
						
						if(state.getStatus().equals("active")) {
							
							status = Boolean.TRUE;
						}
						else if (state.getStatus().equals("inactive")) {
							
							status = Boolean.FALSE;
						}
					}
				}
				
				if(status != null) {
					
					SurveyTemplateContext survey = surveyMap.get(id);
					
					survey.setStatus(status);
					
					UpdateRecordBuilder<SurveyTemplateContext> update = new UpdateRecordBuilder<SurveyTemplateContext>()
							.moduleName(FacilioConstants.Survey.SURVEY_TEMPLATE)
							.fields(Constants.getModBean().getAllFields(FacilioConstants.Survey.SURVEY_TEMPLATE))
							.andCondition(CriteriaAPI.getIdCondition(id, Constants.getModBean().getModule(FacilioConstants.Survey.SURVEY_TEMPLATE)))
							;
					
					update.update(survey);
				}
			}
		}
		
		return false;
	}

}
