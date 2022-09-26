package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VisitorInviteModule extends BaseModuleConfig{

    public VisitorInviteModule() {
        setModuleName(FacilioConstants.ContextNames.VISITOR_INVITE);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule visitorInviteModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);

        FacilioForm visitorPreRegisterForm = new FacilioForm();
        visitorPreRegisterForm.setDisplayName("PRE REGISTER VISITOR");
        visitorPreRegisterForm.setName("web_default");
        visitorPreRegisterForm.setModule(visitorInviteModule);
        visitorPreRegisterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        visitorPreRegisterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> visitorPreRegisterFormFields = new ArrayList<>();
        visitorPreRegisterFormFields.add(new FormField("inviteName", FacilioField.FieldDisplayType.TEXTBOX, "Invite Name", FormField.Required.REQUIRED, 1, 1));
        visitorPreRegisterFormFields.add(new FormField("inviteHost", FacilioField.FieldDisplayType.USER, "Host", FormField.Required.OPTIONAL, 2, 1));
        visitorPreRegisterFormFields.add(new FormField("visitorType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Visitor Type", FormField.Required.OPTIONAL, "visitorType", 3, 1));
        visitorPreRegisterFormFields.add(new FormField("recurringVisitor", FacilioField.FieldDisplayType.RECURRING_VISITOR , "RECURRING VISITOR", FormField.Required.OPTIONAL, 4, 1));
        visitorPreRegisterFormFields.add(new FormField("invitees", FacilioField.FieldDisplayType.VISITOR_INVITEES , "VISITORS", FormField.Required.OPTIONAL, 5, 1));
        visitorPreRegisterFormFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "requester",6, 1));


        FormSection visitorPreRegisterFormSection = new FormSection("Default", 1, visitorPreRegisterFormFields, false);
        visitorPreRegisterFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitorPreRegisterForm.setSections(Collections.singletonList(visitorPreRegisterFormSection));
        visitorPreRegisterForm.setIsSystemForm(true);
        visitorPreRegisterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(visitorPreRegisterForm);
    }

}
