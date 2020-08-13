package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;

public class PreFillInviteVisitorCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InviteVisitorContextV3> inviteVisitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(inviteVisitorLogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<Long, VisitorSettingsContext> settingsMap = VisitorManagementAPI.getVisitorSettingsForType();
            for(InviteVisitorContextV3 vL : inviteVisitorLogs) {
                CommonCommandUtil.handleLookupFormData(modBean.getAllFields(FacilioConstants.ContextNames.INVITE_VISITOR), vL.getData());
            	if(vL.getChildVisitTypeEnum() == null) {
            		vL.setChildVisitType(BaseVisitContextV3.ChildVisitType.INVITE);
                }        
            }
        }
        return false;
    }
}

