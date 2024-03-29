package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

import static com.facilio.accounts.util.AccountUtil.FeatureLicense.*;

@Log4j
public class ReadingPostProcessingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.debug("Executing workflow rules");

        boolean isAddReadings = ! (boolean) context.getOrDefault(FacilioConstants.ContextNames.UPDATE_READINGS, Boolean.FALSE);

        executeRules(context);

        if(isAddReadings) {
            //executeTriggers(context);

            boolean adjustTime = (boolean) context.getOrDefault(FacilioConstants.ContextNames.ADJUST_READING_TTIME, true);
            ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.OLD_CONTROLLER);
            if (controller == null && adjustTime) {
                executeFormulae(context);
            }

            // sending websocket event for live data update
            publishReadingChangeMessage(context);
        }

        LOGGER.debug("Post processing completed");
        return false;
    }

    private void executeRules(Context context) throws Exception {
        if ((boolean) (context.getOrDefault(FacilioConstants.ContextNames.HISTORY_READINGS, false))) {
            LOGGER.debug("Imported reading does not evaluate rules.");
            return;
        }
        boolean isNewReadingRule = AccountUtil.isFeatureEnabled(NEW_READING_RULE);
        boolean isSensorRuleEnabled = AccountUtil.isFeatureEnabled(SENSOR_RULE);
        boolean isNewKpiEnabled = AccountUtil.isFeatureEnabled(NEW_KPI);
        LOGGER.debug("Executing rules. storm exec ?? " + isNewReadingRule);
        if (isNewReadingRule || isSensorRuleEnabled || isNewKpiEnabled) {
            FacilioChain postProcessingChain = ReadOnlyChainFactory.stormReadingPostProcessingChain();
            postProcessingChain.setContext((FacilioContext) context);
            postProcessingChain.execute();
        } else {
            executeWorkflowsRules(context);
        }
    }

    private void executeWorkflowsRules(Context context) {
        try {
            boolean executeReadingRuleCommand = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_COMMAND, Boolean.FALSE));
            FacilioChain execChain = executeReadingRuleCommand ? ReadOnlyChainFactory.executeReadingRuleChain() : ReadOnlyChainFactory.executeWorkflowsForReadingChain();
            execChain.setContext((FacilioContext) context);
            execChain.execute();
        } catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during workflow execution of readings. \n" + readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during workflow execution of readings.", e, String.valueOf(readingMap));
        }
    }

    private void executeTriggers(Context context) {
        try {
            FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeTriggersForReadingChain();
            executeWorkflowChain.setContext((FacilioContext) context);
            executeWorkflowChain.execute();
        } catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during Trigger execution of readings. \n" + readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during Trigger execution of readings.", e, String.valueOf(readingMap));
        }
    }

    private void executeFormulae(Context context) {
        LOGGER.debug("Executing formula");
        try {
            FacilioChain formulaChain = ReadOnlyChainFactory.calculateFormulaChain();
            formulaChain.setContext((FacilioContext) context);
            formulaChain.execute();
        } catch (Exception e) {
            Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            LOGGER.error("Error occurred during formula calculation of readings. \n" + readingMap, e);
            CommonCommandUtil.emailException(this.getClass().getName(), "Error occurred during formula calculation of readings.", e, String.valueOf(readingMap));
        }
    }

    private void publishReadingChangeMessage(Context context) {
        LOGGER.debug("Executing PubSub");
        try {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_LIVE_UPDATE)) {
                LOGGER.debug("Executing PubSub");

                FacilioChain publishChain = ReadOnlyChainFactory.getPubSubPublishMessageChain();
                publishChain.setContext((FacilioContext) context);
                publishChain.execute();
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during publish reading change message. \n", e);
        } catch (NoClassDefFoundError err) {
            LOGGER.error("Error occurred during publish reading change message. \n", err);
        }
    }
}
