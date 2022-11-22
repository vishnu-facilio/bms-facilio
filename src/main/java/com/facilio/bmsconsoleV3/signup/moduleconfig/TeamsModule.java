package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.v3.context.Constants;

import java.util.*;

public class TeamsModule extends BaseModuleConfig {

    public TeamsModule() throws Exception {
        setModuleName(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> teamViews = new ArrayList<FacilioView>();
        teamViews.add(getAllTeamsModuleViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.PeopleGroup.PEOPLE_GROUP);
        groupDetails.put("views", teamViews);
        groupVsViews.add(groupDetails);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        groupDetails.put("appLinkNames", appLinkNames);     // List of AppLinkNames to which new groups should be created

        return groupVsViews;
    }

    private  FacilioView getAllTeamsModuleViews() throws Exception {
        FacilioModule teamModule = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Teams");
        allView.setModuleName(teamModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        return allView;

    }
}
