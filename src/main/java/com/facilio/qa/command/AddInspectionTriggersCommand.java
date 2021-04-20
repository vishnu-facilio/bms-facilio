package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
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
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;

public class AddInspectionTriggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		List<InspectionTriggerContext> triggers = new ArrayList<InspectionTriggerContext>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		inspections.stream()
			.forEach(
				(inspection) -> {
					
					List<InspectionTriggerContext> inspectionTriggers = inspection.getTriggers();
					
					inspection.setTriggers(null);
					
					if(inspectionTriggers != null) {
						
						inspectionTriggers.stream().forEach((trigger) -> {
							try {
								trigger.setParent(inspection);
								BaseScheduleContext scheduleTrigger = trigger.getSchedule();
								
								scheduleTrigger.setModuleId(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE).getModuleId());
								scheduleTrigger.setRecordId(inspection.getId());
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
								
								triggers.add(trigger);
							}
							catch(Exception e) {
								throw new RuntimeException(e);
							}
						});
					}
				}
			)
			;
		
		V3RecordAPI.addRecord(false, triggers, modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
		
		return false;
	}

}
