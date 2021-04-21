package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;

public class AddInspectionTriggersCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddInspectionTriggersCommand.class.getName());

	List<InspectionTriggerContext> triggers = new ArrayList<InspectionTriggerContext>();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = Constants.getModuleName(context);
		
		if(moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
			
			List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
			
			inspections.stream()
			.forEach(
				(inspection) -> {
					List<InspectionTriggerContext> inspectionTriggers = inspection.getTriggers();
					
					inspectionTriggers.forEach((trigger) -> trigger.setParent(inspection));
					inspection.setTriggers(null);
					if(inspectionTriggers != null) {
						triggers.addAll(inspectionTriggers);
					}
				}
				);
			
		}
		else if (moduleName.equals(FacilioConstants.Inspection.INSPECTION_TRIGGER)) {
			
			triggers = Constants.getRecordList((FacilioContext) context);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(triggers != null) {
			
			triggers.stream().forEach((trigger) -> {
				try {
					BaseScheduleContext scheduleTrigger = trigger.getSchedule();
					
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
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			});
			
			if(moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
				V3RecordAPI.addRecord(false, triggers, modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
			}
			
			JSONObject props = new JSONObject();
			props.put("saveAsV3", true);
			
			triggers.stream().forEach((trigger) -> {
				
				BaseScheduleContext scheduleTrigger = trigger.getSchedule();
				
				try {
					FacilioTimer.deleteJob(scheduleTrigger.getId(), "BaseSchedulerSingleInstanceJob");
					FacilioTimer.scheduleOneTimeJobWithDelay(scheduleTrigger.getId(), "BaseSchedulerSingleInstanceJob", 10, "priority");
					
					BmsJobUtil.deleteJobWithProps(scheduleTrigger.getId(), "BaseSchedulerSingleInstanceJob");
					BmsJobUtil.addJobProps(scheduleTrigger.getId(), "BaseSchedulerSingleInstanceJob", props);
					
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			});
		}
		return false;
	}

}

