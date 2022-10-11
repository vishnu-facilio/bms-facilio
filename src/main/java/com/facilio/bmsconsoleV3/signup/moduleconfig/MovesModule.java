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
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class MovesModule extends BaseModuleConfig{
    public MovesModule(){
        setModuleName(FacilioConstants.ContextNames.MOVES);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> moves = new ArrayList<FacilioView>();
        moves.add(getAllMovesView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.MOVES);
        groupDetails.put("views", moves);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllMovesView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Moves");
        allView.setModuleName(FacilioConstants.ContextNames.MOVES);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule movesModule = modBean.getModule(FacilioConstants.ContextNames.MOVES);

        FacilioForm movesForm = new FacilioForm();
        movesForm.setDisplayName("NEW MOVE");
        movesForm.setName("default_move_web");
        movesForm.setModule(movesModule);
        movesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        movesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> movesFormFields = new ArrayList<>();
        movesFormFields.add(new FormField("moveType", FacilioField.FieldDisplayType.SELECTBOX, "Move Type", FormField.Required.REQUIRED, 1, 2));
        movesFormFields.add(new FormField("timeOfMove", FacilioField.FieldDisplayType.DATETIME, "Time of Move", FormField.Required.REQUIRED, 2, 2));
        movesFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 3, 2));
        movesFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.REQUIRED, "department", 4, 2));
        movesFormFields.add(new FormField("to", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "To", FormField.Required.REQUIRED, "desks", 5, 2));
        movesFormFields.add(new FormField("from", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "From", FormField.Required.REQUIRED, "desks", 6, 2));
//        movesForm.setFields(movesFormFields);

        FormSection section = new FormSection("Default", 1, movesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        movesForm.setSections(Collections.singletonList(section));
        movesForm.setIsSystemForm(true);
        movesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(movesForm);
    }
}
