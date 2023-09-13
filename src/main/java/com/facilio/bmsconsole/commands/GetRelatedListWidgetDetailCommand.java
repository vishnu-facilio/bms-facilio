package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class GetRelatedListWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        boolean isBuilderRequest = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        long moduleId = module != null ? module.getModuleId() : -1;
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if ((widgetId == null || widgetId <= 0)) {
            throw new IllegalArgumentException("Invalid widgetId to fetch related list");
        }

        RelatedListWidgetContext relList = RelatedListWidgetUtil.getRelatedListOfWidgetId(widgetId, widgetWrapperType);
        boolean canShowRelList;
        if (relList != null) {
            if (relList.getConnectedAppWidgetId() != null && relList.getConnectedAppWidgetId() > 0 && !isFetchForClone) {
                canShowRelList = CustomPageAPI.validateConnectedAppWidgetCriteria(recordId,
                        moduleName, relList.getConnectedAppWidgetId());
            } else {
                long subModuleId = relList.getSubModuleId();
                FacilioModule subModule;
                subModule = modBean.getModule(subModuleId);
                canShowRelList = PageFactory.relatedListHasPermission(moduleId, subModule, relList.getField());
            }
            if (!canShowRelList && !(isFetchForClone || isBuilderRequest)) {
                relList = null;
            }  else {
                RelatedListWidgetUtil.setRelatedListEnum(relList);
            }
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relList);
        return false;
    }
}
