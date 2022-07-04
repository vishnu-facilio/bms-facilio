package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class PublishAnnouncementCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> announcements = recordMap.get(moduleName);
        AnnouncementContext announcement = (AnnouncementContext) Constants.getRecordList((FacilioContext) context).get(0);

        //CommunityFeaturesAPI.addAnnouncementPeople(announcements.get(0));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        if (CollectionUtils.isNotEmpty(announcements) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("publish")) {
            FacilioContext jobContext =  new FacilioContext();
            jobContext.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS, announcements);
            jobContext.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT_ACTION, 1);
            FacilioTimer.scheduleInstantJob("AddOrUpdateChildAnnouncementsJob", jobContext);
            CommonCommandUtil.addEventType(EventType.ANNOUNCEMENT_PUBLISH, (FacilioContext) context);

        }
        return false;
    }
}
