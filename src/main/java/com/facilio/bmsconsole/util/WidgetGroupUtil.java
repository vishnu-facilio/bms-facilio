package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WidgetGroupUtil {
    private static final Logger LOGGER = Logger.getLogger(WidgetGroupUtil.class.getName());

    public static void insertWidgetGroupConfigToDB(WidgetGroupConfigContext config) throws Exception {
        Map<String, Object> prop = FieldUtil.getAsProperties(config);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWidgetGroupConfigModule().getTableName())
                .fields(FieldFactory.getWidgetGroupConfigFields())
                .addRecord(prop);
        builder.save();
    }

    public static List<WidgetGroupSectionContext> insertWGSectionsToDB(List<WidgetGroupSectionContext> sections) throws Exception{

        List<Map<String, Object>> props = FieldUtil.getAsMapList(sections, WidgetGroupSectionContext.class);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWidgetGroupSectionsModule().getTableName())
                .fields(FieldFactory.getWidgetGroupSectionsFields())
                .addRecords(props);
        builder.save();
        sections = FieldUtil.getAsBeanListFromMapList(props, WidgetGroupSectionContext.class);
        return sections;
    }

    public static void insertWidgetGroupSectionsToDB(List<WidgetGroupSectionContext> widgetGroupSections) throws Exception {
        if (CollectionUtils.isNotEmpty(widgetGroupSections)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(widgetGroupSections, WidgetGroupSectionContext.class);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getWidgetGroupSectionsModule().getTableName())
                    .fields(FieldFactory.getWidgetGroupSectionsFields())
                    .addRecords(props);
            builder.save();

            for (WidgetGroupSectionContext section : widgetGroupSections) {
                section.setId(props.stream().filter(f -> (long)f.get("widgetId") == section.getWidgetId() && f.get("name").equals(section.getName())).mapToLong(f -> (Long) f.get("id")).findFirst().getAsLong());
            }
        }
    }

    public static List<WidgetGroupWidgetContext> setWGSectionIdAndGetWidgets(List<WidgetGroupSectionContext> sections) {
        List<WidgetGroupWidgetContext> widgets = new ArrayList<>();

        for (WidgetGroupSectionContext section : sections) {

            if (CollectionUtils.isNotEmpty(section.getWidgets())) {
                section.getWidgets().forEach(f -> f.setSectionId(section.getId()));

                for(WidgetGroupWidgetContext widget:section.getWidgets()) {
                    FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == null,
                            "WidgetGroupSection widget's Type should be defined");

                    long count = section.getWidgets().stream().filter(widgetGroupWidget ->widgetGroupWidget.getWidgetType() == PageWidget.WidgetType.WIDGET_GROUP)
                            .count();
                    FacilioUtil.throwIllegalArgumentException(count > 0L, "Widget Group widget's type can't be 'WIDGETGROUP'");
                }
                widgets.addAll(section.getWidgets());
            }
        }
        return widgets;
    }

    public static void insertWidgetGroupWidgetToDB(List<WidgetGroupWidgetContext> widgets) throws Exception {
        List<Map<String, Object>> props = new ArrayList<>();
        for(WidgetGroupWidgetContext widget : widgets) {
            Map<String, Object> prop = FieldUtil.getAsProperties(widget);
            prop.replace("widgetParams", widget.getWidgetParamsAsString());
            props.add(prop);
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWidgetGroupWidgetsModule().getTableName())
                .fields(FieldFactory.getWidgetGroupWidgetsFields())
                .addRecords(props);
        builder.save();

        for (WidgetGroupWidgetContext widget : widgets) {
            widget.setId(props.stream().filter(f->(long)f.get("sectionId") == widget.getSectionId() && f.get("name").equals(widget.getName())).mapToLong(f-> (Long) f.get("id")).findFirst().getAsLong());
        }
    }

    public static void insertWidgetGroupWidgetsToDB(List<WidgetGroupWidgetContext> widgets) throws Exception {
        if(CollectionUtils.isNotEmpty(widgets)) {
            CustomPageAPI.insertPageWidget(widgets, WidgetWrapperType.WIDGET_GROUP);
        }
    }

    public static WidgetGroupConfigContext getWidgetGroupConfig(long widgetGroupId) throws Exception{
        FacilioModule module = ModuleFactory.getWidgetGroupConfigModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWidgetGroupConfigFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("WIDGET_ID","widgetId", String.valueOf(widgetGroupId), NumberOperators.EQUALS));
        List<Map<String,Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), WidgetGroupConfigContext.class);
        }
        return null;
    }

    public static List<WidgetGroupSectionContext> fetchWidgetGroupsSections(long widgetId) throws Exception{
        FacilioModule module = ModuleFactory.getWidgetGroupSectionsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWidgetGroupSectionsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("WIDGET_ID","widgetId", String.valueOf(widgetId),NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, WidgetGroupSectionContext.class);
        }
        return null;
    }

    public static Map<Long,List<WidgetGroupWidgetContext>> fetchWidgetGroupWidgets(Long recordId, Long appId, String moduleName, List<Long> sectionIds, boolean isFetchForClone, boolean isBuilderRequest) throws Exception{
        FacilioModule module = ModuleFactory.getWidgetGroupWidgetsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWidgetGroupWidgetsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("SECTION_ID","sectionId",sectionIds,NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");
        List<WidgetGroupWidgetContext> widgets = FieldUtil.getAsBeanListFromMapList(builder.get(),WidgetGroupWidgetContext.class);

        if(CollectionUtils.isNotEmpty(widgets)) {
            Map<Long, PageSectionWidgetContext> widgetIdMap = widgets.stream()
                    .peek(f->f.setWidgetWrapperType(WidgetWrapperType.WIDGET_GROUP))
                    .collect(Collectors.toMap(PageSectionWidgetContext::getId, Function.identity()));

            WidgetConfigUtil.setWidgetDetailForWidgets(recordId, appId, moduleName, widgetIdMap, WidgetWrapperType.WIDGET_GROUP, isFetchForClone, isBuilderRequest);
        }
        return widgets.stream()
                .collect(Collectors.groupingBy(WidgetGroupWidgetContext::getSectionId));
    }
}
