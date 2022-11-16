package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.util.SummaryWidgetUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomPageAction extends FacilioAction {
    private static Logger LOGGER = Logger.getLogger(CustomPageAction.class.getName());
    private long id;
    private long formId;
    private long widgetId;
    private long appId = -1;
    private String moduleName;
    private String widgetName;
    private boolean fetchFormFields;
    private boolean fetchMainFields;
    private Map<String, Object> widgetParams;

    public String getCustomPageWidget() throws Exception {
        FacilioChain getPageWidgetChain = TransactionChainFactory.getPageWidgetChain();

        FacilioContext context = getPageWidgetChain.getContext();
        context.put(FacilioConstants.ContextNames.WIDGET_NAME, widgetName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        getPageWidgetChain.execute();

        CustomPageWidget pageWidget = (CustomPageWidget) context.get(FacilioConstants.ContextNames.CUSTOM_PAGE_WIDGET);

        if (fetchFormFields && pageWidget == null) {
            FacilioChain summaryFieldsChain = ReadOnlyChainFactoryV3.getSummaryFieldsCommand();

            FacilioContext summaryFieldsContext = summaryFieldsChain.getContext();
            summaryFieldsContext.put(FacilioConstants.ContextNames.ID, id);
            summaryFieldsContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
            summaryFieldsContext.put(FacilioConstants.ContextNames.WIDGET_ID, widgetId);
            summaryFieldsContext.put(FacilioConstants.ContextNames.FETCH_LOOKUPS, true);
            summaryFieldsContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            summaryFieldsContext.put(FacilioConstants.ContextNames.WIDGET_PARAMJSON, widgetParams);
            summaryFieldsContext.put(FacilioConstants.ContextNames.FETCH_MAIN_FIELDS, fetchMainFields);
            summaryFieldsChain.execute();

            List<FormField> formFields = (List<FormField>) summaryFieldsContext.get("fields");

            List<FacilioField> allFields = new ArrayList<>();
            for (FormField formField : formFields) {
                FacilioField field = formField.getField();
                if (field != null) {
                    allFields.add(field);
                }
            }
            pageWidget = SummaryWidgetUtil.generateCustomWidget(allFields);
        }

        setResult("summaryFieldWidget", pageWidget);

        return "success";
    }
}