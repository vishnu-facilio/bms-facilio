package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class AddInductionTriggersCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddInductionTriggersCommand.class.getName());

	List<InductionTriggerContext> triggers = new ArrayList<InductionTriggerContext>();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = Constants.getModuleName(context);
		
		if(moduleName.equals(FacilioConstants.Induction.INDUCTION_TEMPLATE)) {
			
			List<InductionTemplateContext> inductions = Constants.getRecordList((FacilioContext) context);
			
			inductions.stream()
			.forEach(
				(induction) -> {
					List<InductionTriggerContext> inductionTriggers = induction.getTriggers();
					if (CollectionUtils.isNotEmpty(inductionTriggers)) {
						inductionTriggers.forEach((trigger) -> trigger.setParent(induction));
						induction.setTriggers(null);
						if (inductionTriggers != null) {
							triggers.addAll(inductionTriggers);
						}
					}
				}
				);
			
		}
		else if (moduleName.equals(FacilioConstants.Induction.INDUCTION_TRIGGER)) {
			
			triggers = Constants.getRecordList((FacilioContext) context);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		boolean isManualTriggerFound = false;
		
		if(triggers != null) {
			
			for(InductionTriggerContext trigger : triggers) {
				
				if(trigger.getType() == InductionTriggerContext.TriggerType.SCHEDULE.getVal()) {
					
					BaseScheduleContext scheduleTrigger = trigger.getSchedule();
					
					scheduleTrigger.setModuleId(modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE).getModuleId());
					scheduleTrigger.setRecordId(trigger.getParent().getId());
					scheduleTrigger.setScheduleType(ScheduleType.INSPECTION);
					scheduleTrigger.setDataModuleId(modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE).getModuleId());
					
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getBaseSchedulerModule().getTableName())
			                .fields(FieldFactory.getBaseSchedulerFields());
					
					Map<String, Object> props = FieldUtil.getAsProperties(scheduleTrigger);
					insertBuilder.addRecord(props);
					insertBuilder.save();
					
					scheduleTrigger.setId((Long) props.get("id"));
					
					trigger.setScheduleId(scheduleTrigger.getId());
				}
				else if (trigger.getType() == InductionTriggerContext.TriggerType.USER.getVal()) {
					V3Util.throwRestException(isManualTriggerFound, ErrorCode.VALIDATION_ERROR, "errors.qa.addInductionTriggers.manualTriggerCheck",true,null);
					//V3Util.throwRestException(isManualTriggerFound, ErrorCode.VALIDATION_ERROR, "Inspection should have only one manual trigger",true,null);
					isManualTriggerFound = true;
				}
			}
			
			if(moduleName.equals(FacilioConstants.Induction.INDUCTION_TEMPLATE)) {
				V3RecordAPI.addRecord(false, triggers, modBean.getModule(FacilioConstants.Induction.INDUCTION_TRIGGER), modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER));
			}
			
			List<InductionTriggerIncludeExcludeResourceContext> inclExclList = new ArrayList<InductionTriggerIncludeExcludeResourceContext>();
			
			triggers.stream().forEach((trigger) -> {
				
				if(trigger.getResInclExclList()!= null) {
					trigger.getResInclExclList().stream().forEach((inclExcl)->{
						
						inclExcl.setInductionTrigger(trigger);
						inclExcl.setInductionTemplate(trigger.getParent());
						
						inclExclList.add(inclExcl);
					});
				}
				
				trigger.setResInclExclList(null);
			});
			
			if(!inclExclList.isEmpty()) {
				V3RecordAPI.addRecord(false, inclExclList, modBean.getModule(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL), modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL));
			}
			
			JSONObject props = new JSONObject();
			props.put("saveAsV3", true);
			
			triggers.stream().forEach((trigger) -> {
				
				if(trigger.getType() == InductionTriggerContext.TriggerType.SCHEDULE.getVal()) {
					BaseScheduleContext baseSchedule = trigger.getSchedule();
					
					try {
						
						//TO DO WHEN SCHEDULE TRIGGER IS DECIDED
						
//						BmsJobUtil.deleteJobWithProps(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob");
//						
//						FacilioTimer.scheduleOneTimeJobWithDelay(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob", 10, "priority");
//						BmsJobUtil.addJobProps(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob", props);
						
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			});
		}
		return false;
	}

}