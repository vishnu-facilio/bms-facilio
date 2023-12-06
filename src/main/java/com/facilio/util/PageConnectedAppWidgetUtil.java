package com.facilio.util;

import com.facilio.bmsconsole.context.PageConnectedAppWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PageConnectedAppWidgetUtil {

    public static List<Long> getConnectedAppWidgetIdForWidgetIds(List<Long> widgetIds) throws Exception {
        return getConnectedAppWidgetIdForWidgetIds(widgetIds, WidgetWrapperType.DEFAULT);
    }

    public static List<Long> getConnectedAppWidgetIdForWidgetIds(List<Long> widgetIds, WidgetWrapperType widgetWrapperType) throws Exception {
        List<PageConnectedAppWidgetContext> pageConnectedAppWidgetContexts = getPageConnectedAppWidgets(widgetIds, widgetWrapperType);
        if(CollectionUtils.isNotEmpty(pageConnectedAppWidgetContexts)) {
            return pageConnectedAppWidgetContexts.stream().map(PageConnectedAppWidgetContext::getConnectedAppWidgetId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static long getConnectedAppWidgetIdForWidgetId(Long widgetId) throws Exception {
        return getConnectedAppWidgetIdForWidgetId(widgetId, WidgetWrapperType.DEFAULT);
    }

    public static long getConnectedAppWidgetIdForWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        PageConnectedAppWidgetContext pageConnectedAppWidgetContext = getPageConnectedAppWidget(widgetId, widgetWrapperType);
        return pageConnectedAppWidgetContext != null ? pageConnectedAppWidgetContext.getConnectedAppWidgetId() : -1L;
    }

    public static PageConnectedAppWidgetContext getPageConnectedAppWidget(long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        List<PageConnectedAppWidgetContext> pageConnectedAppWidgetContexts = getPageConnectedAppWidgets(Arrays.asList(widgetId), widgetWrapperType);
        return CollectionUtils.isNotEmpty(pageConnectedAppWidgetContexts)? pageConnectedAppWidgetContexts.get(0) : null;
    }
    public static List<PageConnectedAppWidgetContext> getPageConnectedAppWidgets(List<Long> widgetIds, WidgetWrapperType widgetWrapperType) throws Exception {
        FacilioModule pageConnectedAppWidgetModule = ModuleFactory.getPageConnectedAppWidgetModule();
        List<FacilioField> fields = FieldFactory.getPageConnectedAppWidgetFields();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageConnectedAppWidgetModule, widgetWrapperType);

        Criteria pageWidgetIdCriteria = new Criteria();
        pageWidgetIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, StringUtils.join(widgetIds, ",")));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(pageConnectedAppWidgetModule.getTableName())
                .andCriteria(pageWidgetIdCriteria);

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageConnectedAppWidgetContext.class);
        }
        return null;
    }

    public static void addPageConnectedAppWidget(Long widgetId, Long connectedAppWidgetId) throws Exception {
        addPageConnectedAppWidget(widgetId, WidgetWrapperType.DEFAULT,connectedAppWidgetId);
    }

    public static void addPageConnectedAppWidget(Long widgetId, WidgetWrapperType widgetWrapperType, Long connectedAppWidgetId) throws Exception {
        FacilioModule pageConnectedAppWidgetModule = ModuleFactory.getPageConnectedAppWidgetModule();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageConnectedAppWidgetModule, widgetWrapperType);
        Map<String, Object> prop = new HashMap<>();

        prop.put(widgetIdField.getName(), widgetId);
        prop.put("connectedAppWidgetId", connectedAppWidgetId);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(pageConnectedAppWidgetModule.getTableName())
                .fields(FieldFactory.getPageConnectedAppWidgetFields())
                .addRecord(prop);
        builder.save();
    }

    public static void updatePageConnectedAppWidget(Long widgetId, long summaryWidgetId) throws Exception {
        updatePageConnectedAppWidget(widgetId, WidgetWrapperType.DEFAULT, summaryWidgetId);
    }
    public static void updatePageConnectedAppWidget(Long widgetId, WidgetWrapperType widgetWrapperType, long summaryWidgetId) throws Exception{
        FacilioModule pageConnectedAppWidgetModule = ModuleFactory.getPageConnectedAppWidgetModule();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageConnectedAppWidgetModule, widgetWrapperType);

        Criteria pageWidgetIdCriteria = new Criteria();
        pageWidgetIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));

        Map<String, Object> prop = new HashMap<>();
        prop.put(widgetIdField.getName(), widgetId);
        prop.put("connectedAppWidgetId", summaryWidgetId);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(pageConnectedAppWidgetModule.getTableName())
                .fields(FieldFactory.getPageConnectedAppWidgetFields())
                .andCriteria(pageWidgetIdCriteria);
        builder.update(prop);
    }

    public static List<Long> getExistingConnectedAppWidgetIdsInPage(long pageId) throws Exception {
        Map<String, FacilioField> pageSectionWidgetsFieldsMap = FieldFactory.getAsMap(FieldFactory.getPageSectionWidgetsFields());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(pageId, ModuleFactory.getPagesModule()));
        criteria.addAndCondition(CriteriaAPI.getEqualsCondition(pageSectionWidgetsFieldsMap.get("widgetType"), PageWidget.WidgetType.CONNNECTED_APP.name()));
        List<Map<String, Object>> widgetIdsMap = PagesUtil.getPageComponent(CustomPageAPI.PageComponent.WIDGET, Arrays.asList(pageSectionWidgetsFieldsMap.get("id")), criteria);

        if(CollectionUtils.isNotEmpty(widgetIdsMap)) {
            List<Long> pageWidgetIds =  widgetIdsMap.stream().map(f->(long)f.get("id")).collect(Collectors.toList());
            return PageConnectedAppWidgetUtil.getConnectedAppWidgetIdForWidgetIds(pageWidgetIds);

        }
        return Collections.emptyList();
    }

}
