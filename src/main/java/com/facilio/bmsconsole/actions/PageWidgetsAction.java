package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

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
    private PageSectionWidgetContext widget;
    private boolean onlyDetails;
    private PagesContext.PageLayoutType layoutType;
    private PageWidget.WidgetType widgetType;
    private List<Map<String, Object>> widgetPositions;

    public String createWidget() throws Exception{
        FacilioContext context = WidgetConfigUtil.createWidget(appId, moduleName, sectionId, widget, widgetPositions, layoutType);

        id = (long) context.get(FacilioConstants.CustomPage.WIDGETID);
        setResult(FacilioConstants.CustomPage.WIDGETID, id);
        return SUCCESS;
    }

    public String updateWidget() throws Exception {
        FacilioContext context = WidgetConfigUtil.updateWidget(appId, moduleName, widget, widgetPositions);

        widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
        setResult("result", SUCCESS);

        return SUCCESS;
    }
    public String fetchWidget() throws Exception {
        FacilioContext context = WidgetConfigUtil.fetchWidget(appId, moduleName, id);

        if(onlyDetails) {
            PageSectionWidgetContext widgetDetail = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
            setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, widgetDetail);
        }
        else {
            widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
            setResult(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, widget);
        }
        return SUCCESS;
    }

    public String rearrangeWidgets() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRearrangeWidgetsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS_POSITIONS, widgetPositions);
        chain.execute();
        return SUCCESS;
    }

    public String deleteWidget() throws Exception {
        WidgetConfigUtil.deleteWidget(id, widgetPositions);
        return SUCCESS;
    }

}
