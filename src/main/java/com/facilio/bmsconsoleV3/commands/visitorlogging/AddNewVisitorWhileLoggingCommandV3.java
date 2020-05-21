package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddNewVisitorWhileLoggingCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> visitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<Long, VisitorSettingsContext> settingsMap = VisitorManagementAPI.getVisitorSettingsForType();
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            for(V3VisitorLoggingContext vL : visitorLogs) {

                if(vL.getRequestedBy() == null || vL.getRequestedBy().getId() <= 0) {
                    vL.setRequestedBy(AccountUtil.getCurrentUser());
                }
                //if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
                //need to uncomment when all users are migrated as employees
                //	vL.setTenant(PeopleAPI.getTenantForUser(vL.getRequestedBy().getId()));
                //}
                //else {
                ContactsContext contact = ContactsAPI.getContactsIdForUser(vL.getRequestedBy().getId());
                if(contact != null && contact.getTenant() != null) {
                    vL.setTenant(contact.getTenant());
                }
                //}

                if(vL.getVisitor() != null && vL.getVisitor().getId() > 0) {
                    vL.setIsReturningVisitor(true);
                    vL.getVisitor().setIsReturningVisitor(true);
                }
                else {
                    vL.setIsReturningVisitor(false);
                    if(vL.getVisitor() != null) {
                        vL.getVisitor().setIsReturningVisitor(false);
                    }
                }
                VisitorSettingsContext setting = settingsMap.get(vL.getVisitorType().getId());
                if(setting != null) {
                    JSONObject hostSetting = setting.getHostSettings();
                    if(!vL.isPreregistered() && hostSetting.get("requireApproval") != null && (boolean)hostSetting.get("requireApproval")) {
                        vL.setIsApprovalNeeded((boolean)hostSetting.get("requireApproval"));
                    }
                    else {
                        vL.setIsApprovalNeeded(false);
                    }

                    vL.setIsInviteApprovalNeeded(setting.getApprovalRequiredForInvite());
                }
                else {
                    vL.setIsApprovalNeeded(false);
                }
                if(vL.getInvite() != null && vL.getInvite().getId() > 0) {
                    vL.setIsInvited(true);
                }
                else {
                    vL.setIsInvited(false);
                }
                if(vL.getAvatar() != null) {
                    vL.setPhotoStatus(true);
                }
                else {
                    vL.setPhotoStatus(false);
                }
                if(vL.getHost() != null && vL.getHost().getId() > 0) {
                    vL.setHostStatus(true);
                }
                else {
                    vL.setHostStatus(false);
                }
                if(vL.getVisitor() != null && vL.getVisitor().getId() <= 0) {
                    if(!VisitorManagementAPI.checkForDuplicateVisitor(vL.getVisitor())) {
                        RecordAPI.addRecord(true, Collections.singletonList(vL.getVisitor()) , module, fields);
                    }
                    else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "A Visitor Already exists with this phone number");
                    }
                }
                else if(vL.getVisitor() != null && vL.getVisitor().getId() > 0) {
                    VisitorContext vLVisitor = VisitorManagementAPI.getVisitor(vL.getVisitor().getId(), null);
                    if(StringUtils.isNotEmpty(vL.getVisitor().getPhone())) {
                        vLVisitor.setPhone(vL.getVisitor().getPhone());
                    }
                    if(!VisitorManagementAPI.checkForDuplicateVisitor(vLVisitor)) {
                        RecordAPI.updateRecord(vL.getVisitor(), module, fields);
                    }
                    else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "A Visitor Already exists with this phone number");
                    }
                }
            }
        }
        return false;
    }
}
