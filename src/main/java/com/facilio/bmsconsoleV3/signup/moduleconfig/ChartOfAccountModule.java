package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ChartOfAccountModule extends BaseModuleConfig{

    public ChartOfAccountModule(){
        setModuleName(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT);
    }

    public void addData() throws Exception {
        addSystemButton();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> chartOfAccount = new ArrayList<FacilioView>();
        chartOfAccount.add(getAllChartOfAccountView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT);
        groupDetails.put("views", chartOfAccount);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllChartOfAccountView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Chart of Accounts");
        allView.setModuleName("chartofaccount");
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
        FacilioModule chartOfAccountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT);

        FacilioForm chartOfAccountForm = new FacilioForm();
        chartOfAccountForm.setDisplayName("Chart Of Account");
        chartOfAccountForm.setName("default_"+ FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT +"_web");
        chartOfAccountForm.setModule(chartOfAccountModule);
        chartOfAccountForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        chartOfAccountForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> chartOfAccountFormFields = new ArrayList<>();
        chartOfAccountFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        chartOfAccountFormFields.add(new FormField("code", FacilioField.FieldDisplayType.TEXTBOX, "Code", FormField.Required.OPTIONAL,2, 1));
        FormField typeField = new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Account Type", FormField.Required.REQUIRED, "accounttype",3, 1);
        typeField.setAllowCreate(true);
        chartOfAccountFormFields.add(typeField);
        chartOfAccountFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 4, 1));
//        chartOfAccountForm.setFields(chartOfAccountFormFields);

        FormSection Section = new FormSection("Default", 1, chartOfAccountFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        chartOfAccountForm.setSections(Collections.singletonList(Section));
        chartOfAccountForm.setIsSystemForm(true);
        chartOfAccountForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(chartOfAccountForm);
    }

    private static void addSystemButton() throws Exception{
        String moduleName = "chartofaccount";

        SystemButtonApi.addListEditButton(moduleName);
        SystemButtonApi.addCreateButton(moduleName);
        SystemButtonApi.addListDeleteButton(moduleName);
    }
}
