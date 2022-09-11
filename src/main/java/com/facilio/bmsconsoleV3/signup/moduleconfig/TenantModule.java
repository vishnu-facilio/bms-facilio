package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class TenantModule extends BaseModuleConfig{
    public TenantModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenant = new ArrayList<FacilioView>();
        tenant.add(getAllTenantsView().setOrder(order++));
        tenant.add(getActiveTenantsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT);
        groupDetails.put("views", tenant);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantsView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenants");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return allView;
    }

    private static FacilioView getActiveTenantsView() {
        FacilioField localId = new FacilioField();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getTenantStateCondition("Active"));


        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView activeTenantsView = new FacilioView();
        activeTenantsView.setName("active");
        activeTenantsView.setDisplayName("Residing Tenants");
        activeTenantsView.setSortFields(Arrays.asList(new SortField(localId, false)));
        activeTenantsView.setCriteria(criteria);

        return activeTenantsView;
    }

    private static Condition getTenantStateCondition(String state) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusCondition = new Condition();
        statusCondition.setField(statusTypeField);
        statusCondition.setOperator(StringOperators.IS);
        statusCondition.setValue(state);

        Criteria statusCriteria = new Criteria() ;
        statusCriteria.addAndCondition(statusCondition);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getTenantsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition condition = new Condition();
        condition.setField(statusField);
        condition.setOperator(LookupOperator.LOOKUP);
        condition.setCriteriaValue(statusCriteria);

        return condition;
    }
}
