package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
public class PatchPageColumnCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PageColumnContext column = (PageColumnContext) context.get(FacilioConstants.CustomPage.COLUMN);

        CustomPageAPI.patchPageColumn(column);

        return false;
    }
}