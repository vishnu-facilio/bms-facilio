package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
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
import com.facilio.v3.context.Constants;
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

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workorder = wos.get(0);

        Map<String, List<V3TaskContext>> taskMap = workorder.getTasksString();
        Map<String, List<V3TaskContext>> preRequestMap = (Map<String, List<V3TaskContext>>)
                context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
        List<FacilioField> fields = modBean.getAllFields(module.getName());


        if (taskMap != null && !taskMap.isEmpty()) {
            Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>)
                    context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
            List<V3TaskContext> tasks = addTasks(module, taskMap, sections, workorder, fields, false);
            context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);

            idsToUpdateTaskCount = Collections.singletonList(workorder.getId());
        }

        if (preRequestMap != null && !preRequestMap.isEmpty()) {
            Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>)
                    context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS);
            List<V3TaskContext> preRequisites =
                    addTasks(module, preRequestMap, sections, workorder, fields, true);
            context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, preRequisites);
        }
        return false;
    }


    private List<V3TaskContext> addTasks(FacilioModule module, Map<String, List<V3TaskContext>> taskMap,
                                         Map<String, TaskSectionContext> sections, V3WorkOrderContext workOrder,
                                         List<FacilioField> fields, boolean isPrerequest) throws Exception {

        InsertRecordBuilder<V3TaskContext> builder = new InsertRecordBuilder<V3TaskContext>()
                .module(module)
                .withLocalId()
                .fields(fields);
        taskMap.forEach((sectionName, tasks) -> {
            long sectionId = -1;
            if (!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                sectionId = sections.get(sectionName).getId();
            }
            for (V3TaskContext task : tasks) {
                task.setCreatedTime(System.currentTimeMillis());
                task.setSectionId(sectionId);
                task.setStatusNew(TaskContext.TaskStatus.OPEN.getValue());
                task.setPreRequest(isPrerequest);
                if (workOrder != null) {
                    task.setParentTicketId(workOrder.getId());
                    if (task.getSiteId() == -1) {
                        task.setSiteId(workOrder.getSiteId());
                    }
                }
                task.setInputValue(isPrerequest ? null : task.getDefaultValue());
                if (StringUtils.isNotEmpty(task.getInputValue()) && StringUtils.isNotEmpty(task.getFailureValue())) {
                    if (task.getInputTypeEnum() == V3TaskContext.InputType.NUMBER) {
                        if (task.getDeviationOperator() != null) {
                            FacilioModulePredicate predicate =
                                    task.getDeviationOperator().getPredicate("inputValue", task.getFailureValue());
                            task.setFailed(predicate.evaluate(task));
                        }
                    } else if (task.getFailureValue().equals(task.getInputValue())) {
                        task.setFailed(true);
                    } else {
                        task.setFailed(false);
                    }
                }
                task.setCreatedBy(AccountUtil.getCurrentUser());
                builder.addRecord(task);
            }
        });

        builder.save();

        return builder.getRecords();
    }

    @Override
    public boolean postExecute() throws Exception {
        V3TicketAPI.updateTaskCount(idsToUpdateTaskCount);
        return false;
    }

}
