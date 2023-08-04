package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class ServiceTaskModule extends BaseModuleConfig {

    public ServiceTaskModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceTaskModule = constructServiceTaskModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceTaskModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addTaskSkillsField();
        addSystemButtons();
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

        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
        status.setEnumName("ServiceTaskStatus");
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

    private static void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> stFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(stFields);

        SystemButtonRuleContext startWork = new SystemButtonRuleContext();
        startWork.setName("Start Work");
        startWork.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        startWork.setIdentifier(FacilioConstants.ServiceAppointment.START_WORK);
        startWork.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        startWork.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        startWork.setPermissionRequired(true);
        Criteria startWorkCriteria = new Criteria();
        startWorkCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("status"),String.valueOf(ServiceTaskContext.ServiceTaskStatus.DISPATCHED.getIndex()), EnumOperators.IS));
        startWork.setCriteria(startWorkCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,startWork);

        SystemButtonRuleContext pause = new SystemButtonRuleContext();
        pause.setName("Pause Work");
        pause.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        pause.setIdentifier(FacilioConstants.ServiceAppointment.PAUSE);
        pause.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        pause.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        pause.setPermissionRequired(true);
        Criteria pauseCriteria = new Criteria();
        pauseCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("status"),String.valueOf(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex()), EnumOperators.IS));
        pause.setCriteria(pauseCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,pause);

        SystemButtonRuleContext resume = new SystemButtonRuleContext();
        resume.setName("Resume Work");
        resume.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        resume.setIdentifier(FacilioConstants.ServiceAppointment.RESUME);
        resume.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        resume.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        resume.setPermissionRequired(true);
        Criteria resumeCriteria = new Criteria();
        resumeCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("status"),String.valueOf(ServiceTaskContext.ServiceTaskStatus.ON_HOLD.getIndex()), EnumOperators.IS));
        resume.setCriteria(resumeCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,resume);


        SystemButtonRuleContext complete = new SystemButtonRuleContext();
        complete.setName("Complete");
        complete.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        complete.setIdentifier(FacilioConstants.ServiceAppointment.COMPLETE);
        complete.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        complete.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        complete.setPermissionRequired(true);
        Criteria completeCriteria = new Criteria();
        completeCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("status"),String.valueOf(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS.getIndex()), EnumOperators.IS));
        complete.setCriteria(completeCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,complete);

        SystemButtonRuleContext reOpen = new SystemButtonRuleContext();
        reOpen.setName("Reopen");
        reOpen.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        reOpen.setIdentifier(FacilioConstants.ServiceAppointment.REOPEN);
        reOpen.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        reOpen.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        reOpen.setPermissionRequired(true);
        Criteria reopenCriteria = new Criteria();
        reopenCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("status"),String.valueOf(ServiceTaskContext.ServiceTaskStatus.COMPLETED.getIndex()), EnumOperators.IS));
        reOpen.setCriteria(reopenCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,reOpen);

    }

}
