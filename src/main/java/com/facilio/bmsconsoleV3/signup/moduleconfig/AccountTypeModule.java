package com.facilio.bmsconsoleV3.signup.moduleconfig;

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

public class AccountTypeModule extends BaseModuleConfig{
    public AccountTypeModule(){
        setModuleName(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE);
    }

    public void addData() throws Exception {
        addSystemButton();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> accountType = new ArrayList<FacilioView>();
        accountType.add(getAllAccountTypeView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE);
        groupDetails.put("views", accountType);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAccountTypeView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Account Types");
        allView.setModuleName("accounttype");
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
        FacilioModule accountTypeModule = modBean.getModule(FacilioConstants.ContextNames.ACCOUNT_TYPE);

        FacilioForm accountTypeForm = new FacilioForm();
        accountTypeForm.setDisplayName("Account Type");
        accountTypeForm.setName("default_" + FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE + "_web");
        accountTypeForm.setModule(accountTypeModule);
        accountTypeForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        accountTypeForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> accountTypeFormFields = new ArrayList<>();
        accountTypeFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        accountTypeFormFields.add(new FormField("group", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 2, 1));
//        accountTypeForm.setFields(accountTypeFormFields);

        FormSection section = new FormSection("Default", 1, accountTypeFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        accountTypeForm.setSections(Collections.singletonList(section));
        accountTypeForm.setIsSystemForm(true);
        accountTypeForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(accountTypeForm);
    }

    private static void addSystemButton() throws Exception{
        String moduleName = FacilioConstants.ContextNames.ACCOUNT_TYPE;

        SystemButtonApi.addListEditButton(moduleName);
        SystemButtonApi.addCreateButtonWithModuleDisplayName(moduleName);
        SystemButtonApi.addListDeleteButton(moduleName);
    }
}