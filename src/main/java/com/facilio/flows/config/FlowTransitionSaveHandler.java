package com.facilio.flows.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class FlowTransitionSaveHandler {
    private Command beforeSaveCommand;
    private Command afterSaveCommand;

    public Command getBeforeSaveCommand() {
        return beforeSaveCommand;
    }

    public Command getAfterSaveCommand() {
        return afterSaveCommand;
    }

    private FlowTransitionSaveHandler(FlowTransitionSaveHandlerBuilder saveHandlerBuilder) {
        this.beforeSaveCommand = saveHandlerBuilder.beforeSaveCommand;
        this.afterSaveCommand = saveHandlerBuilder.afterSaveCommand;
    }

    public static class FlowTransitionSaveHandlerBuilder extends NesterBuilder<FlowConfig.FlowConfigBuilder> {
        private Command beforeSaveCommand;
        private Command afterSaveCommand;


        public FlowTransitionSaveHandler.FlowTransitionSaveHandlerBuilder beforeSaveCommand(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }

        public FlowTransitionSaveHandler.FlowTransitionSaveHandlerBuilder afterSaveCommand(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public FlowTransitionSaveHandlerBuilder(FlowConfig.FlowConfigBuilder parent) {
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
            parent.createHandler = new FlowTransitionSaveHandler(this);
            return parent;
        }
    }
}
