package com.facilio.multiImport.config;


import com.facilio.modules.ModuleBaseWithCustomFields;

public class ImportConfig {
    private Class beanClass;
    private String activityModuleName;
    public Class getBeanClass() {
        return beanClass;
    }

    private ImportConfig() {}

    private ImportConfig(ImportConfigBuilder builder) {
        this.beanClass=builder.beanClass;
        this.uploadHandler = builder.uploadHandler;
        this.importHandler = builder.importHandler;
        this.parseHandler = builder.parseHandler;
        this.createHandler = builder.createHandler;
        this.updateHandler = builder.updateHandler;
        this.activityModuleName = builder.activityModuleName;
    }

    private UploadHandler uploadHandler;
    private ParseHandler parseHandler;
    private ImportHandler importHandler;
    private CreateHandler createHandler;
    private UpdateHandler updateHandler;

    public CreateHandler getCreateHandler() {
        return createHandler;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }



    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }


    public ImportHandler getImportHandler() {
        return importHandler;
    }

    public ParseHandler getParseHandler() {
        return parseHandler;
    }
    public String getActivityModuleName() { return activityModuleName; }

    public static class ImportConfigBuilder {
        private Class beanClass;
        private String activityModuleName;

        private ImportConfigBuilder(){}
        public ImportConfigBuilder(Class<? extends ModuleBaseWithCustomFields> beanClass){
            this.beanClass=beanClass;
        }
        UploadHandler uploadHandler;
        ParseHandler parseHandler;
        ImportHandler importHandler;
        CreateHandler createHandler;
        UpdateHandler updateHandler;
        public UploadHandler.UploadHandlerBuilder uploadHandler() {
            return new UploadHandler.UploadHandlerBuilder(this);
        }
        public ParseHandler.ParseHandlerBuilder parseHandler() {
            return new ParseHandler.ParseHandlerBuilder(this);
        }
        public ImportHandler.ImportHandlerBuilder importHandler() {
            return new ImportHandler.ImportHandlerBuilder(this);
        }
        public CreateHandler.CreateHandlerBuilder createHandler() {
            return new CreateHandler.CreateHandlerBuilder(this);
        }
        public UpdateHandler.UpdateHandlerBuilder updateHandler() {
            return new UpdateHandler.UpdateHandlerBuilder(this);
        }
        public ImportConfigBuilder setActivityModuleName(String activityModuleName) {
            this.activityModuleName = activityModuleName;
            return this;
        }
        public ImportConfig build() {
            return new ImportConfig(this);
        }
    }

}
