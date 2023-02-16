package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.v3.context.Constants;

import java.util.*;

public class BaseSpaceModule extends BaseModuleConfig{
    public BaseSpaceModule(){
        setModuleName(FacilioConstants.ContextNames.BASE_SPACE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> baseSpace = new ArrayList<FacilioView>();
        baseSpace.add(getAllBasespaces().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BASE_SPACE);
        groupDetails.put("views", baseSpace);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="BaseSpace_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE),tableName);
    }

    private static FacilioView getAllBasespaces() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Basespaces");
        allView.setSortFields(sortFields);

        return allView;
    }
}
