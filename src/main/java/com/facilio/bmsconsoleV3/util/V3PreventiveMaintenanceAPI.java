package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3TaskTemplate;

import java.util.Map;

public class V3PreventiveMaintenanceAPI {

    public static void updateTaskTemplateFromTaskContext(Map<V3TaskContext, V3TaskTemplate> taskvsTemplateMap) throws Exception {
        if(taskvsTemplateMap != null && !taskvsTemplateMap.isEmpty()) {
            for(V3TaskContext task :taskvsTemplateMap.keySet()) {
                taskvsTemplateMap.get(task).setTask(task);
            }
        }
    }
}
