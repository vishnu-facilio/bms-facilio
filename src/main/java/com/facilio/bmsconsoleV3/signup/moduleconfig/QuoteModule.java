package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class QuoteModule extends BaseModuleConfig{
    public QuoteModule(){
        setModuleName(FacilioConstants.ContextNames.QUOTE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> quote = new ArrayList<FacilioView>();
        quote.add(getAllQuotations().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.QUOTE);
        groupDetails.put("views", quote);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllQuotations() {

        FacilioModule module = ModuleFactory.getQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
