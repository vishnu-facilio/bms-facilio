package com.facilio.trigger.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class TriggerCreateHandler {

    private Command afterSaveCommand;


    public Command getAfterSaveCommand() {
        return afterSaveCommand;
    }

    private TriggerCreateHandler(TriggerCreateHandlerBuilder createHandlerBuilder) {
        this.afterSaveCommand = createHandlerBuilder.afterSaveCommand;
    }

    public static class TriggerCreateHandlerBuilder{

        private TriggerConfig.TriggerConfigBuilder parent;
        private Command afterSaveCommand;


        public TriggerCreateHandler.TriggerCreateHandlerBuilder afterSaveCommand(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public TriggerCreateHandlerBuilder(TriggerConfig.TriggerConfigBuilder parent) {
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
            parent.createHandler = new TriggerCreateHandler(this);
            return parent;
        }

    }
}
