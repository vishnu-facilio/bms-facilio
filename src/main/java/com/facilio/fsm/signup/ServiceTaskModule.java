package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.context.ServiceTaskStatusContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
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
        addSystemButtons();
        addServiceOrderTaskModule();
        addServiceTasksField();
    }

    private FacilioModule constructServiceTaskModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, "Service Task", "Service_Task", FacilioModule.ModuleType.BASE_ENTITY,true);

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

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER",FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Service Order module doesn't exist."));
        fields.add(serviceOrder);

        fields.add(FieldFactory.getDefaultField("sequence","Sequence","SEQUENCE",FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("isPhotoMandatory","Photo Mandatory","IS_PHOTO_MANDATORY", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualStartTime","Actual Start Time","ACTUAL_START_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("actualEndTime","Actual End Time","ACTUAL_END_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        LookupField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.LOOKUP);
        status.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_STATUS),"Service Task Status module doesn't exist."));
        fields.add(status);

        module.setFields(fields);

        return module;
    }
    private void addTaskSkillsField() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceTaskSkillsRelMod = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_SKILLS, "Service Task Skills", "Service_Task_Skills", FacilioModule.ModuleType.SUB_ENTITY);
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
        taskView.setDisplayName("All Service Tasks");

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
        taskView.setDisplayName("All Service Tasks");

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

    public static void addSystemButtons() throws Exception {
        SystemButtonRuleContext startWork = new SystemButtonRuleContext();
        startWork.setName("Start Work");
        startWork.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        startWork.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
        startWork.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        startWork.setPermission("EXECUTE_SERVICE_TASKS_ALL");
        startWork.setPermissionRequired(true);
        Criteria startWorkCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.DISPATCHED);
        if(startWorkCriteria!=null){
            startWork.setCriteria(startWorkCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,startWork);

        SystemButtonRuleContext pause = new SystemButtonRuleContext();
        pause.setName("Pause Work");
        pause.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        pause.setIdentifier(FacilioConstants.ServiceAppointment.PAUSE);
        pause.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        pause.setPermission("EXECUTE_SERVICE_TASKS_ALL");
        pause.setPermissionRequired(true);
        Criteria pauseCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(pauseCriteria!=null) {
            pause.setCriteria(pauseCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,pause);

        SystemButtonRuleContext resume = new SystemButtonRuleContext();
        resume.setName("Resume Work");
        resume.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        resume.setIdentifier(FacilioConstants.ServiceAppointment.RESUME);
        resume.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        resume.setPermission("EXECUTE_SERVICE_TASKS_ALL");
        resume.setPermissionRequired(true);
        Criteria resumeCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.ON_HOLD);
        if(resumeCriteria!=null){
            resume.setCriteria(resumeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,resume);


        SystemButtonRuleContext complete = new SystemButtonRuleContext();
        complete.setName("Complete");
        complete.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
        complete.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        complete.setPermission("EXECUTE_SERVICE_TASKS_ALL");
        complete.setPermissionRequired(true);
        Criteria completeCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS);
        if(completeCriteria!=null){
            complete.setCriteria(completeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,complete);

        SystemButtonRuleContext reOpen = new SystemButtonRuleContext();
        reOpen.setName("Reopen");
        reOpen.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        reOpen.setIdentifier(FacilioConstants.ServiceAppointment.REOPEN);
        reOpen.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        reOpen.setPermission("EXECUTE_SERVICE_TASKS_ALL");
        reOpen.setPermissionRequired(true);
        Criteria reopenCriteria = getCriteria(FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED);
        if(reopenCriteria!=null){
            reOpen.setCriteria(reopenCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,reOpen);

        SystemButtonRuleContext startWorkOwn = new SystemButtonRuleContext();
        startWorkOwn.setName("Start Work");
        startWorkOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        startWorkOwn.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
        startWorkOwn.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        startWorkOwn.setPermission("EXECUTE_SERVICE_TASKS_OWN");
        startWorkOwn.setPermissionRequired(true);
        if(startWorkCriteria!=null){
            startWorkOwn.setCriteria(startWorkCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,startWorkOwn);

        SystemButtonRuleContext pauseOwn = new SystemButtonRuleContext();
        pauseOwn.setName("Pause Work");
        pauseOwn.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        pauseOwn.setIdentifier(FacilioConstants.ServiceAppointment.PAUSE);
        pauseOwn.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        pauseOwn.setPermission("EXECUTE_SERVICE_TASKS_OWN");
        pauseOwn.setPermissionRequired(true);
        if(pauseCriteria!=null) {
            pauseOwn.setCriteria(pauseCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,pauseOwn);

        SystemButtonRuleContext resumeOwn = new SystemButtonRuleContext();
        resumeOwn.setName("Resume Work");
        resumeOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        resumeOwn.setIdentifier(FacilioConstants.ServiceAppointment.RESUME);
        resumeOwn.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        resumeOwn.setPermission("EXECUTE_SERVICE_TASKS_OWN");
        resumeOwn.setPermissionRequired(true);
        if(resumeCriteria!=null){
            resumeOwn.setCriteria(resumeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,resumeOwn);

        SystemButtonRuleContext completeOwn = new SystemButtonRuleContext();
        completeOwn.setName("Complete");
        completeOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        completeOwn.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
        completeOwn.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        completeOwn.setPermission("EXECUTE_SERVICE_TASKS_OWN");
        completeOwn.setPermissionRequired(true);
        if(completeCriteria!=null){
            completeOwn.setCriteria(completeCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,completeOwn);

        SystemButtonRuleContext reopenOwn = new SystemButtonRuleContext();
        reopenOwn.setName("Reopen");
        reopenOwn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        reopenOwn.setIdentifier(FacilioConstants.ServiceAppointment.REOPEN);
        reopenOwn.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        reopenOwn.setPermission("EXECUTE_SERVICE_TASKS_OWN");
        reopenOwn.setPermissionRequired(true);
        if(reopenCriteria!=null){
            reopenOwn.setCriteria(reopenCriteria);
        }
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,reopenOwn);


    }

    private void addServiceOrderTaskModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceOrderTaskModule = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TASK,"Service Order Tasks","SERVICE_ORDER_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(serviceOrderTaskModule,"right","Service Tasks",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Service Tasks",serviceTaskModule);
        fields.add(serviceTaskField);
        LookupField serviceAppointmentField = new LookupField(serviceOrderTaskModule,"left","Service Order", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_ORDER_ID",FieldType.LOOKUP,true,false,true,true,"Service Orders",serviceOrderModule);
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
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Service Tasks", null, FieldType.MULTI_LOOKUP);
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
    private static Criteria getCriteria(String status) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> stFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(stFields);
        ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(status);
        if(taskStatus!=null){
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get(FacilioConstants.ContextNames.STATUS),Collections.singletonList(taskStatus.getId()), PickListOperators.IS));
            return criteria;
        }
        return null;
    }

}
