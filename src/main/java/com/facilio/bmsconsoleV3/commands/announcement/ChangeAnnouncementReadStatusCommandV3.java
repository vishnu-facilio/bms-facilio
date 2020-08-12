package com.facilio.bmsconsoleV3.commands.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChangeAnnouncementReadStatusCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> recordIds = (List<Long>) context.get(Constants.RECORD_ID_LIST);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);



        if(CollectionUtils.isNotEmpty(recordIds))  {
            for(Long recordId : recordIds) {
                PeopleAnnouncementContext pplAnnouncement = (PeopleAnnouncementContext) V3RecordAPI.getRecord(moduleName, recordId, PeopleAnnouncementContext.class);
                if(pplAnnouncement != null && !pplAnnouncement.isRead()) {
                    pplAnnouncement.setIsRead(true);
                    V3RecordAPI.updateRecord(pplAnnouncement, module, Arrays.asList(fieldsMap.get("isRead")));
                }

            }
        }

        return false;
    }
}
