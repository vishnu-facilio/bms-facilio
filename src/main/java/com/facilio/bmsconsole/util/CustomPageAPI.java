package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class CustomPageAPI {

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

}