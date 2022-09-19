package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class TransactionModule extends BaseModuleConfig{
    public TransactionModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.TRANSACTION);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> transactionViews = new ArrayList<FacilioView>();
        transactionViews.add(getAllTransactionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT);
        groupDetails.put("views", transactionViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTransactionView() {

        FacilioModule module = ModuleFactory.getTransactionModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
