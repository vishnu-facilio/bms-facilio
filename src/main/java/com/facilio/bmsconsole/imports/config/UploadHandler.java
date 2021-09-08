package com.facilio.bmsconsole.imports.config;

import org.apache.commons.chain.Command;

public class UploadHandler {
    private Command beforeUploadCommand;

    public UploadHandler(UploadHandlerBuilder parent) {
        this.beforeUploadCommand = parent.beforeUploadCommand;
    }

    public Command getBeforeUploadCommand() {
        return beforeUploadCommand;
    }

    public static class UploadHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeUploadCommand;

        public UploadHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }

        public UploadHandlerBuilder beforeUploadCommand(Command command) {
            this.beforeUploadCommand = command;
            return this;
        }

        private UploadHandler build() {
            return new UploadHandler(this);
        }

        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.uploadHandler = build();
            return parent;
        }
    }
}