package com.facilio.bmsconsole.imports.config;

import com.facilio.bmsconsole.imports.annotations.RowFunction;
import org.apache.commons.chain.Command;

public class ParseHandler {
    private Command beforeParseCommand;
    public Command getBeforeParseCommand() {
        return beforeParseCommand;
    }

    private Command afterParseCommand;
    public Command getAfterParseCommand() {
        return afterParseCommand;
    }

    private RowFunction uniqueFunction;
    public RowFunction getUniqueFunction() {
        return uniqueFunction;
    }

    private RowFunction rowFunction;
    public RowFunction getRowFunction() {
        return rowFunction;
    }

    public ParseHandler(ParseHandlerBuilder builder) {
        this.beforeParseCommand = builder.beforeParseCommand;
        this.afterParseCommand = builder.afterParseCommand;
        this.uniqueFunction = builder.uniqueFunction;
        this.rowFunction = builder.rowFunction;
    }

    public static class ParseHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeParseCommand;
        public ParseHandlerBuilder beforeParseCommand(Command command) {
            this.beforeParseCommand = command;
            return this;
        }

        private Command afterParseCommand;
        public ParseHandlerBuilder afterParseCommand(Command command) {
            this.afterParseCommand = command;
            return this;
        }

        private RowFunction uniqueFunction;
        public ParseHandlerBuilder uniqueFunction(RowFunction rowFunction) {
            this.uniqueFunction = rowFunction;
            return this;
        }

        private RowFunction rowFunction;
        public ParseHandlerBuilder rowFunction(RowFunction rowFunction) {
            this.rowFunction = rowFunction;
            return this;
        }

        public ParseHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }

        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.parseHandler = new ParseHandler(this);
            return parent;
        }
    }
}
