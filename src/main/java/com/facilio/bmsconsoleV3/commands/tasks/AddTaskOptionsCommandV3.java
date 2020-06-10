package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskOptionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3TaskContext> tasks = (List<V3TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
        if(tasks == null) {
            V3TaskContext task = (V3TaskContext) context.get(FacilioConstants.ContextNames.TASK);
            if(task != null) {
                tasks = Collections.singletonList(task);
            }
        }

        if (tasks != null && !tasks.isEmpty()) {
            FacilioModule module = ModuleFactory.getTaskInputOptionModule();

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getTaskInputOptionsFields())
                    ;

            for(V3TaskContext task : tasks) {
                switch(task.getInputTypeEnum()) {
//					case CHECKBOX:
                    case RADIO:
                        if (task.getOptions() != null && !task.getOptions().isEmpty()) {
                            for (String option : task.getOptions()) {
                                Map<String, Object> optionMap = new HashMap<>();
                                optionMap.put("taskId", task.getId());
                                optionMap.put("option", option);
                                insertBuilder.addRecord(optionMap);
                            }
                        }
                        break;
//					case BOOLEAN:
//						if (task.getOptions() != null && !task.getOptions().isEmpty()) {
//							for (String option : task.getOptions()) {
//								Map<String, Object> optionMap = new HashMap<>();
//								optionMap.put("taskId", task.getId());
//								optionMap.put("option", option);
//								insertBuilder.addRecord(optionMap);
//							}
//						}
//						break;
                    default:
                        break;
                }
            }
            insertBuilder.save();
        }
        return false;
    }
}
