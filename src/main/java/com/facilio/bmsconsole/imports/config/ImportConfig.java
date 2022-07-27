package com.facilio.bmsconsole.imports.config;

public class ImportConfig {
    private ImportConfig() {}

    private ImportConfig(ImportConfigBuilder builder) {
        this.uploadHandler = builder.uploadHandler;
        this.importHandler = builder.importHandler;
        this.parseHandler = builder.parseHandler;
    }

    private UploadHandler uploadHandler;
    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    private ImportHandler importHandler;
    public ImportHandler getImportHandler() {
        return importHandler;
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

        ImportHandler importHandler;
        public ImportHandler.ImportHandlerBuilder importHandler() {
            return new ImportHandler.ImportHandlerBuilder(this);
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
