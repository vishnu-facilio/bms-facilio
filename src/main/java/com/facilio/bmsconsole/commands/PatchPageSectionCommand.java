package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Collections;
public class PatchPageSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PageSectionContext section = (PageSectionContext) context.get(FacilioConstants.CustomPage.SECTION);

        CustomPageAPI.patchPageSection(section);
        return false;
    }
}
