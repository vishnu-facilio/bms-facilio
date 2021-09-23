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

    private RowFunction rowFunction;
    public RowFunction getRowFunction() {
        return rowFunction;
    }

    public ImportHandler(ImportHandlerBuilder importHandlerBuilder) {
        this.beforeImportCommand = importHandlerBuilder.beforeImportCommand;
        this.afterImportCommand = importHandlerBuilder.afterImportCommand;
        this.rowFunction = importHandlerBuilder.rowFunction;
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

        private RowFunction rowFunction;
        public ImportHandlerBuilder rowFunction(RowFunction rowFunction) {
            this.rowFunction = rowFunction;
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