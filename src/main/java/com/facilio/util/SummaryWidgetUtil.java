package com.facilio.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
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
    public static CustomPageWidget getWidgetById(long appId, long widgetId) throws Exception {
        return getAllPageWidgets(appId, widgetId, null, -1);
    }

    public static CustomPageWidget getWidgetByName(long appId, String widgetName) throws Exception {
        return getAllPageWidgets(appId, -1, widgetName, -1);
    }

    public static CustomPageWidget getAllWidgets(long appId, long moduleId) throws Exception {
        return getAllPageWidgets(appId, -1, null, moduleId);
    }

    public static CustomPageWidget getAllPageWidgets(long appId, long widgetId, String widgetName, long moduleId) throws Exception {
        if (appId < 0){
            ApplicationContext application = AccountUtil.getCurrentApp();
            if (application == null) {
                application = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            }
            appId = application.getId();
        }

        CustomPageWidget pageWidget = CustomPageAPI.getCustomPageWidget(appId, widgetId, widgetName, moduleId);
        if (pageWidget == null){
            throw new IllegalArgumentException("No PageWidget found");
        }
        
        long currWidgetId = pageWidget.getId();
        List<SummaryWidgetGroup> widgetGroups = CustomPageAPI.getCustomWidgetGroups(currWidgetId);
        List<SummaryWidgetGroupFields> allWidgetGroupFields = CustomPageAPI.getCustomWidgetGroupFields(currWidgetId);

        if (CollectionUtils.isNotEmpty(allWidgetGroupFields)) {
            getAllFieldDetails(allWidgetGroupFields);
        }

        Map<Long, List<SummaryWidgetGroupFields>> groupVsFieldsMap = new HashMap<>();
        for (SummaryWidgetGroupFields field : allWidgetGroupFields) {
            long groupId = field.getWidgetGroupId();
            if (groupVsFieldsMap.containsKey(groupId)){
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
        pageWidget.setGroups(widgetGroups);

        return pageWidget;
    }

    public static List<SummaryWidgetGroupFields> getAllFieldDetails(List<SummaryWidgetGroupFields> allGroupFields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> allGroupFieldIds = allGroupFields.stream().map(SummaryWidgetGroupFields::getFieldId).collect(Collectors.toList());

        List<FacilioField> allModBeanFields = modBean.getFields(allGroupFieldIds);
        Map<Long, FacilioField> allModBeanFieldsMap = allModBeanFields.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity()));

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

    public static void addPageWidget(CustomPageWidget customPageWidget) throws Exception {
        customPageWidget.setId(-1);
        customPageWidget.setOrgId(AccountUtil.getCurrentOrg().getId());
        if (customPageWidget.getAppId() < 0){
            ApplicationContext application = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            customPageWidget.setAppId(application.getId());
        }

        try {
            Map<String, Object> widgetProps = FieldUtil.getAsProperties(customPageWidget);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getCustomPageWidgetModule().getTableName())
                    .fields(FieldFactory.getCustomPageWidgetFields())
                    .addRecord(widgetProps);

            insertBuilder.save();
            long widgetId = (long) widgetProps.get("id");
            customPageWidget.setId(widgetId);

            addWidgetGroup(customPageWidget);
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
            throw e;
        }
    }

    public static void addWidgetGroup(CustomPageWidget customPageWidget) throws Exception {
        for (SummaryWidgetGroup widgetGroup : customPageWidget.getGroups()){
            widgetGroup.setId(-1);
            widgetGroup.setOrgId(customPageWidget.getOrgId());
            widgetGroup.setWidgetId(customPageWidget.getId());

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
}