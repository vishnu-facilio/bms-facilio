package com.facilio.bmsconsoleV3.signup.moduleconfig;

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

public class AudienceModule extends BaseModuleConfig{
    public AudienceModule(){
        setModuleName(FacilioConstants.ContextNames.AUDIENCE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> audience = new ArrayList<FacilioView>();
        audience.add(getAllAudienceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.AUDIENCE);
        groupDetails.put("views", audience);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAudienceView() {

        FacilioModule module = ModuleFactory.getAudienceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Audiences");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule audienceModule = modBean.getModule(FacilioConstants.ContextNames.AUDIENCE);

        FacilioForm audienceForm = new FacilioForm();
        audienceForm.setDisplayName("Audience");
        audienceForm.setName("default_audience_web");
        audienceForm.setModule(audienceModule);
        audienceForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        audienceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> audienceFormFields = new ArrayList<>();
        audienceFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Group Name", FormField.Required.REQUIRED, 1, 1));
        audienceFormFields.add(new FormField("audienceSharing", FacilioField.FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", FormField.Required.REQUIRED, 6, 1));
        FormField filterSharingTypeField = new FormField("filterSharingType", FacilioField.FieldDisplayType.NUMBER, "Filter Sharing Type", FormField.Required.OPTIONAL, 6, 1);
        filterSharingTypeField.setHideField(true);
        audienceFormFields.add(filterSharingTypeField);
//        audienceForm.setFields(audienceFormFields);

        FormSection section = new FormSection("Default", 1, audienceFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        audienceForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(audienceForm);

    }
}
