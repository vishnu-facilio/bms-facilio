package com.facilio.v3;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GenerateSearchConditionCommand;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.ListCommand;
import com.facilio.v3.commands.SaveCommand;
import com.facilio.v3.commands.SummaryCommand;
import com.facilio.v3.commands.UpdateCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler {
    private static final Map<String, V3Config> MODULE_HANDLER_MAP = new HashMap<>();

    public static void initRESTAPIHandler(String packageName) throws InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName), new MethodAnnotationsScanner());
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Module.class);

        for (Method method: methodsAnnotatedWithModule) {
            validateHandlerMethod(method);

            Module annotation = method.getAnnotation(Module.class);
            String moduleName = annotation.value().trim();
            if (moduleName.isEmpty()) {
                throw new IllegalStateException("Module name cannot be empty.");
            }

            V3Config config = (V3Config) method.invoke(null, null);

            if (MODULE_HANDLER_MAP.containsKey(moduleName)) {
                throw new IllegalStateException("Module config already present.");
            }

            MODULE_HANDLER_MAP.put(moduleName, config);
        }
    }

    private static void validateHandlerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Module annotation should be part of Config class.");
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (!(genericParameterTypes == null || genericParameterTypes.length == 0)) {
            throw new IllegalStateException("Method should not have parameters");
        }

        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(V3Config.class)) {
            throw new IllegalStateException("Return type should be V3Config.");
        }
    }

    private static Map<String, Object> handleSummaryRequest(String moduleName, long id, Map<String, Object> params) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);
        V3Config.SummaryHandler summaryHandler = v3Config.getSummaryHandler();

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new SummaryCommand(module));
        if (summaryHandler != null && summaryHandler.getAfterFetchCommand() != null) {
            nonTransactionChain.addCommand(summaryHandler.getAfterFetchCommand());
        }

        FacilioContext context = nonTransactionChain.getContext();

        context.put(FacilioConstants.ContextNames.RECORD_ID, id);

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        return null;
    }

    private static FacilioModule getModule(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (module == null) {
            //TODO proper excepition handling
            throw new IllegalArgumentException("Invalid module");
        }
        return module;
    }

    private static List<Map<String, Object>> handleListRequest(String moduleName, Map<String, Object> params) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());

        if (v3Config.getListHandler() != null && v3Config.getListHandler().getBeforeFetchCommand() != null) {
            nonTransactionChain.addCommand(v3Config.getListHandler().getBeforeFetchCommand());
        }

        nonTransactionChain.addCommand(new ListCommand(module));

        if (v3Config.getListHandler() != null && v3Config.getListHandler().getAfterFetchCommand() != null) {
            nonTransactionChain.addCommand(v3Config.getListHandler().getAfterFetchCommand());
        }

        FacilioContext context = nonTransactionChain.getContext();

        List<FacilioField> fieldsByAccessType = FieldUtil.getFieldsByAccessType((long) FacilioField.AccessType.CRITERIA.getIntVal(), moduleName);
        String search = (String) params.get("search");
        if (search != null && !CollectionUtils.isEmpty(fieldsByAccessType)) {
            JSONObject searchObj = new JSONObject();
            searchObj.put("fields", fieldsByAccessType.stream().map(field -> moduleName + "." + field.getName()).collect(Collectors.joining(",")));
            searchObj.put("query", search);
            context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
        }

        context.put(FacilioConstants.ContextNames.CV_NAME, params.get("viewName"));
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        String filters = (String) params.get("filters");
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(Constants.FILTERS, json);

            boolean includeParentFilter = params.get("includeParentFilter") == null ? false : (Boolean) params.get("includeParentFilter");
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, includeParentFilter);
        }

        boolean overrideViewOrderBy = params.get("overrideViewOrderBy") == null ? false : (Boolean) params.get("overrideViewOrderBy");
        Object orderBy = params.get("orderBy");
        Object orderByType = params.get("orderType");

        if (overrideViewOrderBy && orderBy != null) {
            JSONObject sorting = new JSONObject();
            sorting.put("orderBy", orderBy);
            sorting.put("orderType", orderByType);
            context.put(FacilioConstants.ContextNames.SORTING, sorting);
            context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
        }

        JSONObject pagination = new JSONObject();
        pagination.put("page", params.get("page"));
        pagination.put("perPage", params.get("perPage"));

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        return null;
    }

    private static V3Config getV3Config(String moduleName) {
        V3Config v3Config = MODULE_HANDLER_MAP.get(moduleName);
        return v3Config;
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    private static void createHandler(String moduleName, Map<String, Object> createObj, Map<String, Object> params) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        V3Config.CreateHandler createHandler = v3Config.getCreateHandler();
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        if (createHandler != null) {
            addIfNotNull(transactionChain, createHandler.getInitCommand());
            addIfNotNull(transactionChain, createHandler.getBeforeSaveCommand());
        }

        transactionChain.addCommand(new SaveCommand(module));

        if (createHandler != null) {
            addIfNotNull(transactionChain, createHandler.getAfterSaveCommand());
            addIfNotNull(transactionChain, createHandler.getAfterTransactionCommand());
        }

        FacilioContext context = transactionChain.getContext();

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(moduleName, Arrays.asList(createObj));

        context.put(Constants.RECORD_MAP, recordMap);
        transactionChain.execute();
    }

    private static void updateHandler(String moduleName, long id, Map<String, Object> updateObj, Map<String, Object> params) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        V3Config.UpdateHandler updateHandler = v3Config.getUpdateHandler();
        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (updateHandler != null) {
            addIfNotNull(transactionChain, updateHandler.getInitCommand());
            addIfNotNull(transactionChain, updateHandler.getBeforeSaveCommand());
        }

        transactionChain.addCommand(new UpdateCommand(module));

        if (updateHandler != null) {
            addIfNotNull(transactionChain, updateHandler.getAfterSaveCommand());
            addIfNotNull(transactionChain, updateHandler.getAfterTransactionCommand());
        }

        FacilioContext context = transactionChain.getContext();

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(moduleName, Arrays.asList(updateObj));
        context.put(Constants.RECORD_MAP, recordMap);

        transactionChain.execute();
    }

    private static void deleteHandler(String moduleName, long id) {

    }
}
