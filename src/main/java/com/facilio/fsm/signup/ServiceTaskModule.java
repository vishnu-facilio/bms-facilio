package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class ServiceTaskModule extends BaseModuleConfig {

    public ServiceTaskModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTaskModule = constructServiceTaskModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceTaskModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        bean.addSubModule(serviceOrder.getModuleId(), serviceTaskModule.getModuleId(),0);

        addTaskSkillsField();
        addServiceOrderTaskModule();
        addServiceTasksField();
    }

    private FacilioModule constructServiceTaskModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, "Task", "Service_Task", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("taskCode","Task Code","TASK_CODE",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("remarks","Remarks","REMARKS",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));

        LookupField parent = FieldFactory.getDefaultField("workType","Work Type","WORK_TYPE",FieldType.LOOKUP);
        parent.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE),"Work Type module doesn't exist."));
        fields.add(parent);

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Work Order","SERVICE_ORDER",FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Work Order module doesn't exist."));
        fields.add(serviceOrder);

        fields.add(FieldFactory.getDefaultField("sequence","Sequence","SEQUENCE",FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("isPhotoMandatory","Photo Mandatory","IS_PHOTO_MANDATORY", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualStartTime","Actual Start Time","ACTUAL_START_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("actualEndTime","Actual End Time","ACTUAL_END_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        LookupField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.LOOKUP);
        status.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS),"Task Status module doesn't exist."));
        fields.add(status);

        module.setFields(fields);

        return module;
    }
    private void addTaskSkillsField() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceTaskSkillsRelMod = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_SKILLS, "Task Skills", "Service_Task_Skills", FacilioModule.ModuleType.SUB_ENTITY);
        FacilioModule skillsMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);

        MultiLookupField skillsMultiLookup = FieldFactory.getDefaultField("skills","Skills","SKILLS", FieldType.MULTI_LOOKUP);
        skillsMultiLookup.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        skillsMultiLookup.setModule(serviceTask);
        skillsMultiLookup.setRequired(false);
        skillsMultiLookup.setDisabled(false);
        skillsMultiLookup.setDefault(true);
        skillsMultiLookup.setRelModule(serviceTaskSkillsRelMod);
        skillsMultiLookup.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        skillsMultiLookup.setLookupModule(skillsMod);
        bean.addField(skillsMultiLookup);
    }

    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> views = new ArrayList<FacilioView>();
        views.add(getServiceTasksAllViews().setOrder(order++));
        views.add(getServiceTasksHiddenAllViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        groupDetails.put("views", views);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getServiceTasksAllViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Labour.ID", FieldType.NUMBER), true));

        FacilioView taskView = new FacilioView();
        taskView.setName("all");
        taskView.setDisplayName("All Tasks");

        taskView.setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        taskView.setSortFields(sortFields);

        List<ViewField> taskViewFields = new ArrayList<>();
        taskViewFields.add(new ViewField("taskCode","Code"));
        taskViewFields.add(new ViewField("name","Task Name"));
        taskViewFields.add(new ViewField("status","Status"));
        taskViewFields.add(new ViewField("workType","Work Type"));
        taskViewFields.add(new ViewField("skills","Skill"));
        taskView.setFields(taskViewFields);

        return taskView;
    }

    private FacilioView getServiceTasksHiddenAllViews(){
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Labour.ID", FieldType.NUMBER), true));

        FacilioView taskView = new FacilioView();
        taskView.setName("hidden-all");
        taskView.setDisplayName("All Tasks");

        taskView.setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        taskView.setSortFields(sortFields);
        taskView.setHidden(true);

        List<ViewField> taskViewFields = new ArrayList<>();
        taskViewFields.add(new ViewField("taskCode","Code"));
        taskViewFields.add(new ViewField("name","Task Name"));
        taskViewFields.add(new ViewField("status","Status"));
        taskViewFields.add(new ViewField("workType","Work Type"));
        taskViewFields.add(new ViewField("skills","Skill"));
        taskView.setFields(taskViewFields);

        return taskView;
    }

    private void addServiceOrderTaskModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceOrderTaskModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TASK,"Work Order Tasks","SERVICE_ORDER_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(serviceOrderTaskModule,"right","Task",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Tasks",serviceTaskModule);
        fields.add(serviceTaskField);
        LookupField serviceAppointmentField = new LookupField(serviceOrderTaskModule,"left","Work Order", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_ORDER_ID",FieldType.LOOKUP,true,false,true,true,"Work Orders",serviceOrderModule);
        fields.add(serviceAppointmentField);
        serviceOrderTaskModule.setFields(fields);
        modules.add(serviceOrderTaskModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addServiceTasksField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Tasks", null, FieldType.MULTI_LOOKUP);
        multiLookupTasksField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupTasksField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupTasksField.setLookupModule( modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK));
        multiLookupTasksField.setRelModule(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TASK));
        multiLookupTasksField.setRelModuleId(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TASK).getModuleId());
        fields.add(multiLookupTasksField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

}
