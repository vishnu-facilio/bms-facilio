package com.facilio.trigger.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class TriggerSummaryHandler {

    private Command afterFetchCommand;

    public Command getAfterFetchCommand() {
        return afterFetchCommand;
    }

    private TriggerSummaryHandler(TriggerSummaryBuilder summaryBuilder) {
        this.afterFetchCommand = summaryBuilder.afterFetchCommand;
    }
    public static class TriggerSummaryBuilder{

        private TriggerConfig.TriggerConfigBuilder parent;
        private Command afterFetchCommand;


        public TriggerSummaryBuilder afterFetchCommand(Command... afterFetchCommand) {
            this.afterFetchCommand = buildTransactionChain(afterFetchCommand);
            return this;
        }

        public TriggerSummaryBuilder(TriggerConfig.TriggerConfigBuilder parent) {
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
            parent.summaryHandler = new TriggerSummaryHandler(this);
            return parent;
        }

    }
}
