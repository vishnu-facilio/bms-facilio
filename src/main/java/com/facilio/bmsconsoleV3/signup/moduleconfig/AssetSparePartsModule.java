package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
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

public class AssetSparePartsModule extends BaseModuleConfig{

    public AssetSparePartsModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> assetSpareParts = new ArrayList<FacilioView>();
        assetSpareParts.add(getAllAssetSparePartsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
        groupDetails.put("views", assetSpareParts);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getAllAssetSparePartsView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getAssetSparePartsModule());
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Asset SpareParts");
        allView.setSortFields(sortFields);
        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Item Type","itemType"));
        columns.add(new ViewField("description", "Description","itemType"));
        columns.add(new ViewField("requiredCount", "Required Count"));
        columns.add(new ViewField("remarks", "Required Count"));
        columns.add(new ViewField("issuedCount", "Issued Count"));

        allView.setFields(columns);

        return allView;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule sparePartsModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);

        FacilioForm AssetSparePartsForm = new FacilioForm();
        AssetSparePartsForm.setDisplayName("ADD SPARE PARTS");
        AssetSparePartsForm.setName("default_assetSpareParts_web");
        AssetSparePartsForm.setModule(sparePartsModule);
        AssetSparePartsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        AssetSparePartsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> assetSparePartsFormFields = new ArrayList<>();
        assetSparePartsFormFields.add(new FormField("itemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item", FormField.Required.REQUIRED, 1, 1));
        assetSparePartsFormFields.add(new FormField("remarks", FacilioField.FieldDisplayType.TEXTBOX, "Remarks", FormField.Required.OPTIONAL, 3, 2));
        assetSparePartsFormFields.add(new FormField("requiredCount", FacilioField.FieldDisplayType.NUMBER, "Spare Count", FormField.Required.REQUIRED, 4, 3));

        FormSection section = new FormSection("Default", 1, assetSparePartsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        AssetSparePartsForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(AssetSparePartsForm);
    }

}
