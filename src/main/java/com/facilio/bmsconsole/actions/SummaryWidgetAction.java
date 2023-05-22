package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class SummaryWidgetAction extends FacilioAction{
    private long id;
    private SummaryWidget summaryWidget;
    private long appId;
    private String moduleName;
    private long formId;
    private long widgetId;
    private String widgetName;
    private boolean fetchFormFields;
    private boolean fetchMainFields;
    private Map<String, Object> widgetParams;

    public String getMainSummaryWidget() throws Exception {
        FacilioChain getPageWidgetChain = TransactionChainFactory.getPageWidgetChain();

        FacilioContext context = getPageWidgetChain.getContext();
        context.put(FacilioConstants.ContextNames.WIDGET_NAME, widgetName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        getPageWidgetChain.execute();

        SummaryWidget pageWidget = (SummaryWidget) context.get(FacilioConstants.ContextNames.CUSTOM_PAGE_WIDGET);

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
    public  String addOrUpdateSummaryWidget() throws Exception {
        FacilioChain chain;
        Objects.requireNonNull(summaryWidget, "Summary widget can't be null");
        if(summaryWidget.getId() > 0) {
             chain = TransactionChainFactory.getUpdateSummaryWidgetChain();
        }
        else {
            chain = TransactionChainFactory.getAddSummaryWidgetChain();
        }
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, summaryWidget);
        chain.execute();
        id = (long) context.getOrDefault(FacilioConstants.ContextNames.ID, -1L);
        setResult(FacilioConstants.ContextNames.ID, id);
        return SUCCESS;
    }

    public String fetchSummaryWidget() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSummaryWidgetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        summaryWidget = (SummaryWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        setResult(FacilioConstants.SummaryWidget.SUMMARY_WIDGET, FieldUtil.getAsJSON(summaryWidget));
        return SUCCESS;
    }
}
