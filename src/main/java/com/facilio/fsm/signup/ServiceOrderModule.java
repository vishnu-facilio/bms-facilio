package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceOrderModule extends BaseModuleConfig {
    public ServiceOrderModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderModule));
//        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    private FacilioModule constructServiceOrderModule() throws Exception{
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL, "Skills", "Sevice_Skills", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField site = FieldFactory.getDefaultField("site","Site","SITE",FieldType.LOOKUP);
        site.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(site);

        LookupField client = FieldFactory.getDefaultField("client","Client","CLIENT",FieldType.LOOKUP);
        client.setLookupModule(bean.getModule(FacilioConstants.ContextNames.CLIENT));
        fields.add(client);

        FacilioField subject = FieldFactory.getDefaultField("subject","Subject","SUBJECT", FieldType.STRING,true);
        subject.setRequired(true);
        fields.add(subject);

        FacilioField description = FieldFactory.getDefaultField("description","Description","DESCRIPTION", FieldType.STRING,FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(description);

        SystemEnumField category = FieldFactory.getDefaultField("category","Category","CATEGORY",FieldType.SYSTEM_ENUM);
        category.setEnumName("ServiceOrderCategory");
        fields.add(category);

        SystemEnumField maintenanceType = FieldFactory.getDefaultField("maintenancetype","MaintenanceType","MAINTENANCE_TYPE",FieldType.SYSTEM_ENUM);
        maintenanceType.setEnumName("ServiceOrderMaintenanceType");
        fields.add(maintenanceType);

        SystemEnumField priority = FieldFactory.getDefaultField("priority","Priority","PRIORITY",FieldType.SYSTEM_ENUM);
        priority.setEnumName("ServiceOrderPriority");
        fields.add(priority);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SPACE));
        fields.add(space);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET",FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
        fields.add(asset);

        FacilioField status = FieldFactory.getDefaultField("status","Status","STATUS", FieldType.NUMBER);
        fields.add(status);

        LookupField vendor = FieldFactory.getDefaultField("vendor","Vendor","VENDOR",FieldType.LOOKUP);
        vendor.setLookupModule(bean.getModule(FacilioConstants.ContextNames.VENDOR));
        fields.add(vendor);

        FacilioField preferredstarttime = FieldFactory.getDefaultField("preferredstarttime","PreferredStartTime","PREFERRED_START_TIME", FieldType.DATE_TIME);
        fields.add(preferredstarttime);

        FacilioField preferredendtime = FieldFactory.getDefaultField("preferredendtime","PreferredEndTime","PREFERRED_END_TIME", FieldType.DATE_TIME);
        fields.add(preferredendtime);

        FacilioField autocreatesa = FieldFactory.getDefaultField("autocreatesa","AutoCreateSA","AUTO_CREATE_SA", FieldType.BOOLEAN);
        fields.add(autocreatesa);

        FacilioField responseDueDate = FieldFactory.getDefaultField("responseduedate","ResponseDueDate","RESPONSE_DUE_DATE", FieldType.DATE_TIME);
        fields.add(responseDueDate);

        FacilioField resolutionDueDate = FieldFactory.getDefaultField("resolutionduedate","ResolutionDueDate","RESOLUTION_DUE_DATE", FieldType.DATE_TIME);
        fields.add(resolutionDueDate);

        FacilioField resolvedTime = FieldFactory.getDefaultField("resolvedtime","ResolvedTime","RESOLVED_TIME", FieldType.DATE_TIME);
        fields.add(resolvedTime);

        LookupField stateflow = FieldFactory.getDefaultField("stateflow","StateFlow","STATE_FLOW",FieldType.LOOKUP);
        stateflow.setLookupModule(bean.getModule(FacilioConstants.ContextNames.STATE_FLOW));
        fields.add(stateflow);

        SystemEnumField sourceType = FieldFactory.getDefaultField("sourceType","SourceType","SOURCE_TYPE",FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("ServiceOrderSourceType");
        fields.add(sourceType);

        LookupField createdby = FieldFactory.getDefaultField("createdby","CreatedBy","CREATED_BY",FieldType.LOOKUP);
        createdby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(createdby);

        FacilioField createdTime = FieldFactory.getDefaultField("createdtime","CreatedTime","CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

        LookupField modifiedby = FieldFactory.getDefaultField("modifiedby","ModifiedBy","MODIFIED_BY",FieldType.LOOKUP);
        modifiedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(modifiedby);

        FacilioField modifiedTime = FieldFactory.getDefaultField("modifiedtime","ModifiedTime","MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(modifiedTime);

        LookupField sysdeletedby = FieldFactory.getDefaultField("sysdeletedby","SysDeletedBy","SYS_DELETED_BY",FieldType.LOOKUP);
        sysdeletedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(sysdeletedby);

        FacilioField sysdeletedtime = FieldFactory.getDefaultField("sysdeletedtime","SysDeletedTime","SYS_DELETED_TIME", FieldType.DATE_TIME);
        fields.add(sysdeletedtime);

        FacilioField actualstarttime = FieldFactory.getDefaultField("actualstarttime","ActualStartTime","ACTUAL_START_TIME", FieldType.DATE_TIME);
        fields.add(actualstarttime);

        FacilioField actualendtime = FieldFactory.getDefaultField("actualendtime","ActualEndTime","ACTUAL_END_TIME", FieldType.DATE_TIME);
        fields.add(actualendtime);

        FacilioField actualduration = FieldFactory.getDefaultField("actualduration","ActualDuration","ACTUAL_DURATION", FieldType.DATE_TIME);
        fields.add(actualduration);

        LookupField assignedto = FieldFactory.getDefaultField("assignedto","AssignedTo","ASSIGNED_TO",FieldType.LOOKUP);
        assignedto.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedto);

        LookupField assignedby = FieldFactory.getDefaultField("assignedby","AssignedBy","ASSIGNED_BY",FieldType.LOOKUP);
        assignedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(assignedby);

        FacilioField duedate = FieldFactory.getDefaultField("duedate","DueDate","DUE_DATE", FieldType.DATE_TIME);
        fields.add(duedate);

        FacilioField estimatedstarttime = FieldFactory.getDefaultField("estimatedstarttime","EstimatedStartTime","ESTIMATED_START_TIME", FieldType.DATE_TIME);
        fields.add(estimatedstarttime);

        FacilioField estimatedendtime = FieldFactory.getDefaultField("estimatedendtime","EstimatedEndTime","ESTIMATED_END_TIME", FieldType.DATE_TIME);
        fields.add(estimatedendtime);

        FacilioField estimatedduration = FieldFactory.getDefaultField("estimatedduration","EstimatedDuration","ESTIMATED_DURATION", FieldType.DATE_TIME);
        fields.add(estimatedduration);

        FacilioField modulestate = FieldFactory.getDefaultField("modulestate","ModuleState","MODULE_STATE", FieldType.NUMBER);
        fields.add(modulestate);

        FacilioField noOfAttachments = FieldFactory.getDefaultField("noofattachments","NoOfAttachments","NO_OF_ATTACHMENTS", FieldType.NUMBER);
        fields.add(noOfAttachments);

        FacilioField noOfClosedTasks = FieldFactory.getDefaultField("noofclosedtasks","NoOfClosedTasks","NO_OF_CLOSEDTASKS", FieldType.NUMBER);
        fields.add(noOfClosedTasks);

        FacilioField noOfNotes = FieldFactory.getDefaultField("noofnotes","NoOfNotes","NO_OF_NOTES", FieldType.NUMBER);
        fields.add(noOfNotes);

        FacilioField noOfTasks = FieldFactory.getDefaultField("nooftasks","NoOfTasks","NO_OF_TASKS", FieldType.NUMBER);
        fields.add(noOfTasks);

        LookupField slapolicyid = FieldFactory.getDefaultField("slapolicyid","SlaPolicyId","SLA_POLICY_ID",FieldType.LOOKUP);
        slapolicyid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SLA_POLICY));
        fields.add(slapolicyid);

        LookupField stateflowid = FieldFactory.getDefaultField("stateflowid","StateFlowId","STATE_FLOW_ID",FieldType.LOOKUP);
        stateflowid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.STATE_FLOW_ID));
        fields.add(stateflowid);

        FacilioField approvalRuleId = FieldFactory.getDefaultField("approvalruleid","ApprovalRuleId","APPROVAL_RULE_ID", FieldType.NUMBER);
        fields.add(approvalRuleId);

        FacilioField approvalState = FieldFactory.getDefaultField("approvalstate","ApprovalState","APPROVAL_STATE", FieldType.NUMBER);
        fields.add(approvalState);

        FacilioField parentso = FieldFactory.getDefaultField("parentso","ParentSo","PARENT_SO", FieldType.NUMBER);
        fields.add(parentso);

        LookupField pm = FieldFactory.getDefaultField("pm","PM","PM",FieldType.LOOKUP);
        pm.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE));
        fields.add(pm);

        LookupField pmresourceplanner = FieldFactory.getDefaultField("pmresourceplanner","PmResourcePlanner","PmResourcePlanner",FieldType.LOOKUP);
        pmresourceplanner.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE));
        fields.add(pmresourceplanner);

        LookupField prerequeststatus = FieldFactory.getDefaultField("prerequeststatus","PreRequestStatus","PRE_REQUEST_STATUS",FieldType.LOOKUP);
        prerequeststatus.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PRE_REQUEST_STATUS));
        fields.add(prerequeststatus);

        FacilioField prerequisiteapproved = FieldFactory.getDefaultField("prerequisiteapproved","PreRequisiteApproved","PRE_REQUISITE_APPROVED", FieldType.BOOLEAN);
        fields.add(prerequisiteapproved);

        FacilioField prerequisiteenabled = FieldFactory.getDefaultField("prerequisiteenabled","PreRequisiteEnabled","PRE_REQUISITE_ENABLED", FieldType.BOOLEAN);
        fields.add(prerequisiteenabled);

        FacilioField qrenabled = FieldFactory.getDefaultField("qrenabled","QrEnabled","QR_ENABLED", FieldType.BOOLEAN);
        fields.add(qrenabled);

        LookupField requestedby = FieldFactory.getDefaultField("requestedby","RequestedBy","REQUESTED_BY",FieldType.LOOKUP);
        requestedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(requestedby);

        FacilioField localid = FieldFactory.getDefaultField("localid","LocalId","LOCAL_ID", FieldType.NUMBER);
        fields.add(localid);

        module.setFields(fields);

        return module;
    }
}
