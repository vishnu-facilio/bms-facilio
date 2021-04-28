package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class FetchRelatedItemsForInspectionTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> inspectionTemplateIds = inspections.stream().map(InspectionTemplateContext::getId).collect(Collectors.toList());
		
		Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
		
		List<InspectionTriggerContext> triggers = InspectionAPI.getInspectionTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), inspectionTemplateIds, NumberOperators.EQUALS), true);
		
		if(triggers != null) {
			Map<Long, List<InspectionTriggerContext>> triggerMap = triggers.stream().collect(Collectors.groupingBy(InspectionTriggerContext::getParentId));
			
			inspections.forEach((inspection)->{inspection.setTriggers(triggerMap.get(inspection.getId()));});
		}
		
		return false;
	}

}
