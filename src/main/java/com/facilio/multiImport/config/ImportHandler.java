package com.facilio.multiImport.config;

import com.facilio.chain.FacilioChain;
import com.facilio.multiImport.annotations.RowFunction;
import org.apache.commons.chain.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class ImportHandler {
    private Command beforeImportCommand;
    private Command afterImportCommand;
    private Command afterDataProcessCommand;
    private RowFunction beforeProcessRowFunction;
    private RowFunction afterProcessRowFunction;
    private  Map<String, List<String>> lookupUniqueFieldsMap;
    private  Map<String,List<String>> loadLookUpExtraSelectFields;
    private  Set<String> batchCollectFieldNames;
    private Set<String> skipLookupNotFoundExceptionFields;

    public Command getBeforeImportCommand() {
        return beforeImportCommand;
    }
    public Command getAfterImportCommand() {
        return afterImportCommand;
    }
    public Command getAfterDataProcessCommand() {
        return afterDataProcessCommand;
    }
    public RowFunction getBeforeProcessRowFunction() {
        return beforeProcessRowFunction;
    }

    public Map<String, List<String>> getLoadLookUpExtraSelectFields() { return loadLookUpExtraSelectFields;}

    public RowFunction getAfterProcessRowFunction() {
        return afterProcessRowFunction;
    }
    public Map<String, List<String>> getLookupUniqueFieldsMap() {
        return lookupUniqueFieldsMap;
    }
    public Set<String> getBatchCollectFieldNames() {return batchCollectFieldNames;}
    public Set<String> getSkipLookupNotFoundExceptionFields() { return skipLookupNotFoundExceptionFields; }

    public ImportHandler(ImportHandlerBuilder importHandlerBuilder) {
        this.beforeImportCommand = importHandlerBuilder.beforeImportCommand;
        this.afterImportCommand = importHandlerBuilder.afterImportCommand;
        this.afterDataProcessCommand = importHandlerBuilder.afterDataProcessCommand;
        this.beforeProcessRowFunction = importHandlerBuilder.beforeProcessRowFunction;
        this.afterProcessRowFunction = importHandlerBuilder.afterProcessRowFunction;
        this.lookupUniqueFieldsMap = importHandlerBuilder.lookupUniqueFieldsMap;
        this.loadLookUpExtraSelectFields = importHandlerBuilder.loadLookUpExtraSelectFields;
        this.batchCollectFieldNames = importHandlerBuilder.batchCollectFieldNames;
        this.skipLookupNotFoundExceptionFields = importHandlerBuilder.skipLookupNotFoundExceptionFields;
    }

    public static class ImportHandlerBuilder extends NesterBuilder<ImportConfig.ImportConfigBuilder> {
        private Command beforeImportCommand;
        private Command afterImportCommand;
        private Command afterDataProcessCommand;
        private RowFunction beforeProcessRowFunction;
        private RowFunction afterProcessRowFunction;
        private Map<String, List<String>> lookupUniqueFieldsMap;
        private Set<String> batchCollectFieldNames;
        private Set<String> skipLookupNotFoundExceptionFields;

        private Map<String, List<String>> loadLookUpExtraSelectFields;
        public ImportHandlerBuilder beforeImportCommand(Command... command) {
            this.beforeImportCommand = buildTransactionChain(command);
            return this;
        }
        public ImportHandlerBuilder afterImportCommand(Command... command) {
            this.afterImportCommand =  buildTransactionChain(command);
            return this;
        }
        public ImportHandlerBuilder afterDataProcessCommand(Command... command) {
            this.afterDataProcessCommand =  buildTransactionChain(command);
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
        public ImportHandlerBuilder lookupUniqueFieldsMap(String lookupFieldName, List<String> fieldNames) {
            if (lookupUniqueFieldsMap == null) {
                lookupUniqueFieldsMap = new HashMap<>();
            }
            lookupUniqueFieldsMap.put(lookupFieldName, fieldNames);
            return this;
        }
        public ImportHandlerBuilder loadLookUpExtraSelectFields(String moduleName, List<String> fieldNames) {
            if (loadLookUpExtraSelectFields == null) {
                loadLookUpExtraSelectFields = new HashMap<>();
            }
            loadLookUpExtraSelectFields.put(moduleName, fieldNames);
            return this;
        }
        public ImportHandlerBuilder skipLookupNotFoundExceptionFields(List<String> fieldNames){
            if(skipLookupNotFoundExceptionFields == null){
                skipLookupNotFoundExceptionFields = new HashSet<>();
            }
            skipLookupNotFoundExceptionFields.addAll(fieldNames);
            return this;
        }
        public ImportHandlerBuilder setBatchCollectFieldNames(List<String> fieldNames){
            if(batchCollectFieldNames == null){
                batchCollectFieldNames = new HashSet<>();
            }
            batchCollectFieldNames.addAll(fieldNames);
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