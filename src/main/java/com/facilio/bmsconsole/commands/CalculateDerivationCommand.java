package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.workflows.util.WorkflowUtil;

public class CalculateDerivationCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(CalculateDerivationCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		
		Set<Object> xValues = (Set<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
		Map<String, Map<String, Map<Object, Object>>> transformedData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		Map<String,Object> wfParams = new HashMap<>();
		wfParams.put("xValues", xValues);
		for( ReportDataPointContext rdp : report.getDataPoints()) {
			
			if(rdp.getTypeEnum().equals(DataPointType.MODULE)) {
				
				Map<String, Map<Object, Object>> repData = transformedData.get(rdp.getName());
				
				if(rdp.getAliases() != null && repData != null) {
					
					for(String aliasKey : rdp.getAliases().keySet()) {
						
						Map<Object, Object> aliasedData = repData.get(aliasKey);
						
						wfParams.put(rdp.getAliases().get(aliasKey), aliasedData);
					}
				}
			}
		}
		
		for(ReportDataPointContext rdp : report.getDataPoints()) {
			
			if(rdp.getTypeEnum().equals(DataPointType.DERIVATION)) {
				Long workflowId = rdp.getTransformWorkflowId();
				if(workflowId != null) {
					Map<Object,Object> derivationResult = (Map<Object,Object>) WorkflowUtil.getResult(workflowId, wfParams);
					transformedData.put(rdp.getName(), Collections.singletonMap(FacilioConstants.Reports.ACTUAL_DATA, derivationResult));
				}
			}
		}
		return false;
	}

}
