package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId == null || pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to create related list");
        }
        BulkRelatedListContext bulkRelatedList = (BulkRelatedListContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        List<RelatedListWidgetContext> existingRelListInWidget = RelatedListWidgetUtil.getRelatedListsOfWidgetId(pageWidgetId, false);
        List<Long> existingRelListIds = new ArrayList<>();
        if(existingRelListInWidget != null) {
            existingRelListIds = existingRelListInWidget.stream().map(RelatedListWidgetContext::getId).collect(Collectors.toList());
        }
        if(bulkRelatedList != null) {
            List<RelatedListWidgetContext> newRelList = new ArrayList<>();
            List<RelatedListWidgetContext> updateRelList = new ArrayList<>();
            double sequenceNumber = 0;
            if(CollectionUtils.isNotEmpty(bulkRelatedList.getRelatedList())) {
                for (RelatedListWidgetContext relList : bulkRelatedList.getRelatedList()) {
                    relList.setStatus(true);
                    relList.setSequenceNumber(sequenceNumber += 10);
                    relList.setWidgetId(pageWidgetId);

                    if (!(relList.getId() > 0)) {
                        if (relList.getSubModuleId() == null) {
                            FacilioModule subModule = modBean.getModule(relList.getSubModuleName());
                            Objects.requireNonNull(subModule, "Invalid module name");
                            relList.setSubModuleId(subModule.getModuleId());
                        }

                        Objects.requireNonNull(relList.getFieldId(), "FieldId can't be null");
                        newRelList.add(relList);
                    } else {
                        existingRelListIds.remove(relList.getId());
                        updateRelList.add(relList);
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(newRelList)) {
                RelatedListWidgetUtil.insertRelatedListsToDB(newRelList);//add newRelList
            }
            
            if(CollectionUtils.isNotEmpty(updateRelList)) {
                RelatedListWidgetUtil.updateRelatedList(updateRelList);//update existing relList status & sequnceNumber
            }

            if(CollectionUtils.isNotEmpty(existingRelListInWidget)) {
                RelatedListWidgetUtil.deleteRelatedLists(existingRelListIds);//deleting relList
            }
        }
        return false;
    }
}
