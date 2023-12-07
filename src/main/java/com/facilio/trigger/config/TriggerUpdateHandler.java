package com.facilio.trigger.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class TriggerUpdateHandler {

    private Command afterUpdateCommand;

    public Command getAfterUpdateCommand() {
        return afterUpdateCommand;
    }

    private TriggerUpdateHandler(TriggerUpdateHandlerBuilder updateHandlerBuilder) {
        this.afterUpdateCommand = updateHandlerBuilder.afterUpdateCommand;
    }

    public static class TriggerUpdateHandlerBuilder{

        private TriggerConfig.TriggerConfigBuilder parent;
        private Command afterUpdateCommand;


        public TriggerUpdateHandlerBuilder afterUpdateCommand(Command... afterUpdateCommand) {
            this.afterUpdateCommand = buildTransactionChain(afterUpdateCommand);
            return this;
        }

        public TriggerUpdateHandlerBuilder(TriggerConfig.TriggerConfigBuilder parent) {
            this.parent = parent;
        }

        private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
            FacilioChain c = FacilioChain.getTransactionChain();
            for (Command facilioCommand : facilioCommands) {
                c.addCommand(facilioCommand);
            }
            return c;
        }

        public TriggerConfig.TriggerConfigBuilder done() {
            parent.updateHandler = new TriggerUpdateHandler(this);
            return parent;
        }

    }

}
