package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabsContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Collections;
public class PatchPageTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PageTabsContext tab = (PageTabsContext) context.get(FacilioConstants.CustomPage.TAB);
        FacilioUtil.throwIllegalArgumentException(tab==null,"Tab can't be null");

        CustomPageAPI.patchPageTab(tab);
        return false;
    }
}