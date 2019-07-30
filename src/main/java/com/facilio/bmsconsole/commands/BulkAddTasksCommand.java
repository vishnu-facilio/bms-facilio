package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BulkAddTasksCommand extends FacilioCommand implements PostTransactionCommand {

    private List<Long> idsToUpdateTaskCount;
    private String moduleName;

    private static Logger LOGGER = LogManager.getLogger(BulkAddTasksCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenanceAPI.logIf(92L,"Entering BulkAddTasksCommand");
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

        if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
            return false;
        }

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);


        List<WorkOrderContext> workOrderContexts = bulkWorkOrderContext.getWorkOrderContexts();
        Map<Long, Integer> workOrderTaskMap = new HashMap<>();
       for (int i = 0; i < workOrderContexts.size(); i++) {
           WorkOrderContext wo = workOrderContexts.get(i);
           Map<String, List<TaskContext>> taskMap = bulkWorkOrderContext.getTaskMaps().get(i);

           if (taskMap == null || taskMap.isEmpty()) {
               continue;
           }

           Collection<List<TaskContext>> listOfTasks = taskMap.values();

           int sum = 0;
           for (List<TaskContext> tasks: listOfTasks) {
               sum += tasks.size();
           }

           PreventiveMaintenanceAPI.logIf(92L,"woid " + wo.getId() + " task Map size " + sum);

           Map<String, TaskSectionContext> sections = bulkWorkOrderContext.getSectionMap().get(wo.getId());

           if (workOrderTaskMap.get(wo.getId()) == null) {
               workOrderTaskMap.put(wo.getId(), 0);
           } else {
               PreventiveMaintenanceAPI.logIf(92L,"Duplicate entry for " + wo.getId());
           }

           for (Map.Entry<String, List<TaskContext>> entry:  taskMap.entrySet()) {
               String sectionName = entry.getKey();
               List<TaskContext> tasks = entry.getValue();
               long sectionId = -1;
               if(!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                   sectionId = sections.get(sectionName).getId();
               }

               for(TaskContext task : tasks) {
                   task.setCreatedTime(System.currentTimeMillis());
                   task.setSectionId(sectionId);
                   task.setStatusNew(TaskContext.TaskStatus.OPEN);
                   task.setPreRequest(Boolean.FALSE);

                   task.setParentTicketId(wo.getId());

                   task.setInputValue(task.getDefaultValue());
                   if(StringUtils.isNotEmpty(task.getFailureValue()) && task.getFailureValue().equals(task.getInputValue())) {
                       task.setFailed(true);
                   }

                   // LOGGER.log(Level.ERROR, "task uniqueness " + task.getParentTicketId() + " - " + task.getResource().getId() + " - " + task.getUniqueId());

                   task.setCreatedBy(AccountUtil.getCurrentUser());
                   bulkWorkOrderContext.getTaskContextList().add(task);
               }

               List<TaskContext> taskContexts = bulkWorkOrderContext.getTaskContextList().stream().filter(task -> task.getParentTicketId() == wo.getId()).collect(Collectors.toList());

               workOrderTaskMap.put(wo.getId(), taskContexts.size());
           }
       }

       int taskContextSize = bulkWorkOrderContext.getTaskContextList().size();

        Set<String> countMap = new HashSet<>();

       for (TaskContext taskContext: bulkWorkOrderContext.getTaskContextList()) {
           String key;
           if (taskContext.getResource() == null) {
               key = taskContext.getParentTicketId()+"-"+"null"+"-"+taskContext.getUniqueId();
           } else {
               key = taskContext.getParentTicketId()+"-"+taskContext.getResource().getId()+"-"+taskContext.getUniqueId();
           }
           if (!countMap.contains(key)) {
               countMap.add(key);
           } else {
               PreventiveMaintenanceAPI.logIf(92L,"Duplicate " + key);
           }
       }

       for (Map.Entry<Long, Integer> entry:workOrderTaskMap.entrySet()) {
           PreventiveMaintenanceAPI.logIf(92L,"after-Wo " + entry.getKey() + " size: " +entry.getValue());
       }

        int startIndex = 0;
        int batchSize = 10000;
        while (taskContextSize > 0) {
            InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
                    .module(module)
                    .withLocalId()
                    .fields(fields);

            List<TaskContext> records;
            int upperLimit = startIndex + batchSize;
            if (batchSize > taskContextSize) {
                upperLimit = startIndex + taskContextSize;
            }
            records = bulkWorkOrderContext.getTaskContextList().subList(startIndex, upperLimit);
            startIndex = upperLimit;
            taskContextSize = taskContextSize - batchSize;

            builder.addRecords(records);
            builder.save();
            PreventiveMaintenanceAPI.logIf(92L, "remaining tasks " + taskContextSize);
        }

       idsToUpdateTaskCount = bulkWorkOrderContext.getWorkOrderContexts().stream().map(WorkOrderContext::getId).collect(Collectors.toList());
       this.moduleName = moduleName;

       if (bulkWorkOrderContext.getPreRequestMaps() == null || bulkWorkOrderContext.getPreRequestMaps().isEmpty()) {
           PreventiveMaintenanceAPI.logIf(92L,"pre request is not executed");
           return false;
       } else {
           PreventiveMaintenanceAPI.logIf(92L,"pre request is executed");
       }



       Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS);

       InsertRecordBuilder<TaskContext> preReqBuilder = new InsertRecordBuilder<TaskContext>().module(module)
                .withLocalId().fields(fields);

       for (int i = 0; i < workOrderContexts.size(); i++) {
           WorkOrderContext workOrder = workOrderContexts.get(i);
           Map<String, List<TaskContext>> preRequestMap = bulkWorkOrderContext.getPreRequestMaps().get(i);

           if (preRequestMap == null) {
               continue;
           }

           preRequestMap.forEach((sectionName, tasks) -> {
               long sectionId = -1;
               if (!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                   if (sections != null && sections.get(sectionName) != null) {
                       sectionId = sections.get(sectionName).getId();
                   }
               }
               if (tasks != null) {
                   for (TaskContext task : tasks) {
                       task.setCreatedTime(System.currentTimeMillis());
                       task.setSectionId(sectionId);
                       task.setStatusNew(TaskContext.TaskStatus.OPEN);
                       task.setPreRequest(Boolean.TRUE);
                       if (workOrder != null) {
                           task.setParentTicketId(workOrder.getId());
                       }
                       task.setInputValue(task.getDefaultValue());
                       if (StringUtils.isNotEmpty(task.getFailureValue())
                               && task.getFailureValue().equals(task.getInputValue())) {
                           task.setFailed(true);
                       }
                       task.setCreatedBy(AccountUtil.getCurrentUser());
                       preReqBuilder.addRecord(task);
                   }
               }
           });
       }

       preReqBuilder.save();

        PreventiveMaintenanceAPI.logIf(92L,"done BulkAddTasksCommand");

       return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        TicketAPI.updateTaskCount(idsToUpdateTaskCount);
        return false;
    }
}
