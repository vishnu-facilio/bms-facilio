package com.facilio.readingrule.faultimpact.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.util.WorkflowUtil;

import io.jsonwebtoken.lang.Collections;

public class FaultImpactAfterSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FaultImpactContext> faultImpacts = Constants.getRecordList((FacilioContext)context);
		
		List<FaultImpactNameSpaceFieldContext> faultImpactFields = new ArrayList<FaultImpactNameSpaceFieldContext>(); 
		
		List<Long> fieldImpactIdsToBeDeleted = new ArrayList<Long>();
		
		for(FaultImpactContext faultImpact : faultImpacts) {
			
			if(faultImpact.getId() > 0) {
				fieldImpactIdsToBeDeleted.add(faultImpact.getId());
			}
			
			if(!Collections.isEmpty(faultImpact.getFields())) {
				
				faultImpact.getFields().forEach(f -> f.setImpact(faultImpact.getId()));
				
				faultImpactFields.addAll(faultImpact.getFields());
			}

 		}
		
		deleteFaultImpactFields(fieldImpactIdsToBeDeleted);
		
		if(!Collections.isEmpty(faultImpactFields)) {
			
			V3Util.createRecordList(Constants.getModBean().getModule(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME), FieldUtil.getAsMapList(faultImpactFields, FaultImpactNameSpaceFieldContext.class),null, null);
			
			faultImpactFields.forEach(f -> f.setImpact(null));
		}
		
		return false;
	}

	private void deleteFaultImpactFields(List<Long> fieldImpactIdsToBeDeleted) throws Exception {
		
		FacilioField impactField = Constants.getModBean().getField("impact", FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME);
		
		DeleteRecordBuilder<FaultImpactNameSpaceFieldContext> delete = new DeleteRecordBuilder<FaultImpactNameSpaceFieldContext>()
				.moduleName(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(impactField, fieldImpactIdsToBeDeleted, NumberOperators.EQUALS));
		
		delete.delete();
		
	}

}
