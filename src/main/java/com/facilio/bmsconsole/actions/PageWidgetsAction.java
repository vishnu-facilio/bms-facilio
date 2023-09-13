package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PageWidgetsAction extends FacilioAction{

    private static final Logger LOGGER = Logger.getLogger(PageWidgetsAction.class.getName());


    private String moduleName;
    private Long appId;
    private long id;
    private long sectionId;
    private long widgetId;
    private long recordId;
    private PageSectionWidgetContext widget;
    private boolean onlyDetails = false;
    private WidgetWrapperType widgetWrapperType = WidgetWrapperType.DEFAULT;
    private PageWidget.WidgetType widgetType;
    private boolean isBuilderRequest = false;
    private PagesContext.PageLayoutType layoutType;
    private List<Map<String, Object>> widgetPositions;

    public String createWidget() throws Exception{
        FacilioContext context = WidgetConfigUtil.createWidget(appId, moduleName, sectionId, widget, widgetPositions, layoutType, widgetWrapperType);

        id = (long) context.get(FacilioConstants.CustomPage.WIDGETID);
        setResult(FacilioConstants.CustomPage.WIDGETID, id);
        return SUCCESS;
    }

    public String updateWidget() throws Exception {
        FacilioContext context = WidgetConfigUtil.updateWidget(appId, moduleName, widgetWrapperType, widget, widgetPositions);

        widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET);
        setResult("result", SUCCESS);

        return SUCCESS;
    }
    public String fetchWidget() throws Exception {
        PageSectionWidgetContext widget = WidgetConfigUtil.fetchWidget(recordId, appId, moduleName, id, widgetWrapperType, false, isBuilderRequest);

        if(onlyDetails) {
            setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, widget.getWidgetDetail());
        }
        else {
            setResult(FacilioConstants.CustomPage.WIDGET, widget);
        }
        return SUCCESS;
    }

    public String fetchWidgetDetail() throws Exception {
        JSONObject widgetDetail = WidgetConfigUtil.fetchWidgetDetail(recordId, appId, moduleName, id,widgetType, widgetWrapperType, false, isBuilderRequest);
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, widgetDetail);
        return SUCCESS;
    }
    public String rearrangeWidgets() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRearrangeWidgetsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGETS_POSITIONS, widgetPositions);
        chain.execute();
        return SUCCESS;
    }

    public String deleteWidget() throws Exception {
        WidgetConfigUtil.deleteWidget(id, widgetPositions, widgetWrapperType);
        return SUCCESS;
    }

}
