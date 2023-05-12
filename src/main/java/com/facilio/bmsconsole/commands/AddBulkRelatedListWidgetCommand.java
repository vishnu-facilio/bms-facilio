package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId == null || pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to create related list");
        }
        List<RelatedListWidgetContext> existingRelListInWidget = RelatedListWidgetUtil.getRelatedListsOfWidgetId(pageWidgetId, false);
        if(CollectionUtils.isNotEmpty(existingRelListInWidget)) {
            throw new IllegalArgumentException("Related list already exists, add call is Invalid");
        }
        BulkRelatedListContext bulkRelatedList = (BulkRelatedListContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        if(bulkRelatedList != null) {
            List<RelatedListWidgetContext> relLists = new ArrayList<>();
            double sequenceNumber = 0;
            for(RelatedListWidgetContext relList : bulkRelatedList.getRelatedLists()) {
                FacilioUtil.throwIllegalArgumentException(relList.getId()>0, "Invalid relList id");
                relList.setStatus(true);
                relList.setSequenceNumber(sequenceNumber += 10);
                relList.setWidgetId(pageWidgetId);

                if (relList.getSubModuleId() == null) {
                    FacilioModule subModule = modBean.getModule(relList.getSubModuleName());
                    Objects.requireNonNull(subModule, "Invalid module name");
                    relList.setSubModuleId(subModule.getModuleId());
                }

                Objects.requireNonNull(relList.getFieldId(), "FieldId can't be null");
                relLists.add(relList);
            }

            if(CollectionUtils.isNotEmpty(relLists)) {
                RelatedListWidgetUtil.insertRelatedListsToDB(relLists);//add newRelList
            }
        }

        return false;
    }
}
