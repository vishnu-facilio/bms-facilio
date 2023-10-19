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
        addRelationships();

    }

    public FacilioModule addGasMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.GAS_METER, "Gas Meter", "Gas_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
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
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        gasMeterFormFields.add(utilityTypeField);
        gasMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        gasMeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        gasMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Check Meter", FormField.Required.OPTIONAL, 6, 2));

        FormSection section = new FormSection("Gas Meter Details", 1, gasMeterFormFields, false);
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
        gasTypeContext.setDescription("The gas meter captures energy consumption and its flow.");
        gasTypeContext.setIsDefault(true);
        gasTypeContext.setMeterModuleID(modBean.getModule(FacilioConstants.Meter.GAS_METER).getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertGasMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertGasMeterBuilder.addRecord(gasTypeContext);
        insertGasMeterBuilder.save();
    }

    private void addRelationships() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule parentModule = modBean.getModule(FacilioConstants.Meter.GAS_METER);
        FacilioModule childModule = modBean.getModule(FacilioConstants.Meter.GAS_METER);
        RelationRequestContext parentMeterVsChildMeterRelation = new RelationRequestContext();
        parentMeterVsChildMeterRelation.setName("Gas Meter Hierarchy");
        parentMeterVsChildMeterRelation.setDescription("Relationship between Parent and Child meters");
        parentMeterVsChildMeterRelation.setFromModuleId(parentModule.getModuleId());
        parentMeterVsChildMeterRelation.setToModuleId(childModule.getModuleId());
        parentMeterVsChildMeterRelation.setRelationType(RelationRequestContext.RelationType.ONE_TO_MANY);
        parentMeterVsChildMeterRelation.setRelationName("Parent to Child Gas Meter");
        parentMeterVsChildMeterRelation.setReverseRelationName("Child to Parent Gas Meter");
        parentMeterVsChildMeterRelation.setRelationCategory(RelationContext.RelationCategory.METER);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, parentMeterVsChildMeterRelation);
        chain.execute();
    }
}
