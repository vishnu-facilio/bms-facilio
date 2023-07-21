package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetWidgetUnUsedRelationShipsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long widgetId = (long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<RelationRequestContext> relationRequests = RelationUtil.getAllRelations(module);
        List<Long> existingRelMappingInWidget = RelationshipWidgetUtil.getRelationMappingIdInWidget(widgetId, widgetWrapperType);

        if(CollectionUtils.isNotEmpty(existingRelMappingInWidget)) {
            relationRequests.removeIf(f -> existingRelMappingInWidget.contains(f.getRelMappingId()));
        }

        List<RelationshipWidget> relationshipWidgets = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(relationRequests)) {

            for(RelationRequestContext relRequest : relationRequests) {
                RelationshipWidget relShipWidget = new RelationshipWidget();
                relShipWidget.setDisplayName(relRequest.getName());
                relShipWidget.setRelationMappingId(relRequest.getRelMappingId());
                relShipWidget.setRelationName(relRequest.getRelationName());
                relationshipWidgets.add(relShipWidget);
            }
        }

        context.put(FacilioConstants.ContextNames.RELATIONSHIP, FieldUtil.getAsJSONArray(relationshipWidgets, RelationshipWidget.class));
        return false;
    }
}
