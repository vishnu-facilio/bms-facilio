package com.facilio.multiImport.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class CreateHandler {

    private Command initCommand;
    private Command beforeSaveCommand;
    private Command afterSaveCommand;
    private Command afterTransactionCommand;
    private Command activitySaveCommand;

    public Command getInitCommand() {
        return initCommand;
    }

    public Command getBeforeSaveCommand() {
        return beforeSaveCommand;
    }

    public Command getAfterSaveCommand() {
        return afterSaveCommand;
    }

    public Command getAfterTransactionCommand() {
        return afterTransactionCommand;
    }

    public Command getActivitySaveCommand() {
        return activitySaveCommand;
    }

    private CreateHandler(CreateHandler.CreateHandlerBuilder createHandlerBuilder) {
      this.initCommand = createHandlerBuilder.initCommand;
      this.beforeSaveCommand = createHandlerBuilder.beforeSaveCommand;
      this.afterSaveCommand = createHandlerBuilder.afterSaveCommand;
      this.afterTransactionCommand = createHandlerBuilder.afterTransactionCommand;
      this.activitySaveCommand = createHandlerBuilder.activitySaveCommand;
    }

    public static class CreateHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {

        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;
        private Command activitySaveCommand;

        public void initCommand(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
        }

        public void beforeSaveCommand(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
        }

        public void afterSaveCommand(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
        }

        public void afterTransactionCommand(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
        }

        public void activitySaveCommand(Command... activitySaveCommand) {
            this.activitySaveCommand = buildTransactionChain(activitySaveCommand);
        }

        public CreateHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }
        private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
            FacilioChain c = FacilioChain.getTransactionChain();
            for (Command facilioCommand: facilioCommands) {
                c.addCommand(facilioCommand);
            }
            return c;
        }
        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.createHandler = new CreateHandler(this);
            return parent;
        }
    }
}
