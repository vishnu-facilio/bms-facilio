package com.facilio.bmsconsole.widgetConfig;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WidgetConfigUtil {

    public static FacilioContext createWidget(Long appId, String moduleName, long sectionId, PageSectionWidgetContext widget, List<Map<String, Object>> widgetPositions) throws Exception {
        FacilioChain createChain = WidgetConfigChain.getCreateChain(widget.getWidgetType().name());

        FacilioContext context = createChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.SECTION_ID, sectionId);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, widget);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS_POSITIONS, widgetPositions);

        createChain.execute();

        return context;
    }

    public static FacilioContext updateWidget(PageSectionWidgetContext pageWidget) throws Exception {
        return updateWidget(pageWidget, null);
    }

        public static FacilioContext updateWidget(PageSectionWidgetContext pageWidget, List<Map<String, Object>> widgetPositions) throws Exception {
        FacilioChain updateChain = WidgetConfigChain.getUpdateChain(pageWidget.getWidgetType().name());

        FacilioContext context = updateChain.getContext();

        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, pageWidget);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS_POSITIONS, widgetPositions);

        updateChain.execute();

        return updateChain.getContext();
    }

    public static FacilioContext fetchWidget(long pageWidgetId) throws Exception {
            PageSectionWidgetContext pageWidget = CustomPageAPI.getPageSectionWidget(pageWidgetId);
            Objects.requireNonNull(pageWidget, "Widget does not exists");

            FacilioChain fetchWidgetDetailChain = WidgetConfigChain.fetchWidgetDetailChain(pageWidget.getWidgetType().name(), false);

            FacilioContext context = fetchWidgetDetailChain.getContext();

            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, pageWidget);

            fetchWidgetDetailChain.execute();

            PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

            pageWidget.setWidgetDetail(FieldUtil.getAsJSON(widget));

            return context;
    }

    public static void setWidgetDetailForWidgets(Map<Long, PageSectionWidgetContext> pageWidgetIdMap)throws Exception {
        if (MapUtils.isNotEmpty(pageWidgetIdMap)) {

            for (Map.Entry<Long, PageSectionWidgetContext> entry : pageWidgetIdMap.entrySet()) {

                FacilioChain fetchWidgetDetailChain = WidgetConfigChain.fetchWidgetDetailChain(entry.getValue().getWidgetType().name(), true);

                FacilioContext context = fetchWidgetDetailChain.getContext();

                context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, entry.getValue().getId());
                context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, entry.getValue());

                fetchWidgetDetailChain.execute();

                PageSectionWidgetContext widgetDetail = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

                entry.getValue().setWidgetDetail(FieldUtil.getAsJSON(widgetDetail));
            }
        }
    }

    public static void deleteWidget(long pageWidgetId, List<Map<String,Object>> widgetPositions) throws Exception {
        PageSectionWidgetContext pageWidget = CustomPageAPI.getPageSectionWidget(pageWidgetId);
        Objects.requireNonNull(pageWidget, "Widget does not exists");

        FacilioChain deleteChain = WidgetConfigChain.getDeleteWidgetChain(pageWidget.getWidgetType().name());

        FacilioContext context = deleteChain.getContext();
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS_POSITIONS, widgetPositions);
        deleteChain.execute();

    }
}
