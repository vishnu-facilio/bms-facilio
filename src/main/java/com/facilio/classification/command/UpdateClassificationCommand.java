package com.facilio.classification.command;

import com.facilio.classification.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class UpdateClassificationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ClassificationContext classification = (ClassificationContext) context.get(FacilioConstants.ContextNames.CLASSIFICATION);
        FacilioUtil.throwIllegalArgumentException(classification == null, "Classification cannot be emtpy");

        

        return false;
    }
}
