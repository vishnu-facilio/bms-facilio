package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BulkRelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipWidgetsAction extends FacilioAction {

    private String moduleName;
    private long pageWidgetId;
    private long id;
    private WidgetWrapperType widgetWrapperType = WidgetWrapperType.DEFAULT;
    private BulkRelationshipWidget bulkRelationShipWidget;


    public String getRelationshipsOfModule() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getUnusedRelationShipsOfWidgetChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGETID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        chain.execute();

        setResult("relationships", context.get(FacilioConstants.ContextNames.RELATIONSHIP));
        return SUCCESS;
    }

    public String addBulkRelationshipWidget() throws Exception {
        FacilioChain  chain = TransactionChainFactory.getAddBulkRelationshipWidgetCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGETID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelationShipWidget);
        chain.execute();
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, FieldUtil.getAsJSON(context.get(FacilioConstants.WidgetNames.BULK_RELATION_SHIP_WIDGET)));
        return SUCCESS;
    }

    public String updateBulkRelationshipWidget() throws Exception {
        FacilioChain  chain = TransactionChainFactory.getUpdateBulkRelationshipWidgetCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGETID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelationShipWidget);
        chain.execute();
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, FieldUtil.getAsJSON(context.get(FacilioConstants.WidgetNames.BULK_RELATION_SHIP_WIDGET)));
        return SUCCESS;
    }

    public String fetchSummaryWidgetForRelation() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSummaryWidgetForRelationWidget();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, context.get(FacilioConstants.CustomPage.WIDGET_DETAIL));
        return SUCCESS;
    }
    public String fetchBulkRelationshipWidget() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getBulkRelationShipWidgetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGETID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, widgetWrapperType);
        chain.execute();

        bulkRelationShipWidget = (BulkRelationshipWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, FieldUtil.getAsJSON(bulkRelationShipWidget));
        return SUCCESS;
    }
}
