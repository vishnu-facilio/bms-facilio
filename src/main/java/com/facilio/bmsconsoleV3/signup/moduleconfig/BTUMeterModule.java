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

public class BTUMeterModule extends BaseModuleConfig{
    public BTUMeterModule() throws Exception{
        setModuleName(FacilioConstants.Meter.BTU_METER);
    }

    @Override
    public void addData() throws Exception{

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule btuMeterModule = addBtuMeterModule();
        modules.add(btuMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        insertBtuUtilityType();

    }

    public FacilioModule addBtuMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule("btumeter", "BTU Meter", "BTU_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule btuMeterModule = modBean.getModule(FacilioConstants.Meter.BTU_METER);

        FacilioForm BTUMeterForm = new FacilioForm();
        BTUMeterForm.setDisplayName("BTU Meter");
        BTUMeterForm.setName("default_btumeter_web");
        BTUMeterForm.setModule(btuMeterModule);
        BTUMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        BTUMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> btuMeterFormFields = new ArrayList<>();
        btuMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        btuMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        btuMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 4, 2);
        utilityTypeField.setIsDisabled(true);
        btuMeterFormFields.add(utilityTypeField);
        btuMeterFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        btuMeterFormFields.add(new FormField("servingTo", FacilioField.FieldDisplayType.SPACECHOOSER, "Serving To", FormField.Required.REQUIRED, 5, 2));
        btuMeterFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        btuMeterFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        btuMeterFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        btuMeterFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        btuMeterFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        btuMeterFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        btuMeterFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        btuMeterFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        btuMeterFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        btuMeterFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        btuMeterFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        btuMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 12, 2));
        btuMeterFormFields.add(new FormField("isBillable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Billable", FormField.Required.OPTIONAL, 13, 2));


        FormSection section = new FormSection("Default", 1, btuMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        BTUMeterForm.setSections(Collections.singletonList(section));
        BTUMeterForm.setIsSystemForm(true);
        BTUMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(BTUMeterForm);
    }

    public void insertBtuUtilityType() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule("utilitytype");
        List<FacilioField> fields = modBean.getAllFields("utilitytype");

        V3UtilityTypeContext btuTypeContext = new V3UtilityTypeContext();
        btuTypeContext.setName("BTU Meter");
        btuTypeContext.setDisplayName("BTU Meter");
        btuTypeContext.setIsDefault(true);
        btuTypeContext.setMeterModuleID(modBean.getModule("btumeter").getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertBTUMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertBTUMeterBuilder.addRecord(btuTypeContext);
        insertBTUMeterBuilder.save();
    }
}
