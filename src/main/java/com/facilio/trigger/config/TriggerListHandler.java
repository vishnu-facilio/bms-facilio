package com.facilio.trigger.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class TriggerListHandler {

    private Command afteFetchCommand;

    public Command getAfteFetchCommand() {
        return afteFetchCommand;
    }

    private TriggerListHandler(TriggerListBuilder triggerListBuilder) {
        this.afteFetchCommand = triggerListBuilder.afterFetchCommand;
    }

    public static class TriggerListBuilder{

        private TriggerConfig.TriggerConfigBuilder parent;
        private Command afterFetchCommand;


        public TriggerListBuilder afterFetchcommand(Command... afterFetchCommand) {
            this.afterFetchCommand = buildTransactionChain(afterFetchCommand);
            return this;
        }

        public TriggerListBuilder(TriggerConfig.TriggerConfigBuilder parent) {
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
            parent.listHandler = new TriggerListHandler(this);
            return parent;
        }

    }
}
