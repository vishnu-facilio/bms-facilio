package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0) ) {
            throw new IllegalArgumentException("widgetId should be defined, to create related list");
        }
        List<RelatedListWidgetContext> existingRelListInWidget = RelatedListWidgetUtil.getRelatedListsOfWidgetId(widgetId, widgetWrapperType, false);
        if(CollectionUtils.isNotEmpty(existingRelListInWidget)) {
            throw new IllegalArgumentException("Related list already exists in the widget, add call is invalid");
        }
        BulkRelatedListContext bulkRelatedList = (BulkRelatedListContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        if(bulkRelatedList != null) {
            List<RelatedListWidgetContext> relLists = new ArrayList<>();
            double sequenceNumber = 0;
            for(RelatedListWidgetContext relList : bulkRelatedList.getRelatedList()) {
                relList.setStatus(true);
                relList.setSequenceNumber(sequenceNumber += 10);
                RelatedListWidgetUtil.setWidgetIdForRelList(widgetId, relList, widgetWrapperType);
                if(relList.getConnectedAppWidgetId() == null || relList.getConnectedAppWidgetId() <= 0) {
                    if (relList.getSubModuleId() == null) {
                        FacilioModule subModule = modBean.getModule(relList.getSubModuleName());
                        Objects.requireNonNull(subModule, "Invalid module name");
                        relList.setSubModuleId(subModule.getModuleId());
                    }
                    Objects.requireNonNull(relList.getFieldId(), "FieldId can't be null");
                }
                relLists.add(relList);
            }

            if(CollectionUtils.isNotEmpty(relLists)) {
                RelatedListWidgetUtil.insertRelatedListsToDB(relLists);//add newRelList
            }
        }

        context.put(FacilioConstants.WidgetNames.BULK_RELATED_LIST_WIDGET, RelatedListWidgetUtil.getBulkRelatedListOfWidgetId(widgetId, widgetWrapperType));

        return false;
    }


}
