package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.peoplegroup.PeopleGroupUtils;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddPeopleRelatedDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);

        if(user != null){
            PeopleContext people = user.getPeople();
            Long ouid = user.getOrgUserId();
            if(people != null && people.getId() > 0){
                List<Long> groups = people.getGroups();
                if(groups != null) {
                    PeopleGroupUtils.addPeopleToGroups(groups, people.getId(), ouid);
                }
                List<Long> accessibleSpace = people.getAccessibleSpace();
                if(accessibleSpace != null){
                    AccessibleSpacesUtil.addAccessibleSpace(ouid,people.getId(),accessibleSpace);
                }
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                    List<Long> permissionSetIds = people.getPermissionSets();
                    if(permissionSetIds != null) {
                        permissionSetBean.updateUserPermissionSets(people.getId(), permissionSetIds);
                    }
                }
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = people.getScopingId();
                    userScopeBean.updatePeopleScoping(people.getId(), scopingId);
                }else{
                    ScopingContext defaultScoping = ApplicationApi.getDefaultScopingForApp(user.getApplicationId());
                    if(defaultScoping != null){
                        ApplicationApi.updateScopingForUser(defaultScoping.getId(), user.getApplicationId(),user.getOrgUserId());
                    }
                }
//                Long shiftId = people.getShiftId();
//                if(shiftId != null && shiftId > 0){
//                    V3EmployeeContext employee = new V3EmployeeContext();
//                    employee.setId(people.getId());
//                    Shift shift = new Shift();
//                    shift.setId(shiftId);
//                    ShiftAPI.updateShift(employee,shift,ShiftAPI.UNLIMITED_PERIOD,ShiftAPI.UNLIMITED_PERIOD);
//                } else {
//                    ShiftAPI.assignDefaultShiftToEmployee(people.getId());
//                }
            }
        }
        return false;
    }
}
