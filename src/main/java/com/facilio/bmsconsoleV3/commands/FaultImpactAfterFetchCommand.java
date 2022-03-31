package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.util.WorkflowUtil;

import io.jsonwebtoken.lang.Collections;

public class FaultImpactAfterFetchCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FaultImpactContext> faultImpacts = Constants.getRecordList((FacilioContext)context);
		
		for(FaultImpactContext faultImpact : faultImpacts) {
			faultImpact.setWorkflow(WorkflowUtil.getWorkflowContext(faultImpact.getWorkflowId()));
		}
		
		List<Long> faultImpactIds = faultImpacts.stream().map(FaultImpactContext::getId).collect(Collectors.toList());
		
		SelectRecordsBuilder<FaultImpactNameSpaceFieldContext> select = new SelectRecordsBuilder<FaultImpactNameSpaceFieldContext>()
				.moduleName(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME)
				.select(Constants.getModBean().getAllFields(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME))
				.beanClass(FaultImpactNameSpaceFieldContext.class)
				.andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("impact", FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME), faultImpactIds, NumberOperators.EQUALS));
		
		List<FaultImpactNameSpaceFieldContext> faultImpactFieldList = select.get();
		
		if(!Collections.isEmpty(faultImpactFieldList)) {
			
			Map<Long, List<FaultImpactNameSpaceFieldContext>> faultImpactFieldMap = faultImpactFieldList.stream().collect(Collectors.groupingBy(FaultImpactNameSpaceFieldContext::getImpact));
			
			faultImpacts.forEach(faultImpact -> faultImpact.setFields(faultImpactFieldMap.get(faultImpact.getId())));
		}
		
		return false;
	}

}
