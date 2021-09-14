package com.facilio.bmsconsole.imports.config;

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

    public ParseHandler(ParseHandlerBuilder builder) {
        this.beforeParseCommand = builder.beforeParseCommand;
        this.afterParseCommand = builder.afterParseCommand;
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
