package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class GetRelationshipWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        boolean isBuilderRequest = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, false);

        if((widgetId == null || widgetId <= 0) ) {
            throw new IllegalArgumentException("widgetId should be defined, to get relationship widget detail");
        }
        RelationshipWidget relationship = RelationshipWidgetUtil.getRelationshipOfWidget(widgetId, widgetWrapperType);

        if(!isBuilderRequest && relationship != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            Objects.requireNonNull(module, "Module name can't be empty to fetch relationship widget");

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(relationship.getRelationMappingId(), ModuleFactory.getRelationMappingModule()));

            List<RelationRequestContext> relationRequestContext = RelationUtil.getAllRelations(module, criteria);
            if(CollectionUtils.isNotEmpty(relationRequestContext)) {
                RelationRequestContext relReq = relationRequestContext.get(0);
                relationship.setDisplayName(relReq.getName());
                relationship.setRelationName(relReq.getRelationName());
                relationship.setRelation(relReq);
            }
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relationship);
        return false;
    }
}
