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
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class EnergyMeterModule extends BaseModuleConfig{
    public EnergyMeterModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.ENERGY_METER);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> employee = new ArrayList<FacilioView>();
        employee.add(getAllEnergyMetersView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ENERGY_METER);
        groupDetails.put("views", employee);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllEnergyMetersView() {
        FacilioView allView = new FacilioView();
        allView.setName("energy");
        allView.setDisplayName("All Energy Meters");
        allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));

        return allView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(ModuleFactory.getAssetsModule());

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule energyMeterModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

        FacilioForm energyMeterForm = new FacilioForm();
        energyMeterForm.setDisplayName("Energy Meter");
        energyMeterForm.setName("default_energymeter_web");
        energyMeterForm.setModule(energyMeterModule);
        energyMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        energyMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> energyMeterFormFields = new ArrayList<>();
        energyMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        energyMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        energyMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField categoryField = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.REQUIRED, "assetcategory", 4, 2);
        categoryField.setIsDisabled(true);
        energyMeterFormFields.add(categoryField);
        energyMeterFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.OPTIONAL,"assetdepartment", 4, 3));
        energyMeterFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Asset Location", FormField.Required.OPTIONAL, 5, 2));
        energyMeterFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.OPTIONAL,"assettype", 5, 3));
        energyMeterFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        energyMeterFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        energyMeterFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        energyMeterFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        energyMeterFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        energyMeterFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        energyMeterFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        energyMeterFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        energyMeterFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        energyMeterFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        energyMeterFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        // new fields
        energyMeterFormFields.add(new FormField("rotatingItem", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Item", FormField.Required.OPTIONAL, "item", 12,2));
        energyMeterFormFields.add(new FormField("rotatingTool", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Tool", FormField.Required.OPTIONAL, "tool", 12,3));
        energyMeterFormFields.add(new FormField("geoLocationEnabled", FacilioField.FieldDisplayType.DECISION_BOX, "Is Movable", FormField.Required.OPTIONAL, 13,2));
        energyMeterFormFields.add(new FormField("moveApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Move Approval Needed", FormField.Required.OPTIONAL, 13,2));
        energyMeterFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 14, 2));
        energyMeterFormFields.add(new FormField("childMeterExpression", FacilioField.FieldDisplayType.TEXTBOX, "Expression", FormField.Required.OPTIONAL, 15, 2));
        energyMeterFormFields.add(new FormField("isVirtual", FacilioField.FieldDisplayType.DECISION_BOX, "Is Virtual", FormField.Required.OPTIONAL, 16, 2));
        energyMeterFormFields.add(new FormField("purpose", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Purpose", FormField.Required.OPTIONAL, 17, 2));
        energyMeterFormFields.add(new FormField("purposeSpace", FacilioField.FieldDisplayType.SPACECHOOSER, "Operational Space", FormField.Required.OPTIONAL, 18, 2));
        energyMeterFormFields.add(new FormField("root", FacilioField.FieldDisplayType.DECISION_BOX, "Is Root", FormField.Required.OPTIONAL, 19, 2));
        energyMeterFormFields.add(new FormField("multiplicationFactor", FacilioField.FieldDisplayType.TEXTBOX, "Multiplication Factor", FormField.Required.OPTIONAL, 20, 2));
        energyMeterFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",10, 2));
//        energyMeterForm.setFields(energyMeterFormFields);

        FormSection section = new FormSection("Default", 1, energyMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        energyMeterForm.setSections(Collections.singletonList(section));
        energyMeterForm.setIsSystemForm(true);
        energyMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(energyMeterForm);
    }
}
