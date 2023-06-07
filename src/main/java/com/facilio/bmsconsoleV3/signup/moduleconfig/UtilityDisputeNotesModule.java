package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class UtilityDisputeNotesModule extends BaseModuleConfig{

    public UtilityDisputeNotesModule() throws Exception {
        setModuleName(FacilioConstants.UTILITY_DISPUTE_NOTES);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule UtilityDisputeNotesModule = addNotesModule();
        modules.add(UtilityDisputeNotesModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, disputeModule.getName());
        addModuleChain.execute();

        addParentField();

    }

    public FacilioModule addNotesModule() throws Exception{

        FacilioModule module = new FacilioModule("utilityDisputeNotes", "Utility Dispute Notes", "Utility_Dispute_Notes", FacilioModule.ModuleType.NOTES);

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
        FacilioModule notesModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE_NOTES);

        FacilioForm notesForm = new FacilioForm();
        notesForm.setName("default_utilityDisputeNotes_web");
        notesForm.setModule(notesModule);
        notesForm.setDisplayName("Add Notes");
        notesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        notesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        notesForm.setShowInWeb(true);
        notesForm.setShowInMobile(true);
        notesForm.setHideInList(false);

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

        notesForm.setSections(sections);
        notesForm.setIsSystemForm(true);
        notesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(notesForm);
    }

    public void addParentField() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        LookupField parentNote = new LookupField(moduleBean.getModule(FacilioConstants.UTILITY_DISPUTE_NOTES), "parentNote", "Parent Note", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "PARENT_NOTE", FieldType.LOOKUP, false, false, true, null, "Parent Note", moduleBean.getModule(FacilioConstants.UTILITY_DISPUTE_NOTES));
        moduleBean.addField(parentNote);
    }
}



