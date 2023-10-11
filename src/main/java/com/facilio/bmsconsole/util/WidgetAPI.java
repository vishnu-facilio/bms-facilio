package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class WidgetAPI {

    private static final Logger LOGGER = Logger.getLogger(WidgetAPI.class.getName());
    public static void addWidgets(String moduleName,List<WidgetContext> widgets)throws Exception{

        LOGGER.info("Started to add widget configurations for ORGID --"+ Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId() +" For module -- "+moduleName);

        if(CollectionUtils.isNotEmpty(widgets)) {

            FacilioChain chain = TransactionChainFactory.getAddWidgetConfigCommand();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(FacilioConstants.Widget.WIDGETS, widgets);
            chain.execute();

            LOGGER.info("Completed adding widget configurations for ORGID --"+ Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId()+" For Module -- "+moduleName);
        }

    }

    public static void addWidgetList(WidgetContext widget)throws Exception {
        FacilioModule module = ModuleFactory.getWidgetListModule();

        if (widget != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(widget);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getWidgetListFields());
            builder.addRecord(prop);
            builder.save();

            widget.setId((Long) prop.get("id"));
        }
    }

    public static List<WidgetContext> getWidgetTypes() throws Exception{
        FacilioModule module = ModuleFactory.getWidgetListModule();
        List<FacilioField> fields = new ArrayList<>(Arrays.asList(FieldFactory.getIdField(module)));
        fields.add(FieldFactory.getStringField("widgetType", "WIDGET_TYPE", module));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields);
        List<Map<String,Object>>  props = builder.get();

        if(CollectionUtils.isNotEmpty(props)){
            return FieldUtil.getAsBeanListFromMapList(props, WidgetContext.class);
        }
        return null;
    }

    public static List<Long> getCommonWidgetIds() throws Exception{
        FacilioModule module = ModuleFactory.getWidgetModuleModule();
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId","MODULEID",module);
        FacilioField idField = FieldFactory.getIdField(ModuleFactory.getWidgetListModule());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .innerJoin(ModuleFactory.getWidgetListModule().getTableName())
                .on("Widget_List.ID = Widget_Modules.WIDGETID")
                .select(Collections.singletonList(idField))
                .andCondition(CriteriaAPI.getCondition(moduleIdField, CommonOperators.IS_EMPTY));
        List<Map<String,Object>>  props = builder.get();

        if(CollectionUtils.isNotEmpty(props)){
            return props.stream().map(f->(Long)f.get("id")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static void addWidgetToModules(List<WidgetToModulesContext> widgetToModules)throws Exception{
        FacilioModule module = ModuleFactory.getWidgetModuleModule();

        if(CollectionUtils.isNotEmpty(widgetToModules)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(widgetToModules, WidgetToModulesContext.class);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getWidgetModuleFields());
            builder.addRecords(props);
            builder.save();
        }

    }

    public static Map<Long,List<Long>> getModuleIdsAsMapOfWidgetId(List<Long> widgetIds)throws Exception{
        FacilioModule module = ModuleFactory.getWidgetModuleModule();
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId","MODULEID",module);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getWidgetModuleFields())
                .andCondition(CriteriaAPI.getCondition(moduleIdField, CommonOperators.IS_NOT_EMPTY));

        if(CollectionUtils.isNotEmpty(widgetIds)){
                builder.andCondition(CriteriaAPI.getConditionFromList("WIDGETID","widgetId",widgetIds,NumberOperators.EQUALS));
        }

        List<Map<String,Object>>  props = builder.get();
        List<WidgetToModulesContext> widgetToModules = FieldUtil.getAsBeanListFromMapList(props, WidgetToModulesContext.class);

        Map<Long, List<Long>> widgetToModulesMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(widgetToModules)){

            widgetToModulesMap = widgetToModules.stream()
                    .collect(Collectors.groupingBy(WidgetToModulesContext::getWidgetId,
                            Collectors.mapping(WidgetToModulesContext::getModuleId, Collectors.toList())));

        }

        return widgetToModulesMap;
    }

    public static void addWidgetConfigs(List<WidgetConfigContext> configs) throws Exception{
        FacilioModule module = ModuleFactory.getWidgetConfigsModule();

        if(CollectionUtils.isNotEmpty(configs)) {

            List<Map<String, Object>> props = FieldUtil.getAsMapList(configs, WidgetConfigContext.class);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getWidgetConfigFields());
            builder.addRecords(props);
            builder.save();
        }

    }

    public static List<WidgetContext> getWidgetsForModule(long moduleId) throws Exception {
        FacilioModule module = ModuleFactory.getWidgetModuleModule();

        if(moduleId > 0){
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(module.getTableName())
                    .select(FieldFactory.getWidgetListFields())
                    .innerJoin(ModuleFactory.getWidgetListModule().getTableName())
                    .on(module.getTableName()+".WIDGETID = Widget_List.ID")
                    .andCondition(CriteriaAPI.getModuleIdIdCondition(moduleId, module))
                    .orCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), CommonOperators.IS_EMPTY));
            List<Map<String, Object>> props = builder.get();

            if(CollectionUtils.isNotEmpty(props)){
                return FieldUtil.getAsBeanListFromMapList(props, WidgetContext.class);
            }
        }
        return null;
    }

    public static WidgetConfigContext getWidgetConfiguration(PageWidget.WidgetType widgetType, Long id, String configName, PagesContext.PageLayoutType layoutType) throws Exception {
        FacilioModule configsModule = ModuleFactory.getWidgetConfigsModule();
        List<FacilioField> fields = FieldFactory.getWidgetConfigFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        FacilioModule widgetListModule = ModuleFactory.getWidgetListModule();
        Map<String, FacilioField> widgetListFieldsMap = FieldFactory.getAsMap(FieldFactory.getWidgetListFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(configsModule.getTableName())
                .select(fields)
                .innerJoin(widgetListModule.getTableName())
                .on(widgetListFieldsMap.get("id").getCompleteColumnName() +"="+fieldsMap.get("widgetId").getCompleteColumnName())
                .andCondition(CriteriaAPI.getEqualsCondition(widgetListFieldsMap.get("widgetType"), widgetType.name()));

        if(id != null && id > 0) {
            builder.andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("id"), String.valueOf(id)));
        }else if(StringUtils.isNotBlank(configName)){
            builder.andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("name"), configName))
                    .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("layoutType"), layoutType.name()));
        }

        List<Map<String, Object>> props = builder.get();
        return CollectionUtils.isEmpty(props)? null : FieldUtil.getAsBeanFromMap(props.get(0), WidgetConfigContext.class);
    }

    public static Long getWidgetConfigId(PageWidget.WidgetType widgetType, long width, long height, WidgetConfigContext.ConfigType configType) throws Exception {
        FacilioModule configsModule = ModuleFactory.getWidgetConfigsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(configsModule.getTableName())
                .select(Arrays.asList(FieldFactory.getIdField(configsModule)))
                .innerJoin(ModuleFactory.getWidgetListModule().getTableName())
                .on("Widget_List.ID = Widget_Configs.WIDGETID")
                .andCondition(CriteriaAPI.getCondition("WIDGET_TYPE","widgetType",widgetType.name(), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("CONFIG_TYPE","configType", configType.name(), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("MIN_HEIGHT","minHeight", String.valueOf(height), NumberOperators.EQUALS));

        if(configType.equals(WidgetConfigContext.ConfigType.FIXED)) {
                builder.andCondition(CriteriaAPI.getCondition("MIN_WIDTH","minWidth", String.valueOf(width),NumberOperators.EQUALS));
        }
        Map<String, Object> prop = builder.fetchFirst();
        if(MapUtils.isNotEmpty(prop)) {
            return (Long) prop.get("id");
        }
        return null;
    }
    public static List<WidgetConfigContext> getWidgetConfigs(long id) throws Exception {
        FacilioModule module = ModuleFactory.getWidgetConfigsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getWidgetConfigFields())
                .andCondition(CriteriaAPI.getEqualsCondition(FieldFactory.getNumberField("widgetId","WIDGETID",module),String.valueOf(id)));

        List<Map<String,Object>> props = builder.get();
        if(CollectionUtils.isNotEmpty(props)){
            return FieldUtil.getAsBeanListFromMapList(props,WidgetConfigContext.class);
        }
        return null;
    }

    public static boolean checkIfWidgetConfigExistsWithLayoutType() throws Exception {

        FacilioModule module = ModuleFactory.getWidgetConfigsModule();

        GenericSelectRecordBuilder withLayoutTypeBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getWidgetConfigFields())
                .andCondition(CriteriaAPI.getCondition("LAYOUT_TYPE","layoutType", "WEB, MOBILE", StringOperators.IS));
        List<Map<String, Object>> withLayoutTypeProps = withLayoutTypeBuilder.get();

        return CollectionUtils.isNotEmpty(withLayoutTypeProps);
    }

    public static PageSectionWidgetContext parsePageWidgetDetails(PageWidget.WidgetType type, JSONObject widgetDetails) throws Exception {
        switch (type){
            case SUMMARY_FIELDS_WIDGET:
            case SR_DETAILS_WIDGET:
                return  FieldUtil.getAsBeanFromJson(widgetDetails, SummaryWidget.class);
            case BULK_RELATED_LIST:
                return FieldUtil.getAsBeanFromJson(widgetDetails, BulkRelatedListContext.class);
            case WIDGET_GROUP:
                return FieldUtil.getAsBeanFromJson(widgetDetails, WidgetGroupContext.class);
            case BULK_RELATION_SHIP_WIDGET:
                return FieldUtil.getAsBeanFromJson(widgetDetails, BulkRelationshipWidget.class);
            case RELATED_LIST:
            case SITE_LIST_WIDGET:
            case CLIENT_CONTACT_LIST_WIDGET:
            case TENANT_CONTACT_RELATED_LIST:
                return FieldUtil.getAsBeanFromJson(widgetDetails, RelatedListWidgetContext.class);
            case RELATIONSHIP_WIDGET:
                return FieldUtil.getAsBeanFromJson(widgetDetails, RelationshipWidget.class);
            default:
                return null;

        }
    }

    public static void setConfigDetailsForWidgets(PagesContext.PageLayoutType layoutType, PageSectionWidgetContext widget) throws Exception {
        FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == null, "widgetType can't be null");
        WidgetConfigContext config = WidgetAPI.getWidgetConfiguration(widget.getWidgetType(), widget.getWidgetConfigId(), widget.getWidgetConfigName(), layoutType);
        Objects.requireNonNull(config, "widget configuration does not exists for configId -- " + widget.getWidgetConfigId() + " or configName -- " + widget.getWidgetConfigName()
                + " for layoutType -- " + layoutType);

        widget.setWidgetConfigId(config.getId());
        widget.setConfigType(config.getConfigType());
        widget.setWidth(config.getMinWidth());
        widget.setHeight(config.getMinHeight());
    }
}
