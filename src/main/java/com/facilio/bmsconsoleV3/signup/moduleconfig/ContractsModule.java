package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ContractsModule extends BaseModuleConfig{
    public ContractsModule(){
        setModuleName(FacilioConstants.ContextNames.CONTRACTS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> contracts = new ArrayList<FacilioView>();
        contracts.add(getAllContractView(ModuleFactory.getContractsModule()).setOrder(order++));
        contracts.add(getExpiringContractView(ModuleFactory.getContractsModule(), -1).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTRACTS);
        groupDetails.put("views", contracts);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllContractView(FacilioModule module) {

        FacilioField parentIdField = new FacilioField();
        parentIdField.setName("parentId");
        parentIdField.setColumnName("PARENT_ID");
        parentIdField.setDataType(FieldType.NUMBER);
        parentIdField.setModule(module);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setCriteria(getAllContractListCriteria());
        allView.setSortFields(Arrays.asList(new SortField(parentIdField, false)));

        return allView;
    }

    private static FacilioView getExpiringContractView(FacilioModule module, int type) {
        FacilioField endDateField = new FacilioField();
        endDateField.setName("endDate");
        endDateField.setColumnName("END_DATE");
        endDateField.setDataType(FieldType.DATE);
        endDateField.setModule(module);

        FacilioView allView = new FacilioView();
        allView.setName("expiring");
        allView.setDisplayName("Expiring This Month");
        allView.setCriteria(getExpiringContractListCriteria(module, type));
        allView.setSortFields(Arrays.asList(new SortField(endDateField, false)));

        return allView;
    }

    private static Criteria getAllContractListCriteria() {

        FacilioField revisionNumberField = new FacilioField();
        revisionNumberField.setName("revisionNumber");
        revisionNumberField.setColumnName("REVISION_NUMBER");
        revisionNumberField.setDataType(FieldType.NUMBER);
        revisionNumberField.setModule(ModuleFactory.getContractsModule());

        Condition revisionNumberCond = new Condition();
        revisionNumberCond.setField(revisionNumberField);
        revisionNumberCond.setOperator(NumberOperators.EQUALS);
        revisionNumberCond.setValue("0");

        Criteria criteria = new Criteria ();
        criteria.addAndCondition(revisionNumberCond);

        return criteria;
    }

    private static Criteria getExpiringContractListCriteria(FacilioModule module, int type) {

        FacilioField statusField = new FacilioField();
        statusField.setName("status");
        statusField.setColumnName("STATUS");
        statusField.setDataType(FieldType.NUMBER);
        FacilioModule contract = module;
        statusField.setModule(contract);

        FacilioField endDateField = new FacilioField();
        endDateField.setName("endDate");
        endDateField.setColumnName("END_DATE");
        endDateField.setDataType(FieldType.DATE);
        endDateField.setModule(contract);

        Condition expiryCond = new Condition();
        expiryCond.setField(endDateField);
        expiryCond.setOperator(DateOperators.CURRENT_MONTH);

        Condition statusCond = new Condition();
        statusCond.setField(statusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(ContractsContext.Status.APPROVED.getValue()));

        Criteria criteria = new Criteria ();
        criteria.addAndCondition(statusCond);
        criteria.addAndCondition(expiryCond);

        if(type > 0) {
            FacilioField typeField = new FacilioField();
            typeField.setName("contractType");
            typeField.setColumnName("CONTRACT_TYPE");
            typeField.setDataType(FieldType.ENUM);
            typeField.setModule(contract);

            Condition typeCond = new Condition();
            typeCond.setField(typeField);
            typeCond.setValue(String.valueOf(type));
            typeCond.setOperator(EnumOperators.IS);
            criteria.addAndCondition(typeCond);

        }

        return criteria;
    }
}
