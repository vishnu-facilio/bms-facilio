package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class LabourContractsModule extends BaseModuleConfig{
    public LabourContractsModule(){
        setModuleName(FacilioConstants.ContextNames.LABOUR_CONTRACTS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> labourContracts = new ArrayList<FacilioView>();
        labourContracts.add(getAllLabourContractView().setOrder(order++));
        labourContracts.add(getExpiringContractView(ModuleFactory.getContractsModule(), 2).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.LABOUR_CONTRACTS);
        groupDetails.put("views", labourContracts);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllLabourContractView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getLabourContractModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        //allView.setCriteria(getContractListCriteria());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

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

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
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

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule labourContractsModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS);

        FacilioForm labourContractsForm = new FacilioForm();
        labourContractsForm.setDisplayName("LABOUR CONTRACT");
        labourContractsForm.setName("web_default");
        labourContractsForm.setModule(labourContractsModule);
        labourContractsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        labourContractsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> labourContractsFormFields = new ArrayList<>();
        labourContractsFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        labourContractsFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        labourContractsFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 3, 1).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
        labourContractsFormFields.add(new FormField("fromDate", FacilioField.FieldDisplayType.DATE, "From Date", FormField.Required.OPTIONAL, 4, 2));
        labourContractsFormFields.add(new FormField("endDate", FacilioField.FieldDisplayType.DATE, "End Date", FormField.Required.OPTIONAL, 4, 3));
        labourContractsFormFields.add(new FormField("totalCost", FacilioField.FieldDisplayType.DECIMAL, "Total Cost", FormField.Required.OPTIONAL, 5, 2));
        labourContractsFormFields.add(new FormField("renewalDate", FacilioField.FieldDisplayType.DATE, "Renewal Date", FormField.Required.OPTIONAL, 5, 3));
        labourContractsFormFields.add(new FormField("lineItems", FacilioField.FieldDisplayType.LABOUR_LINE_ITEMS, "LABOUR RECORDS", FormField.Required.REQUIRED, 6, 1));
        labourContractsFormFields.add(new FormField("payment", FacilioField.FieldDisplayType.SCHEDULER_INFO, "SCHEDULER INFO", FormField.Required.REQUIRED, 7, 1));
//        labourContractsForm.setFields(labourContractsFormFields);

        FormSection section = new FormSection("Default", 1, labourContractsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        labourContractsForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(labourContractsForm);
    }
}
