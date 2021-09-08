package com.facilio.bmsconsole.imports.config;

import org.apache.commons.chain.Command;

public class InsertHandler {
    private Command beforeInsertCommand;

    public InsertHandler(InsertHandlerBuilder insertHandlerBuilder) {
        this.beforeInsertCommand = insertHandlerBuilder.beforeInsertCommand;
    }

    public Command getBeforeInsertCommand() {
        return beforeInsertCommand;
    }

    public static class InsertHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeInsertCommand;

        public InsertHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }

        public InsertHandlerBuilder beforeInsertCommand(Command command) {
            this.beforeInsertCommand = command;
            return this;
        }

        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.insertHandler = new InsertHandler(this);
            return parent;
        }
    }
}