package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class BreakTransactionModule extends BaseModuleConfig{
    public BreakTransactionModule(){
        setModuleName(FacilioConstants.ContextNames.BREAK_TRANSACTION);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllBreakTransactionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BREAK_TRANSACTION);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBreakTransactionView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("startTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("START_TIME");
        createdTime.setModule(ModuleFactory.getBreakTransactionModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Break Transactions");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, true)));

        return allView;
    }
}

