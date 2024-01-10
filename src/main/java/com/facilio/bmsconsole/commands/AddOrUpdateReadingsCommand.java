package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.BooleanUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class AddOrUpdateReadingsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long startTime = System.currentTimeMillis();

        if(BooleanUtils.isTrue((Boolean) context.get(FacilioConstants.ContextNames.UPDATE_READINGS))) { //update readings
            TransactionChainFactory.updateOnlyReadingsChain().execute(context);
            ReadOnlyChainFactory.readingPostProcessingChain().execute(context);
        } else { //add readings
            TransactionChainFactory.onlyAddOrUpdateReadingsChain().execute(context);
            postOperationsForAddReadings(context);
        }

        LOGGER.debug("AddOrUpdateReadingsCommand time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }

    private void postOperationsForAddReadings(Context context) throws Exception {

        boolean isReqFromStorm = (boolean) context.getOrDefault(FacilioConstants.ContextNames.CALL_FROM_STORM, Boolean.FALSE);

        if (!context.containsKey(AgentConstants.IS_NEW_AGENT) && !isReqFromStorm) {
            ControllerContext controller = updateCheckPointAndControllerActivity(context);
            context.put(FacilioConstants.ContextNames.OLD_CONTROLLER, controller);
        }

        boolean forkPostProcessing = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FORK_POST_READING_PROCESSING, false);
        if (forkPostProcessing) {
            try {
                FacilioTimer.scheduleInstantJob("rule", "ReadingPostProcessingJob", (FacilioContext) context);
            } catch (Exception e) {
                LOGGER.debug("Error occurred while creating instant job for post processing of reading", e);
                CommonCommandUtil.emailException("AddOrUpdateReadingsCommand", "Post processing instant job failed", e);
            }
        } else {
            FacilioChain postProcessingChain = ReadOnlyChainFactory.readingPostProcessingChain();
            postProcessingChain.setContext((FacilioContext) context);
            postProcessingChain.execute();
        }
    }

    private ControllerContext updateCheckPointAndControllerActivity(Context context) throws Exception {
        //Update Check point
        try {
            FacilioChain controllerActivityChain = TransactionChainFactory.controllerActivityAndWatcherChain();
            controllerActivityChain.execute(context);

            return (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
        } catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred while adding controller activity for readings. \n" + readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred while adding controller activity for readings", e, String.valueOf(readingMap));
        }
        return null;
    }

}
