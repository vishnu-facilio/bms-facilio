package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class BudgetModule extends BaseModuleConfig{
    public BudgetModule(){
        setModuleName(FacilioConstants.ContextNames.BUDGET);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> budget = new ArrayList<FacilioView>();
        budget.add(getAllBudgetView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BUDGET);
        groupDetails.put("views", budget);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBudgetView() {

        FacilioModule module = ModuleFactory.getBudgetModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Budgets");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule budgetModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET);

        FacilioForm budgetForm = new FacilioForm();
        budgetForm.setDisplayName("Budget");
        budgetForm.setName("default_"+ FacilioConstants.ContextNames.Budget.BUDGET +"_web");
        budgetForm.setModule(budgetModule);
        budgetForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        budgetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> budgetDetailFields = new ArrayList<>();
        budgetDetailFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        budgetDetailFields.add(new FormField("fiscalYearStart", FacilioField.FieldDisplayType.SELECTBOX, "Fiscal Year Start", FormField.Required.REQUIRED, 2, 2));
        budgetDetailFields.add(new FormField("fiscalYear", FacilioField.FieldDisplayType.SELECTBOX, "Fiscal Year", FormField.Required.REQUIRED, 2, 3));
        budgetDetailFields.add(new FormField("focalPointType", FacilioField.FieldDisplayType.SELECTBOX, "Scope", FormField.Required.OPTIONAL, 3, 1));
        budgetDetailFields.add(new FormField("focalPointResource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 4, 1));

        List<FormField> budgetAmountFields = new ArrayList<>();
        budgetAmountFields.add(new FormField("budgetamount", FacilioField.FieldDisplayType.BUDGET_AMOUNT, "Budget Amounts", FormField.Required.OPTIONAL, 5, 1));

        List<FormField> defaultFormFields = new ArrayList<FormField>();
        defaultFormFields.addAll(budgetDetailFields);
        defaultFormFields.addAll(budgetAmountFields);

//        budgetForm.setFields(defaultFormFields);

        FormSection defaultSection = new FormSection("BUDGET DETAILS", 1, budgetDetailFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection budgetAmountSection = new FormSection("BUDGET AMOUNTS", 2, budgetAmountFields, true);
        budgetAmountSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> budgetFormSections = new ArrayList<>();
        budgetFormSections.add(defaultSection);
        budgetFormSections.add(budgetAmountSection);

        budgetForm.setSections(budgetFormSections);

        return Collections.singletonList(budgetForm);

    }
}
