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

public class ElectricityMeterModule extends BaseModuleConfig{
    public ElectricityMeterModule() throws Exception{
        setModuleName(FacilioConstants.Meter.ELECTRICITY_METER);
    }

    @Override
    public void addData() throws Exception{

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule electricityMeterModule = addElectricityMeterModule();
        modules.add(electricityMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        insertElectricityUtilityType();

    }

    public FacilioModule addElectricityMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule("electricitymeter", "Electricity Meter", "Electricity_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule electricityMeterModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_METER);

        FacilioForm ElectricityMeterForm = new FacilioForm();
        ElectricityMeterForm.setDisplayName("Electricity Meter");
        ElectricityMeterForm.setName("default_electricitymeter_web");
        ElectricityMeterForm.setModule(electricityMeterModule);
        ElectricityMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        ElectricityMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> electricityMeterFormFields = new ArrayList<>();
        electricityMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        electricityMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        electricityMeterFormFields.add(utilityTypeField);
        electricityMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        electricityMeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        electricityMeterFormFields.add(new FormField("parentMeter", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent Meter", FormField.Required.OPTIONAL, "meter", 6, 2));
        electricityMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 7, 2));

        FormSection section = new FormSection("", 1, electricityMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        ElectricityMeterForm.setSections(Collections.singletonList(section));
        ElectricityMeterForm.setIsSystemForm(true);
        ElectricityMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(ElectricityMeterForm);
    }

    public void insertElectricityUtilityType() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule("utilitytype");
        List<FacilioField> fields = modBean.getAllFields("utilitytype");

        V3UtilityTypeContext electricityTypeContext = new V3UtilityTypeContext();
        electricityTypeContext.setName("Electricity Meter");
        electricityTypeContext.setDisplayName("Electricity Meter");
        electricityTypeContext.setDescription("The electricity meter gauges energy consumption and its associated parameters.");
        electricityTypeContext.setIsDefault(true);
        electricityTypeContext.setMeterModuleID(modBean.getModule("electricitymeter").getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertElectricityMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertElectricityMeterBuilder.addRecord(electricityTypeContext);
        insertElectricityMeterBuilder.save();
    }
}
