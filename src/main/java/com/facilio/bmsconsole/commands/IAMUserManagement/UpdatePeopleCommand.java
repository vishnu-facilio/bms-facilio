package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class UpdatePeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioChain c = TransactionChainFactory.updateEmployeeChain();
        c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);

        if(!user.getUser().getIsSuperUser() && user.getRole() != null && !user.getRole().isSuperAdmin()){
            PeopleContext existingPeople = PeopleAPI.getPeople(user.getUser().getEmail());
            user.setPeopleId(existingPeople.getId());
            if (user.getPeople() != null) {
                user.getPeople().setId(existingPeople.getId());
            }

            EmployeeContext employee = new EmployeeContext();
            employee.setId(existingPeople.getId());
            employee.setName(user.getUser().getName());
            employee.setPhone(user.getUser().getPhone());
            employee.setLanguage(user.getUser().getLanguage());
            employee.setTimezone(user.getUser().getTimezone());
            employee.setMobile(user.getUser().getMobile());

            employee.parseFormData();
            RecordAPI.handleCustomLookup(employee.getData(), FacilioConstants.ContextNames.EMPLOYEE);

            c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(employee));
            c.execute();
        }else {
            throw new IllegalArgumentException("SuperUser can't be updated");
        }

        return false;
    }
}
