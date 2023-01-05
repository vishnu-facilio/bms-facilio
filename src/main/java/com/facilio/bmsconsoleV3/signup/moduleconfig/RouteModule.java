package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import org.apache.log4j.Logger;

import java.util.*;

public class RouteModule extends BaseModuleConfig{
    public RouteModule(){
        setModuleName(FacilioConstants.Routes.NAME);
    }

    private static final Logger LOGGER = Logger.getLogger(RouteModule.class.getName());

    @Override
    public void addData() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule routesModule = constructRoutesModule();
        modules.add(routesModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

    }

    private FacilioModule constructRoutesModule() throws Exception{

        FacilioModule module = new FacilioModule(FacilioConstants.Routes.NAME,
                FacilioConstants.Routes.DISPLAY_NAME,
                FacilioConstants.Routes.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();

        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);

        StringField nameField = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true);
        fields.add(nameField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.BIG_STRING, FacilioField.FieldDisplayType.TEXTAREA));

        module.setFields(fields);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule routesModule = modBean.getModule(FacilioConstants.Routes.NAME);

        FacilioForm defaultForm = new FacilioForm();
        defaultForm.setName("standard");
        defaultForm.setModule(routesModule);
        defaultForm.setDisplayName("Standard");
        defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultForm.setShowInWeb(true);
        defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultForm.setIsSystemForm(true);
        defaultForm.setType(FacilioForm.Type.FORM);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(routesModule.getName()));
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection section = new FormSection();
        section.setName("Route");
        section.setSectionType(FormSection.SectionType.FIELDS);
        section.setShowLabel(true);

        List<FormField> fields = new ArrayList<>();
        int seq = 0;
        fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seq, 1));
        fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("siteId").getFieldId(), "siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, ++seq, 1));

        section.setFields(fields);
        section.setSequenceNumber(1);
        sections.add(section);

        defaultForm.setSections(sections);

        return Collections.singletonList(defaultForm);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Routes.ID", FieldType.NUMBER), true));

        FacilioView routeView = new FacilioView();
        routeView.setName("all");
        routeView.setDisplayName(FacilioConstants.Routes.DISPLAY_NAME);

        routeView.setModuleName(FacilioConstants.Routes.NAME);
        routeView.setSortFields(sortFields);

        int order = 1;
        ArrayList<FacilioView> routeModule = new ArrayList<FacilioView>();
        routeModule.add(routeView.setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Routes.NAME);
        groupDetails.put("views", routeModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
}

