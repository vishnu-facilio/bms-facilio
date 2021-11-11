package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.util.SurveyAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;

public class AddSurveyTriggersCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddSurveyTriggersCommand.class.getName());

	List<SurveyTriggerContext> triggers = new ArrayList<SurveyTriggerContext>();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = Constants.getModuleName(context);
		
		if(moduleName.equals(FacilioConstants.Survey.SURVEY_TEMPLATE)) {
			
			List<SurveyTemplateContext> surveys = Constants.getRecordList((FacilioContext) context);
			
			surveys.stream()
			.forEach(
				(survey) -> {
					List<SurveyTriggerContext> surveyTriggers = survey.getTriggers();
					if (CollectionUtils.isNotEmpty(surveyTriggers)) {
						surveyTriggers.forEach((trigger) -> trigger.setParent(survey));
						survey.setTriggers(null);
						if (surveyTriggers != null) {
							triggers.addAll(surveyTriggers);
						}
					}
				}
				);
			
		}
		else if (moduleName.equals(FacilioConstants.Survey.SURVEY_TRIGGER)) {
			
			triggers = Constants.getRecordList((FacilioContext) context);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(triggers != null) {
			
			triggers.stream().forEach((trigger) -> {
				try {
					
					if(trigger.getType() == SurveyTriggerContext.TriggerType.SCHEDULE.getVal()) {
						
						BaseScheduleContext scheduleTrigger = trigger.getSchedule();
						
						scheduleTrigger.setGeneratedUptoTime(null);
						scheduleTrigger.setModuleId(modBean.getModule(FacilioConstants.Survey.SURVEY_TEMPLATE).getModuleId());
						scheduleTrigger.setRecordId(trigger.getParent().getId());
						scheduleTrigger.setScheduleType(ScheduleType.SURVEY);
						scheduleTrigger.setDataModuleId(modBean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE).getModuleId());
						
						GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
								.table(ModuleFactory.getBaseSchedulerModule().getTableName())
				                .fields(FieldFactory.getBaseSchedulerFields());
						
						Map<String, Object> props = FieldUtil.getAsProperties(scheduleTrigger);
						insertBuilder.addRecord(props);
						insertBuilder.save();
						
						scheduleTrigger.setId((Long) props.get("id"));
						
						trigger.setScheduleId(scheduleTrigger.getId());
					}
					
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			});
			
			if(moduleName.equals(FacilioConstants.Survey.SURVEY_TEMPLATE)) {
				V3RecordAPI.addRecord(false, triggers, modBean.getModule(FacilioConstants.Survey.SURVEY_TRIGGER), modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER));
			}
			
			List<SurveyTriggerIncludeExcludeResourceContext> inclExclList = new ArrayList<SurveyTriggerIncludeExcludeResourceContext>();
			
			triggers.stream().forEach((trigger) -> {
				
				if(trigger.getResInclExclList()!= null) {
					trigger.getResInclExclList().stream().forEach((inclExcl)->{
						
						inclExcl.setSurveyTrigger(trigger);
						inclExcl.setSurveyTemplate(trigger.getParent());
						
						inclExclList.add(inclExcl);
					});
				}
				
				trigger.setResInclExclList(null);
			});
			
			if(!inclExclList.isEmpty()) {
				V3RecordAPI.addRecord(false, inclExclList, modBean.getModule(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL), modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL));
			}
			
			SurveyAPI.scheduleResponseCreationJob(triggers);
		}
		return false;
	}

}