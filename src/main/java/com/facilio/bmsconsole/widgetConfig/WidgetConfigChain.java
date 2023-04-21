package com.facilio.bmsconsole.widgetConfig;

import com.facilio.bmsconsole.commands.*;
import com.facilio.chain.FacilioChain;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.annotation.Config;
import org.apache.commons.chain.Command;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class WidgetConfigChain {
    private static final Map<String, Supplier<WidgetConfig>> WIDGET_TYPE_HANDLER_MAP = new HashMap<>();

    public static void initWidgetConfigHandler() throws InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com\\.facilio\\.bmsconsole\\.widgetConfig"), new MethodAnnotationsScanner());
        fillWidgetTypeMap(reflections);
    }

    private static void fillWidgetTypeMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithWidgetType = reflections.getMethodsAnnotatedWith(WidgetType.class);
        for (Method method: methodsAnnotatedWithWidgetType) {
            validateHandlerMethod(method);

            WidgetType annotation = method.getAnnotation(WidgetType.class);
            String widgetType = annotation.value().name();

            if (!StringUtils.isNotEmpty(widgetType)) {
                throw new IllegalStateException("Widget type cannot be empty.");
            }

            Supplier<WidgetConfig> config = (Supplier<WidgetConfig>) method.invoke(null, null);

            if (WIDGET_TYPE_HANDLER_MAP.containsKey(widgetType)) {
                throw new IllegalStateException("Widget config already present.");
            }

            WIDGET_TYPE_HANDLER_MAP.put(widgetType, config);
        }
    }

    private static void validateHandlerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Widget annotation should be part of " + declaringClass.getName() + " Config class.");
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (!(genericParameterTypes == null || genericParameterTypes.length == 0)) {
            throw new IllegalStateException("Method should not have parameters");
        }

        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Supplier.class)) {
            throw new IllegalStateException("Return type should be Supplier<WidgetConfig>.");
        }
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    public static WidgetConfig getWidgetConfig (String widgetType) {
        FacilioUtil.throwIllegalArgumentException(!StringUtils.isNotEmpty(widgetType), "Invalid widgetType to fetch widgetConfig");

        return WIDGET_TYPE_HANDLER_MAP.containsKey(widgetType) ? WIDGET_TYPE_HANDLER_MAP.get(widgetType).get() : null;
    }

    public static FacilioChain getCreateChain(String widgetType) throws Exception {
        WidgetConfig widgetConfig = getWidgetConfig(widgetType);

        Command saveCommand = null;

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (widgetConfig != null) {
            WidgetCreateHandler createHandler = widgetConfig.getWidgetCreateHandler();

            if (createHandler != null) {
                saveCommand = createHandler.getSaveCommand();
            }
        }

        transactionChain.addCommand(new AddPageSectionWidgetCommand());
        addIfNotNull(transactionChain, saveCommand);
        transactionChain.addCommand(new UpdatePageWidgetPositionsCommand());

        return transactionChain;
    }

    public static FacilioChain getUpdateChain(String widgetType) throws Exception{
        WidgetConfig widgetConfig = getWidgetConfig(widgetType);

        Command updateCommand = null;

        if(widgetConfig != null) {
            WidgetUpdateHandler widgetUpdateHandler = widgetConfig.getWidgetUpdateHandler();

            if(widgetUpdateHandler != null) {
                updateCommand = widgetUpdateHandler.getUpdateCommand();
            }
        }

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        transactionChain.addCommand(new UpdatePageWidgetCommand());
        addIfNotNull(transactionChain, updateCommand);
        transactionChain.addCommand(new UpdatePageWidgetPositionsCommand());

        return transactionChain;
    }

    public static FacilioChain getDeleteWidgetChain(String widgetType) throws Exception {
        WidgetConfig widgetConfig = getWidgetConfig(widgetType);

        Command deleteCommand = null;

        if(widgetConfig != null) {
            WidgetDeleteHandler deleteHandler = widgetConfig.getWidgetDeleteHandler();

            if(deleteHandler != null) {
                deleteCommand = deleteHandler.getDeleteCommand();
            }
        }

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        transactionChain.addCommand(new DeletePageWidgetCommand());
        addIfNotNull(transactionChain, deleteCommand);
        transactionChain.addCommand(new UpdatePageWidgetPositionsCommand());

        return transactionChain;
    }
    public static FacilioChain fetchWidgetDetailChain(String widgetType, Boolean isFetchForSection) throws Exception {
        WidgetConfig widgetConfig = getWidgetConfig(widgetType);

        Command fetchCommand = null;

        if (widgetConfig != null) {
            WidgetSummaryHandler summaryHandler = widgetConfig.getWidgetSummaryHandler();

            if (summaryHandler != null) {
                fetchCommand = summaryHandler.getFetchCommand();
            }
        }

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        if(!isFetchForSection) {
            nonTransactionChain.addCommand(new GetPageSectionWidgetCommand());
        }
        addIfNotNull(nonTransactionChain, fetchCommand);

        return nonTransactionChain;
    }


}
