package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.Objects;

public class AddOrUpdateRelatedListWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0) ) {
            throw new IllegalArgumentException("widgetId should be defined, to create related list");
        }
        RelatedListWidgetContext existingRelListInWidget = RelatedListWidgetUtil.getRelatedListOfWidgetId(widgetId, widgetWrapperType);
        if(existingRelListInWidget != null) {
            RelatedListWidgetUtil.deleteRelatedLists(existingRelListInWidget.getId());
        }

        RelatedListWidgetContext relList = (RelatedListWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        if(relList != null) {
            relList.setStatus(true);
            relList.setSequenceNumber(10D);
            RelatedListWidgetUtil.setWidgetIdForRelList(widgetId, relList, widgetWrapperType);
            if (relList.getSubModuleId() == null) {
                FacilioModule subModule = modBean.getModule(relList.getSubModuleName());
                Objects.requireNonNull(subModule, "Invalid module name");
                relList.setSubModuleId(subModule.getModuleId());
            }

            Objects.requireNonNull(relList.getFieldId(), "FieldId can't be null");
            RelatedListWidgetUtil.insertRelatedListToDB(relList);
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, RelatedListWidgetUtil.getRelatedListOfWidgetId(widgetId, widgetWrapperType));
        return false;
    }
}
