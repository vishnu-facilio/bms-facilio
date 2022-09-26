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

public class PrintersModule extends BaseModuleConfig{

    public PrintersModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.PRINTERS);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule printersModule = modBean.getModule(FacilioConstants.ContextNames.PRINTERS);

        FacilioForm printerForm = new FacilioForm();
        printerForm.setDisplayName("Printers");
        printerForm.setName("web_default");
        printerForm.setModule(printersModule);
        printerForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        printerForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> printerFormFields = new ArrayList<>();
        printerFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        printerFormFields.add(new FormField("ip", FacilioField.FieldDisplayType.TEXTBOX, "IP Address", FormField.Required.OPTIONAL, 2, 1));
        printerFormFields.add(new FormField("model", FacilioField.FieldDisplayType.SELECTBOX, "Printer Model", FormField.Required.REQUIRED,3, 1));
        printerFormFields.add(new FormField("connectionMode", FacilioField.FieldDisplayType.SELECTBOX, "Printer Model", FormField.Required.REQUIRED,4, 1));
//        printerForm.setFields(printerFormFields);

        FormSection section = new FormSection("Default", 1, printerFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        printerForm.setSections(Collections.singletonList(section));
        printerForm.setIsSystemForm(true);
        printerForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(printerForm);
    }
}
