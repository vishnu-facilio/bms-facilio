package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GasMeterModule extends BaseModuleConfig{
    public GasMeterModule() throws Exception{
        setModuleName(FacilioConstants.Meter.GAS_METER);
    }

    @Override
    public void addData() throws Exception{

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule gasMeterModule = addGasMeterModule();
        modules.add(gasMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        insertGasUtilityType();

    }

    public FacilioModule addGasMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule("gasmeter", "Gas Meter", "Gas_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule gasMeterModule = modBean.getModule(FacilioConstants.Meter.GAS_METER);

        FacilioForm GasMeterForm = new FacilioForm();
        GasMeterForm.setDisplayName("Gas Meter");
        GasMeterForm.setName("default_gasmeter_web");
        GasMeterForm.setModule(gasMeterModule);
        GasMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        GasMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> gasMeterFormFields = new ArrayList<>();
        gasMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        gasMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        gasMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 4, 2);
        utilityTypeField.setIsDisabled(true);
        gasMeterFormFields.add(utilityTypeField);
        gasMeterFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        gasMeterFormFields.add(new FormField("servingTo", FacilioField.FieldDisplayType.SPACECHOOSER, "Serving To", FormField.Required.REQUIRED, 5, 2));
        gasMeterFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        gasMeterFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        gasMeterFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        gasMeterFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        gasMeterFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        gasMeterFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        gasMeterFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        gasMeterFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        gasMeterFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        gasMeterFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        gasMeterFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        gasMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 12, 2));
        gasMeterFormFields.add(new FormField("isBillable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Billable", FormField.Required.OPTIONAL, 13, 2));


        FormSection section = new FormSection("Default", 1, gasMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        GasMeterForm.setSections(Collections.singletonList(section));
        GasMeterForm.setIsSystemForm(true);
        GasMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(GasMeterForm);
    }

    public void insertGasUtilityType() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule("utilitytype");
        List<FacilioField> fields = modBean.getAllFields("utilitytype");

        V3UtilityTypeContext gasTypeContext = new V3UtilityTypeContext();
        gasTypeContext.setName("Gas Meter");
        gasTypeContext.setDisplayName("Gas Meter");
        gasTypeContext.setIsDefault(true);
        gasTypeContext.setMeterModuleID(modBean.getModule("gasmeter").getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertGasMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertGasMeterBuilder.addRecord(gasTypeContext);
        insertGasMeterBuilder.save();
    }
}
