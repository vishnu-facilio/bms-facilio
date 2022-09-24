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

public class AnnouncementModule extends BaseModuleConfig{
    public AnnouncementModule(){
        setModuleName(FacilioConstants.ContextNames.ANNOUNCEMENT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> announcement = new ArrayList<FacilioView>();
        announcement.add(getAllAnnouncementView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ANNOUNCEMENT);
        groupDetails.put("views", announcement);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAnnouncementView() {

        FacilioModule module = ModuleFactory.getAnnouncementModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Announcements");
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
        FacilioModule announcementModule = modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT);

        FacilioForm announcementForm = new FacilioForm();
        announcementForm.setDisplayName("Announcement");
        announcementForm.setName("default_announcement_web");
        announcementForm.setModule(announcementModule);
        announcementForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        announcementForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> announcementFormfields = new ArrayList<>();
        announcementFormfields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descField = new FormField("longDescription", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descField.addToConfig("richText", true);
        announcementFormfields.add(descField);
        announcementFormfields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.OPTIONAL, 3, 3));
        FormField categoryField = new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED,4, 2);
        categoryField.setAllowCreateOptions(true);
        announcementFormfields.add(categoryField);
        descField.addToConfig("richText", true);
        FormField attachment = new FormField("announcementattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 6, 1);
        attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        announcementFormfields.add(attachment);
        FormField sendMail = new FormField("sendMail", FacilioField.FieldDisplayType.DECISION_BOX, "Send Mail", FormField.Required.OPTIONAL, 7, 1);
        announcementFormfields.add(sendMail);
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",5, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        announcementFormfields.add(field);

//        announcementForm.setFields(announcementFormfields);

        FormSection section = new FormSection("Default", 1, announcementFormfields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        announcementForm.setSections(Collections.singletonList(section));
        announcementForm.setIsSystemForm(true);
        announcementForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(announcementForm);
    }
}
