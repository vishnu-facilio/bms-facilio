package com.facilio.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SummaryWidgetUtil {
    public static Logger LOGGER = LogManager.getLogger(SummaryWidgetUtil.class.getName());
    public static SummaryWidget getSummaryWidgetById(long appId, long widgetId) throws Exception {
        return getSummaryWidget(appId, widgetId, null, -1);
    }

    public static SummaryWidget getSummaryWidgetByName(long appId, String widgetName) throws Exception {
        return getSummaryWidget(appId, -1, widgetName, -1);
    }

    public static SummaryWidget getAllWidgets(long appId, long moduleId) throws Exception {
        return getSummaryWidget(appId, -1, null, moduleId);
    }

    public static SummaryWidget getSummaryWidget(long appId, long widgetId, String widgetName, long moduleId) throws Exception {
        if(widgetId <= 0 && appId <= 0) {
                ApplicationContext application = AccountUtil.getCurrentApp();
                if (application == null) {
                    application = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                }
                appId = application.getId();
        }

        SummaryWidget summaryWidget = getCustomPageWidget(appId, widgetId, widgetName, moduleId);
        if (summaryWidget != null) {
            long currWidgetId = summaryWidget.getId();
            List<SummaryWidgetGroup> widgetGroups = getSummaryWidgetGroups(currWidgetId);
            List<SummaryWidgetGroupFields> allWidgetGroupFields = getSummaryWidgetGroupFields(currWidgetId);

            if (CollectionUtils.isNotEmpty(allWidgetGroupFields)) {
                getAllFieldDetails(allWidgetGroupFields);
            }

            Map<Long, List<SummaryWidgetGroupFields>> groupVsFieldsMap = new HashMap<>();
            for (SummaryWidgetGroupFields field : allWidgetGroupFields) {
                long groupId = field.getWidgetGroupId();
                if (groupVsFieldsMap.containsKey(groupId)) {
                    groupVsFieldsMap.get(groupId).add(field);
                } else {
                    List<SummaryWidgetGroupFields> currGroupFields = new ArrayList<>();
                    currGroupFields.add(field);
                    groupVsFieldsMap.put(groupId, currGroupFields);
                }
            }

            for (SummaryWidgetGroup widgetGroup : widgetGroups) {
                long groupId = widgetGroup.getId();
                if (groupVsFieldsMap.containsKey(groupId)) {
                    widgetGroup.setFields(groupVsFieldsMap.get(groupId));
                }
            }
            summaryWidget.setGroups(widgetGroups);
        }

        return summaryWidget;
    }

    public static List<SummaryWidgetGroupFields> getAllFieldDetails(List<SummaryWidgetGroupFields> allGroupFields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> allGroupFieldIds = allGroupFields.stream().map(SummaryWidgetGroupFields::getFieldId).collect(Collectors.toList());

        List<FacilioField> allModBeanFields = modBean.getFields(allGroupFieldIds);
        Map<Long, FacilioField> allModBeanFieldsMap = allModBeanFields.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (oldValue, newValue) -> newValue));

        for (SummaryWidgetGroupFields groupField : allGroupFields){
            long fieldId = groupField.getFieldId();
            if (allModBeanFieldsMap.containsKey(fieldId)){
                FacilioField field = allModBeanFieldsMap.get(fieldId);
                groupField.setField(field);

                long parentLookupFieldId = groupField.getParentLookupFieldId();
                if (parentLookupFieldId != -1){
                    FacilioField parentLookupField = modBean.getField(parentLookupFieldId);
                    groupField.setParentLookupField(parentLookupField);
                    if(isLookupField(field)) {
                        groupField.setSecondLevelLookup(true);
                    }
                }
            }
        }

        return allGroupFields;
    }

    public static boolean isLookupField(FacilioField field){
        return field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP;
    }

    public static List<String> getExistingSummaryWidgetsNameOfModuleInApp(long appId, long moduleId) throws Exception{
        if(moduleId > 0){
            Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(FieldFactory.getCustomPageWidgetFields());
            List<FacilioField> fields = Collections.singletonList(fieldMap.get("name"));

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                    .andCondition(CriteriaAPI.getEqualsCondition(fieldMap.get("appId"), String.valueOf(appId)))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(props)){
                return props.stream().map(f->(String)f.get("name")).collect(Collectors.toList());
            }
        }
        return null;
    }

    public static void insertSummaryWidgetToDB(SummaryWidget summaryWidget) throws Exception {
        if (summaryWidget != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(summaryWidget);

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                    .fields(FieldFactory.getCustomPageWidgetFields())
                    .addRecord(prop);
            insertBuilder.save();

            summaryWidget.setId((long) prop.get("id"));
        }
    }

    public static void updateSummaryWidget(SummaryWidget summaryWidget) throws Exception {
        if (summaryWidget != null) {
            FacilioModule module = ModuleFactory.getCustomPageWidgetModule();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getCustomPageWidgetFields());
            List<FacilioField> fields = new ArrayList<>(Arrays.asList(fieldsMap.get("displayName")));

            Map<String, Object> prop = new HashMap<>();
            prop.put("displayName", summaryWidget.getDisplayName());

            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(summaryWidget.getId(), module));
            updateRecordBuilder.update(prop);
        }
    }

    public static void addPageWidget(SummaryWidget summaryWidget) throws Exception {

        FacilioChain chain = TransactionChainFactory.getAddSummaryWidgetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, summaryWidget.getAppId());
        context.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, summaryWidget);
        chain.execute();
//        customPageWidget.setId(-1);
//        customPageWidget.setOrgId(AccountUtil.getCurrentOrg().getId());
//        if (customPageWidget.getAppId() < 0){
//            ApplicationContext application = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//            customPageWidget.setAppId(application.getId());
//        }
//
//        try {
//            Map<String, Object> widgetProps = FieldUtil.getAsProperties(customPageWidget);
//            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
//                    .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
//                    .fields(FieldFactory.getCustomPageWidgetFields())
//                    .addRecord(widgetProps);
//
//            insertBuilder.save();
//            long widgetId = (long) widgetProps.get("id");
//            customPageWidget.setId(widgetId);
//
//            addWidgetGroup(customPageWidget);
//        } catch (Exception e) {
//            LOGGER.info("Exception occurred ", e);
//            throw e;
//        }
    }

    public static void insertSummaryWidgetGroupToDB(SummaryWidgetGroup summaryWidgetGroup) throws Exception {
        if (summaryWidgetGroup != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(summaryWidgetGroup);

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSummaryWidgetGroupModule().getTableName())
                    .fields(FieldFactory.getSummaryWidgetGroupFields())
                    .addRecord(prop);
            insertBuilder.save();

            summaryWidgetGroup.setId((long) prop.get("id"));
        }
    }

    public static void updateSummaryWidgetGroup(SummaryWidgetGroup summaryWidgetGroup) throws Exception {
        if (summaryWidgetGroup != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(summaryWidgetGroup);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getSummaryWidgetGroupModule().getTableName())
                    .fields(FieldFactory.getSummaryWidgetGroupFields())
                    .andCondition(CriteriaAPI.getIdCondition(summaryWidgetGroup.getId(), ModuleFactory.getSummaryWidgetGroupModule()));
            updateBuilder.update(prop);
        }
    }

    public static void deleteSummaryWidgetGroup(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            FacilioModule module = ModuleFactory.getSummaryWidgetGroupModule();

            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids, module));
            deleteRecordBuilder.delete();
        }
    }
    public static void addWidgetGroup(SummaryWidget summaryWidget) throws Exception {
        for (SummaryWidgetGroup widgetGroup : summaryWidget.getGroups()){
            widgetGroup.setId(-1);
            widgetGroup.setOrgId(summaryWidget.getOrgId());
            widgetGroup.setWidgetId(summaryWidget.getId());

            try {
                Map<String, Object> widgetGroupProps = FieldUtil.getAsProperties(widgetGroup);
                GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getSummaryWidgetGroupModule().getTableName())
                        .fields(FieldFactory.getSummaryWidgetGroupFields())
                        .addRecord(widgetGroupProps);

                insertBuilder.save();
                long widgetGroupId = (long) widgetGroupProps.get("id");
                widgetGroup.setId(widgetGroupId);

                addWidgetGroupFields(widgetGroup);
            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
                throw e;
            }
        }
    }

    public static void insertSummaryWidgetGroupFieldsToDB(List<SummaryWidgetGroupFields> fields) throws Exception {
        if (fields != null) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(fields, SummaryWidgetGroupFields.class);

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSummaryWidgetGroupFieldsModule().getTableName())
                    .fields(FieldFactory.getSummaryWidgetGroupFieldsFields())
                    .addRecords(props);
            insertBuilder.save();
        }
    }

    public static void updateSummaryWidgetGroupFields(SummaryWidgetGroupFields field) throws Exception {
        if (field != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(field);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getSummaryWidgetGroupFieldsModule().getTableName())
                    .fields(FieldFactory.getSummaryWidgetGroupFieldsFields())
                    .andCondition(CriteriaAPI.getIdCondition(field.getId(), ModuleFactory.getSummaryWidgetGroupFieldsModule()));
            updateBuilder.update(prop);
        }
    }

    public static void deleteSummaryWidgetGroupFiedls(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            FacilioModule module = ModuleFactory.getSummaryWidgetGroupFieldsModule();

            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids, module));
            deleteRecordBuilder.delete();
        }
    }
    public static void addWidgetGroupFields(SummaryWidgetGroup widgetGroup) throws Exception {
        widgetGroupRowSpanValidator(widgetGroup);
        List<Map<String, Object>> groupFieldsProps = new ArrayList<>();
        for (SummaryWidgetGroupFields widgetGroupField : widgetGroup.getFields()) {
            FacilioField fieldDetails = widgetGroupField.getField();
            if (widgetGroupField.getFieldId() == -1 && StringUtils.isEmpty(widgetGroupField.getName()) && (fieldDetails.getFieldId() == -1 && StringUtils.isEmpty(fieldDetails.getName()))) {
                throw new IllegalArgumentException("WidgetGroupField is required");
            }
            widgetGroupField.setId(-1);
            widgetGroupField.setOrgId(widgetGroup.getOrgId());
            widgetGroupField.setWidgetGroupId(widgetGroup.getId());
            widgetGroupField.setWidgetId(widgetGroup.getWidgetId());

            Map<String, Object> props = FieldUtil.getAsProperties(widgetGroupField);
            groupFieldsProps.add(props);
        }

        try {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSummaryWidgetGroupFieldsModule().getTableName())
                    .fields(FieldFactory.getSummaryWidgetGroupFieldsFields())
                    .addRecords(groupFieldsProps);

            insertBuilder.save();

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
            throw e;
        }
    }

    public static void widgetGroupRowSpanValidator(SummaryWidgetGroup widgetGroup) throws Exception {
        long widgetGroupSpan = widgetGroup.getColumns();
        Map<Long, List<SummaryWidgetGroupFields>> rowWiseFieldsMap = new HashMap<>();
        for (SummaryWidgetGroupFields widgetGroupField : widgetGroup.getFields()) {
            long rowIndex = widgetGroupField.getRowIndex();
            if (rowWiseFieldsMap.containsKey(rowIndex)) {
                rowWiseFieldsMap.get(rowIndex).add(widgetGroupField);
            } else {
                List<SummaryWidgetGroupFields> currRowFields = new ArrayList<>();
                currRowFields.add(widgetGroupField);
                rowWiseFieldsMap.put(rowIndex, currRowFields);
            }
        }

        for (Map.Entry<Long, List<SummaryWidgetGroupFields>> entry : rowWiseFieldsMap.entrySet()) {
            long nextColIndex = 0;
            List<SummaryWidgetGroupFields> currRowFields = entry.getValue();
            currRowFields = currRowFields.stream().sorted(Comparator.comparingLong(SummaryWidgetGroupFields::getColIndex)).collect(Collectors.toList());
            int currRowColumnSpan = currRowFields.stream().mapToInt(SummaryWidgetGroupFields::getColSpan).sum();

            if (currRowColumnSpan <= widgetGroupSpan){
                for (SummaryWidgetGroupFields field : currRowFields) {
                    long colSpan = field.getColSpan();
                    long colIndex = field.getColIndex();
                    if (colIndex <= nextColIndex) {
                        throw new IllegalArgumentException("Found field overlap");
                    }
                    if (!(colSpan <= (widgetGroupSpan - colIndex + 1))){
                        throw new IllegalArgumentException("Field Span exceeds Group span");
                    }
                    nextColIndex += colSpan;
                }
            } else {
                throw new IllegalArgumentException("Span of fields should be less than Group span");
            }
        }
    }

    public static SummaryWidget generateCustomWidget(List<FacilioField> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            SummaryWidget pageWidget = new SummaryWidget();
            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            List<SummaryWidgetGroupFields> widgetGroupFields = new ArrayList<>();

            for (FacilioField field : fields) {
                SummaryWidgetGroupFields groupField = new SummaryWidgetGroupFields();
                groupField.setField(field);
                groupField.setName(field.getName());
                groupField.setFieldId(field.getFieldId());
                groupField.setDisplayName(field.getDisplayName());

                widgetGroupFields.add(groupField);
            }

            SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
            widgetGroup.setFields(widgetGroupFields);
            widgetGroup.setDisplayName("");
            widgetGroup.setName("");
            widgetGroupList.add(widgetGroup);

            pageWidget.setGroups(widgetGroupList);
            pageWidget.setName("mainsummarywidget");
            pageWidget.setDisplayName("Summary Fields Widget");

            return pageWidget;
        }
        return null;
    }

    public static long getSummaryWidgetIdForWidgetId(Long widgetId, WidgetWrapperType widgetWrapperType) throws Exception {
        FacilioModule pageSummaryWidgetModule = ModuleFactory.getPageSummaryWidgetModule();
        List<FacilioField> fields = FieldFactory.getPageSummaryWidgetFields();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageSummaryWidgetModule, widgetWrapperType);

        Criteria pageWidgetIdCriteria = new Criteria();
        pageWidgetIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
        if(widgetWrapperType == WidgetWrapperType.DEFAULT) {
            //TODO remove this once PAGE_WIDGET_ID column is migrated
            pageWidgetIdCriteria.addOrCondition(CriteriaAPI.getCondition("PAGE_WIDGET_ID", "pageWidgetId", String.valueOf(widgetId), NumberOperators.EQUALS));
        }
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getPageSummaryWidgetModule().getTableName())
                .andCriteria(pageWidgetIdCriteria);

        List<Map<String, Object>> props = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(props)) {
            return (long) props.get(0).get("summaryWidgetId");
        }
        return -1L;
    }

    public static void addPageSummaryWidget(Long widgetId, WidgetWrapperType widgetWrapperType, Long summaryWidgetId) throws Exception{
        FacilioModule pageSummaryWidgetModule = ModuleFactory.getPageSummaryWidgetModule();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageSummaryWidgetModule, widgetWrapperType);
        Map<String, Object> prop = new HashMap<>();

        if(widgetWrapperType == WidgetWrapperType.DEFAULT) {
            //TODO remove this once PAGE_WIDGET_ID column is migrated
            prop.put("pageWidgetId", widgetId);
        }
        prop.put(widgetIdField.getName(), widgetId);;
        prop.put("summaryWidgetId", summaryWidgetId);

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(pageSummaryWidgetModule.getTableName())
                .fields(FieldFactory.getPageSummaryWidgetFields())
                .addRecord(prop);
        builder.save();
    }

    public static void updatePageSummaryWidget(Long widgetId, WidgetWrapperType widgetWrapperType, long summaryWidgetId) throws Exception{
        FacilioModule pageSummaryWidgetModule = ModuleFactory.getPageSummaryWidgetModule();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(pageSummaryWidgetModule, widgetWrapperType);

        Criteria pageWidgetIdCriteria = new Criteria();
        pageWidgetIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));

        Map<String, Object> prop = new HashMap<>();
        if(widgetWrapperType == WidgetWrapperType.DEFAULT) {
            //TODO remove this once PAGE_WIDGET_ID column is migrated
            pageWidgetIdCriteria.addOrCondition(CriteriaAPI.getCondition("PAGE_WIDGET_ID", "pageWidgetId", String.valueOf(widgetId), NumberOperators.EQUALS));
            prop.put("pageWidgetId", widgetId);
        }
        prop.put(widgetIdField.getName(), widgetId);;
        prop.put("summaryWidgetId", summaryWidgetId);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(pageSummaryWidgetModule.getTableName())
                .fields(FieldFactory.getPageSummaryWidgetFields())
                .andCriteria(pageWidgetIdCriteria);
        builder.update(prop);
    }

    public static SummaryWidget getMainSummaryWidgetForApp(long moduleId) throws Exception {
        ApplicationContext app = AccountUtil.getCurrentApp();
        if(app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        long appId = app.getId();
        List<FacilioField> fields = FieldFactory.getCustomPageWidgetFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getEqualsCondition(fieldMap.get("appId"), String.valueOf(appId)));

        List<SummaryWidget> pageWidgets = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidget.class);
        if (CollectionUtils.isNotEmpty(pageWidgets)){
            return pageWidgets.get(0);
        }
        return null;
    }

    public static SummaryWidget getCustomPageWidget(long appId, long widgetId, String widgetName, long moduleId) throws Exception{
        List<SummaryWidget> pageWidgets = null;
        ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
            app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }

        List<FacilioField> fields = FieldFactory.getCustomPageWidgetFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getCustomPageWidgetModule().getTableName());

        if (moduleId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
        }

        if (widgetId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"),String.valueOf(widgetId), NumberOperators.EQUALS));
        } else if (org.apache.commons.lang.StringUtils.isNotEmpty(widgetName)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), widgetName, StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
        }

        pageWidgets = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidget.class);
        if (CollectionUtils.isNotEmpty(pageWidgets)){
            return pageWidgets.get(0);
        }
        return null;
    }

    public static List<SummaryWidgetGroup> getSummaryWidgetGroups(long widgetId) throws Exception{
        List<SummaryWidgetGroup> widgetGroups = null;

        List<FacilioField> fields = FieldFactory.getSummaryWidgetGroupFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getSummaryWidgetGroupModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId") ,String.valueOf(widgetId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER");

        widgetGroups = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidgetGroup.class);
        return widgetGroups;
    }

    public static List<SummaryWidgetGroupFields> getSummaryWidgetGroupFields(long widgetId) throws Exception{
        List<SummaryWidgetGroupFields> widgetGroupFields = null;

        List<FacilioField> fields = FieldFactory.getSummaryWidgetGroupFieldsFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getSummaryWidgetGroupFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId"), String.valueOf(widgetId), NumberOperators.EQUALS))
                .orderBy("ROW_INDEX, COL_INDEX");

        widgetGroupFields = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), SummaryWidgetGroupFields.class);
        return widgetGroupFields;
    }
}
