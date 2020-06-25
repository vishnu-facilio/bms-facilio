package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddTasksCommandV3 extends FacilioCommand implements PostTransactionCommand {

    private static final Logger LOGGER = Logger.getLogger(AddTasksCommandV3.class.getName());
    private List<Long> idsToUpdateTaskCount;
    private String moduleName;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<V3TaskContext>> taskMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
        Map<String, List<V3TaskContext>> preRequestMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);
        V3WorkOrderContext workOrder = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(taskMap != null && !taskMap.isEmpty()) {
            Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

            InsertRecordBuilder<V3TaskContext> builder = new InsertRecordBuilder<V3TaskContext>()
                    .module(module)
                    .withLocalId()
                    .fields(fields)
                    ;
            taskMap.forEach((sectionName, tasks) -> {
                long sectionId = -1;
                if(!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    sectionId = sections.get(sectionName).getId();
                }
                for(V3TaskContext task : tasks) {
                    task.setCreatedTime(System.currentTimeMillis());
                    task.setSectionId(sectionId);
                    task.setStatusNew(V3TaskContext.TaskStatus.OPEN.getValue());
                    task.setPreRequest(Boolean.FALSE);
                    if(workOrder != null) {
                        task.setParentTicketId(workOrder.getId());
                    }
                    task.setInputValue(task.getDefaultValue());
                    if(StringUtils.isNotEmpty(task.getInputValue()) && StringUtils.isNotEmpty(task.getFailureValue())) {
                        if (task.getInputTypeEnum() == V3TaskContext.InputType.NUMBER) {
                            FacilioModulePredicate predicate = task.getDeviationOperator().getPredicate("inputValue", task.getFailureValue());
                            task.setFailed(predicate.evaluate(task));
                        }
                        else if (task.getFailureValue().equals(task.getInputValue())) {
                            task.setFailed(true);
                        }
                        else {
                            task.setFailed(false);
                        }
                    }
                    task.setCreatedBy(AccountUtil.getCurrentUser());
                    builder.addRecord(task);
                }
            });

            builder.save();
            context.put(FacilioConstants.ContextNames.TASK_LIST, builder.getRecords());
            V3WorkOrderContext workOrdernew = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);

            idsToUpdateTaskCount = Collections.singletonList(workOrder.getId());
            this.moduleName = moduleName;

// 			FacilioChain.addPostTransactionListObject(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, workOrder.getId());
//			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

//			List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
//            List<Object> tasklist = new ArrayList<Object>();
//			if (!tasks.isEmpty()) {
//				for(TaskContext task:tasks) {
//					JSONObject info = new JSONObject();
//				info.put("Task", task.getSubject().toString());
//				tasklist.add(info);
//
//				}
//				JSONObject newinfo = new JSONObject();
//                newinfo.put("Task",tasklist);
//			CommonCommandUtil.addActivityToContext(tasks.get(0).getParentTicketId(), -1, WorkOrderActivityType.ADD_TASK, newinfo, (FacilioContext) context);
//			}
        }
        else {
//			throw new IllegalArgumentException("Task list cannot be null/ empty");
        }
        if (preRequestMap != null && !preRequestMap.isEmpty()) {
            Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context
                    .get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            List<FacilioField> fields = (List<FacilioField>) context
                    .get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
            InsertRecordBuilder<V3TaskContext> builder = new InsertRecordBuilder<V3TaskContext>().module(module)
                    .withLocalId().fields(fields);
            preRequestMap.forEach((sectionName, tasks) -> {
                long sectionId = -1;
                if (!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    sectionId = sections.get(sectionName).getId();
                }
                for (V3TaskContext task : tasks) {
                    task.setCreatedTime(System.currentTimeMillis());
                    task.setSectionId(sectionId);
                    task.setStatusNew(V3TaskContext.TaskStatus.OPEN.getValue());
                    task.setPreRequest(Boolean.TRUE);
                    if (workOrder != null) {
                        task.setParentTicketId(workOrder.getId());
                    }
                    task.setInputValue(null);
                    if (StringUtils.isNotEmpty(task.getFailureValue())
                            && task.getFailureValue().equals(task.getInputValue())) {
                        task.setFailed(true);
                    }
                    else {
                        task.setFailed(false);
                    }
                    task.setCreatedBy(AccountUtil.getCurrentUser());
                    builder.addRecord(task);
                }
            });
            builder.save();
            context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, builder.getRecords());
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        V3TicketAPI.updateTaskCount(idsToUpdateTaskCount);
        return false;
    }

}
