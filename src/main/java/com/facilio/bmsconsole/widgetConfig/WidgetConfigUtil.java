package com.facilio.bmsconsole.widgetConfig;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class WidgetConfigUtil {

    private static final String WIDGET_CONF_PATH = FacilioUtil.normalizePath("conf/pageWidget/widgetConfig.yml");
    private static final String WIDGET_MODULES_PATH = FacilioUtil.normalizePath("conf/pageWidget/widgetModules.yml");
    public static final Map<String, Map<String, WidgetConfigContext>> WIDGET_LIST_CONFIG = initWidgetConfig();
    public static final Map<String, List<String>> WIDGET_MODULES = initWidgetModules();

    public static void fillWidgetModuleAndConfigMap() throws Exception {
        initWidgetModules();
        initWidgetConfig();
    }

    private static Map<String, List<String>> initWidgetModules() {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;

        //read widget to modules
        try (InputStream inputStream = WidgetConfigUtil.class.getClassLoader().getResourceAsStream(WIDGET_MODULES_PATH);) {
            json = yaml.load(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error occured while reading widgetModules yml file of pageBuilder "+e.getMessage());
        }
        Map<String, List<String>> widgetModulesMap = (Map<String, List<String>>)json.get("widgetModules");
        if(MapUtils.isNotEmpty(widgetModulesMap)) {
            return Collections.unmodifiableMap(widgetModulesMap);
        }
        return Collections.emptyMap();

    }
    public static Map<String, Map<String, WidgetConfigContext>> initWidgetConfig() {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;

        //read widget configs
        try (InputStream inputStream = WidgetConfigUtil.class.getClassLoader().getResourceAsStream(WIDGET_CONF_PATH);) {
            json = yaml.load(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error occured while reading widgetConfig yml file of pageBuilder "+e.getMessage());
        }

        Map<String, Map<String, WidgetConfigContext>> widgetListConfigMap = new HashMap<>();
        if(json != null) {
            Map<String, Map<String, Object>> widgets = (Map<String, Map<String, Object>>) json.get("widgets");

            if (MapUtils.isNotEmpty(widgets)) {
                for (Map.Entry<String, Map<String, Object>> widget : widgets.entrySet()) {
                    String widgetTypeName = widget.getKey();
                    Map<String, Object>  layoutWidgetConfigsMap = widget.getValue();

                    Map<String, WidgetConfigContext> widgetConfigsMap = new HashMap<>();
                    if(MapUtils.isNotEmpty(layoutWidgetConfigsMap)) {

                        for (Map.Entry<String, Object> entry : layoutWidgetConfigsMap.entrySet()) {
                            String layoutTypeEnumName = entry.getKey();
                            if (StringUtils.isNotEmpty(layoutTypeEnumName)) {
                                PagesContext.PageLayoutType layoutType = PagesContext.PageLayoutType.valueOf(layoutTypeEnumName);
                                List<Map<String, Object>> widgetConfigsMapList = (List<Map<String, Object>>) entry.getValue();

                                if (CollectionUtils.isNotEmpty(widgetConfigsMapList)) {
                                    for (Map<String, Object> widgetConfig : widgetConfigsMapList) {
                                        addWidgetConfigToList(widgetConfig, widgetConfigsMap, layoutType);
                                    }
                                }
                            }
                        }
                    }
                    widgetListConfigMap.put(widgetTypeName, widgetConfigsMap);
                }
            }
        }
        return Collections.unmodifiableMap(widgetListConfigMap);
    }

    private static void addWidgetConfigToList(Map<String, Object> widgetConfigMap, @NonNull Map<String, WidgetConfigContext> widgetConfigs, @NonNull PagesContext.PageLayoutType layoutType) {
        WidgetConfigContext widgetConfigContext = new WidgetConfigContext();
        String name = (String) widgetConfigMap.get("name");
        widgetConfigContext.setName(name);
        widgetConfigContext.setDisplayName((String) widgetConfigMap.get("displayName"));

        WidgetConfigContext.ConfigType configType = WidgetConfigContext.ConfigType.valueOf((String) widgetConfigMap.get("configType"));
        widgetConfigContext.setConfigType(configType);
        widgetConfigContext.setLayoutType(layoutType);

        String height = (String) widgetConfigMap.get("minHeight");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(height), "Height should be defined for widget -- "+name);
        WidgetConfigContext.WidgetHeight heightEnum = WidgetConfigContext.WidgetHeight.valueOf(height);
        widgetConfigContext.setMinHeight(heightEnum.getHeight());

        String width = (String) widgetConfigMap.get("minWidth");
        FacilioUtil.throwIllegalArgumentException(configType == WidgetConfigContext.ConfigType.FIXED && StringUtils.isEmpty(width), "Height should be defined for widgets -- "+name);
        if(StringUtils.isNotEmpty(width)) {
            WidgetConfigContext.WidgetWidth widthEnum = WidgetConfigContext.WidgetWidth.valueOf(width);
            widgetConfigContext.setMinWidth(widthEnum.getWidth());
        }

        widgetConfigs.put(name, widgetConfigContext);
    }

    public static FacilioContext createWidget(Long appId, String moduleName, long sectionId, PageSectionWidgetContext widget, List<Map<String, Object>> widgetPositions, PagesContext.PageLayoutType layoutType, WidgetWrapperType widgetWrapperType) throws Exception {
        return createWidget(appId, moduleName, sectionId, widget, widgetPositions, layoutType, widgetWrapperType, false);
    }

    public static FacilioContext createWidget(Long appId, String moduleName, long sectionId, PageSectionWidgetContext widget, List<Map<String, Object>> widgetPositions, PagesContext.PageLayoutType layoutType, WidgetWrapperType widgetWrapperType, boolean isSystem) throws Exception {
        FacilioChain createChain = WidgetConfigChain.getCreateChain(widget.getWidgetType().name());

        FacilioContext context = createChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        context.put(FacilioConstants.CustomPage.SECTION_ID, sectionId);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET, widget);
        context.put(FacilioConstants.CustomPage.WIDGETS_POSITIONS, widgetPositions);

        createChain.execute();

        return context;
    }

    public static FacilioContext updateWidget(Long appId, String moduleName, WidgetWrapperType widgetWrapperType, PageSectionWidgetContext pageWidget) throws Exception {
        return updateWidget(appId, moduleName, widgetWrapperType, pageWidget, null);
    }

        public static FacilioContext updateWidget(Long appId, String moduleName, WidgetWrapperType widgetWrapperType, PageSectionWidgetContext pageWidget, List<Map<String, Object>> widgetPositions) throws Exception {
        FacilioChain updateChain = WidgetConfigChain.getUpdateChain(widgetWrapperType, pageWidget.getWidgetType().name());

        FacilioContext context = updateChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET, pageWidget);
        context.put(FacilioConstants.CustomPage.WIDGETS_POSITIONS, widgetPositions);

        updateChain.execute();

        return updateChain.getContext();
    }

    public static PageSectionWidgetContext fetchWidget(Long recordId, Long appId, String moduleName, long id,  WidgetWrapperType widgetWrapperType, boolean isFetchForClone, boolean isBuilderRequest) throws Exception {
            PageSectionWidgetContext widget = getWidgetForWrapperType(id, widgetWrapperType);
            Objects.requireNonNull(widget, "Widget does not exists");

            widget.setWidgetDetail(fetchWidgetDetail(recordId, appId, moduleName, id, widget.getWidgetType(), widgetWrapperType, isFetchForClone, isBuilderRequest));
            return widget;
    }

    public static JSONObject fetchWidgetDetail(Long recordId, Long appId, String moduleName, long id, PageWidget.WidgetType widgetType, WidgetWrapperType widgetWrapperType, boolean isFetchForClone, boolean isBuilderRequest) throws Exception {
        Objects.requireNonNull(widgetType, "widgetType can't be null");
        FacilioChain fetchWidgetDetailChain = WidgetConfigChain.fetchWidgetDetailChain(widgetType.name());

        FacilioContext context = fetchWidgetDetailChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, isFetchForClone);
        context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, isBuilderRequest);
        context.put(FacilioConstants.CustomPage.WIDGETID, id);

        fetchWidgetDetailChain.execute();

        PageSectionWidgetContext widgetDetail = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        return FieldUtil.getAsJSON(widgetDetail);
    }
    public static PageSectionWidgetContext getWidgetForWrapperType(long id, WidgetWrapperType widgetWrapperType) throws Exception {
        FacilioModule module = null;
        List<FacilioField> fields = null;
        if (widgetWrapperType == WidgetWrapperType.DEFAULT) {
            module = ModuleFactory.getPageSectionWidgetsModule();
            fields = FieldFactory.getPageSectionWidgetsFields();
        } else if (widgetWrapperType == WidgetWrapperType.WIDGET_GROUP) {
            module = ModuleFactory.getWidgetGroupWidgetsModule();
            fields = FieldFactory.getWidgetGroupWidgetsFields();
        }

        if (module != null) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            List<Map<String, Object>> props = builder.get();

            if (CollectionUtils.isNotEmpty(props)) {
                return FieldUtil.getAsBeanFromMap(props.get(0), PageSectionWidgetContext.class);
            }
        }

        return null;
    }

    public static <T extends  PageSectionWidgetContext> void setWidgetDetailForWidgets(Long recordId, long appId, String moduleName, Map<Long, T> pageWidgetIdMap, WidgetWrapperType widgetWrapperType, boolean isFetchForClone, boolean isBuilderRequest)throws Exception {
        if (MapUtils.isNotEmpty(pageWidgetIdMap)) {

            for (Map.Entry<Long, T> entry : pageWidgetIdMap.entrySet()) {

                FacilioChain fetchWidgetDetailChain = WidgetConfigChain.fetchWidgetDetailChain(entry.getValue().getWidgetType().name());

                FacilioContext context = fetchWidgetDetailChain.getContext();

                context.put(FacilioConstants.ContextNames.APP_ID, appId);
                context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                context.put(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, isFetchForClone);
                context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, isBuilderRequest);
                context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
                context.put(FacilioConstants.CustomPage.WIDGETID, entry.getValue().getId());
                context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, entry.getValue());

                fetchWidgetDetailChain.execute();

                PageSectionWidgetContext widgetDetail = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

                entry.getValue().setWidgetDetail(FieldUtil.getAsJSON(widgetDetail));
            }
        }
    }

    public static void deleteWidget(long widgetId, List<Map<String,Object>> widgetPositions, WidgetWrapperType widgetWrapperType) throws Exception {
        PageSectionWidgetContext pageWidget = getWidgetForWrapperType(widgetId, widgetWrapperType);
        Objects.requireNonNull(pageWidget, "Widget does not exists");

        FacilioChain deleteChain = WidgetConfigChain.getDeleteWidgetChain(pageWidget.getWidgetType().name());

        FacilioContext context = deleteChain.getContext();
        context.put(FacilioConstants.CustomPage.WIDGETID, widgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGETS_POSITIONS, widgetPositions);
        deleteChain.execute();

    }

    public static void addWidgetDetail(Long appId, String moduleName, PagesContext.PageLayoutType layoutType, PageSectionWidgetContext widget, WidgetWrapperType widgetWrapperType, boolean isSystem) throws Exception {
        FacilioChain addWidgetDetailChain = WidgetConfigChain.addWidgetDetailChain(widget.getWidgetType().name());

        FacilioContext context = addWidgetDetailChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        context.put(FacilioConstants.CustomPage.WIDGETID, widget.getId());
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, WidgetAPI.parsePageWidgetDetails(widget.getWidgetType(), widget.getWidgetDetail()));

        addWidgetDetailChain.execute();
    }
}
