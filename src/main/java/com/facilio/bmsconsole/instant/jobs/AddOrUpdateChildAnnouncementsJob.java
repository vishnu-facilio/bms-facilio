package com.facilio.bmsconsole.instant.jobs;

import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.InstantJob;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddOrUpdateChildAnnouncementsJob extends InstantJob {

    @Override
    public void execute(FacilioContext context) throws Exception {
      List<AnnouncementContext> parentAnnouncements = (List<AnnouncementContext>) context.get(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS);
      Integer action = (Integer) context.get(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT_ACTION);
      //1-publish 2-cancel 3-delete

        if(action != null) {
          if(action == 1){
              if(CollectionUtils.isNotEmpty(parentAnnouncements)){
                  for (AnnouncementContext announcement : parentAnnouncements) {
                      AnnouncementAPI.addAnnouncementPeople(announcement);
                  }
              }
          }
          else if(action == 2) {
              if(CollectionUtils.isNotEmpty(parentAnnouncements)) {
                  for (AnnouncementContext announcement : parentAnnouncements) {
                      AnnouncementAPI.cancelChildAnnouncements(announcement);
                  }
              }
          }
          else if(action == 3) {
              List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
              if(CollectionUtils.isNotEmpty(recordIds)) {
                  AnnouncementAPI.deleteChildAnnouncements(recordIds);
              }
          }
      }
    }
}
