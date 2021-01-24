package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class PreFillVisitorLogCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VisitorLogContextV3> visitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<Long, VisitorSettingsContext> settingsMap = VisitorManagementAPI.getVisitorSettingsForType();
            for(VisitorLogContextV3 vL : visitorLogs) {
                CommonCommandUtil.handleLookupFormData(modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOG), vL.getData());
            	if(vL.getChildVisitTypeEnum() == null) {
            		vL.setChildVisitType(BaseVisitContextV3.ChildVisitType.VISIT);
                } 
                VisitorSettingsContext setting = settingsMap.get(vL.getVisitorType().getId());
                if(setting != null) {
                    JSONObject hostSetting = setting.getHostSettings();
                    if(hostSetting.get("requireApproval") != null && (boolean)hostSetting.get("requireApproval")) {
                        vL.setIsApprovalNeeded((boolean)hostSetting.get("requireApproval"));
                    }
                    else {
                        vL.setIsApprovalNeeded(false);
                    }
                }
                else {
                    vL.setIsApprovalNeeded(false);
                }
              
            }
        }
        return false;
    }
}

