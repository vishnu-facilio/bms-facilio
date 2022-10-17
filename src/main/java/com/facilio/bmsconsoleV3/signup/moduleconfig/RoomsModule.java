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

public class RoomsModule extends BaseModuleConfig{
    public RoomsModule(){
        setModuleName(FacilioConstants.ContextNames.ROOMS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> rooms = new ArrayList<FacilioView>();
        rooms.add(getAllRoomsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ROOMS);
        groupDetails.put("views", rooms);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllRoomsView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Rooms");
        allView.setModuleName(FacilioConstants.ContextNames.ROOMS);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule roomsModule = modBean.getModule(FacilioConstants.ContextNames.ROOMS);

        FacilioForm roomsForm = new FacilioForm();
        roomsForm.setDisplayName("NEW ROOM");
        roomsForm.setName("default_room_web");
        roomsForm.setModule(roomsModule);
        roomsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        roomsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> roomsFormFields = new ArrayList<>();
        roomsFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        roomsFormFields.add(new FormField("roomType", FacilioField.FieldDisplayType.SELECTBOX, "Room Type", FormField.Required.REQUIRED, 2, 2));
        roomsFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        roomsFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 5, 2));
        roomsFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 6, 2));
        roomsFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Capacity", FormField.Required.OPTIONAL,7, 2));

        FormSection roomsFormSection = new FormSection("Default", 1, roomsFormFields, false);
        roomsFormSection.setSectionType(FormSection.SectionType.FIELDS);
        roomsForm.setSections(Collections.singletonList(roomsFormSection));
        roomsForm.setIsSystemForm(true);
        roomsForm.setType(FacilioForm.Type.FORM);



        List<FacilioForm> roomsModuleForms = new ArrayList<>();
        roomsModuleForms.add(roomsForm);

        return roomsModuleForms;
    }
}
