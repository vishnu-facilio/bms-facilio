package com.facilio.flows.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class FlowTransitionListHandler {
    private Command afterFetchCommand;

    public Command getAfterFetchCommand() {
        return afterFetchCommand;
    }

    private FlowTransitionListHandler(FlowTransitionListHandler.FlowTransitionListBuilder flowHandlerBuilder) {
        this.afterFetchCommand = flowHandlerBuilder.afterFetchCommand;
    }

    public static class FlowTransitionListBuilder extends NesterBuilder<FlowConfig.FlowConfigBuilder> {
        private Command afterFetchCommand;

        public FlowTransitionListHandler.FlowTransitionListBuilder afterFetchCommand(Command... afterSaveCommand) {
            this.afterFetchCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public FlowTransitionListBuilder(FlowConfig.FlowConfigBuilder parent) {
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
            parent.listHandler = new FlowTransitionListHandler(this);
            return parent;
        }
    }
}
