package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext.TemplateVariableTypeEnum;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetOCRTemplateVariableBeforeSaveCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
    	
    	
    	String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<OCRTemplateVariableContext> templateVariables = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(templateVariables)){
            for(OCRTemplateVariableContext templateVariable : templateVariables){
            	
            	if(templateVariable.getTypeEnum() == TemplateVariableTypeEnum.SCRIPT) {
        			
        			if(templateVariable.getWorkflow().getId() >0) {
        				
        				FacilioChain updateWorkflowChain =  TransactionChainFactory.getUpdateWorkflowChain();
        				FacilioContext contextnew = updateWorkflowChain.getContext();
        				
        				contextnew.put(WorkflowV2Util.WORKFLOW_CONTEXT, templateVariable.getWorkflow());
        				updateWorkflowChain.execute();
        			}
        			else {
        				FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowChain();
        				FacilioContext contextnew = addWorkflowChain.getContext();
        				
        				contextnew.put(WorkflowV2Util.WORKFLOW_CONTEXT, templateVariable.getWorkflow());
        				addWorkflowChain.execute();
        			}
        			
        			templateVariable.setWorkflowId(templateVariable.getWorkflow().getId());
        		}
            }
            
        }
    	
    	
		// TODO Auto-generated method stub
		return false;
	}

}
