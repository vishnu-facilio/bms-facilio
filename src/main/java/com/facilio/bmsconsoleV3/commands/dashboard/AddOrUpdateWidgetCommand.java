package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class AddOrUpdateWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DashboardWidgetContext widget = (DashboardWidgetContext) context.get(PackageConstants.DashboardConstants.WIDGET);
        context.put(PackageConstants.DashboardConstants.SKIP_LINK_NAME,true);
        if(widget.getId() <= 0) {

            if(widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION))
            {
                FacilioChain addSectionWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
                addSectionWidgetChain.execute(context);

                if(widget != null && ((WidgetSectionContext) widget).getWidgets_in_section().size() > 0) {
                    //skip this if widget id updated in createOrUpdate method
//                    List<DashboardWidgetContext> sectionWidgetsList = ((WidgetSectionContext) widget).getWidgets_in_section();
//                    for(DashboardWidgetContext sectionWidget: sectionWidgetsList){
//                        sectionWidget.setSectionId(widget.getId());
//                    }
                    V3UpdateDashboardWithWidgets.createOrUpdateWidgetInSection(context, null, widget, null);
                }
            }
            else
            {
                FacilioChain addWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
                addWidgetChain.execute(context);
            }
        }
        else if(widget.getId() > 0 && widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION)){
            if(((WidgetSectionContext)widget).getWidgets_in_section().size() > 0)
            {
                V3UpdateDashboardWithWidgets.createOrUpdateWidgetInSection(context, null, widget, null);
            }
        }
        else if(widget.getId()>0){
            List<DashboardWidgetContext> updatedWidgets = Collections.singletonList(widget);
            FacilioChain updateWidgetChain = TransactionChainFactoryV3.getUpdateWidgetsChainV3();
            context.put(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST,updatedWidgets);
            updateWidgetChain.execute(context);
        }
        return false;
    }
}
