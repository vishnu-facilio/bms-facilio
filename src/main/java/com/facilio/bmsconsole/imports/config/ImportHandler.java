package com.facilio.bmsconsole.imports.config;

import com.facilio.bmsconsole.imports.annotations.RowFunction;
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

    private Command afterInsertCommand;
    public Command getAfterInsertCommand() {
        return afterInsertCommand;
    }

    private RowFunction beforeImportFunction;
    public RowFunction getBeforeImportFunction() {
        return beforeImportFunction;
    }

    private RowFunction afterImportFunction;
    public RowFunction getAfterImportFunction() {
        return afterImportFunction;
    }

    public ImportHandler(ImportHandlerBuilder importHandlerBuilder) {
        this.beforeImportCommand = importHandlerBuilder.beforeImportCommand;
        this.afterImportCommand = importHandlerBuilder.afterImportCommand;
        this.afterInsertCommand = importHandlerBuilder.afterInsertCommand;
        this.beforeImportFunction = importHandlerBuilder.beforeImportFunction;
        this.afterImportFunction = importHandlerBuilder.afterImportFunction;
    }

    public static class ImportHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeImportCommand;
        public ImportHandlerBuilder beforeImportCommand(Command command) {
            this.beforeImportCommand = command;
            return this;
        }

        private Command afterImportCommand;
        public ImportHandlerBuilder afterImportCommand(Command command) {
            this.afterImportCommand = command;
            return this;
        }

        private Command afterInsertCommand;
        public ImportHandlerBuilder afterInsertCommand(Command command) {
            this.afterInsertCommand = command;
            return this;
        }

        private RowFunction beforeImportFunction;
        public ImportHandlerBuilder beforeImportFunction(RowFunction rowFunction) {
            this.beforeImportFunction = rowFunction;
            return this;
        }

        private RowFunction afterImportFunction;
        public ImportHandlerBuilder afterImportFunction(RowFunction rowFunction) {
            this.afterImportFunction = rowFunction;
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