package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.identity.client.IdentityClient;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3PeopleContext> pplList = Constants.getRecordList((FacilioContext) context);
        for(V3PeopleContext ppl:pplList){
            List<User> users = PeopleAPI.getUsersForPeopleId(ppl.getId());
            if(CollectionUtils.isNotEmpty(users)) {
                for (User user : users) {
                    com.facilio.identity.client.dto.User userObj = new com.facilio.identity.client.dto.User();
                    userObj.setName(ppl.getName());
                    userObj.setPhone(ppl.getPhone());
                    userObj.setMobile(ppl.getMobile());
                    userObj.setLanguage(ppl.getLanguage());
                    userObj.setTimezone(ppl.getTimezone());
                    IdentityClient.getDefaultInstance().getUserBean().updateUser(user.getUid(),userObj);
                }
            }

        }
        return false;
    }
}
