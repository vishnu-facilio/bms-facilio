package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.glassfish.jersey.spi.ScheduledThreadPoolExecutorProvider;

import java.util.List;
import java.util.Map;

public class DropControlActionsOfControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if(CollectionUtils.isEmpty(recordIds)){
            return false;
        }
        for(Long id : recordIds){
            ControlActionAPI.dropControlActionsOfControlActionTemplate(id);
        }
        return false;
    }
}
