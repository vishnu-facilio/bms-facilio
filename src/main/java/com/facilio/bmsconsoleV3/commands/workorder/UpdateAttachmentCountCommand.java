package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateAttachmentCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception{
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)) {
            List<Long> idsToUpdate = wos.stream().map(V3WorkOrderContext::getId).collect(Collectors.toList());
            AttachmentsAPI.updateAttachmentCount(idsToUpdate,FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
        }
        return false;

    }

}
