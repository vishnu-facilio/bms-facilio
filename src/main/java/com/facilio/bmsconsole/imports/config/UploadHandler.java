package com.facilio.bmsconsole.imports.config;

import org.apache.commons.chain.Command;

public class UploadHandler {
    private Command beforeUploadCommand;
    public Command getBeforeUploadCommand() {
        return beforeUploadCommand;
    }

    private Command afterUploadCommand;
    public Command getAfterUploadCommand() {
        return afterUploadCommand;
    }

    public UploadHandler(UploadHandlerBuilder parent) {
        this.beforeUploadCommand = parent.beforeUploadCommand;
        this.afterUploadCommand = parent.afterUploadCommand;
    }

    public static class UploadHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeUploadCommand;
        public UploadHandlerBuilder beforeUploadCommand(Command command) {
            this.beforeUploadCommand = command;
            return this;
        }

        private Command afterUploadCommand;
        public UploadHandlerBuilder afterUploadCommand(Command command) {
            this.afterUploadCommand = command;
            return this;
        }

        public UploadHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }

        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.uploadHandler = new UploadHandler(this);
            return parent;
        }
    }
}