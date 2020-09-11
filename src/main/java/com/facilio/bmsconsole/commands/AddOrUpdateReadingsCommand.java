package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
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
			context.put(FacilioConstants.ContextNames.OLD_CONTROLLER, controller);
		}

		boolean forkPostProcessing = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FORK_POST_READING_PROCESSING, false);
//		if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
//			LOGGER.info(MessageFormat.format("Fork post processing --> {0}", forkPostProcessing));
//		}
		LOGGER.debug(MessageFormat.format("Fork post processing --> {0}", forkPostProcessing));
		if (forkPostProcessing) {
			long time = System.currentTimeMillis();
			FacilioTimer.scheduleInstantJob("rule", "ReadingPostProcessingJob", (FacilioContext) context);
//			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
//				LOGGER.info(MessageFormat.format("Time taken to create instant job : {0}", (System.currentTimeMillis() - time)));
//			}
			LOGGER.debug(MessageFormat.format("Time taken to create instant job : {0}", (System.currentTimeMillis() - time)));
		}
		else {
			FacilioChain postProcessingChain = ReadOnlyChainFactory.readingPostProcessingChain();
			postProcessingChain.setContext((FacilioContext) context);
			postProcessingChain.execute();
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

}
