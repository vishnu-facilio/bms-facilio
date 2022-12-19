package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetAnnouncementPhotoIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> announcements = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField photoID = modBean.getField("photoId",FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT);
        if(photoID == null){
            return false;
        }

        if(CollectionUtils.isNotEmpty(announcements)) {
            for(AnnouncementContext announcement : announcements){
                if(announcement.getPhotoId() != null){
                    continue;
                }
                if(context.containsKey("rawInput")){
                    Map<String,Object> rawInput = (Map<String,Object>)context.get("rawInput");
                    if(!rawInput.isEmpty()){
                        if(rawInput.containsKey("announcementattachments")){
                            List<HashMap> announcementAttachments = (List<HashMap>) rawInput.get("announcementattachments");
                            if(!announcementAttachments.isEmpty()){
                                FileStore fs = FacilioFactory.getFileStore();
                                for (HashMap attachment : announcementAttachments) {
                                    Long fileId =(Long) attachment.get("fileId");
                                    FileInfo fileinfo = fs.getFileInfo(fileId);
                                    if (fileinfo != null && fileinfo.getContentType() != null && fileinfo.getContentType().contains("image")) {
                                        announcement.setPhotoId(fileId);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return false;
    }
}
