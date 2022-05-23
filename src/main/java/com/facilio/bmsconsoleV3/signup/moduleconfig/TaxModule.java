package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class TaxModule extends BaseModuleConfig{
    public TaxModule(){
        setModuleName(FacilioConstants.ContextNames.TAX);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tax = new ArrayList<FacilioView>();
        tax.add(getAllTaxes().setOrder(order++));
        tax.add(getActiveTaxes().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TAX);
        groupDetails.put("views", tax);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTaxes() {

        FacilioModule module = ModuleFactory.getTaxModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Taxes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getActiveTaxes() {

        FacilioModule module = ModuleFactory.getTaxModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView view = new FacilioView();
        view.setName("active");
        view.setDisplayName("Active Taxes");
        view.setModuleName(module.getName());
        view.setSortFields(sortFields);

        Criteria activeCriteria = new Criteria();
        activeCriteria.addAndCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive",String.valueOf(true), BooleanOperators.IS));
        view.setCriteria(activeCriteria);

        return view;
    }
}
