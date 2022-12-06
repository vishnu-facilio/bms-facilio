package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class WeatherStationModule extends BaseModuleConfig {

    public WeatherStationModule() throws Exception {
        setModuleName(FacilioConstants.ModuleNames.WEATHER_STATION);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();

        int order = 1;
        List<FacilioView> weatherStation = new ArrayList<>();
        weatherStation.add(getAllStationViews().setOrder(order));

        Map<String, Object> groupDetails = new HashMap<>();
        groupDetails.put("name", "stationviews");
        groupDetails.put("displayName", "Weather Station");
        groupDetails.put("views", weatherStation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllStationViews() throws Exception {
        String moduleName = FacilioConstants.ModuleNames.WEATHER_STATION;
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Stations");
        allView.setSortFields(Arrays.asList(new SortField(FieldFactory.getIdField(), false)));


        List<String> listViewFieldNames = new ArrayList() {{
            add("name");
            add("stationCode");
            add("lat");
            add("lng");
            add("service");
        }};

        ModuleBean modBean = Constants.getModBean();

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));

        List<ViewField> selectedFields = new ArrayList<>();
//        selectedFields.add(new ViewField("id", "ID"));
        listViewFieldNames.stream().forEach( row -> {
            FacilioField rf = fieldMap.get(row);
            selectedFields.add(new ViewField(rf.getName(), rf.getDisplayName()));
        });
        allView.setFields(selectedFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);

        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

}
