package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;

public class DeleteInspectionTriggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> inspectionIds = inspections.stream()
			.map(InspectionTemplateContext::getId)
			.collect(Collectors.toList())
			;
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<InspectionTriggerContext> triggers = InspectionAPI.getInspectionTrigger(CriteriaAPI.getCondition(fieldMap.get("parent"), inspectionIds, NumberOperators.EQUALS), false);
		
		if(triggers != null && triggers.size() > 0) {
			
			List<Long> triggerIds = triggers.stream().map(InspectionTriggerContext::getId).collect(Collectors.toList());
			
			DeleteRecordBuilder<InspectionTriggerContext> deleteBuilder = new DeleteRecordBuilder<InspectionTriggerContext>()
					.module(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER))
					.andCondition(CriteriaAPI.getIdCondition(triggerIds, modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER)));
			deleteBuilder.delete();
			
			List<Long> scheduleIds = triggers.stream().filter(t -> (t.getScheduleId() != null && t.getScheduleId() > 0)).map(InspectionTriggerContext::getScheduleId).collect(Collectors.toList());
			
			if(scheduleIds != null && scheduleIds.size() > 0) {
				
				GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
						.table(ModuleFactory.getBaseSchedulerModule().getTableName())
						.andCondition(CriteriaAPI.getIdCondition(scheduleIds, ModuleFactory.getBaseSchedulerModule()))
						;
				
				delete.delete();
			}
			
		}
		
		InspectionAPI.deleteScheduledPreOpenInspections(inspectionIds);
		
		
		
		return false;
	}

}
