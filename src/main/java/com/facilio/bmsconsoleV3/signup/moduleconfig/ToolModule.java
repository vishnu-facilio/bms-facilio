package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class ToolModule extends BaseModuleConfig{
    public ToolModule(){
        setModuleName(FacilioConstants.ContextNames.TOOL);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tool = new ArrayList<FacilioView>();
        tool.add(getAllTools().setOrder(order++));



        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TOOL);
        groupDetails.put("views", tool);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTools() {

        FacilioModule toolmodule = ModuleFactory.getToolModule();

        FacilioField createdTime = new LookupField();
        createdTime.setName("toolType");
        createdTime.setDataType(FieldType.LOOKUP);
        createdTime.setColumnName("TOOL_TYPE_ID");
        createdTime.setModule(toolmodule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tools");
        allView.setSortFields(sortFields);

        return allView;
    }
}
