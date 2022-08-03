package com.facilio.v3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.PublicFileUtil;
import org.apache.commons.chain.Context;

import java.io.File;

public class AddPublicFileCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String fileName = (String)context.get(FacilioConstants.ContextNames.FILE_NAME);
        String fileContentType = (String)context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);
        File file = (File)context.get(FacilioConstants.ContextNames.FILE);
        long expiryOn = (long)context.get(FacilioConstants.ContextNames.EXPIRY_ON);

        String publicFileUrl = PublicFileUtil.createPublicFileWithOrgId(file,fileName,null, fileContentType,expiryOn);

        context.put(FacilioConstants.ContextNames.PUBLIC_FILE_URL, publicFileUrl);

        return false;
    }
}
