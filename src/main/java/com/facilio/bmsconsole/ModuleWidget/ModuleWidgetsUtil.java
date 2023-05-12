package com.facilio.bmsconsole.ModuleWidget;

import com.facilio.bmsconsole.context.ModuleWidgets;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.annotation.Config;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class ModuleWidgetsUtil {

    private static final Logger LOGGER = Logger.getLogger(ModuleWidgetsUtil.class.getName());
    private static final Map<String, Supplier<ModuleWidgets>> MODULE_WIDGETS_HANDLER_MAP = new HashMap<>();

    public static void initWidgetConfigHandler() throws InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com\\.facilio\\.bmsconsole\\.ModuleWidget"), new MethodAnnotationsScanner());
        fillWidgetTypeMap(reflections);
    }

    private static void fillWidgetTypeMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(WidgetsForModule.class);
        for (Method method: methodsAnnotatedWithModule) {
            validateHandlerMethod(method);

            WidgetsForModule annotation = method.getAnnotation(WidgetsForModule.class);
            String moduleName = annotation.value();

            if (!StringUtils.isNotEmpty(moduleName)) {
                throw new IllegalStateException("Module name cannot be empty");
            }

            Supplier<ModuleWidgets> moduleWidgets = (Supplier<ModuleWidgets>) method.invoke(null, null);

            if (MODULE_WIDGETS_HANDLER_MAP.containsKey(moduleName)) {
                throw new IllegalStateException("Module widgets already present");
            }

            MODULE_WIDGETS_HANDLER_MAP.put(moduleName, moduleWidgets);
        }
    }

    private static void validateHandlerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Module annotation should be part of " + declaringClass.getName() + " Config class.");
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (!(genericParameterTypes == null || genericParameterTypes.length == 0)) {
            throw new IllegalStateException("Method should not have parameters");
        }

        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Supplier.class)) {
            throw new IllegalStateException("Return type should be Supplier<ModuleWidgets>.");
        }
    }

    public static List<WidgetContext> getWidgetsForModuleName(String moduleName) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid module name");

        if(MODULE_WIDGETS_HANDLER_MAP.containsKey(moduleName)) {
            Supplier<ModuleWidgets> supplierModuleWidgets = MODULE_WIDGETS_HANDLER_MAP.get(moduleName);
            ModuleWidgets moduleWidgets = supplierModuleWidgets.get();

            if(moduleWidgets == null) {
                LOGGER.info("Widgets does not exists for module --"+moduleName);
                return null;
            }
            return moduleWidgets.getWidgets();
        }
        else {
            LOGGER.info("Widgets entry does not exists in APIModuleWidgets");
            return new ArrayList<>();
        }
    }
}
