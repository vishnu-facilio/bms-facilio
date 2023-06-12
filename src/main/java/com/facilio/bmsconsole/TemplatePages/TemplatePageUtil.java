package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.modules.FacilioModule;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemplatePageUtil {

    private static final Map<String, Class<?>> MODULE_TEMPLATE_HANDLER_MAP = new HashMap<>();

    private static void fillModuleTemplateHandler() throws Exception {
        Reflections reflections = new Reflections("com.facilio.bmsconsole.TemplatePages");

        Set<Class<? extends TemplatePageFactory>> templatePageImplementations = reflections.getSubTypesOf(TemplatePageFactory.class);


        for (Class<? extends TemplatePageFactory> implementation : templatePageImplementations) {

            Method[] methods = implementation.getMethods();
            String moduleName = null;

            for (Method method : methods) {
                if (method.getName().equals("getModuleName")) {
                    moduleName = (String) method.invoke(implementation.getConstructor().newInstance(), null);
                }
            }

            if (StringUtils.isEmpty(moduleName)) {
                throw new IllegalStateException("moduleName cannot be empty");
            }


            if (MODULE_TEMPLATE_HANDLER_MAP.containsKey(moduleName)) {
                throw new IllegalStateException("moduleName already present.");
            }

            MODULE_TEMPLATE_HANDLER_MAP.put(moduleName, implementation);
        }
    }

    public static PagesContext getTemplatePageFromFactory(FacilioModule module, ApplicationContext app) throws Exception {

        if(MapUtils.isEmpty(MODULE_TEMPLATE_HANDLER_MAP)) {
            fillModuleTemplateHandler();
        }

        Class<?> templatePageClass = null;
        if(module.isCustom()) {
             templatePageClass = MODULE_TEMPLATE_HANDLER_MAP.get("custom");
        }
        else if(MODULE_TEMPLATE_HANDLER_MAP.containsKey(module.getName())) {
            templatePageClass = MODULE_TEMPLATE_HANDLER_MAP.get(module.getName());
        }
        else {
            throw new IllegalArgumentException(module.getDisplayName()+" does not contains template page");
        }

        for (Method method : templatePageClass.getDeclaredMethods()) {
            if (method.getName().equals("getTemplatePage")) {
                return (PagesContext) method.invoke(templatePageClass.getConstructor().newInstance(),app, module);
            }
        }
        return null;
    }

}
