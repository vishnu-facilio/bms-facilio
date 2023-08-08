package com.facilio.flows.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class FlowTransitionUpdateHandler {
    private Command beforeUpdateCommand;
    private Command afterUpdateCommand;

    public Command getBeforeUpdateCommand() {
        return beforeUpdateCommand;
    }

    public Command getAfterUpdateCommand() {
        return afterUpdateCommand;
    }

    private FlowTransitionUpdateHandler(FlowTransitionUpdateHandlerBuilder saveHandlerBuilder) {
        this.beforeUpdateCommand = saveHandlerBuilder.beforeUpdateCommand;
        this.afterUpdateCommand = saveHandlerBuilder.afterUpdateCommand;
    }

    public static class FlowTransitionUpdateHandlerBuilder extends NesterBuilder<FlowConfig.FlowConfigBuilder> {
        private Command beforeUpdateCommand;
        private Command afterUpdateCommand;


        public FlowTransitionUpdateHandlerBuilder beforeUpdateCommand(Command... beforeSaveCommand) {
            this.beforeUpdateCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }

        public FlowTransitionUpdateHandlerBuilder afterUpdateCommand(Command... afterSaveCommand) {
            this.afterUpdateCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public FlowTransitionUpdateHandlerBuilder(FlowConfig.FlowConfigBuilder parent) {
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
            parent.updateHandler = new FlowTransitionUpdateHandler(this);
            return parent;
        }
    }
}
