package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class FetchInviteAcceptedUsersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3PeopleContext> peoples = V3PeopleAPI.getAllPeople();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (CollectionUtils.isNotEmpty(peoples)) {
            List<Long> peopleIds = peoples.stream().map(V3PeopleContext::getId).collect(Collectors.toList());
            fetchPeopleAndUsers(peopleIds,jsonArray);
        }

        jsonObject.put("pickList",jsonArray);
        context.put(FacilioConstants.ContextNames.DATA,jsonObject);

        return false;
    }

    private void fetchPeopleAndUsers(List<Long> peopleIds, JSONArray jsonArray) throws Exception {

        List<User> users = V3PeopleAPI.fetchUserContext(peopleIds,true);

        if (CollectionUtils.isNotEmpty(users)) {
            JSONObject jsonObject = new JSONObject();
            for (User user : users) {
                jsonObject.put("label",user.getName());
                jsonObject.put("value",user.getPeopleId());
            }
            jsonArray.add(jsonObject);
        }
    }
}
