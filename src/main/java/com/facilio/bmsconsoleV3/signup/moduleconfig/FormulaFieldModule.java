package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class FormulaFieldModule extends BaseModuleConfig{
    public FormulaFieldModule(){
        setModuleName(FacilioConstants.ContextNames.FORMULA_FIELD);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> formulaField = new ArrayList<FacilioView>();
        Map<String, FacilioField> formulaFieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldFields());
        formulaField.add(getAssetKPIView("asset", formulaFieldMap).setOrder(order++));
        formulaField.add(getSpaceKPIView("space", formulaFieldMap).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FORMULA_FIELD);
        groupDetails.put("views", formulaField);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAssetKPIView(String name, Map<String, FacilioField> fieldMap) {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getKPICondition(fieldMap));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceType"), String.valueOf(FormulaFieldContext.ResourceType.ASSET_CATEGORY.getValue()), NumberOperators.EQUALS));

        FacilioView assetKpisView = new FacilioView();
        assetKpisView.setName(name);
        assetKpisView.setDisplayName("Asset KPIs");
        assetKpisView.setCriteria(criteria);

        return assetKpisView;
    }

    private static FacilioView getSpaceKPIView(String name, Map<String, FacilioField> fieldMap) {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getKPICondition(fieldMap));
        // Assuming all the asset kpis will have asset category
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceType"), String.valueOf(FormulaFieldContext.ResourceType.ASSET_CATEGORY.getValue()), NumberOperators.NOT_EQUALS));

        FacilioView assetKpisView = new FacilioView();
        assetKpisView.setName(name);
        assetKpisView.setDisplayName("Space KPIs");
        assetKpisView.setCriteria(criteria);

        return assetKpisView;
    }

    private static Condition getKPICondition(Map<String, FacilioField> fieldMap) {
        return CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldContext.FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS);
    }
}
