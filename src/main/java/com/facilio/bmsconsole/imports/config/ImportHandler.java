package com.facilio.bmsconsole.imports.config;

import org.apache.commons.chain.Command;

public class ImportHandler {
    private Command beforeImportCommand;
    public Command getBeforeImportCommand() {
        return beforeImportCommand;
    }

    private Command afterImportCommand;
    public Command getAfterImportCommand() {
        return afterImportCommand;
    }

    public ImportHandler(ImportHandlerBuilder importHandlerBuilder) {
        this.beforeImportCommand = importHandlerBuilder.beforeImportCommand;
        this.afterImportCommand = importHandlerBuilder.afterImportCommand;
    }

    public static class ImportHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeImportCommand;
        public ImportHandlerBuilder beforeImportCommand(Command command) {
            this.beforeImportCommand = command;
            return this;
        }

        private Command afterImportCommand;
        public ImportHandlerBuilder afterImportCommand(Command command) {
            this.afterImportCommand = afterImportCommand;
            return this;
        }

        public ImportHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }

        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.importHandler = new ImportHandler(this);
            return parent;
        }
    }
}