package com.facilio.multiImport.config;

import com.facilio.chain.FacilioChain;
import com.facilio.multiImport.annotations.RowFunction;
import org.apache.commons.chain.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportHandler {
    private Command beforeImportCommand;
    private Command afterImportCommand;
    private Command afterInsertCommand;
    private RowFunction beforeProcessRowFunction;
    private RowFunction afterProcessRowFunction;
    public Map<String, List<String>> lookupUniqueFieldsMap;
    public Command getBeforeImportCommand() {
        return beforeImportCommand;
    }
    public Command getAfterImportCommand() {
        return afterImportCommand;
    }
    public Command getAfterInsertCommand() {
        return afterInsertCommand;
    }
    public RowFunction getBeforeProcessRowFunction() {
        return beforeProcessRowFunction;
    }
    public RowFunction getAfterProcessRowFunction() {
        return afterProcessRowFunction;
    }
    public Map<String, List<String>> getLookupUniqueFieldsMap() {
        return lookupUniqueFieldsMap;
    }

    public ImportHandler(ImportHandlerBuilder importHandlerBuilder) {
        this.beforeImportCommand = importHandlerBuilder.beforeImportCommand;
        this.afterImportCommand = importHandlerBuilder.afterImportCommand;
        this.afterInsertCommand = importHandlerBuilder.afterInsertCommand;
        this.beforeProcessRowFunction = importHandlerBuilder.beforeProcessRowFunction;
        this.afterProcessRowFunction = importHandlerBuilder.afterProcessRowFunction;
        this.lookupUniqueFieldsMap = importHandlerBuilder.lookupUniqueFieldsMap;
    }

    public static class ImportHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeImportCommand;
        private Command afterImportCommand;
        private Command afterInsertCommand;
        private RowFunction beforeProcessRowFunction;
        private RowFunction afterProcessRowFunction;
        private Map<String, List<String>> lookupUniqueFieldsMap;

        public ImportHandlerBuilder beforeImportCommand(Command... command) {
            this.beforeImportCommand = buildTransactionChain(command);
            return this;
        }
        public ImportHandlerBuilder afterImportCommand(Command... command) {
            this.afterImportCommand =  buildTransactionChain(command);
            return this;
        }
        public ImportHandlerBuilder afterInsertCommand(Command... command) {
            this.afterInsertCommand =  buildTransactionChain(command);
            return this;
        }
        public ImportHandlerBuilder beforeProcessRowFunction(RowFunction rowFunction) {
            this.beforeProcessRowFunction = rowFunction;
            return this;
        }
        public ImportHandlerBuilder afterProcessRowFunction(RowFunction rowFunction) {
            this.afterProcessRowFunction = rowFunction;
            return this;
        }
        public ImportHandlerBuilder lookupUniqueFieldsMap(String moduleName, List<String> fieldNames) {
            if (lookupUniqueFieldsMap == null) {
                lookupUniqueFieldsMap = new HashMap<>();
            }
            lookupUniqueFieldsMap.put(moduleName, fieldNames);
            return this;
        }
        public ImportHandlerBuilder(ImportConfig.ImportConfigBuilder parent) {
            super(parent);
        }
        private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
            FacilioChain c = FacilioChain.getTransactionChain();
            for (Command facilioCommand: facilioCommands) {
                c.addCommand(facilioCommand);
            }
            return c;
        }
        @Override
        public ImportConfig.ImportConfigBuilder done() {
            parent.importHandler = new ImportHandler(this);
            return parent;
        }
    }
}