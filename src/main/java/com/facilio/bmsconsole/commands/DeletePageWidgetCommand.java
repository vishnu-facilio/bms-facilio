package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeletePageWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);

        if(id != null && id > 0){
            CustomPageAPI.deletePageComponent(id, ModuleFactory.getPageSectionWidgetsModule());
        }
        return false;
    }
}
