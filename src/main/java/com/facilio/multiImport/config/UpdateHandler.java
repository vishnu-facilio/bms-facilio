package com.facilio.multiImport.config;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;

public class UpdateHandler {

    private Command initCommand;
    private Command beforeUpdateCommand;
    private Command afterUpdateCommand;
    private Command afterTransactionCommand;
    private Command activitySaveCommand;

    public Command getInitCommand() {
        return initCommand;
    }

    public Command getBeforeUpdateCommand() {
        return beforeUpdateCommand;
    }

    public Command getAfterUpdateCommand() {
        return afterUpdateCommand;
    }

    public Command getAfterTransactionCommand() {
        return afterTransactionCommand;
    }

    public Command getActivitySaveCommand() {
        return activitySaveCommand;
    }

    private UpdateHandler(UpdateHandler.UpdateHandlerBuilder updateHandlerBuilder) {
        this.initCommand = updateHandlerBuilder.initCommand;
        this.beforeUpdateCommand = updateHandlerBuilder.beforeUpdateCommand;
        this.afterUpdateCommand = updateHandlerBuilder.afterUpdateCommand;
        this.afterTransactionCommand = updateHandlerBuilder.afterTransactionCommand;
        this.activitySaveCommand = updateHandlerBuilder.activitySaveCommand;
    }

    public static class UpdateHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {

        private Command initCommand;
        private Command beforeUpdateCommand;
        private Command afterUpdateCommand;
        private Command afterTransactionCommand;
        private Command activitySaveCommand;

        public UpdateHandlerBuilder initCommand(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        public UpdateHandlerBuilder beforeUpdateCommand(Command... beforeUpdateCommand) {
            this.beforeUpdateCommand = buildTransactionChain(beforeUpdateCommand);
            return this;
        }

        public UpdateHandlerBuilder afterUpadteCommand(Command... afterUpdateCommand) {
            this.afterUpdateCommand = buildTransactionChain(afterUpdateCommand);
            return this;
        }

        public UpdateHandlerBuilder afterTransactionCommand(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
        }

        public UpdateHandlerBuilder activitySaveCommand(Command... activitySaveCommand) {
            this.activitySaveCommand = buildTransactionChain(activitySaveCommand);
            return this;
        }

        public UpdateHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
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
            parent.updateHandler = new UpdateHandler(this);
            return parent;
        }
    }
}
