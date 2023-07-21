package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.constants.FacilioConstants;
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

    public static List<String> getPageBuilderEnabledModules() throws Exception {
        List<FacilioModule> modules = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PAGE_BUILDER)?
                ModuleSettingConfigUtil.getConfigEnabledModules(FacilioConstants.SettingConfigurationContextNames.PAGE_BUILDER) : null                ;
        return CollectionUtils.isNotEmpty(modules)? modules.stream().map(FacilioModule::getName).collect(Collectors.toList()) : null;
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
    public static String generateUniqueName(String name, List<String> nameList, Boolean isSystem) {
        while (nameList != null && (nameList.contains(name) || nameList.contains(name+"__c"))) {
            if (name.contains("_")) {
                name = name.split("_", 1)[0];
            }
            String finalName = name;
            int noToAppend = (int) nameList.stream().filter(e -> e.contains(finalName + "_")).count() + 1;
            name = name + "_" + (noToAppend);
        }

        if(isSystem == null || !isSystem){
            name = name + "__c";
        }
        return name;
    }
    public static void insertCustomPageToDB(PagesContext customPage) throws Exception {
        if(customPage!=null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(customPage);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPagesModule().getTableName())
                    .fields(FieldFactory.getPagesFields())
                    .addRecord(prop);
            builder.save();
            customPage.setId((long) prop.get("id"));
        }
    }

    public static void insertTemplatePageAppDomains(long templatePageId, List<Long> domainTypes) throws Exception {
        if(templatePageId > 0 && CollectionUtils.isNotEmpty(domainTypes)) {
            FacilioModule module = ModuleFactory.getTemplatePageAppDomainModule();
            List<FacilioField> fields = FieldFactory.getTemplatePageAppDomainFields();
            List<Map<String, Object>> props = new ArrayList<>();

            for(Long domainType : domainTypes) {
                Map<String, Object> prop = new HashMap<>();
                prop.put("pageId", templatePageId);
                prop.put("appDomainType", domainType);
                props.add(prop);
            }

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .ignoreSplNullHandling()
                    .addRecords(props);
            builder.save();
        }
    }
    public static void insertPageTabToDB(PageTabContext tab) throws Exception {
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
            updateSysModifiedFields(tab.getLayoutId(), sysModifiedProps, PageComponent.PAGE);

            tab.setId((long)prop.get("id"));
        }
    }
    public static List<PageColumnContext> insertPageColumnsToDB(List<PageColumnContext> columns) throws Exception {
        if(CollectionUtils.isNotEmpty(columns)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(columns, PageColumnContext.class);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageColumnsModule().getTableName())
                    .fields(FieldFactory.getPageColumnsFields())
                    .addRecords(props);
            builder.save();

            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy",columns.get(0).getSysCreatedBy());
            sysModifiedProps.put("sysModifiedTime",columns.get(0).getSysCreatedTime());
            updateSysModifiedFields(columns.get(0).getTabId(), sysModifiedProps, PageComponent.TAB);

            return FieldUtil.getAsBeanListFromMapList(props, PageColumnContext.class);
        }
        return null;
    }
    public static void insertPageSectionToDB(PageSectionContext section) throws Exception {
        if(section != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(section);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageSectionsModule().getTableName())
                    .fields(FieldFactory.getPageSectionsFields())
                    .addRecord(prop);
            builder.save();

            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy",section.getSysCreatedBy());
            sysModifiedProps.put("sysModifiedTime",section.getSysCreatedTime());
            updateSysModifiedFields(section.getColumnId(), sysModifiedProps, PageComponent.COLUMN);

            section.setId((long)prop.get("id"));
        }
    }

    public static void insertPageSectionWidgettoDB(PageSectionWidgetContext widget) throws Exception {
        if(widget != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(widget);
            prop.replace("widgetParams", widget.getWidgetParamsAsString());
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageSectionWidgetsModule().getTableName())
                    .fields(FieldFactory.getPageSectionWidgetsFields())
                    .addRecord(prop);
            builder.save();

            widget.setId((Long) prop.get("id"));
        }

    }

    public static List<Map<String, Object>> getAllCustomPageForBuilder(long appId, long moduleId, PagesContext.PageLayoutType layoutType) throws Exception {
        if(layoutType == null) {
            layoutType = PagesContext.PageLayoutType.WEB;
        }
        GenericSelectRecordBuilder builder = getCustomPageBuilder(appId, moduleId, layoutType);
        List<Map<String, Object>> props = builder.get();
        if(CollectionUtils.isNotEmpty(props)) {
            List<Long> pageIds = props.stream().filter(f->(Long)f.get("id") != null).map(f->(Long)f.get("id")).collect(Collectors.toList());
            Map<Long, SharingContext<SingleSharingContext>> pageSharingMap = SharingAPI.getSharing(pageIds, ModuleFactory.getPageSharingModule(), SingleSharingContext.class, FieldFactory.getPageSharingFields());

            props.forEach(f->{ if((Long)f.get("id") != null && pageSharingMap.get((Long)f.get("id")) != null)
                                                    {
                                                        f.put("pageSharing", pageSharingMap.get((Long)f.get("id")));
                                                    }
                               });
        }
        return props;
    }

    private static GenericSelectRecordBuilder getCustomPageBuilder(long appId, long moduleId, PagesContext.PageLayoutType layoutType) {
        FacilioModule pagesModule = ModuleFactory.getPagesModule();
        FacilioModule pageLayoutsModule = ModuleFactory.getPageLayoutsModule();

        List<FacilioField> fields = FieldFactory.getPagesFields();
        fields.add(FieldFactory.getNumberField("layoutId","ID",pageLayoutsModule));
        fields.add(FieldFactory.getStringField("layoutType","LAYOUT_TYPE",pageLayoutsModule));
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        return new GenericSelectRecordBuilder()
                .table(pagesModule.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"),String.valueOf(false)))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(pagesModule), CommonOperators.IS_EMPTY))
                .innerJoin(pageLayoutsModule.getTableName())
                .on("Page_Layouts.PAGE_ID = Pages.ID")
                .andCondition(CriteriaAPI.getCondition("LAYOUT_TYPE","layoutType", String.valueOf(layoutType), StringOperators.IS))
                .orderBy("SEQUENCE_NUMBER, ID");
    }

    public static List<PagesContext> getAllCustomPage(long appId, long moduleId, PagesContext.PageLayoutType layoutType) throws Exception {
        if (layoutType == null) {
            layoutType = PagesContext.PageLayoutType.WEB;
        }
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPagesFields());
        Map<String, FacilioField> sharingFieldMap = FieldFactory.getAsMap(FieldFactory.getPageSharingFields());

        Criteria pageSharingCriteria = new Criteria();
        pageSharingCriteria.addAndCondition(CriteriaAPI.getCondition(sharingFieldMap.get("roleId"), String.valueOf(AccountUtil.getCurrentUser().getRoleId()), NumberOperators.EQUALS));
        pageSharingCriteria.addOrCondition(CriteriaAPI.getCondition(sharingFieldMap.get("roleId"), CommonOperators.IS_EMPTY));
        pageSharingCriteria.addOrCondition(CriteriaAPI.getCondition("IS_DEFAULT_PAGE", "isDefaultPage", String.valueOf(true), BooleanOperators.IS));

        GenericSelectRecordBuilder builder = getCustomPageBuilder(appId, moduleId, layoutType);
        builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS))
               .leftJoin(ModuleFactory.getPageSharingModule().getTableName())
                .on("Pages.ID =Page_Sharing.PARENT_ID")
                .andCriteria(pageSharingCriteria);

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
    public static PageTabContext getTab(Long id, Long layoutId, String tabName) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageTabsFields())
                .table(module.getTableName());

        if(id != null && id > 0){
            builder.andCondition(CriteriaAPI.getIdCondition(id, module));
        }
        else if(layoutId != null && layoutId > 0 && StringUtils.isNotEmpty(tabName)){
            builder.andCondition(CriteriaAPI.getNameCondition(tabName, module))
                    .andCondition(CriteriaAPI.getCondition("LAYOUT_ID","layoutId", String.valueOf(layoutId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PageTabContext.class);
        }
        return null;
    }

    public static PageColumnContext getColumn(Long id) throws Exception {
        FacilioModule module = ModuleFactory.getPageColumnsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageColumnsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PageColumnContext.class);
        }
        return null;
    }

    public static PageSectionContext getSection(Long id) throws Exception {
        FacilioModule module = ModuleFactory.getPageSectionsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageSectionsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PageSectionContext.class);
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

    public static String getUniqueName(FacilioModule module, Criteria criteria, FacilioField parentField, long parentId, String name, Boolean isSystem) throws Exception {

        Map<Long, List<String>> existingNamesMap = getExistingNameListAsMap(module, criteria, parentField);
        name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");

        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(parentId), isSystem);

        return name;
    }

    //Temp handling for mobile pageBuilder
    public static boolean checkMobileTabAvailability(String moduleName, long appId) throws Exception {
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(moduleName);
        //exception handles in CustomPageAction
        boolean isMobilePageAvailable = false;
        PagesContext defaultPage = getDefaultPage(appId, module.getModuleId());
        long layoutId = getLayoutIdForPageId(defaultPage.getId(), PagesContext.PageLayoutType.MOBILE);
        List<PageTabContext> mobTabs = fetchTabs(layoutId, false);
        isMobilePageAvailable = CollectionUtils.isNotEmpty(mobTabs);
        return isMobilePageAvailable;
    }

    public static List<PageTabContext> fetchTabs(Long layoutId, Boolean isBuilderRequest) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();
        List<FacilioField> fields = FieldFactory.getPageTabsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("layoutId"), String.valueOf(layoutId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");

        if (!isBuilderRequest) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageTabContext.class);
        }
        return new ArrayList<>();
    }

    public static Map<Long,List<PageColumnContext>> fetchColumns(List<Long> tabIds) throws Exception {
        FacilioModule module = ModuleFactory.getPageColumnsModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageColumnsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("TAB_ID", "tabId", tabIds, NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");
        List<Map<String, Object>> props = builder.get();

        if (props != null && !props.isEmpty()) {
            List<PageColumnContext> columns = FieldUtil.getAsBeanListFromMapList(props, PageColumnContext.class);

            if (CollectionUtils.isNotEmpty(columns)) {
                return columns.stream()
                        .collect(Collectors.groupingBy(PageColumnContext::getTabId));
            }
        }
        return null;
    }

    public static Map<Long, List<PageSectionContext>> fetchSections(List<Long> columnIds) throws Exception {
        FacilioModule module = ModuleFactory.getPageSectionsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageSectionsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("COLUMN_ID", "columnId", columnIds, NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, ID");

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<PageSectionContext> sections = FieldUtil.getAsBeanListFromMapList(props, PageSectionContext.class);

            return sections.stream()
                    .collect(Collectors.groupingBy(PageSectionContext::getColumnId));
        }
        return null;
    }
    public static Map<Long, List<PageSectionWidgetContext>> fetchPageSectionWidgets(List<Long> sectionIds) throws Exception {
        FacilioModule module = ModuleFactory.getPageSectionWidgetsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageSectionWidgetsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("SECTION_ID", "sectionId", sectionIds, NumberOperators.EQUALS))
                .orderBy("Y_POSITION, X_POSITION");

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<PageSectionWidgetContext> widgets = FieldUtil.getAsBeanListFromMapList(props, PageSectionWidgetContext.class);

            return widgets.stream().peek(f->f.setWidgetWrapperType(WidgetWrapperType.DEFAULT))
                    .collect(Collectors.groupingBy(PageSectionWidgetContext::getSectionId));
        }
        return null;
    }

    public static PageSectionWidgetContext getPageSectionWidget(long id) throws Exception {
        FacilioModule module = ModuleFactory.getPageSectionWidgetsModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getPageSectionWidgetsFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props.get(0), PageSectionWidgetContext.class);
        }

        return null;
    }
    public static List<PageTabContext> getAllRelatedTabsToOrder(long id) throws Exception{
        FacilioModule module = ModuleFactory.getPageTabsModule();
        PageTabContext tab = getTab(id, null, null);

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPageTabsFields());
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldsMap.get("id"));
        fields.add(fieldsMap.get("layoutId"));
        fields.add(fieldsMap.get("sequenceNumber"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("layoutId"), String.valueOf(tab.getLayoutId())));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageTabContext.class);
        }

        return null;
    }

    public static List<PageSectionContext> getAllRelatedSectionsToOrder(long id) throws Exception{
        FacilioModule module = ModuleFactory.getPageSectionsModule();
        PageSectionContext section = getSection(id);

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPageSectionsFields());
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldsMap.get("id"));
        fields.add(fieldsMap.get("columnId"));
        fields.add(fieldsMap.get("sequenceNumber"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("columnId"), String.valueOf(section.getColumnId())));

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PageSectionContext.class);
        }

        return null;
    }
    public static void patchCustomPage(PagesContext customPage) throws Exception {
        if(customPage != null) {
            PagesContext existingPage = getCustomPage(customPage.getId());
            FacilioUtil.throwIllegalArgumentException(existingPage == null, "Page does not exists");

            FacilioModule module = ModuleFactory.getPagesModule();
            List<FacilioField> patchFields = new ArrayList<>();
            if (existingPage.getDisplayName() == null || !existingPage.getDisplayName().equals(customPage.getDisplayName())) {
                patchFields.add(FieldFactory.getStringField("displayName", "DISPLAY_NAME", module));
            }
            if (existingPage.getStatus() != customPage.getStatus()) {
                patchFields.add(FieldFactory.getBooleanField("status", "STATUS", module));
            }
            if (existingPage.getDescription() == null || !existingPage.getDescription().equals(customPage.getDescription())) {
                patchFields.add(FieldFactory.getField("description", "DESCRIPTION", module, FieldType.STRING));
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
            } else {
                LOGGER.info("No New Values To Get Updated In Custom Page");
            }
        }
    }
    public static void patchPageTab(PageTabContext tab) throws Exception {
        if(tab != null) {
            PageTabContext existingTab = getTab(tab.getId(), null, null);
            FacilioUtil.throwIllegalArgumentException(existingTab == null, "Tab does not exists");
            Map<String, Object> prop = new HashMap<>();

            FacilioModule module = ModuleFactory.getPageTabsModule();
            List<FacilioField> patchFields = new ArrayList<>();
            if (existingTab.getDisplayName() == null || !existingTab.getDisplayName().equals(tab.getDisplayName())) {
                patchFields.add(FieldFactory.getStringField("displayName", "DISPLAY_NAME", module));
                prop.put("displayName", tab.getDisplayName());

                patchPageTab(tab.getId(), patchFields, prop);
            }
        }

    }

    public static void updateTabStatus(long id, boolean status) throws Exception {
        FacilioModule module = ModuleFactory.getPageTabsModule();
        List<FacilioField> patchFields = new ArrayList<>();
        patchFields.add(FieldFactory.getBooleanField("status", "STATUS", module));

        Map<String, Object> prop = new HashMap<>();
        prop.put("status", status);

        patchPageTab(id, patchFields, prop);
    }

    private static void patchPageTab(long id, List<FacilioField> patchFields, Map<String, Object> prop) throws Exception {
        if (CollectionUtils.isNotEmpty(patchFields)) {
            FacilioModule module = ModuleFactory.getPageTabsModule();

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(patchFields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            builder.update(prop);

            updateSysModifiedFields(id, getSysModifiedProps(), PageComponent.TAB);
        }
    }

    public static void patchPageColumn(PageColumnContext column) throws Exception {
        if(column != null) {
            PageColumnContext existingColumn = getColumn(column.getId());
            FacilioUtil.throwIllegalArgumentException(existingColumn == null, "Column does not exists");

            FacilioModule module = ModuleFactory.getPageColumnsModule();
            List<FacilioField> patchFields = new ArrayList<>();
            if (existingColumn.getDisplayName() == null || existingColumn.getDisplayName() != column.getDisplayName()) {
                patchFields.add(FieldFactory.getStringField("displayName", "DISPLAY_NAME", module));
            }
            if (CollectionUtils.isNotEmpty(patchFields)) {

                Map<String, Object> prop = FieldUtil.getAsProperties(column);
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(patchFields)
                        .andCondition(CriteriaAPI.getIdCondition(column.getId(), module));
                builder.update(prop);

                updateSysModifiedFields(column.getId(), getSysModifiedProps(), PageComponent.COLUMN);
            }
        }
    }
    public static void patchPageSection(PageSectionContext section) throws Exception {
        if (section != null) {
            PageSectionContext existingSection = getSection(section.getId());
            FacilioUtil.throwIllegalArgumentException(existingSection == null, "Section does not exists");

            FacilioModule module = ModuleFactory.getPageSectionsModule();
            List<FacilioField> patchFields = new ArrayList<>();
            if (existingSection.getDisplayName() == null || !section.getDisplayName().equals(existingSection.getDisplayName())) {
                patchFields.add(FieldFactory.getField("displayName", "DISPLAY_NAME", module, FieldType.STRING));
            }
            if (existingSection.getDescription() == null || !section.getDescription().equals(existingSection.getDescription())) {
                patchFields.add(FieldFactory.getField("description", "DESCRIPTION", module, FieldType.STRING));
            }
            if (CollectionUtils.isNotEmpty(patchFields)) {

                Map<String, Object> prop = FieldUtil.getAsProperties(section);
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(patchFields)
                        .andCondition(CriteriaAPI.getIdCondition(section.getId(), module));
                builder.update(prop);

                updateSysModifiedFields(section.getId(), getSysModifiedProps(), PageComponent.SECTION);
            }
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
        if(id > 0 && module != null) {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            deleteRecordBuilder.delete();
        }
    }

    public static void updateSysModifiedFields(long id, Map<String, Object> props, PageComponent type) throws Exception {

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(type.getModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, type.getModule()));
        List<FacilioField> patchFields = new ArrayList<>();
        getSysModifiedBuilderForCustomPage(builder, type, patchFields);
        builder.update(props);
    }

    public static void getSysModifiedBuilderForCustomPage(GenericUpdateRecordBuilder builder, PageComponent type, List<FacilioField> patchFields ){
        FacilioModule module;
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
                        .on("Page_Sections.ID = Page_Section_Widgets.SECTION_ID");*/
            case SECTION:
                module = PageComponent.SECTION.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageColumnsModule().getTableName())
                        .on("Page_Columns.ID = Page_Sections.COLUMN_ID");
                getSysModifiedBuilderForCustomPage(builder, PageComponent.COLUMN, patchFields);
                break;
            case COLUMN:
                module = PageComponent.COLUMN.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageTabsModule().getTableName())
                        .on("Page_Tabs.ID = Page_Columns.TAB_ID");
                getSysModifiedBuilderForCustomPage(builder, PageComponent.TAB, patchFields);
                break;
            case TAB:
                module = PageComponent.TAB.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.innerJoin(ModuleFactory.getPageLayoutsModule().getTableName())
                        .on("Page_Layouts.ID = Page_Tabs.LAYOUT_ID")
                        .innerJoin(ModuleFactory.getPagesModule().getTableName())
                        .on("Pages.ID = Page_Layouts.PAGE_ID");
                getSysModifiedBuilderForCustomPage(builder, PageComponent.PAGE, patchFields);
                break;
            case PAGE:
                module = PageComponent.PAGE.getModule();
                patchFields.addAll(getSysModifiedFields(module));
                builder.fields(patchFields);
                break;
            default:
                LOGGER.error("It's not a Page Component");
                throw new IllegalArgumentException("It's not a page component");
        }
    }

    public static void updateWidgetPositions(List<Map<String, Object>> props) throws Exception {
        if (CollectionUtils.isNotEmpty(props)) {
            FacilioModule module = ModuleFactory.getPageSectionWidgetsModule();
            List<FacilioField> fields = FieldFactory.getPageSectionWidgetsFields();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

            List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>();
            for (Map<String, Object> prop : props) {

                GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateContext.addUpdateValue("positionX", prop.get("positionX"));
                updateContext.addUpdateValue("positionY", prop.get("positionY"));

                updateContext.addWhereValue(fieldsMap.get("id").getName(), prop.get("id"));
                updateBatch.add(updateContext);
            }

            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(fieldsMap.get("id"));

            List<FacilioField> updateField = new ArrayList<>();
            updateField.add(fieldsMap.get("positionX"));
            updateField.add(fieldsMap.get("positionY"));

            updateField.add(fieldsMap.get("sysModifiedBy"));
            updateField.add(fieldsMap.get("sysModifiedTime"));
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(updateField)
                    .table(module.getTableName());
            builder.batchUpdate(whereFields, updateBatch);
        }
    }

    public static void updatePageSectionWidgets(PageSectionWidgetContext widget) throws Exception {
        if(widget != null) {
            FacilioModule module = ModuleFactory.getPageSectionWidgetsModule();
            List<FacilioField> fields = new ArrayList<>();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPageSectionWidgetsFields());
            fields.add(fieldsMap.get("displayName"));
            fields.add(fieldsMap.get("positionX"));
            fields.add(fieldsMap.get("positionY"));
            fields.add(fieldsMap.get("width"));
            fields.add(fieldsMap.get("height"));
            fields.add(fieldsMap.get("sysModifiedBy"));
            fields.add(fieldsMap.get("sysModifiedTime"));

            widget.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            widget.setSysModifiedTime(System.currentTimeMillis());

            Map<String, Object> prop = FieldUtil.getAsProperties(widget);
            GenericUpdateRecordBuilder updateWidget = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(widget.getId(), module));
            updateWidget.update(prop);
        }
    }

    public static Map<String, Long> createLayoutsForPage(Long id) throws Exception{
        if(id != null && id > 0) {
            List<Map<String, Object>> props = new ArrayList<>();
            props.add(new HashMap<String, Object>(){{put("pageId", id);
                                                     put("layoutType", PagesContext.PageLayoutType.WEB.name());}});
            props.add(new HashMap<String, Object>(){{put("pageId", id);
                                                     put("layoutType", PagesContext.PageLayoutType.MOBILE.name());}});

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPageLayoutsModule().getTableName())
                    .fields(FieldFactory.getPageLayoutsFields())
                    .addRecords(props);
            builder.save();

            Map<String,Long> layoutMap = new HashMap<>();
            for(Map<String, Object> prop : props) {
                layoutMap.put((String) prop.get("layoutType"), (Long)prop.get("id"));
            }
            return layoutMap;
        }
        return null;
    }

    public static Long getLayoutIdForPageId(Long pageId, PagesContext.PageLayoutType layoutType) throws Exception{
        if(layoutType == null) {
            layoutType = PagesContext.PageLayoutType.WEB;
        }

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPageLayoutsFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPageLayoutsModule().getTableName())
                .select(Collections.singletonList(fieldsMap.get("id")))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("pageId"), String.valueOf(pageId)))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("layoutType"), layoutType.name()));
        Map<String, Object> prop = builder.fetchFirst();

        if(MapUtils.isNotEmpty(prop)) {
            return (Long) prop.get("id");
        }
        else {
            throw new IllegalArgumentException("Layout doesn't not exists for page");
        }
    }

    public static PagesContext getTemplatePageFromDB(ApplicationContext app, Long moduleId, String layoutType) throws Exception {
        FacilioModule pagesModule = ModuleFactory.getPagesModule();
        List<FacilioField> pagesFields = FieldFactory.getPagesFields();
        Map<String, FacilioField> pagesFieldsMap = FieldFactory.getAsMap(pagesFields);

        FacilioModule templatePageAppDomainModule = ModuleFactory.getTemplatePageAppDomainModule();
        List<FacilioField> templatePageAppDomainFields = FieldFactory.getTemplatePageAppDomainFields();
        Map<String, FacilioField> templatePageAppDomainFieldsMap = FieldFactory.getAsMap(templatePageAppDomainFields);

        List<FacilioField> fields = new ArrayList<>(pagesFields);
        fields.add(templatePageAppDomainFieldsMap.get("appDomainType"));

        Criteria moduleAndTemplateCriteria = new Criteria();
        moduleAndTemplateCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(pagesFieldsMap.get("moduleId"), String.valueOf(moduleId)));
        moduleAndTemplateCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(pagesFieldsMap.get("isTemplate"), String.valueOf(true)));

        Criteria appIdOrDomainCriteria = new Criteria();
        appIdOrDomainCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(pagesFieldsMap.get("appId"), String.valueOf(app.getId())));
        appIdOrDomainCriteria.addOrCondition(CriteriaAPI.getEqualsCondition(templatePageAppDomainFieldsMap.get("appDomainType"), String.valueOf(app.getDomainType())));

        if(AppDomain.AppDomainType.valueOf(app.getDomainType()) == AppDomain.AppDomainType.FACILIO) {
            appIdOrDomainCriteria.addOrCondition(CriteriaAPI.getEqualsCondition(templatePageAppDomainFieldsMap.get("appDomainType"), String.valueOf(app.getId())));
        } else {
            appIdOrDomainCriteria.addOrCondition(CriteriaAPI.getEqualsCondition(templatePageAppDomainFieldsMap.get("appDomainType"), String.valueOf(-1)));
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pagesModule.getTableName())
                .select(fields)
                .andCriteria(moduleAndTemplateCriteria)
                .andCriteria(appIdOrDomainCriteria)
                .innerJoin(templatePageAppDomainModule.getTableName())
                .on(pagesFieldsMap.get("id").getCompleteColumnName()+"="+templatePageAppDomainFieldsMap.get("pageId").getCompleteColumnName())
                .orderBy("APP_ID desc, APP_DOMAIN desc");

        if (layoutType != null) {
            builder.innerJoin(ModuleFactory.getPageLayoutsModule().getTableName())
                    .on("Page_Layouts.PAGE_ID = Pages.ID")
                    .andCondition(CriteriaAPI.getCondition("LAYOUT_TYPE", "layoutType", layoutType, StringOperators.IS));
        }

        Map<String, Object> prop = builder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            return FieldUtil.getAsBeanFromMap(prop, PagesContext.class);
        }
        return null;
    }

    public static long getPageCountForModuleInApp(Long moduleId, Long appId) throws Exception {
        FacilioModule pagesModule = ModuleFactory.getPagesModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPagesFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pagesModule.getTableName())
                .select(new HashSet<>())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("appId"), String.valueOf(appId)))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("isTemplate"), String.valueOf(false)))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(pagesModule), CommonOperators.IS_EMPTY))
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(pagesModule));


        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Long pageCount = (Long) props.get(0).get("id");
            if (pageCount != null) {
                return pageCount;
            }
        }
        return -1;
    }

    public enum PageComponent {
        PAGE(ModuleFactory.getPagesModule(), FieldFactory.getPagesFields()),
        TAB(ModuleFactory.getPageTabsModule(), FieldFactory.getPageTabsFields()),
        COLUMN(ModuleFactory.getPageColumnsModule(), FieldFactory.getPageColumnsFields()),
        SECTION(ModuleFactory.getPageSectionsModule(), FieldFactory.getPageSectionsFields());
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