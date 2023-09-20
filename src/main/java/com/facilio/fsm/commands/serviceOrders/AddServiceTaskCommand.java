package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddServiceTaskCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            for(ServiceOrderContext serviceOrder : serviceOrders){
                if(serviceOrder.getServicePlannedMaintenance()!=null && serviceOrder.getServicePlannedMaintenance().getId()>0){
                    List<ServiceTaskContext> serviceTasks = getServiceTasks(serviceOrder.getServicePlannedMaintenance(),serviceOrder);
                    V3Util.preCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, FieldUtil.getAsMapList(serviceTasks,ServiceTaskContext.class),null,null);
                }
            }
        }
        return false;
    }
    private List<ServiceTaskContext> getServiceTasks(ServicePlannedMaintenanceContext servicePM,ServiceOrderContext serviceOrder)throws Exception{
           ServicePlannedMaintenanceContext servicePMRec = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,servicePM.getId());
           if(servicePMRec.getServicePlan()!=null && servicePMRec.getServicePlan().getId()>0){
               List<ServiceTaskTemplateContext> serviceTaskTemplates = getServiceTaskTemplates(servicePMRec.getServicePlan().getId());
               if(CollectionUtils.isNotEmpty(serviceTaskTemplates)){
                   List<ServiceTaskContext> serviceTasks = new ArrayList<>();
                   for(ServiceTaskTemplateContext serviceTaskTemplate : serviceTaskTemplates){
                       ServiceTaskContext serviceTask = new ServiceTaskContext();
                       serviceTask.setServiceOrder(serviceOrder);
                       serviceTask.setName(serviceTaskTemplate.getName());
                       serviceTask.setDescription(serviceTaskTemplate.getDescription());
                       serviceTask.setTaskCode(serviceTaskTemplate.getTaskCode());
                       serviceTask.setWorkType(serviceTaskTemplate.getWorkType());
                       serviceTask.setSequence(serviceTaskTemplate.getSequence());
                       serviceTask.setIsPhotoMandatory(serviceTaskTemplate.getIsPhotoMandatory());
                       if(CollectionUtils.isNotEmpty(serviceTaskTemplate.getSkills())){
                           List<ServiceTaskSkillsContext> taskSkills = new ArrayList<>();
                           for(ServiceTaskTemplateSkillsContext skill : serviceTaskTemplate.getSkills()){
                               ServiceTaskSkillsContext taskSkill = new ServiceTaskSkillsContext();
                               taskSkill.setRight(skill.getRight());
                               taskSkill.setLeft(serviceTask);
                               taskSkills.add(taskSkill);
                           }
                           serviceTask.setSkills(taskSkills);
                       }
                       serviceTasks.add(serviceTask);
                   }
                   return serviceTasks;
               }
           }
           return null;
    }
    private List<ServiceTaskTemplateContext> getServiceTaskTemplates(Long servicePlanId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);
        SelectRecordsBuilder<ServiceTaskTemplateContext> recordsBuilder = new SelectRecordsBuilder<ServiceTaskTemplateContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServiceTaskTemplateContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PLAN","servicePlan",String.valueOf(servicePlanId), NumberOperators.EQUALS));
        List<ServiceTaskTemplateContext>  serviceTaskTemplates = recordsBuilder.get();
        return serviceTaskTemplates;
    }
}
