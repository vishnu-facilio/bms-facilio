package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateTaskReadingInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS_MAP);
        if (readingMap != null) {
            Map<Long, ReadingContext> readings = readingMap.values().stream().flatMap(List::stream).collect(Collectors.toMap(r -> (Long) r.getReading("taskId"), Function.identity()));
            Map<String, List<V3TaskContext>> taskMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            FacilioModule module = modBean.getModule(moduleName);
            List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

            if (taskMap != null && !taskMap.isEmpty()) {
                for( Map.Entry<String, List<V3TaskContext>> entry : taskMap.entrySet()) {
                    for(V3TaskContext task : entry.getValue()) {
                        if (readings.containsKey(task.getId())) {
                            V3TaskContext newTask = new V3TaskContext();
                            newTask.setReadingDataId(readings.get(task.getId()).getId());

                            UpdateRecordBuilder<V3TaskContext> updateBuilder = new UpdateRecordBuilder<V3TaskContext>()
                                    .module(module)
                                    .fields(fields)
                                    .andCondition(CriteriaAPI.getIdCondition(task.getId(), module));
                            updateBuilder.update(newTask);
                        }
                    }
                }
            }
        }
        return false;
    }
}
