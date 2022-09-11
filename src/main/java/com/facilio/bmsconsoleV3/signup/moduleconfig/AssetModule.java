package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class AssetModule extends BaseModuleConfig{
    public AssetModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.ASSET);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails = new HashMap<>();

        int order = 1;
        ArrayList<FacilioView> asset = new ArrayList<FacilioView>();
        asset.add(getAllAssetsView().setOrder(order++));
        asset.add(getAssets("Energy").setOrder(order++));
        asset.add(getAssets("HVAC").setOrder(order++));
        asset.add(getAssetsByState("Active").setOrder(order++));
        asset.add(getAssetsByState("Retired").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "assetviews");
        groupDetails.put("displayName", "Asset");
        groupDetails.put("views", asset);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAssetsView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Assets");
        allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));

        return allView;
    }

    private static FacilioView getAssets(String category) {

        FacilioView assetView = new FacilioView();
        if (category.equals("Energy")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("energy");
            assetView.setDisplayName("Energy Assets");
            assetView.setCriteria(criteria);
        } else if (category.equals("HVAC")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("hvac");
            assetView.setDisplayName("HVAC Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return assetView;
    }

    private static FacilioView getAssetsByState(String state) {

        FacilioView assetView = new FacilioView();
        Criteria criteria = getAssetStatusCriteria(state);
        if (state.equals("Active")) {
            assetView.setName("active");
            assetView.setDisplayName("Active Assets");
            assetView.setCriteria(criteria);
        } else if (state.equals("Retired")) {
            assetView.setName("retired");
            assetView.setDisplayName("Retired Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return assetView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(ModuleFactory.getAssetsModule());

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

    private static Condition getAssetCategoryCondition(String category) {
        FacilioModule module = ModuleFactory.getAssetsModule();
        LookupField statusField = new LookupField();
        statusField.setName("category");
        statusField.setColumnName("CATEGORY");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getAssetCategoryModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getAssetCategoryCriteria(category));

        return open;
    }

    private static Criteria getAssetStatusCriteria(String status) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusActive = new Condition();
        statusActive.setField(statusTypeField);
        statusActive.setOperator(StringOperators.IS);
        statusActive.setValue(status);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusActive);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getAssetsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition ticketActive = new Condition();
        ticketActive.setField(statusField);
        ticketActive.setOperator(LookupOperator.LOOKUP);
        ticketActive.setCriteriaValue(statusCriteria);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketActive);
        return criteria;
    }

    private static Criteria getAssetCategoryCriteria(String category) {
        FacilioField categoryType = new FacilioField();
        categoryType.setName("categoryType");
        categoryType.setColumnName("CATEGORY_TYPE");
        categoryType.setDataType(FieldType.NUMBER);
        categoryType.setModule(ModuleFactory.getAssetCategoryModule());

        Condition statusOpen = new Condition();
        statusOpen.setField(categoryType);
        statusOpen.setOperator(NumberOperators.EQUALS);
        if (category.equals("Energy")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.ENERGY.getIntVal()));
        } else if (category.equals("HVAC")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.HVAC.getIntVal()));
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }
}
