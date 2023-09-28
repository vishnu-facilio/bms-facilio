package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class ControlActionTemplateNotes extends BaseModuleConfig {
    public ControlActionTemplateNotes(){
        setModuleName(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME);
    }
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule notesModule = addNotesModule();
        modules.add(notesModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, module.getName());
        addModuleChain.execute();

        addParentField();

    }

    public FacilioModule addNotesModule() throws Exception{

        FacilioModule module = new FacilioModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME,
                "Control Action Template Notes", "Control_Action_Template_Notes",
                FacilioModule.ModuleType.NOTES);

        List<FacilioField> fields = new ArrayList<>();

        DateField createdTime = new DateField(module, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, null);
        fields.add(createdTime);

        LookupField createdBy = new LookupField(module, "createdBy", "Created By", FacilioField.FieldDisplayType.LOOKUP_POPUP, "CREATED_BY", FieldType.LOOKUP, false, false, true, null, "users");
        fields.add(createdBy);

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        StringField title = new StringField(module, "title", "Title", FacilioField.FieldDisplayType.TEXTBOX, "TITLE", FieldType.STRING, false, false, true, null);
        fields.add(title);

        StringField bodyHTML = new StringField(module, "bodyHTML", "Body HTML", FacilioField.FieldDisplayType.TEXTAREA, "BODY_HTML", FieldType.STRING, false, false, true, null);
        fields.add(bodyHTML);

        StringField body = new StringField(module, "body", "Body", FacilioField.FieldDisplayType.TEXTAREA, "BODY", FieldType.STRING, false, false, true, null);
        fields.add(body);

        module.setFields(fields);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule notesModule = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME);

        FacilioForm meterNotesForm = new FacilioForm();
        meterNotesForm.setName("default_controlActionTemplateNotes_web");
        meterNotesForm.setModule(notesModule);
        meterNotesForm.setDisplayName("Add Notes");
        meterNotesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        meterNotesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        meterNotesForm.setShowInWeb(true);
        meterNotesForm.setShowInMobile(true);
        meterNotesForm.setHideInList(false);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(notesModule.getName()));

        List<FormSection> sections = new ArrayList<FormSection>();

        FormSection configSection = new FormSection();
        configSection.setName("Default");
        configSection.setSectionType(FormSection.SectionType.FIELDS);
        configSection.setShowLabel(false);

        List<FormField> configFields = new ArrayList<>();

        int seq = 0;

        configFields.add(new FormField(fieldMap.get("body").getFieldId(), "body", FacilioField.FieldDisplayType.TEXTAREA, "Comment", FormField.Required.OPTIONAL, ++seq, 1));

        configSection.setFields(configFields);

        configSection.setSequenceNumber(1);

        sections.add(configSection);

        meterNotesForm.setSections(sections);
        meterNotesForm.setIsSystemForm(true);
        meterNotesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(meterNotesForm);
    }

    public void addParentField() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        LookupField parentNote = new LookupField(moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME), "parentNote", "Parent Note", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "PARENT_NOTE", FieldType.LOOKUP, false, false, true, null, "Parent Note", moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME));
        moduleBean.addField(parentNote);
    }
}
