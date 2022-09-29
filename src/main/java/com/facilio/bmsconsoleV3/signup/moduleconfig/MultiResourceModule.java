package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;

import java.util.*;

public class MultiResourceModule extends BaseModuleConfig{
    public MultiResourceModule(){
        setModuleName(FacilioConstants.MultiResource.NAME);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule multiResourceModule = constructMultiResourceModule();
        modules.add(multiResourceModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    public FacilioModule constructMultiResourceModule() throws Exception{

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.MultiResource.NAME,
                FacilioConstants.MultiResource.DISPLAY_NAME,
                FacilioConstants.MultiResource.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));

        fields.add(asset);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.BASE_SPACE));

        fields.add(space);

        NumberField parentModuleIdField = (NumberField) FieldFactory.getDefaultField("parentModuleId", "Parent_Module_Id", "PARENT_MODULE_ID", FieldType.NUMBER,true);
        fields.add(parentModuleIdField);

        NumberField parentRecordIdField = (NumberField) FieldFactory.getDefaultField("parentRecordId", "Parent_Record_Id", "PARENT_RECORD_ID", FieldType.NUMBER);
        fields.add(parentRecordIdField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.BIG_STRING, FacilioField.FieldDisplayType.TEXTAREA));

        fields.add(FieldFactory.getDefaultField("sequence", "Sequence", "SEQUENCE", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule multiResourceModule = modBean.getModule(FacilioConstants.MultiResource.NAME);

        FacilioForm defaultForm = new FacilioForm();
        defaultForm.setName("standard");
        defaultForm.setModule(multiResourceModule);
        defaultForm.setDisplayName("Standard");
        defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultForm.setShowInWeb(true);
        defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));   //form needs to be created for multiple applications
        defaultForm.setIsSystemForm(true);
        defaultForm.setType(FacilioForm.Type.FORM);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(multiResourceModule.getName()));
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection section = new FormSection();
        section.setName("Add Associated Asset / Space ");
        section.setSectionType(FormSection.SectionType.FIELDS);
        section.setShowLabel(true);

        List<FormField> fields = new ArrayList<>();
        int seq = 0;
        fields.add(new FormField(fieldMap.get("sequence").getFieldId(), "sequence", FacilioField.FieldDisplayType.NUMBER, "Sequence", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("asset").getFieldId(), "asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.REQUIRED, ++seq, 1));
        fields.add(new FormField(fieldMap.get("space").getFieldId(), "space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.REQUIRED, ++seq, 1));
        fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));

        section.setFields(fields);
        section.setSequenceNumber(1);
        sections.add(section);

        defaultForm.setSections(sections);

        return Collections.singletonList(defaultForm);
    }

    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> multiResourceModule = new ArrayList<FacilioView>();
        multiResourceModule.add(getMultiResource().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.MultiResource.NAME);
        groupDetails.put("views", multiResourceModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getMultiResource() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Multi_Resource.ID", FieldType.NUMBER), true));

        FacilioView multiResourceView = new FacilioView();
        multiResourceView.setName("all");
        multiResourceView.setDisplayName(FacilioConstants.MultiResource.DISPLAY_NAME);

        multiResourceView.setModuleName(FacilioConstants.MultiResource.NAME);
        multiResourceView.setSortFields(sortFields);

        List<ViewField> multiResourceViewFields = new ArrayList<>();

        multiResourceViewFields.add(new ViewField("sequence","Sequence"));
        multiResourceViewFields.add(new ViewField("asset","Asset"));
        multiResourceViewFields.add(new ViewField("space","Space"));
        multiResourceViewFields.add(new ViewField("description","Description"));

        multiResourceView.setFields(multiResourceViewFields);

        return multiResourceView;
    }
}
