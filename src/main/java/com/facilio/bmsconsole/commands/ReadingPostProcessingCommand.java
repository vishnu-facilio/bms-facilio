package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ReadingPostProcessingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(ReadingPostProcessingCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
//        if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
            LOGGER.debug("Executing workflow rules");
//        }
            
        executeWorkflowsRules(context);
        //executeTriggers(context);
        
        boolean adjustTime = (boolean) context.getOrDefault(FacilioConstants.ContextNames.ADJUST_READING_TTIME, true);
        ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.OLD_CONTROLLER);
        if (controller == null && adjustTime) {
//            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
                LOGGER.debug("Executing formula");
//            }
            executeFormulae(context);
        }
        if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 146 || AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 343 || AccountUtil.getCurrentOrg().getId() == 324) ){
            publishReadingChangeMessage(context);
        }
        LOGGER.debug("Post processing completed");
        return false;
    }

    private void executeWorkflowsRules (Context context) {
        try {
        	boolean isReadingRuleWorkflowExecution = false;
        	Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_READING_RULE_WORKFLOW_EXECUTION);
			if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
				String isReadingRuleWorkflowExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_READING_RULE_WORKFLOW_EXECUTION);
				if (isReadingRuleWorkflowExecutionProp != null && !isReadingRuleWorkflowExecutionProp.isEmpty() && StringUtils.isNotEmpty(isReadingRuleWorkflowExecutionProp) && Boolean.valueOf(isReadingRuleWorkflowExecutionProp) != null && Boolean.parseBoolean(isReadingRuleWorkflowExecutionProp)){
					isReadingRuleWorkflowExecution = true;
				}	
        	}
			
			if(isReadingRuleWorkflowExecution) {
				FacilioChain executeReadingRuleChain = ReadOnlyChainFactory.executeReadingRuleChain();
        		executeReadingRuleChain.setContext((FacilioContext) context);
        		executeReadingRuleChain.execute();
			}
        	else {
        		FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeWorkflowsForReadingChain();
                executeWorkflowChain.setContext((FacilioContext) context);
                executeWorkflowChain.execute();
        	}    
        }
        catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during workflow execution of readings. \n"+readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during workflow execution of readings.", e, String.valueOf(readingMap));
        }
    }
    
    private void executeTriggers (Context context) {
        try {
            FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeTriggersForReadingChain();
            executeWorkflowChain.setContext((FacilioContext) context);
            executeWorkflowChain.execute();
        }
        catch (Exception e) {
        	e.printStackTrace();
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during Trigger execution of readings. \n"+readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during Trigger execution of readings.", e, String.valueOf(readingMap));
        }
    }

    private void executeFormulae (Context context) {
        try {
            FacilioChain formulaChain = ReadOnlyChainFactory.calculateFormulaChain();
            formulaChain.setContext((FacilioContext) context);
            formulaChain.execute();
        }
        catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during formula calculation of readings. \n"+readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during formula calculation of readings.", e, String.valueOf(readingMap));
        }
    }

    private void publishReadingChangeMessage (Context context) {
        try {
//            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
                LOGGER.debug("Executing PubSub");
//            }
            FacilioChain publishChain = ReadOnlyChainFactory.getPubSubPublishMessageChain();
            publishChain.setContext((FacilioContext) context);
            publishChain.execute();
        }
        catch (Exception e) {
            LOGGER.error("Error occurred during publish reading change message. \n", e);
        }
    }
}
