package com.facilio.multiImport.util;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import com.facilio.multiImport.annotations.ImportModule;
import com.facilio.multiImport.annotations.RowFunction;
import com.facilio.multiImport.command.*;
import com.facilio.multiImport.config.*;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.commands.AddMultiSelectFieldsCommand;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.CustomModuleDataV3;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class MultiImportChainUtil {
    private static final Map<String, Supplier<ImportConfig>> IMPORT_HANDLER_MAP = new HashMap<>();

    private static void initMultiImportHandlerMap() {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.multiImport"), new MethodAnnotationsScanner());
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(ImportModule.class);

        for (Method method : methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                continue;
            }

            if (!method.getReturnType().equals(Supplier.class)) {
                continue;
            }

            ImportModule annotation = method.getAnnotation(ImportModule.class);
            String moduleName = annotation.value().trim();
            if (StringUtils.isEmpty(moduleName)) {
                continue;
            }

            try {
                Supplier<ImportConfig> config = (Supplier<ImportConfig>) method.invoke(null, null);
                IMPORT_HANDLER_MAP.put(moduleName, config);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static ImportConfig getMultiImportConfig(String moduleName) {
        if (IMPORT_HANDLER_MAP.isEmpty()) {
            initMultiImportHandlerMap();
        }

        Supplier<ImportConfig> importConfigSupplier = IMPORT_HANDLER_MAP.get(moduleName);
        if (importConfigSupplier != null) {
            return importConfigSupplier.get();
        }
        return null;
    }

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getParseChain(String moduleName) {
        ImportConfig importConfig = getMultiImportConfig(moduleName);

        Command beforeParseCommand = null;
        Command afterParseCommand = null;
        RowFunction uniqueFunction = null;
        RowFunction rowFunction = null;

        if (importConfig != null) {
            ParseHandler parseHandler = importConfig.getParseHandler();
            if (parseHandler != null) {
                beforeParseCommand = parseHandler.getBeforeParseCommand();
                afterParseCommand = parseHandler.getAfterParseCommand();
                uniqueFunction = parseHandler.getUniqueFunction();
                rowFunction = parseHandler.getRowFunction();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeParseCommand);
        chain.addCommand(new ParseMultiImportFileCommand());
        addIfNotNull(chain, afterParseCommand);
        chain.addCommand(new InsertMultiImportDataIntoLogCommand());

        FacilioContext context = chain.getContext();
        context.put(MultiImportApi.ImportProcessConstants.UNIQUE_FUNCTION, uniqueFunction);
        context.put(MultiImportApi.ImportProcessConstants.ROW_FUNCTION, rowFunction);

        return chain;
    }

    public static FacilioChain getImportChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new MultiImportDataProcessCommand());
        return chain;
    }

    public static FacilioChain getImportProcessChain(String moduleName) throws Exception {
        ImportConfig importConfig = getMultiImportConfig(moduleName);

        Command beforeImportCommand = null;
        Command afterImportCommand = null;
        Command afterDataProcessCommand = null;


        RowFunction beforeProcessRowFunction = null;
        RowFunction afterProcessRowFunction = null;
        Map<String, List<String>> lookupUniqueFieldsMap = null;
        Map<String,List<String>> loadLookUpExtraSelectFields = null;
        Set<String> batchCollectFieldNames = null;
        Set<String> skipLookupNotFoundExceptionFields = null;

        if (importConfig != null) {
            ImportHandler importHandler = importConfig.getImportHandler();
            if (importHandler != null) {
                beforeImportCommand = importHandler.getBeforeImportCommand();
                afterImportCommand = importHandler.getAfterImportCommand();
                afterDataProcessCommand = importHandler.getAfterDataProcessCommand();
                beforeProcessRowFunction = importHandler.getBeforeProcessRowFunction();
                afterProcessRowFunction = importHandler.getAfterProcessRowFunction();
                lookupUniqueFieldsMap = importHandler.getLookupUniqueFieldsMap();
                loadLookUpExtraSelectFields = importHandler.getLoadLookUpExtraSelectFields();
                batchCollectFieldNames = importHandler.getBatchCollectFieldNames();
                skipLookupNotFoundExceptionFields = importHandler.getSkipLookupNotFoundExceptionFields();
            }
        }

        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ImportInItCommand());
        addIfNotNull(chain, beforeImportCommand);


        if(useV3Import()){
            chain.addCommand(new V3ProcessMultiImportCommand());
        }else {
            chain.addCommand(new V4MultiImportProcessCommand());
        }

        addIfNotNull(chain,afterDataProcessCommand);
        chain.addCommand(new UpdateRowStatusCommand());
        addIfNotNull(chain, afterImportCommand);

        FacilioContext context = chain.getContext();
        context.put(MultiImportApi.ImportProcessConstants.BEFORE_PROCESS_ROW_FUNCTION, beforeProcessRowFunction);
        context.put(MultiImportApi.ImportProcessConstants.AFTER_PROCESS_ROW_FUNCTION, afterProcessRowFunction);
        context.put(MultiImportApi.ImportProcessConstants.LOOKUP_UNIQUE_FIELDS_MAP, lookupUniqueFieldsMap);
        context.put(MultiImportApi.ImportProcessConstants.LOAD_LOOK_UP_EXTRA_SELECT_FIELDS_MAP,loadLookUpExtraSelectFields);
        context.put(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_FIELD_NAMES,batchCollectFieldNames);
        context.put(MultiImportApi.ImportProcessConstants.SKIP_LOOKUP_NOT_FOUND_EXCEPTION,skipLookupNotFoundExceptionFields);
        return chain;
    }

    public static FacilioChain getOneLevelImportProcessChain(String moduleName, List<ImportFieldMappingContext> fieldMappingList) throws Exception {
        ImportConfig importConfig = getMultiImportConfig(moduleName);

        Command beforeImportCommand = null;
        Command afterImportCommand = null;
        Command afterDataProcessCommand = null;


        RowFunction beforeProcessRowFunction = null;
        RowFunction afterProcessRowFunction = null;
        Map<String, List<String>> lookupUniqueFieldsMap = null;
        Map<String,List<String>> loadLookUpExtraSelectFields = null;
        Set<String> batchCollectFieldNames = null;
        Set<String> skipLookupNotFoundExceptionFields = null;

        if (importConfig != null) {
            ImportHandler importHandler = importConfig.getImportHandler();
            if (importHandler != null) {
                beforeImportCommand = importHandler.getBeforeImportCommand();
                afterImportCommand = importHandler.getAfterImportCommand();
                afterDataProcessCommand = importHandler.getAfterDataProcessCommand();
                beforeProcessRowFunction = importHandler.getBeforeProcessRowFunction();
                afterProcessRowFunction = importHandler.getAfterProcessRowFunction();
                lookupUniqueFieldsMap = importHandler.getLookupUniqueFieldsMap();
                loadLookUpExtraSelectFields = importHandler.getLoadLookUpExtraSelectFields();
                batchCollectFieldNames = importHandler.getBatchCollectFieldNames();
                skipLookupNotFoundExceptionFields = importHandler.getSkipLookupNotFoundExceptionFields();
            }
        }

        FacilioChain chain = getDefaultChain();
        chain.addCommand(new OneLevelImportInitCommand());
        addIfNotNull(chain, beforeImportCommand);

        chain.addCommand(new OneLevelMultiImportProcessCommand(moduleName,fieldMappingList));
        chain.addCommand(new FilterAndImportOneLevelRecordsCommand());

        addIfNotNull(chain,afterDataProcessCommand);
        addIfNotNull(chain, afterImportCommand);

        FacilioContext context = chain.getContext();
        context.put(MultiImportApi.ImportProcessConstants.BEFORE_PROCESS_ROW_FUNCTION, beforeProcessRowFunction);
        context.put(MultiImportApi.ImportProcessConstants.AFTER_PROCESS_ROW_FUNCTION, afterProcessRowFunction);
        context.put(MultiImportApi.ImportProcessConstants.LOOKUP_UNIQUE_FIELDS_MAP, lookupUniqueFieldsMap);
        context.put(MultiImportApi.ImportProcessConstants.LOAD_LOOK_UP_EXTRA_SELECT_FIELDS_MAP,loadLookUpExtraSelectFields);
        context.put(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_FIELD_NAMES,batchCollectFieldNames);
        context.put(MultiImportApi.ImportProcessConstants.SKIP_LOOKUP_NOT_FOUND_EXCEPTION,skipLookupNotFoundExceptionFields);
        return chain;
    }
    public static FacilioChain getImportChain(String moduleName, MultiImportSetting setting) throws Exception {
        FacilioChain chain = getDefaultChain();
        if(useV3Import()){
            chain.addCommand(new FilterMultiImportDataCommand());
        }else{
            chain.addCommand(new V4FilterMultiImportDataCommand());
        }
        addCreateAndPatchChainsBySettings(chain,setting,moduleName,false);
        return chain;
    }
    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }
    public static FacilioChain getCreateChain(String moduleName,boolean isOneLevelImport) throws Exception {
        ImportConfig importConfig = getMultiImportConfig(moduleName);
        FacilioModule module = ChainUtil.getModule(moduleName);

        Command initCommand = new DefaultBulkInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;

        if (importConfig != null) {
            CreateHandler createHandler = importConfig.getCreateHandler();
            if (createHandler != null) {
                if (createHandler.getInitCommand() != null) {
                    initCommand = createHandler.getInitCommand();
                }
                beforeSaveCommand = createHandler.getBeforeSaveCommand();
                afterSaveCommand = createHandler.getAfterSaveCommand();
                afterTransactionCommand = createHandler.getAfterTransactionCommand();
            }
        }

        FacilioChain transactionChain = getDefaultChain();

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeSaveCommand);
        transactionChain.addCommand(new AddMultiSelectFieldsCommand());
        if(!isOneLevelImport){
            transactionChain.addCommand(new ValidateRelationshipConstraints(false));
            transactionChain.addCommand(new AddOrUpdateOneLevelImportRecordsCommand(false));
        }
        transactionChain.addCommand(new ImportSaveCommand(module));
        if(!isOneLevelImport){
            transactionChain.addCommand(new AssociateRelationshipCommand(false));
        }
        addIfNotNull(transactionChain, afterSaveCommand);
        addIfNotNull(transactionChain, afterTransactionCommand);

        return transactionChain;
    }

    public static FacilioChain getPatchChain(String moduleName,boolean isOneLevelImport) throws Exception {
        ImportConfig importConfig = getMultiImportConfig(moduleName);
        FacilioModule module = ChainUtil.getModule(moduleName);

        Command initCommand = new BulkPatchInit();
        Command beforeUpdateCommand = null;
        Command afterUpdateCommand = null;
        Command afterTransactionCommand = null;

        if (importConfig != null) {
            UpdateHandler updateHandler = importConfig.getUpdateHandler();
            if (updateHandler != null) {
                if(updateHandler.getInitCommand() != null){
                    initCommand = updateHandler.getInitCommand();
                }
                beforeUpdateCommand = updateHandler.getBeforeUpdateCommand();
                afterUpdateCommand = updateHandler.getAfterUpdateCommand();
                afterTransactionCommand = updateHandler.getAfterTransactionCommand();
            }
        }

        FacilioChain transactionChain = getDefaultChain();

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeUpdateCommand);
        transactionChain.addCommand(new SetSupplementsForImportUpdateCommand());
        if(!isOneLevelImport){
            transactionChain.addCommand(new ValidateRelationshipConstraints(true));
            transactionChain.addCommand(new AddOrUpdateOneLevelImportRecordsCommand(true));
        }
        transactionChain.addCommand(new ImportUpdateCommand(module));
        if(!isOneLevelImport){
            transactionChain.addCommand(new AssociateRelationshipCommand(true));
        }
        addIfNotNull(transactionChain, afterUpdateCommand);
        addIfNotNull(transactionChain, afterTransactionCommand);
        return transactionChain;
    }

    public static void addCreateAndPatchChainsBySettings(FacilioChain chain,MultiImportSetting setting,String moduleName,boolean isOneLevelImport) throws Exception {
        if(setting == MultiImportSetting.INSERT || setting == MultiImportSetting.INSERT_SKIP){
            chain.addCommand(getCreateChain(moduleName,isOneLevelImport));
        }else if(setting == MultiImportSetting.UPDATE || setting == MultiImportSetting.UPDATE_NOT_NULL){
            chain.addCommand(getPatchChain(moduleName,isOneLevelImport));
        } else if ( setting == MultiImportSetting.BOTH || setting == MultiImportSetting.BOTH_NOT_NULL) {
            chain.addCommand(getCreateChain(moduleName,isOneLevelImport));
            chain.addCommand(getPatchChain(moduleName,isOneLevelImport));
        }
    }
    public static FacilioChain getDownloadErrorRecordsChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new DownloadErrorRecordsCommand());
        return chain;
    }

    public static Class getBeanClass(ImportConfig config, FacilioModule module) {
        Class beanClass = null;
        if (config != null) {
            beanClass = config.getBeanClass();
        }
        if (beanClass == null) {
            if (module.isCustom()) {
                if (module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                    beanClass = AttachmentV3Context.class;
                } else {
                    beanClass = CustomModuleDataV3.class;
                }
            } else {
                if (beanClass == null) {
                    if (module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                        beanClass = AttachmentV3Context.class;
                    } else {
                        beanClass = V3Context.class;
                    }
                }
            }
        }
        return beanClass;
    }
    private static boolean useV3Import()  {
        try{
            long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
            Map<String,Object> map = CommonCommandUtil.getOrgInfo(orgId,"useV3Import");
            if(map!=null){
                Object value = map.getOrDefault("value",false);
                return FacilioUtil.parseBoolean(value);
            }
        }catch (Exception e){

        }
        return false;

    }
}
