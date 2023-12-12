package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateViewCriteriaAndCustomizationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long viewId = (long) context.get(FacilioConstants.ContextNames.VIEWID);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        List<ViewField> viewFields = (List<ViewField>) context.get(FacilioConstants.ContextNames.VIEWCOLUMNS);

        if(filterCriteria != null) {
            FacilioView view = ViewAPI.getView(viewId);
            ViewAPI.appendFilterCriteriaWithViewCriteria(view, filterCriteria);
        }

        if(CollectionUtils.isNotEmpty(viewFields)) {
            ViewAPI.updateViewColumnCustomization(viewFields);
        }

        context.put(FacilioConstants.ContextNames.EXISTING_CV, ViewAPI.getView(viewId));
        return false;
    }
}
