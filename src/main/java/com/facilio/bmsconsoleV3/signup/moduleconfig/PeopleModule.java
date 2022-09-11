package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class PeopleModule extends BaseModuleConfig{
    public PeopleModule(){
        setModuleName(FacilioConstants.ContextNames.PEOPLE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> people = new ArrayList<FacilioView>();
        people.add(getAllPeople().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PEOPLE);
        groupDetails.put("views", people);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPeople() {

        FacilioModule peopleModule = ModuleFactory.getPeopleModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("People");
        allView.setModuleName(peopleModule.getName());
        allView.setSortFields(sortFields);
        return allView;
    }

}
