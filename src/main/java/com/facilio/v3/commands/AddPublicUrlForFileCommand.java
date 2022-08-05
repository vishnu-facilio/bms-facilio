package com.facilio.v3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.PublicFileUtil;
import org.apache.commons.chain.Context;

public class AddPublicUrlForFileCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long fileId =(long) context.get(FacilioConstants.ContextNames.FILE_ID);

        String publicFileUrl = PublicFileUtil.createPublicFileUrl(fileId);

        context.put(FacilioConstants.ContextNames.PUBLIC_FILE_URL, publicFileUrl);

        return false;
    }
}
