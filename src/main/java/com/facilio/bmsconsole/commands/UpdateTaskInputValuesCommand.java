package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateTaskInputValuesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule taskModule = modBean.getModule("task");
        Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(modBean.getAllFields("task"));
        for (TaskContext task: tasks) {
            UpdateRecordBuilder<TaskContext> updateRecordBuilder = new UpdateRecordBuilder<>();
            updateRecordBuilder.module(taskModule)
                    .fields(Arrays.asList(taskFieldMap.get("inputTime"), taskFieldMap.get("inputValue")))
                    .andCondition(CriteriaAPI.getIdCondition(task.getId(), taskModule));
            updateRecordBuilder.update(task);
        }
        return false;
    }
}
