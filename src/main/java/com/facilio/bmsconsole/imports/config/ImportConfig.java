package com.facilio.bmsconsole.imports.config;

public class ImportConfig {
    private ImportConfig() {}

    private ImportConfig(ImportConfigBuilder builder) {
        this.uploadHandler = builder.uploadHandler;
        this.insertHandler = builder.insertHandler;
    }

    private UploadHandler uploadHandler;
    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    private InsertHandler insertHandler;
    public InsertHandler getInsertHandler() {
        return insertHandler;
    }

    private ParseHandler parseHandler;
    public ParseHandler getParseHandler() {
        return parseHandler;
    }

    public static class ImportConfigBuilder {
        UploadHandler uploadHandler;
        public UploadHandler.UploadHandlerBuilder uploadHandler() {
            return new UploadHandler.UploadHandlerBuilder(this);
        }

        InsertHandler insertHandler;
        public InsertHandler.InsertHandlerBuilder insertHandler() {
            return new InsertHandler.InsertHandlerBuilder(this);
        }

        ParseHandler parseHandler;
        public ParseHandler.ParseHandlerBuilder parseHandler() {
            return new ParseHandler.ParseHandlerBuilder(this);
        }

        public ImportConfig build() {
            return new ImportConfig(this);
        }
    }
}
