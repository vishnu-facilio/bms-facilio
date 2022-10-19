package com.facilio.bmsconsoleV3.signup.workOrder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class AddWorkOrderLabourPlanModules extends BaseModuleConfig {

    public AddWorkOrderLabourPlanModules(){
        setModuleName(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean bean = Constants.getModBean();
        FacilioModule parentModule = bean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

        Objects.requireNonNull(parentModule,"WorkOrder module doesn't exists.");

        FacilioModule workOrderJobPlanModule = constructWorkOrderJobPlanModule(parentModule,bean);

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(workOrderJobPlanModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,parentModule.getName());
        addModuleChain.execute();
    }

    private FacilioModule constructWorkOrderJobPlanModule(FacilioModule workOrderModule, ModuleBean bean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN, "WorkOrder Labour Plan", "WorkOrder_Labour_Plan", FacilioModule.ModuleType.SUB_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("parent","Parent","PARENT_ID", FieldType.LOOKUP,true);
        parent.setLookupModule(workOrderModule);
        fields.add(parent);

        LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID",FieldType.LOOKUP);
        craft.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.CRAFT),"Craft module doesn't exists."));
        fields.add(craft);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.SKILLS),"Skill module doesn't exists."));
        fields.add(skill);

        fields.add(FieldFactory.getDefaultField("rate","Rate per Hour","RATE",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalPrice","Total Amount","TOTAL_PRICE",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER,FacilioField.FieldDisplayType.DURATION));

        module.setFields(fields);

        return module;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderPlansLabourModule = modBean.getModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);

        FacilioForm defaultForm = new FacilioForm();
        defaultForm.setName("standard");
        defaultForm.setModule(workOrderPlansLabourModule);
        defaultForm.setDisplayName("Standard");
        defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultForm.setShowInWeb(true);
        defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));   //form needs to be created for multiple applications
        defaultForm.setIsSystemForm(true);
        defaultForm.setType(FacilioForm.Type.FORM);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(workOrderPlansLabourModule.getName()));
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection section = new FormSection();
        section.setName("WorkOrder Labour Plans");
        section.setSectionType(FormSection.SectionType.FIELDS);
        section.setShowLabel(false);

        List<FormField> fields = new ArrayList<>();
        int seq = 0;
        fields.add(new FormField(fieldMap.get("craft").getFieldId(), "craft", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Craft", FormField.Required.REQUIRED, ++seq, 1));
        fields.add(new FormField(fieldMap.get("skill").getFieldId(), "skill", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Skill", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("quantity").getFieldId(), "quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("duration").getFieldId(), "duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("rate").getFieldId(), "rate", FacilioField.FieldDisplayType.NUMBER, "Rate per Hour", FormField.Required.OPTIONAL, ++seq, 1));
        fields.add(new FormField(fieldMap.get("totalPrice").getFieldId(), "totalPrice", FacilioField.FieldDisplayType.NUMBER, "Total Amount", FormField.Required.OPTIONAL, ++seq, 1));

        section.setFields(fields);
        section.setSequenceNumber(1);
        sections.add(section);

        defaultForm.setSections(sections);

        return Collections.singletonList(defaultForm);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlansLabourModule = new ArrayList<FacilioView>();
        workOrderPlansLabourModule.add(getJobPlanCraftViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
        groupDetails.put("views", workOrderPlansLabourModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getJobPlanCraftViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "workorderLabourPlan.ID", FieldType.NUMBER), true));

        FacilioView workorderPlanLabourView = new FacilioView();
        workorderPlanLabourView.setName("all");
        workorderPlanLabourView.setDisplayName("Job Plan Crafts");

        workorderPlanLabourView.setModuleName(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
        workorderPlanLabourView.setSortFields(sortFields);

        List<ViewField> workorderPlanLabourViewFields = new ArrayList<>();

        workorderPlanLabourViewFields.add(new ViewField("craft","Craft"));
        workorderPlanLabourViewFields.add(new ViewField("skill","Skill"));
        workorderPlanLabourViewFields.add(new ViewField("quantity","Quantity"));
        workorderPlanLabourViewFields.add(new ViewField("duration","Duration"));
        workorderPlanLabourViewFields.add(new ViewField("rate","Rate per Hour"));
        workorderPlanLabourViewFields.add(new ViewField("totalPrice","Total Amount"));

        workorderPlanLabourView.setFields(workorderPlanLabourViewFields);

        return workorderPlanLabourView;
    }
}
