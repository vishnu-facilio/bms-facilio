package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ViewColumnCustomizationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ViewField> viewFields = (List<ViewField>) context.get(FacilioConstants.ContextNames.VIEWCOLUMNS);

        if(CollectionUtils.isNotEmpty(viewFields)) {
            ViewAPI.updateViewColumnCustomization(viewFields);
        }
        return false;
    }
}
