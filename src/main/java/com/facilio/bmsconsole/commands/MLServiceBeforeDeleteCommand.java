package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class MLServiceBeforeDeleteCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get("recordIds");
        if (CollectionUtils.isNotEmpty(recordIds)) {
            for(Long mlServiceId : recordIds) {
                V3MLServiceContext mlServiceContext = new V3MLServiceContext();
                mlServiceContext.setId(mlServiceId);
                List<MLContext> mlContexts = MLServiceUtil.getMLRecordsByMLServiceId(mlServiceContext);
                if (mlContexts == null) {
                    continue;
                }
                for (MLContext mlContext : mlContexts) {
                    boolean isDeleted = MLUtil.deleteML(mlContext.getId());
                    if (!isDeleted) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
