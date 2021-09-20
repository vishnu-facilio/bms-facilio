package com.facilio.bmsconsole.imports.config;

import com.facilio.bmsconsole.commands.InsertImportDataIntoLogCommand;
import com.facilio.bmsconsole.imports.annotations.ImportModule;
import com.facilio.bmsconsole.imports.command.ImportUploadFileCommand;
import com.facilio.bmsconsole.imports.command.ParseImportFileCommand;
import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ImportChainUtil {
    private static final Map<String, Supplier<ImportConfig>> IMPORT_HANDLER_MAP = new HashMap<>();

    private static void initImportHandlerMap() {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.bmsconsole.imports"), new MethodAnnotationsScanner());
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(ImportModule.class);

        for (Method method: methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                // skip if method has parameters
                continue;
            }

            if (!method.getReturnType().equals(Supplier.class)) {
                // skip if method return type is other than Supplier
                continue;
            }

            ImportModule annotation = method.getAnnotation(ImportModule.class);
            String moduleName = annotation.value().trim();
            if (StringUtils.isEmpty(moduleName)) {
                // Skip if module name is empty
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

    public static ImportConfig getImportConfig(String moduleName) {
        if (IMPORT_HANDLER_MAP.isEmpty()) {
            initImportHandlerMap();
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

    public static FacilioChain getUploadChain(String moduleName) {
        ImportConfig importConfig = getImportConfig(moduleName);

        Command beforeUploadCommand = null;
        Command afterUploadCommand = null;

        if (importConfig != null) {
            UploadHandler uploadHandler = importConfig.getUploadHandler();
            if (uploadHandler != null) {
                beforeUploadCommand = uploadHandler.getBeforeUploadCommand();
                afterUploadCommand = uploadHandler.getAfterUploadCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeUploadCommand);
        chain.addCommand(new ImportUploadFileCommand());
        addIfNotNull(chain, afterUploadCommand);
        return chain;
    }

    public static FacilioChain getParseChain(String moduleName) {
        ImportConfig importConfig = getImportConfig(moduleName);

        Command beforeParseCommand = null;
        Command afterParseCommand = null;

        if (importConfig != null) {
            ParseHandler parseHandler = importConfig.getParseHandler();
            if (parseHandler != null) {
                beforeParseCommand = parseHandler.getBeforeParseCommand();
                afterParseCommand = parseHandler.getAfterParseCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeParseCommand);
        chain.addCommand(new ParseImportFileCommand());
        addIfNotNull(chain, afterParseCommand);
        chain.addCommand(new InsertImportDataIntoLogCommand());

        return chain;
    }

    public static FacilioChain getImportChain(String moduleName) {
        ImportConfig importConfig = getImportConfig(moduleName);

        Command beforeImportCommand = null;
        Command afterImportCommand = null;

        if (importConfig != null) {
            ImportHandler importHandler = importConfig.getImportHandler();
            if (importHandler != null) {
                beforeImportCommand = importHandler.getBeforeImportCommand();
                afterImportCommand = importHandler.getAfterImportCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeImportCommand);
//        chain.addCommand();

        return chain;
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }
}
