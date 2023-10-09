package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class V3UpdateDashboardTabWidgetCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        // TODO Auto-generated method stub
        DashboardTabContext dashboardTabContext = (DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
        DashboardContext dashboardContext = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
        if(dashboardTabContext != null) {
            if (dashboardContext != null && dashboardContext.getId() > 0) {
                DashboardUtil.updateDashboard(dashboardContext);
            }

            DashboardUtil.updateDashboardTab(dashboardTabContext);
            List<DashboardWidgetContext> existingWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(null,dashboardTabContext.getId());

            JSONObject widgetMapping = new JSONObject();

            List<DashboardWidgetContext> widgets = dashboardTabContext.getDashboardWidgets();
            V3DashboardAPIHandler.checkAndGenerateWidgetLinkName(widgets, null, dashboardTabContext.getId());
            if (widgets != null && widgets.size() > 0)  {
                for (int i = 0; i < widgets.size(); i++) {

                    DashboardWidgetContext widget = widgets.get(i);
                    widgetMapping.put(widget.getId(), true);

                    if(widget.getId() <= 0)
                    {
                        if(widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION))
                        {
                            FacilioChain addSectionWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                            widget.setDashboardTabId(dashboardTabContext.getId());
                            context.put(FacilioConstants.ContextNames.WIDGET, widget);
                            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
                            addSectionWidgetChain.execute(context);
                            widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
                            if(widget != null && ((WidgetSectionContext) widget).getWidgets_in_section().size() > 0) {
                                createOrUpdateWidgetInSection(context, dashboardTabContext, widget, widgetMapping);
                            }
                        }
                        else {
                            FacilioChain addWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                            widget.setDashboardTabId(dashboardTabContext.getId());
                            context.put(FacilioConstants.ContextNames.WIDGET, widget);
                            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
                            addWidgetChain.execute(context);
                        }
                    }
                    else if(widget.getId() > 0 && widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION)){
                        if(((WidgetSectionContext)widget).getWidgets_in_section().size() > 0)
                        {
                            createOrUpdateWidgetInSection(context, dashboardTabContext, widget, widgetMapping);
                        }
                    }
                }
            }


            List<DashboardWidgetContext> updatedWidgets = dashboardTabContext.getDashboardWidgets();

            FacilioChain updateWidgetChain = TransactionChainFactoryV3.getUpdateWidgetsChainV3();

            context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST,updatedWidgets);

            updateWidgetChain.execute(context);


            List<Long> removedWidgets = new ArrayList<Long>();
            List<Long> removedFilters = new ArrayList<Long>();
            for (int i = 0; i < existingWidgets.size(); i++) {
                if (!widgetMapping.containsKey(existingWidgets.get(i).getId())) {
                    removedWidgets.add(existingWidgets.get(i).getId());
                    if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
                        if(existingWidgets.get(i).getWidgetType() == DashboardWidgetContext.WidgetType.FILTER){
                            removedFilters.add(existingWidgets.get(i).getId());
                        }else if(existingWidgets.get(i).getWidgetType() == DashboardWidgetContext.WidgetType.CARD){
                            List<WidgetCardContext>childCards = CardUtil.getChildCards(existingWidgets.get(i).getId());
                            if(childCards != null && childCards.size() > 0){
                                for(WidgetCardContext childCard : childCards){
                                    removedWidgets.add(childCard.getId());
                                }
                            }
                        }
                    }
                }
            }
            Boolean isFromReport = (Boolean) context.get(FacilioConstants.ContextNames.IS_FROM_REPORT);
            if(!(isFromReport != null && isFromReport) && removedWidgets.size() > 0) {
                GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
                genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedWidgets, ","), StringOperators.IS));

                genericDeleteRecordBuilder.delete();
            }
            if(removedFilters.size() > 0){
                GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
                genericDeleteRecordBuilder.table(ModuleFactory.getDashboardUserFilterModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition(ModuleFactory.getDashboardUserFilterModule().getTableName()+".WIDGET_ID ", "widget_id", StringUtils.join(removedWidgets, ","), StringOperators.IS));

                genericDeleteRecordBuilder.delete();
            }
        }

        return false;
    }

    public void createOrUpdateWidgetInSection(Context context, DashboardTabContext dashboardTabContext, DashboardWidgetContext widget, JSONObject widgetMapping)throws Exception
    {
        List<DashboardWidgetContext> update_widget_list = new ArrayList<DashboardWidgetContext>();
        List<DashboardWidgetContext> widget_list = ((WidgetSectionContext) widget).getWidgets_in_section();
        V3DashboardAPIHandler.checkAndGenerateWidgetLinkName(widget_list, null, dashboardTabContext.getId());
        for(DashboardWidgetContext dashboard_widget: widget_list)
        {
            if( dashboard_widget.getId() <=0) {
                FacilioChain addWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                dashboard_widget.setSectionId(widget.getId());
                dashboard_widget.setDashboardTabId(dashboardTabContext.getId());
                context.put(FacilioConstants.ContextNames.WIDGET, dashboard_widget);
                context.put(FacilioConstants.ContextNames.WIDGET_TYPE, dashboard_widget.getWidgetType());
                addWidgetChain.execute(context);
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2) && dashboard_widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.CARD)){
                    createOrUpdateWidgetInCard(context, dashboardTabContext, widget, dashboard_widget);
                }
            }
            else
            {
                widgetMapping.put(dashboard_widget.getId(), true);
                dashboard_widget.setDashboardTabId(dashboardTabContext.getId());
                dashboard_widget.setSectionId(widget.getId());
                update_widget_list.add(dashboard_widget);
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2) && dashboard_widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.CARD)){
                    createOrUpdateWidgetInCard(context, dashboardTabContext, widget, dashboard_widget);
                }
            }
        }
        if(update_widget_list.size() > 0)
        {
            FacilioChain updateWidgetChain = TransactionChainFactoryV3.getUpdateWidgetsChainV3();
            context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST,update_widget_list);
            updateWidgetChain.execute(context);
        }
    }
    public void createOrUpdateWidgetInCard(Context context, DashboardTabContext dashboardTab, DashboardWidgetContext widget, DashboardWidgetContext dashboard_widget)throws Exception{
        WidgetCardContext cardContext = (WidgetCardContext) dashboard_widget;
        List<WidgetCardContext> update_card_list = new ArrayList<>();
        List<WidgetCardContext> existingCards = CardUtil.getChildCards(cardContext.getId());
        if(cardContext.getChildCards() != null && cardContext.getChildCards().size() > 0) {
            for(WidgetCardContext childCard : cardContext.getChildCards()){
                if(childCard.getId() < 0) {
                    FacilioChain addCardChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                    childCard.setDashboardTabId(dashboardTab.getId());
                    childCard.setSectionId(widget.getId());
                    context.put(FacilioConstants.ContextNames.WIDGET, childCard);
                    context.put(FacilioConstants.ContextNames.WIDGET_TYPE, childCard.getWidgetType());
                    context.put(FacilioConstants.ContextNames.DASHBOARD_TAB_ID, dashboardTab.getId());
                    childCard.setParentId(cardContext.getId());
                    addCardChain.execute(context);
                }
                else{
                    childCard.setDashboardTabId(dashboardTab.getId());
                    childCard.setSectionId(widget.getId());
                    childCard.setParentId(cardContext.getId());
                    update_card_list.add(childCard);
                }
            }
            List<Long> update_card_Ids = update_card_list.stream().map(WidgetCardContext::getId).collect(Collectors.toList());
            List<Long> removedIds = existingCards.stream().filter(card -> !update_card_Ids.contains(card.getId())).map(WidgetCardContext::getId).collect(Collectors.toList());
            if(removedIds.size() > 0) {
                GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
                genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedIds, ","), StringOperators.IS));
                genericDeleteRecordBuilder.delete();
            }
            if(update_card_list.size() > 0){

                FacilioChain updateWidgetChain = TransactionChainFactoryV3.getUpdateWidgetsChainV3();
                context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST,update_card_list);
                updateWidgetChain.execute(context);
            }
        }
    }
}
