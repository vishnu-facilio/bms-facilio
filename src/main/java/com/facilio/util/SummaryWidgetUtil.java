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
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SummaryWidgetUtil {
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
                widgetGroup.setWidgetGroupFields(groupVsFieldsMap.get(groupId));
            }
        }
        pageWidget.setWidgetGroups(widgetGroups);

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
                groupField.setField(allModBeanFieldsMap.get(fieldId));
            }
        }

        return allGroupFields;
    }
}
