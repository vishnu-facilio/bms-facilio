package com.facilio.bmsconsoleV3.commands.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CancelParentChildAnnouncementsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> announcements = recordMap.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        if (CollectionUtils.isNotEmpty(announcements)) {
            for (AnnouncementContext announcement : announcements) {
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("cancel")) {
                    //announcement.setIsCancelled(true);
                    //V3RecordAPI.updateRecord(announcement, module, Arrays.asList(fieldMap.get("isCancelled")));

                    AnnouncementAPI.cancelChildAnnouncements(announcement);
                }
            }
        }
        return false;
    }
}
