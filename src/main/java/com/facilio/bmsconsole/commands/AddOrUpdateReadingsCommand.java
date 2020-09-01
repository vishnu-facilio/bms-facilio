package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AddOrUpdateReadingsCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(AddOrUpdateReadingsCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioChain addOrUpdateChain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		addOrUpdateChain.execute(context);
		ControllerContext controller = null;
		if (!context.containsKey(AgentConstants.IS_NEW_AGENT)){
			controller = updateCheckPointAndControllerActivity(context);
		}
		executeWorkflowsRules(context);

		Boolean adjustTime = (Boolean) context.get(FacilioConstants.ContextNames.ADJUST_READING_TTIME);
		if (adjustTime == null) {
			adjustTime = true;
		}
		if (controller == null && adjustTime) {
			executeFormulae(context);
		}

		if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() != 343l) {
			publishReadingChangeMessage(context);
		}

		return false;
	}
	
	private ControllerContext updateCheckPointAndControllerActivity (Context context) throws Exception {
		//Update Check point
		try {
			FacilioChain controllerActivityChain = TransactionChainFactory.controllerActivityAndWatcherChain();
			controllerActivityChain.execute(context);
			
			return (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
		}
		catch (Exception e) {
			Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			LOGGER.error("Error occurred while adding controller activity for readings. \n"+readingMap, e);
			CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred while adding controller activity for readings", e, String.valueOf(readingMap));
		}
		return null;
	}
	
	private void executeWorkflowsRules (Context context) {
		try {
			FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeWorkflowsForReadingChain();
			executeWorkflowChain.execute(context);
		}
		catch (Exception e) {
			Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			LOGGER.error("Error occurred during workflow execution of readings. \n"+readingMap, e);
			CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during workflow execution of readings.", e, String.valueOf(readingMap));
		}
	}
	
	private void executeFormulae (Context context) {
		try {
			FacilioChain formulaChain = ReadOnlyChainFactory.calculateFormulaChain();
			formulaChain.execute(context);
		}
		catch (Exception e) {
			Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			LOGGER.error("Error occurred during formula calculation of readings. \n"+readingMap, e);
			CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during formula calculation of readings.", e, String.valueOf(readingMap));
		}
	}
	
	private void publishReadingChangeMessage (Context context) {
		try {
			FacilioChain publishChain = ReadOnlyChainFactory.getPubSubPublishMessageChain();
			publishChain.execute(context);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during publish reading change message. \n", e);
		}
	}

}
