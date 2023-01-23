package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.*;

public class StoreRoomModule extends BaseModuleConfig{
    public StoreRoomModule(){
        setModuleName(FacilioConstants.ContextNames.STORE_ROOM);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> storeRoom = new ArrayList<FacilioView>();
        storeRoom.add(getAllStoreRooms().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.STORE_ROOM);
        groupDetails.put("views", storeRoom);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllStoreRooms() {

        FacilioModule storeRoomModule = ModuleFactory.getStoreRoomModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(storeRoomModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Stores");
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
        FacilioModule storeRoomModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);

        FacilioForm storeRoomForm = new FacilioForm();
        storeRoomForm.setDisplayName("NEW STORE ROOM");
        storeRoomForm.setName("web_default");
        storeRoomForm.setModule(storeRoomModule);
        storeRoomForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        storeRoomForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> storeRoomFormFields = new ArrayList<>();
        storeRoomFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        storeRoomFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        storeRoomFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Located Site", FormField.Required.REQUIRED, "site", 3, 1));
        storeRoomFormFields.add(new FormField("owner", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Owner", FormField.Required.OPTIONAL,"users", 4, 1));
        FormField field = new FormField("servingsites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Serving Sites", FormField.Required.OPTIONAL,14, 1);
        JSONObject filterObj = new JSONObject();
        filterObj.put("skipSiteFilter", true);
        field.setConfig(filterObj);
        storeRoomFormFields.add(field);
        storeRoomFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 6, 2));
        storeRoomFormFields.add(new FormField("isGatePassRequired", FacilioField.FieldDisplayType.DECISION_BOX, "Gate Pass Needed", FormField.Required.OPTIONAL, 6, 3));
//        storeRoomForm.setFields(storeRoomFormFields);

        FormSection section = new FormSection("Default", 1, storeRoomFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        storeRoomForm.setSections(Collections.singletonList(section));
        storeRoomForm.setIsSystemForm(true);
        storeRoomForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(storeRoomForm);
    }
}
