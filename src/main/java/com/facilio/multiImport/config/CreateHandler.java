package com.facilio.multiImport.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class CreateHandler {

    private Command initCommand;
    private Command beforeSaveCommand;
    private Command afterSaveCommand;
    private Command afterTransactionCommand;

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

    private CreateHandler(CreateHandler.CreateHandlerBuilder createHandlerBuilder) {
      this.initCommand = createHandlerBuilder.initCommand;
      this.beforeSaveCommand = createHandlerBuilder.beforeSaveCommand;
      this.afterSaveCommand = createHandlerBuilder.afterSaveCommand;
      this.afterTransactionCommand = createHandlerBuilder.afterTransactionCommand;
    }

    public static class CreateHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {

        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;

        public CreateHandlerBuilder initCommand(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        public CreateHandlerBuilder beforeSaveCommand(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }

        public CreateHandlerBuilder afterSaveCommand(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        public CreateHandlerBuilder afterTransactionCommand(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
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
