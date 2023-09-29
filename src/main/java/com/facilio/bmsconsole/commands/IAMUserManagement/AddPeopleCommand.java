package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);
        PeopleContext existingPeople = ApplicationUserUtil.checkIfExistsinPeople(user.getUser().getEmail());
        if(existingPeople==null){
            V3PeopleAPI.validatePeopleEmail(user.getUser().getEmail());

            if( (AccountUtil.getCurrentOrg().getDomain() != null) && (user.getUser().getTimezone() == null) ) {
                user.getUser().setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
            }
            if( (AccountUtil.getCurrentUser() != null) && (user.getUser().getLanguage() == null) ) {
                user.getUser().setLanguage(AccountUtil.getCurrentUser().getLanguage());
            }

            EmployeeContext employee = new EmployeeContext();
            employee.setPeopleType(PeopleContext.PeopleType.EMPLOYEE.getIndex());
            employee.setEmail(user.getUser().getEmail());
            employee.setName(user.getUser().getName());
            employee.setPhone(user.getUser().getPhone());
            employee.setLanguage(user.getUser().getLanguage());
            employee.setTimezone(user.getUser().getTimezone());
            employee.setMobile(user.getUser().getMobile());
            employee.setUser(true);

            FacilioChain c = TransactionChainFactory.addEmployeeChain();
            c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, false);
            c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

            c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(employee));
            c.execute();

            user.setPeopleId(employee.getId());
            if(user.getPeople() != null){
                user.getPeople().setId(employee.getId());
            }
            user.getUser().setInvitedBy(AccountUtil.getCurrentUser().getUid());
        }
        else{
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                List<PermissionSetContext> permissionSetList = permissionSetBean.getUserPermissionSetMapping(existingPeople.getId(), false);
                if (CollectionUtils.isNotEmpty(permissionSetList)) {
                    List<Long> permissionSetIds = permissionSetList.stream().map(PermissionSetContext::getId).collect(Collectors.toList());
                    existingPeople.setPermissionSets(permissionSetIds);
                }
            }

            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                Long scopingId = userScopeBean.getPeopleScoping(existingPeople.getId());
                existingPeople.setScopingId(scopingId);
            }
            existingPeople.setUser(true);
            RecordAPI.updateRecord(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(existingPeople), PeopleContext.class), Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE), Constants.getModBean().getAllFields(FacilioConstants.ContextNames.PEOPLE));
            user.setPeopleId(existingPeople.getId());
            user.setPeople(existingPeople);
            user.getUser().setName(existingPeople.getName());
            user.getUser().setEmail(existingPeople.getEmail());
            user.getUser().setTimezone(existingPeople.getTimezone());
            user.getUser().setLanguage(existingPeople.getLanguage());
            user.getUser().setPhone(existingPeople.getPhone());
            user.getUser().setInvitedBy(AccountUtil.getCurrentUser().getUid());
            user.getUser().setMobile(existingPeople.getMobile());

        }


        return false;
    }
}
