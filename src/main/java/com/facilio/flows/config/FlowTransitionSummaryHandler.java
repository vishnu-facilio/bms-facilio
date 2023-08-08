package com.facilio.flows.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class FlowTransitionSummaryHandler {

    private Command afterFetchCommand;



    public Command getAfterFetchCommand() {
        return afterFetchCommand;
    }

    private FlowTransitionSummaryHandler(FlowTransitionSummaryBuilder flowHandlerBuilder) {
        this.afterFetchCommand = flowHandlerBuilder.afterFetchCommand;
    }

    public static class FlowTransitionSummaryBuilder extends NesterBuilder<FlowConfig.FlowConfigBuilder> {
        private Command afterFetchCommand;

        public FlowTransitionSummaryHandler.FlowTransitionSummaryBuilder afterFetchCommand(Command... afterSaveCommand) {
            this.afterFetchCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public FlowTransitionSummaryBuilder(FlowConfig.FlowConfigBuilder parent) {
            super(parent);
        }

        private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
            FacilioChain c = FacilioChain.getTransactionChain();
            for (Command facilioCommand : facilioCommands) {
                c.addCommand(facilioCommand);
            }
            return c;
        }
        @Override
        public FlowConfig.FlowConfigBuilder done() {
            parent.summaryHandler = new FlowTransitionSummaryHandler(this);
            return parent;
        }
    }
}
