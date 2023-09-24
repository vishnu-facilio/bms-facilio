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

public class WaterMeterModule extends BaseModuleConfig{
    public WaterMeterModule() throws Exception{
        setModuleName(FacilioConstants.Meter.WATER_METER);
    }

    @Override
    public void addData() throws Exception{

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule waterMeterModule = addWaterMeterModule();
        modules.add(waterMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        insertWaterUtilityType();

    }

    public FacilioModule addWaterMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule("waterutilitymeter", "Water Meter", "Water_Utility_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule waterMeterModule = modBean.getModule(FacilioConstants.Meter.WATER_METER);

        FacilioForm WaterMeterForm = new FacilioForm();
        WaterMeterForm.setDisplayName("Water Meter");
        WaterMeterForm.setName("default_watermeter_web");
        WaterMeterForm.setModule(waterMeterModule);
        WaterMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        WaterMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> waterMeterFormFields = new ArrayList<>();
        waterMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        waterMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        waterMeterFormFields.add(utilityTypeField);
        waterMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        waterMeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        waterMeterFormFields.add(new FormField("parentMeter", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent Meter", FormField.Required.OPTIONAL, "meter", 6, 2));
        waterMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 7, 2));

        FormSection section = new FormSection("", 1, waterMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        WaterMeterForm.setSections(Collections.singletonList(section));
        WaterMeterForm.setIsSystemForm(true);
        WaterMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(WaterMeterForm);
    }

    public void insertWaterUtilityType() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule("utilitytype");
        List<FacilioField> fields = modBean.getAllFields("utilitytype");

        V3UtilityTypeContext waterTypeContext = new V3UtilityTypeContext();
        waterTypeContext.setName("Water Meter");
        waterTypeContext.setDisplayName("Water Meter");
        waterTypeContext.setDescription("The water meter captures energy consumption and its flow.");
        waterTypeContext.setIsDefault(true);
        waterTypeContext.setMeterModuleID(modBean.getModule("waterutilitymeter").getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertWaterMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertWaterMeterBuilder.addRecord(waterTypeContext);
        insertWaterMeterBuilder.save();
    }
}
