package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CustomPageAPI {

    private static final Logger LOGGER = Logger.getLogger(CustomPageAPI.class.getName());

    public static CustomPageWidget getMainSummaryWidget(long moduleId) throws Exception {
        List<FacilioField> fields = FieldFactory.getCustomPageWidgetFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));

        List<CustomPageWidget> pageWidgets = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), CustomPageWidget.class);
        if (CollectionUtils.isNotEmpty(pageWidgets)){
            return pageWidgets.get(0);
        }
        return null;
    }

    public static CustomPageWidget getCustomPageWidget(long appId, long widgetId, String widgetName, long moduleId) throws Exception{
        List<CustomPageWidget> pageWidgets = null;
        ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }

        List<FacilioField> fields = FieldFactory.getCustomPageWidgetFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));

        if (moduleId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
        }

        if (widgetId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"),String.valueOf(widgetId), NumberOperators.EQUALS));
        } else if (StringUtils.isNotEmpty(widgetName)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), widgetName, StringOperators.IS));
        }

        pageWidgets = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), CustomPageWidget.class);
        if (CollectionUtils.isNotEmpty(pageWidgets)){
            return pageWidgets.get(0);
        }
        return null;
    }

    public static List<SummaryWidgetGroup> getCustomWidgetGroups(long widgetId) throws Exception{
        List<SummaryWidgetGroup> widgetGroups = null;

        List<FacilioField> fields = FieldFactory.getSummaryWidgetGroupFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getSummaryWidgetGroupModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId") ,String.valueOf(widgetId), NumberOperators.EQUALS));

        widgetGroups = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidgetGroup.class);
        return widgetGroups;
    }

    public static List<SummaryWidgetGroupFields> getCustomWidgetGroupFields(long widgetId) throws Exception{
        List<SummaryWidgetGroupFields> widgetGroupFields = null;

        List<FacilioField> fields = FieldFactory.getSummaryWidgetGroupFieldsFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getSummaryWidgetGroupFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId"), String.valueOf(widgetId), NumberOperators.EQUALS));

        widgetGroupFields = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidgetGroupFields.class);
        return widgetGroupFields;
    }
    public static double getMaxSequenceNumber(FacilioModule module, Criteria criteria) throws Exception {

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(new HashSet<>())
                .table(module.getTableName())
                .andCriteria(criteria)
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.MAX, FieldFactory.getField("sequenceNumber", "SEQUENCE_NUMBER", FieldType.DECIMAL));
        Map<String, Object> map = builder.fetchFirst();

        if (MapUtils.isNotEmpty(map)) {
            return (Double) map.get("sequenceNumber");
        }
        return 0D;
    }

    public static double getSequenceNumberForNewPage(PagesContext defaultPage,long appId, long moduleId)throws Exception{
        if(defaultPage == null || defaultPage.getId() == -1){
            defaultPage = getDefaultPage(appId, moduleId);
        }
        if(defaultPage != null) {
            double sequenceNumber = defaultPage.getSequenceNumber();
            defaultPage.setSequenceNumber(sequenceNumber + 10);
            CustomPageAPI.updateFetchedPage(defaultPage);
            return sequenceNumber;
        }
        return 10;
    }
    public static Map<Long, List<String>> getExistingNameListAsMap(FacilioModule module, Criteria criteria, FacilioField parentField) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        FacilioField nameField = FieldFactory.getField("name", "NAME", module, FieldType.STRING);

        if (parentField != null) {
            fields.add(parentField);
            fields.add(nameField);

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(module.getTableName())
                    .andCriteria(criteria);
            List<Map<String, Object>> props = builder.get();

            Map<Long, List<String>> nameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(props)) {
                nameMap = props.stream()
                        .filter(prop -> prop.containsKey(parentField.getName()) && prop.containsKey(nameField.getName()))
                        .collect(Collectors.groupingBy(
                                prop -> (Long) prop.get(parentField.getName()), // key
                                Collectors.mapping(prop -> (String) prop.get(nameField.getName())//value
                                        , Collectors.toList())));
            }
            return nameMap;
        }
        LOGGER.info("Since parent field is null, nameMap is Empty");
        return new HashMap<>();
    }
    public static String generateUniqueName(String name, List<String> nameList) {
        while (nameList != null && nameList.contains(name)) {
            if (name.contains("_")) {
                name = name.split("_", 1)[0];
            }
            String finalName = name;
            int noToAppend = (int) nameList.stream().filter(e -> e.contains(finalName + "_")).count() + 1;
            name = name + "_" + (noToAppend);
        }
        return name;
    }
    public static PagesContext insertCustomPageToDB(PagesContext customPage) throws Exception {
        Map<String, Object> prop = FieldUtil.getAsProperties(customPage);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getPagesModule().getTableName())
                .fields(FieldFactory.getPagesFields())
                .addRecord(prop);
        builder.save();
        return FieldUtil.getAsBeanFromMap(prop, PagesContext.class);
    }
    public static PageTabsContext insertPageTabsToDB(PageTabsContext tab) throws Exception {
        if(tab != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(tab);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageTabsModule().getTableName())
                    .fields(FieldFactory.getPageTabsFields())
                    .addRecord(prop);
            builder.save();

            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy",tab.getSysCreatedBy());
            sysModifiedProps.put("sysModifiedTime",tab.getSysCreatedTime());
            updateSysModifiedFields(tab.getPageId(), sysModifiedProps, PageComponent.PAGE);
            return FieldUtil.getAsBeanFromMap(prop, PageTabsContext.class);
        }
        return null;
    }
    public static List<Map<String, Object>> getAllCustomPageForBuilder(long appId, long moduleId) throws Exception{
        FacilioModule module = ModuleFactory.getPagesModule();

        List<FacilioField> fields = FieldFactory.getPagesFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"),String.valueOf(false)))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(module), CommonOperators.IS_EMPTY))
                .orderBy("SEQUENCE_NUMBER, ID");
        return builder.get();
    }
    public static List<PagesContext> getAllCustomPage(long appId, long moduleId) throws Exception {
        FacilioModule module = ModuleFactory.getPagesModule();
        List<FacilioField> fields = FieldFactory.getPagesFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"),String.valueOf(false)))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(module), CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS))
                .orderBy("SEQUENCE_NUMBER, ID");

        List<Map<String, Object>> props = builder.get();
        if (props != null && !props.isEmpty()) {
            return FieldUtil.getAsBeanListFromMapList(props, PagesContext.class);
        }

        return null;
    }
    public static PagesContext getCustomPage(Long id) throws Exception {
        FacilioModule module = ModuleFactory.getPagesModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPagesFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(module), CommonOperators.IS_EMPTY))
                .limit(1);

        List<Map<String, Object>> props = builder.get();
        if (props != null && !props.isEmpty()) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PagesContext.class);
        }
        return null;
    }
    public static List<PagesContext> getAllRelatedPagesToOrder(long id) throws Exception{
        PagesContext page = getCustomPage(id);

        if(page != null) {
            FacilioModule module = ModuleFactory.getPagesModule();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPagesFields());

            List<FacilioField> fields = new ArrayList<>();
            fields.add(fieldsMap.get("id"));
            fields.add(fieldsMap.get("moduleId"));
            fields.add(fieldsMap.get("appId"));
            fields.add(fieldsMap.get("sequenceNumber"));
            fields.add(fieldsMap.get("isDefaultPage"));

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(module.getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("moduleId"), String.valueOf(page.getModuleId())))
                    .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(page.getAppId())))
                    .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"),String.valueOf(false)))
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(module), CommonOperators.IS_EMPTY));

            List<Map<String, Object>> props = builder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                return FieldUtil.getAsBeanListFromMapList(props, PagesContext.class);
            }
        }
        return null;
    }
    public static PageTabsContext getTab(Long id, Long pageId, String tabName) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageTabsFields())
                .table(module.getTableName());

        if(id != null && id > 0){
            builder.andCondition(CriteriaAPI.getIdCondition(id, module));
        }
        else if(pageId != null && pageId > 0 && StringUtils.isNotEmpty(tabName)){
            builder.andCondition(CriteriaAPI.getNameCondition(tabName, module))
                    .andCondition(CriteriaAPI.getCondition("PAGE_ID","pageId", String.valueOf(pageId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PageTabsContext.class);
        }
        return null;
    }


    public static PagesContext getDefaultPage(long appId, long moduleId) throws Exception{
        FacilioModule module = ModuleFactory.getPagesModule();
        List<FacilioField> fields = FieldFactory.getPagesFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("isDefaultPage"), String.valueOf(true), BooleanOperators.IS));

        List<Map<String, Object>> props = builder.get();
        if (props != null && !props.isEmpty()) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PagesContext.class);
        }
        return null;
    }
    public static void updateFetchedPage(PagesContext page) throws Exception{
        FacilioModule module = ModuleFactory.getPagesModule();
        if(page.getId()!=   -1) {
            page.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            page.setSysModifiedTime(System.currentTimeMillis());

            Map<String, Object> prop = FieldUtil.getAsProperties(page);

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getPagesFields())
                    .andCondition(CriteriaAPI.getIdCondition(page.getId(), module));
            builder.update(prop);
        }
    }

    public static String getUniqueName(FacilioModule module, Criteria criteria, FacilioField parentField, long parentId, String name) throws Exception {

        Map<Long, List<String>> existingNamesMap = getExistingNameListAsMap(module, criteria, parentField);
        name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");

        if(existingNamesMap.get(parentId) != null){
            name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(parentId));
        }
        return name;
    }
    public static List<PageTabsContext> fetchTabs(Long pageId, Boolean isBuilderRequest) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();
        List<FacilioField> fields = FieldFactory.getPageTabsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("pageId"), String.valueOf(pageId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");

        if (!isBuilderRequest) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageTabsContext.class);
        }
        return new ArrayList<>();
    }

    public static List<PageTabsContext> getAllRelatedTabsToOrder(long id) throws Exception{
        FacilioModule module = ModuleFactory.getPageTabsModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPageTabsFields());
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldsMap.get("id"));
        fields.add(fieldsMap.get("pageId"));
        fields.add(fieldsMap.get("sequenceNumber"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("pageId"),"(select Page_Tabs.PAGE_ID from Page_Tabs where Page_Tabs.ID = "+id+")"));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageTabsContext.class);
        }

        return null;
    }
    public static void patchCustomPage(PagesContext customPage) throws Exception {
        FacilioModule module = ModuleFactory.getPagesModule();

        List<FacilioField> patchFields = new ArrayList<>();
        if (customPage.getDisplayName() != null) {
            patchFields.add(FieldFactory.getStringField("displayName", "DISPLAY_NAME", module));
        }
        if (customPage.getStatus() != null) {
            patchFields.add(FieldFactory.getBooleanField("status", "STATUS", module));
        }

        patchFields.add(FieldFactory.getNumberField("criteriaId", "CRITERIA_ID", module));

        if (CollectionUtils.isNotEmpty(patchFields)) {
            customPage.setSysModifiedBy(Objects.requireNonNull(AccountUtil.getCurrentUser()).getId());
            customPage.setSysModifiedTime(System.currentTimeMillis());

            patchFields.add(FieldFactory.getSystemField("sysModifiedTime", module));
            patchFields.add(FieldFactory.getSystemField("sysModifiedBy", module));

            Map<String, Object> prop = FieldUtil.getAsProperties(customPage);
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(patchFields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(customPage.getId(), module));
            builder.update(prop);
        }
        else{
            LOGGER.info("No New Values To Get Updated In Custom Page");
        }
    }
    public static void patchPageTab(PageTabsContext tab) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();

        List<FacilioField> patchFields = new ArrayList<>();
        if (tab.getDisplayName() != null) {
            patchFields.add(FieldFactory.getStringField("displayName", "DISPLAY_NAME", module));
        }
        if (tab.getStatus() != null) {
            patchFields.add(FieldFactory.getBooleanField("status", "STATUS", module));
        }
        if (CollectionUtils.isNotEmpty(patchFields)) {
            tab.setSysModifiedBy(Objects.requireNonNull(AccountUtil.getCurrentUser()).getId());
            tab.setSysModifiedTime(System.currentTimeMillis());

            patchFields.add(FieldFactory.getSystemField("sysModifiedTime", module));
            patchFields.add(FieldFactory.getSystemField("sysModifiedBy", module));

            Map<String, Object> prop = FieldUtil.getAsProperties(tab);
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(patchFields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(tab.getId(), module));
            builder.update(prop);

            updateSysModifiedFields(tab.getPageId(), prop, PageComponent.PAGE);
        }
        else{
            LOGGER.info("No New Values To Get Updated In Page Tab");
        }

    }
    public static List<FacilioField> getSysModifiedFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getSystemField("sysModifiedTime", module));
        fields.add(FieldFactory.getSystemField("sysModifiedBy", module));
        return fields;
    }
    public static Map<String, Object> getSysModifiedProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("sysModifiedBy", AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1);
        props.put("sysModifiedTime", System.currentTimeMillis());
        return props;
    }
    public static List<FacilioField> getSysDeletedFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getSysDeletedByField(module));
        fields.add(FieldFactory.getSysDeletedTimeField(module));
        return fields;
    }
    public static Map<String, Object> getSysDeletedProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("sysDeletedBy", AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1);
        props.put("sysDeletedTime", System.currentTimeMillis());
        return props;
    }
    public static void deletePage(List<Long> ids, FacilioModule module) throws Exception {
        List<FacilioField> fields = getSysDeletedFields(module);
        fields.add(FieldFactory.getIdField(module));
        Map<String, Object> props = getSysDeletedProps();

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(module.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        builder.update(props);

    }
    public static void deletePageComponent(long id, FacilioModule module) throws Exception{
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,module));
        deleteRecordBuilder.delete();
    }

    public static void updateSysModifiedFields(long id, Map<String, Object> props, PageComponent type) throws Exception {
        List<FacilioField> patchFields = new ArrayList<>();
        FacilioModule module;
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(type.getModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, type.getModule()));
        ;
        switch (type) {
            /*case WIDGETGROUP_WIDGETS:
                module = PageComponent.WIDGETGROUP_WIDGETS.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getWidgetGroupSectionsModule().getTableName())
                        .on("WidgetGroup_Sections.ID = WidgetGroup_Section_Widgets.WIDGETGROUP_SECTION_ID");
            case WIDGETGROUP_SECTION:
                module = PageComponent.WIDGETGROUP_SECTION.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageSectionWidgetsModule().getTableName())
                        .on("Page_Section_Widgets.ID = WidgetGroup_Sections.WIDGETGROUP_ID");
            case WIDGET:
                module = PageComponent.WIDGET.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageSectionsModule().getTableName())
                        .on("Page_Sections.ID = Page_Section_Widgets.SECTION_ID");
            case SECTION:
                module = PageComponent.SECTION.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageColumnsModule().getTableName())
                        .on("Page_Columns.ID = Page_Sections.COLUMN_ID");
            case COLUMN:
                module = PageComponent.COLUMN.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageTabsModule().getTableName())
                        .on("Page_Tabs.ID = Page_Columns.TAB_ID");*/
            case TAB:
                module = PageComponent.TAB.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPagesModule().getTableName())
                        .on("Pages.ID = Page_Tabs.PAGE_ID");
            case PAGE:
                module = PageComponent.PAGE.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.fields(patchFields);
                break;
            default:
                LOGGER.error("It's not a Page Component");
                throw new IllegalArgumentException("It's not a Page Component");
        }
        builder.update(props);
    }

    public enum PageComponent {
        PAGE(ModuleFactory.getPagesModule(), FieldFactory.getPagesFields()),
        TAB(ModuleFactory.getPageTabsModule(), FieldFactory.getPageTabsFields());
//        COLUMN(ModuleFactory.getPageColumnsModule(), FieldFactory.getPageColumnsFields()),
//        SECTION(ModuleFactory.getPageSectionsModule(), FieldFactory.getPageSectionsFields()),
//        WIDGET(ModuleFactory.getPageSectionWidgetsModule(), FieldFactory.getPageSectionWidgetsFields()),
//        WIDGETGROUP_SECTION(ModuleFactory.getWidgetGroupSectionsModule(), FieldFactory.getWidgetGroupSectionsFields()),
//        WIDGETGROUP_WIDGETS(ModuleFactory.getWidgetGroupSectionWidgetsModule(), FieldFactory.getWidgetGroupSectionWidgetsFields());
        FacilioModule module;
        List<FacilioField> fields;
        PageComponent(FacilioModule module, List<FacilioField> fields) {
            this.module = module;
            this.fields = fields;
        }
        public FacilioModule getModule() {
            return this.module;
        }
        public List<FacilioField> getFields() {
            return this.fields;
        }
    }
    public static double updateAndGetSequenceNumber(long previousId, long id, long nextId, String orderFieldName, FacilioModule module, Criteria criteria, List<FacilioField> fieldsToQuery)throws Exception{
        double sequenceNumber = generateSequenceNumber(previousId, id, nextId, orderFieldName, module, criteria, fieldsToQuery);

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsToQuery);
        updateSequenceNumber(id, sequenceNumber,  module, fieldsMap.get(orderFieldName));
        return sequenceNumber;
    }
    public static double generateSequenceNumber(long previousId, long id, long nextId, String orderFieldName, FacilioModule module, Criteria criteria, List<FacilioField> fieldsToQuery)throws Exception{
        double sequenceNumber = 0;
        double previousSequenceNumber = 0;
        double nextSequenceNumber = 0;

        if(!(CollectionUtils.isNotEmpty(fieldsToQuery))){
            LOGGER.error("To update sequence number, queryable fields can't be empty");
            throw new IllegalArgumentException("Error occured while generating new sequence number");
        }

        FacilioField idField = FieldFactory.getIdField(module);
        fieldsToQuery.add(idField);

        FacilioUtil.throwIllegalArgumentException(id < 0, "Invalid Id");
        List<Long> ids = new ArrayList<>();
        if (previousId > 0) {
            ids.add(previousId);
        }
        if (nextId > 0) {
            ids.add(nextId);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fieldsToQuery)
                .andCriteria(criteria)
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        List<Map<String, Object>> props = builder.get();

        for (Map<String, Object> prop : props) {
            if (previousId == (long) prop.get("id")) {
                previousSequenceNumber = (double) prop.get(orderFieldName);
            }
            else if (nextId == (long) prop.get("id")) {
                nextSequenceNumber = (double) prop.get(orderFieldName);
            }
        }

        if (previousSequenceNumber <= 0 && nextSequenceNumber <= 0) {
            return sequenceNumber + 10;
        }
        else if (previousSequenceNumber > 0 && nextSequenceNumber <= 0) {
            return previousSequenceNumber + 10;
        }
        else {
            sequenceNumber = (previousSequenceNumber + nextSequenceNumber) / 2;

            if (sequenceNumber < 0.000009d || (sequenceNumber-previousSequenceNumber < 0.000009d)
                    || ((nextSequenceNumber!=0) && (nextSequenceNumber-sequenceNumber < 0.000009d))){

                return recomputeSequenceNumber(id, nextId, orderFieldName, module, criteria, fieldsToQuery);
            }
        }
        return sequenceNumber;
    }

    public static double recomputeSequenceNumber(long id, long nextId, String orderFieldName, FacilioModule module, Criteria criteria, List<FacilioField> fieldsToQuery) throws Exception {
        double sequenceNumber =0;
        double newSequenceNumber =0;

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsToQuery);

        if(criteria != null) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fieldsToQuery)
                    .table(module.getTableName())
                    .andCriteria(criteria)
                    .orderBy(fieldsMap.get(orderFieldName).getColumnName());

            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

            for (Map<String, Object> record : selectRecordBuilder.get()) {

                if ((long) record.get("id") == nextId) {
                    sequenceNumber+=10;
                    newSequenceNumber = sequenceNumber;
                }

                if (!((long) record.get("id") == id)) {
                    sequenceNumber += 10;

                    GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                    updateVal.addWhereValue("id", record.get("id"));
                    updateVal.addUpdateValue(orderFieldName, sequenceNumber);
                    batchUpdateList.add(updateVal);
                }
            }

            FacilioField fieldIdField = fieldsMap.get("id");
            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(fieldIdField);

            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(Collections.singletonList(fieldsMap.get(orderFieldName)));
            updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);

        }
        return newSequenceNumber;
    }

    public static void updateSequenceNumber(long id, double sequenceNumber, FacilioModule module, FacilioField orderField)throws Exception{

        Map<String, Object> prop = new HashMap<>();
        prop.put(orderField.getName(),sequenceNumber);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(orderField))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        updateRecordBuilder.update(prop);
    }
}