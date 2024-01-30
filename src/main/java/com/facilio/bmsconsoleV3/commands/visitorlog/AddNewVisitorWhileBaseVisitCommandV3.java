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
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
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

public class AddNewVisitorWhileBaseVisitCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BaseVisitContextV3> visitorLogs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            for(BaseVisitContextV3 vL : visitorLogs) {

                if(vL.getRequestedBy() == null || vL.getRequestedBy().getId() <= 0) {
                    vL.setRequestedBy(AccountUtil.getCurrentUser());
                }
                else {
                    if(vL.getHost() == null) {
//                        V3ContactsContext tenantContact = V3ContactsAPI.getContactsIdForUser(vL.getRequestedBy().getId());
//                        if(tenantContact != null){
//                            vL.setHost(tenantContact);
//                        }
                    	long tenantPeopleId = V3PeopleAPI.getPeopleIdForUser(vL.getRequestedBy().getId());
                    	if(tenantPeopleId != -1l) {
                    		V3PeopleContext tenantPeople = V3PeopleAPI.getPeopleById(tenantPeopleId);
                    		if(tenantPeople != null) {
                    			vL.setHost(tenantPeople);
                    		}  		
                    	}
                    }
                }
                if(vL.getTenant() == null) {
                        V3TenantContext tenant = V3PeopleAPI.getTenantForUser(vL.getRequestedBy().getOuid());
                        vL.setTenant(tenant);

                    if (vL.getTenant() == null && vL.getHost() != null) {
                        V3ContactsContext host = (V3ContactsContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.CONTACT, vL.getHost().getId(), V3ContactsContext.class);
                        if (host != null) {
                            vL.setTenant(host.getTenant());
                        }
                    }
                }
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
                    if(!V3VisitorManagementAPI.checkForDuplicateVisitor(vL.getVisitor())) {
                        V3RecordAPI.addRecord(true, Collections.singletonList(vL.getVisitor()) , module, fields);
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
                        V3RecordAPI.updateRecord(vL.getVisitor(), module, fields);
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

