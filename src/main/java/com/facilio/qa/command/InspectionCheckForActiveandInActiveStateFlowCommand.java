package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.modules.UpdateRecordBuilder;

public class InspectionCheckForActiveandInActiveStateFlowCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(InspectionCheckForActiveandInActiveStateFlowCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		//changeSetMap={inspectionTemplate={10=[Field ID : 3954::Old Value : 106::New Value : 105]}},
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		Map<Long, InspectionTemplateContext> inspectionMap = inspections.stream().collect(Collectors.toMap(InspectionTemplateContext::getId, Function.identity()));
		
		FacilioField moduleStateField = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE)).get("moduleState");
		
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
					
					InspectionTemplateContext inspection = inspectionMap.get(id);
					
					inspection.setStatus(status);
					
					UpdateRecordBuilder<InspectionTemplateContext> update = new UpdateRecordBuilder<InspectionTemplateContext>()
							.moduleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
							.fields(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE))
							.andCondition(CriteriaAPI.getIdCondition(id, Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE)))
							;
					
					update.update(inspection);
				}
			}
		}
		
		return false;
	}

}
