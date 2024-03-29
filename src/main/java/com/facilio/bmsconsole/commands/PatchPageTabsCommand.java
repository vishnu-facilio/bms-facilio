package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class PatchPageTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PageTabContext tab = (PageTabContext) context.get(FacilioConstants.CustomPage.TAB);
        FacilioUtil.throwIllegalArgumentException(tab==null,"Tab can't be null");

        CustomPageAPI.patchPageTab(tab);
        return false;
    }
}