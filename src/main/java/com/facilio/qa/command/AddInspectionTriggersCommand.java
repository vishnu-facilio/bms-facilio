package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.wmsv2.handler.InspectionGenerationHandler;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
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

public class AddInspectionTriggersCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddInspectionTriggersCommand.class.getName());

	List<InspectionTriggerContext> triggers = new ArrayList<InspectionTriggerContext>();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("Reached Add Inspection Trigger Command");
		String moduleName = Constants.getModuleName(context);
		
		if(moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
			
			List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
			
			inspections.stream()
			.forEach(
				(inspection) -> {
					List<InspectionTriggerContext> inspectionTriggers = inspection.getTriggers();
					if (CollectionUtils.isNotEmpty(inspectionTriggers)) {
						inspectionTriggers.forEach((trigger) -> trigger.setParent(inspection));
						inspection.setTriggers(null);
						if (inspectionTriggers != null) {
							triggers.addAll(inspectionTriggers);
						}
					}
				}
				);
			
		}
		else if (moduleName.equals(FacilioConstants.Inspection.INSPECTION_TRIGGER)) {
			
			triggers = Constants.getRecordList((FacilioContext) context);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		boolean isManualTriggerFound = false;
		
		if(triggers != null) {
			
			for(InspectionTriggerContext trigger : triggers) {
				
				if(trigger.getType() == InspectionTriggerContext.TriggerType.SCHEDULE.getVal()) {
					
					BaseScheduleContext scheduleTrigger = trigger.getSchedule();
					
					scheduleTrigger.setGeneratedUptoTime(null);
					scheduleTrigger.setModuleId(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE).getModuleId());
					scheduleTrigger.setRecordId(trigger.getParent().getId());
					scheduleTrigger.setScheduleType(ScheduleType.INSPECTION);
					scheduleTrigger.setDataModuleId(modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE).getModuleId());
					
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getBaseSchedulerModule().getTableName())
			                .fields(FieldFactory.getBaseSchedulerFields());
					
					Map<String, Object> props = FieldUtil.getAsProperties(scheduleTrigger);
					insertBuilder.addRecord(props);
					insertBuilder.save();
					
					scheduleTrigger.setId((Long) props.get("id"));
					
					trigger.setScheduleId(scheduleTrigger.getId());
				}
				else if (trigger.getType() == InspectionTriggerContext.TriggerType.USER.getVal()) {
					V3Util.throwRestException(isManualTriggerFound, ErrorCode.VALIDATION_ERROR, "Inspection should have only one manual trigger");
					isManualTriggerFound = true;
				}
			}
			
			if(moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
				V3RecordAPI.addRecord(false, triggers, modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
			}
			
			List<InspectionTriggerIncludeExcludeResourceContext> inclExclList = new ArrayList<InspectionTriggerIncludeExcludeResourceContext>();
			
			triggers.stream().forEach((trigger) -> {
				
				if(trigger.getResInclExclList()!= null) {
					trigger.getResInclExclList().stream().forEach((inclExcl)->{
						
						inclExcl.setInspectionTrigger(trigger);
						inclExcl.setInspectionTemplate(trigger.getParent());
						
						inclExclList.add(inclExcl);
					});
				}
				
				trigger.setResInclExclList(null);
			});
			
			if(!inclExclList.isEmpty()) {
				V3RecordAPI.addRecord(false, inclExclList, modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL));
			}

			List<BaseScheduleContext> baseSchedules = triggers.stream().map(InspectionTriggerContext::getSchedule).collect(Collectors.toList());
			if(baseSchedules!=null && !baseSchedules.isEmpty()) {
				LOGGER.info("Base Schedules Size : "+baseSchedules.size());
				JSONObject baseScheduleListObject = new JSONObject();
				baseScheduleListObject.put(com.facilio.qa.rules.Constants.Command.BASESCHEDULES, baseSchedules);
				SessionManager.getInstance().sendMessage(new Message()
						.setTopic(InspectionGenerationHandler.TOPIC)
						.setContent(baseScheduleListObject));
			}
		}
		return false;
	}

}

