package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WorkAssetModule extends BaseModuleConfig{
    public WorkAssetModule(){
        setModuleName(FacilioConstants.ContextNames.WORK_ASSET);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workAsset = new ArrayList<FacilioView>();
        workAsset.add(getAllWorkAssetsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WORK_ASSET);
        groupDetails.put("views", workAsset);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkAssetsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Work Assets");

        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Asset","asset"));
        columns.add(new ViewField("name", "Space","space"));
        columns.add(new ViewField("description", "Asset Description","asset"));
        columns.add(new ViewField("description", "Space Description","space"));

        allView.setFields(columns);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workAssetModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ASSET);

        FacilioForm workAssetForm = new FacilioForm();
        workAssetForm.setDisplayName("WORK ASSET");
        workAssetForm.setName("default_workasset_web");
        workAssetForm.setModule(workAssetModule);
        workAssetForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workAssetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workAssetFields = new ArrayList<>();
        workAssetFields.add(new FormField("asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, 1, 1));
        workAssetFields.add(new FormField("space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.OPTIONAL, 2, 1));

        FormSection section = new FormSection("Default", 1, workAssetFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workAssetForm.setSections(Collections.singletonList(section));
        workAssetForm.setIsSystemForm(true);
        workAssetForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(workAssetForm);
    }

}
