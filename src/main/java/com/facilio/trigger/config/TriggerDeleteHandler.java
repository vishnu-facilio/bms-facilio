package com.facilio.trigger.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class TriggerDeleteHandler {

    private Command afterDeleteCommand;

    public Command getAfterDeleteCommand() {
        return afterDeleteCommand;
    }

    private TriggerDeleteHandler(TriggerDeleteBuilder deleteBuilder) {
        this.afterDeleteCommand = deleteBuilder.afterDeleteCommand;
    }

    public static class TriggerDeleteBuilder{

        private TriggerConfig.TriggerConfigBuilder parent;
        private Command afterDeleteCommand;


        public TriggerDeleteBuilder afterDeleteCommand(Command... afterDeleteCommand) {
            this.afterDeleteCommand = buildTransactionChain(afterDeleteCommand);
            return this;
        }

        public TriggerDeleteBuilder(TriggerConfig.TriggerConfigBuilder parent) {
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
            parent.deleteHandler = new TriggerDeleteHandler(this);
            return parent;
        }

    }
}
