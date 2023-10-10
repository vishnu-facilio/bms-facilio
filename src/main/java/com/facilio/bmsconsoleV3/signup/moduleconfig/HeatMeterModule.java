package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeatMeterModule extends BaseModuleConfig{
    public HeatMeterModule() throws Exception{
        setModuleName(FacilioConstants.Meter.HEAT_METER);
    }

    @Override
    public void addData() throws Exception{

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule heatMeterModule = addHeatMeterModule();
        modules.add(heatMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        insertHeatUtilityType();
        addRelationships();

    }

    public FacilioModule addHeatMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule("heatmeter", "Heat Meter", "Heat_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule heatMeterModule = modBean.getModule(FacilioConstants.Meter.HEAT_METER);

        FacilioForm HeatMeterForm = new FacilioForm();
        HeatMeterForm.setDisplayName("Heat Meter");
        HeatMeterForm.setName("default_heatmeter_web");
        HeatMeterForm.setModule(heatMeterModule);
        HeatMeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        HeatMeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> heatMeterFormFields = new ArrayList<>();
        heatMeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        heatMeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        heatMeterFormFields.add(utilityTypeField);
        heatMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        heatMeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        heatMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 6, 2));

        FormSection section = new FormSection("Heat Meter Details", 1, heatMeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        HeatMeterForm.setSections(Collections.singletonList(section));
        HeatMeterForm.setIsSystemForm(true);
        HeatMeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(HeatMeterForm);
    }

    public void insertHeatUtilityType() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule("utilitytype");
        List<FacilioField> fields = modBean.getAllFields("utilitytype");

        V3UtilityTypeContext heatTypeContext = new V3UtilityTypeContext();
        heatTypeContext.setName("Heat Meter");
        heatTypeContext.setDisplayName("Heat Meter");
        heatTypeContext.setDescription("The heat meter gauges thermal energy consumption.");
        heatTypeContext.setIsDefault(true);
        heatTypeContext.setMeterModuleID(modBean.getModule("heatmeter").getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertHeatMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertHeatMeterBuilder.addRecord(heatTypeContext);
        insertHeatMeterBuilder.save();
    }

    private void addRelationships() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule parentModule = modBean.getModule(FacilioConstants.Meter.HEAT_METER);
        FacilioModule childModule = modBean.getModule(FacilioConstants.Meter.HEAT_METER);
        RelationRequestContext parentMeterVsChildMeterRelation = new RelationRequestContext();
        parentMeterVsChildMeterRelation.setName("Heat Meter Hierarchy");
        parentMeterVsChildMeterRelation.setDescription("Relationship between Parent and Child meters");
        parentMeterVsChildMeterRelation.setFromModuleId(parentModule.getModuleId());
        parentMeterVsChildMeterRelation.setToModuleId(childModule.getModuleId());
        parentMeterVsChildMeterRelation.setRelationType(RelationRequestContext.RelationType.ONE_TO_MANY);
        parentMeterVsChildMeterRelation.setRelationName("Parent to Child Heat Meter");
        parentMeterVsChildMeterRelation.setReverseRelationName("Child to Parent Heat Meter");
        parentMeterVsChildMeterRelation.setRelationCategory(RelationContext.RelationCategory.METER);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, parentMeterVsChildMeterRelation);
        chain.execute();
    }
}
