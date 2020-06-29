package com.facilio.bmsconsoleV3.commands.clientcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForMandatoryClientIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ClientContactContext> clientContacts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(clientContacts)) {
            for(V3ClientContactContext cc : clientContacts) {
                cc.setPeopleType(V3PeopleContext.PeopleType.CLIENT_CONTACT.getIndex());
                if(cc.getClient() == null || cc.getClient().getId() <=0 ) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Client Contact must have a client id associated");
                }
            }
        }
        return false;
    }
}
