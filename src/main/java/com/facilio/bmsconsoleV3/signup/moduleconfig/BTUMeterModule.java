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
        addRelationships();

    }

    public FacilioModule addBtuMeterModule() throws Exception{

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.BTU_METER, "BTU Meter", "BTU_Meter", FacilioModule.ModuleType.BASE_ENTITY, meterModule, true);
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
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        btuMeterFormFields.add(utilityTypeField);
        btuMeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        btuMeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        btuMeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Check Meter", FormField.Required.OPTIONAL, 6, 2));

        FormSection section = new FormSection("BTU Meter Details", 1, btuMeterFormFields, false);
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
        btuTypeContext.setDescription("The BTU meter gauges heat energy consumption.");
        btuTypeContext.setIsDefault(true);
        btuTypeContext.setMeterModuleID(modBean.getModule(FacilioConstants.Meter.BTU_METER).getModuleId());
        InsertRecordBuilder<V3UtilityTypeContext> insertBTUMeterBuilder = new InsertRecordBuilder<V3UtilityTypeContext>()
                .fields(fields)
                .table(module.getTableName())
                .moduleName(module.getName());

        insertBTUMeterBuilder.addRecord(btuTypeContext);
        insertBTUMeterBuilder.save();
    }

    private void addRelationships() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule parentModule = modBean.getModule(FacilioConstants.Meter.BTU_METER);
        FacilioModule childModule = modBean.getModule(FacilioConstants.Meter.BTU_METER);
        RelationRequestContext parentMeterVsChildMeterRelation = new RelationRequestContext();
        parentMeterVsChildMeterRelation.setName("BTU Meter Hierarchy");
        parentMeterVsChildMeterRelation.setDescription("Relationship between Parent and Child meters");
        parentMeterVsChildMeterRelation.setFromModuleId(parentModule.getModuleId());
        parentMeterVsChildMeterRelation.setToModuleId(childModule.getModuleId());
        parentMeterVsChildMeterRelation.setRelationType(RelationRequestContext.RelationType.ONE_TO_MANY);
        parentMeterVsChildMeterRelation.setRelationName("Parent to Child BTU Meter");
        parentMeterVsChildMeterRelation.setReverseRelationName("Child to Parent BTU Meter");
        parentMeterVsChildMeterRelation.setRelationCategory(RelationContext.RelationCategory.METER);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, parentMeterVsChildMeterRelation);
        chain.execute();
    }
}
