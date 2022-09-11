package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class AnnouncementModule extends BaseModuleConfig{
    public AnnouncementModule(){
        setModuleName(FacilioConstants.ContextNames.ANNOUNCEMENT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> announcement = new ArrayList<FacilioView>();
        announcement.add(getAllAnnouncementView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ANNOUNCEMENT);
        groupDetails.put("views", announcement);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAnnouncementView() {

        FacilioModule module = ModuleFactory.getAnnouncementModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Announcements");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
