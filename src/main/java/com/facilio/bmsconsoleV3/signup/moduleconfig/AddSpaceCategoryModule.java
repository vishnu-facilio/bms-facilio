package com.facilio.bmsconsoleV3.signup.moduleconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.v3.context.Constants;

public class AddSpaceCategoryModule extends BaseModuleConfig {
	
	
	public AddSpaceCategoryModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.SPACE_CATEGORY);
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> spaceCategoryViews = new ArrayList<FacilioView>();
        spaceCategoryViews.add(getAllSpaceCategoryViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SPACE_CATEGORY);
        groupDetails.put("views", spaceCategoryViews);
        groupVsViews.add(groupDetails);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        groupDetails.put("appLinkNames", appLinkNames);     // List of AppLinkNames to which new groups should be created

        return groupVsViews;
    }

    private  FacilioView getAllSpaceCategoryViews() throws Exception {
        FacilioModule spaceCategoryModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Space Category");
        allView.setModuleName(spaceCategoryModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        return allView;
    }

}
