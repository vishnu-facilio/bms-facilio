package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsoleV3.commands.peoplegroup.PeopleGroupUtils;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import com.facilio.beans.UserScopeBean;

import java.util.List;

public class UpdatePeopleRelatedDataCommand  extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);

        if(user != null){
            PeopleContext people = user.getPeople();
            Long ouid = user.getOrgUserId();
            if(people != null && people.getId() > 0){
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                    List<Long> permissionSetIds = people.getPermissionSets();
                    if(permissionSetIds != null) {
                        permissionSetBean.updateUserPermissionSets(people.getId(), permissionSetIds);
                    }
                }
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = people.getScopingId();
                    userScopeBean.updatePeopleScoping(people.getId(), scopingId);
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
