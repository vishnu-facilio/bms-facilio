package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class DeletePageTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);

        if(id > 0){
            CustomPageAPI.deletePageComponent(id,ModuleFactory.getPageTabsModule());
        }

        return false;
    }
}