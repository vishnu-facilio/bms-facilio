package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.AddModuleViewsAndGroups;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class AccountTypeModule extends BaseModuleConfig{
    public AccountTypeModule(){
        setModuleName(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> accountType = new ArrayList<FacilioView>();
        accountType.add(getAllAccountTypeView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE);
        groupDetails.put("views", accountType);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAccountTypeView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Account Types");
        allView.setModuleName("accounttype");
        allView.setSortFields(sortFields);

        return allView;
    }
}