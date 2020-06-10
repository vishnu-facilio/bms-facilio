package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskSectionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<V3TaskContext>> taskMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
        Map<String, List<V3TaskContext>> preRequestMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);
        V3WorkOrderContext workOrder = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(workOrder != null && taskMap != null && !taskMap.isEmpty()) {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTaskSectionModule().getTableName())
                    .fields(FieldFactory.getTaskSectionFields())
                    ;
            int sequence = 1;
            List<TaskSectionContext> sections = new ArrayList<>();
            for(Map.Entry<String, List<V3TaskContext>> entry : taskMap.entrySet()) {
                if(!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    TaskSectionContext section = new TaskSectionContext();
                    section.setParentTicketId(workOrder.getId());
                    section.setName(entry.getKey());
                    section.setSequenceNumber(sequence++);
                    section.setPreRequest(Boolean.FALSE);
                    insertBuilder.addRecord(FieldUtil.getAsProperties(section));
                    sections.add(section);
                }
            }
            insertBuilder.save();

            Map<String, TaskSectionContext> sectionMap = new HashMap<>();
            List<Map<String, Object>> sectionProps = insertBuilder.getRecords();
            for(int i = 0; i<sectionProps.size(); i++) {
                long id = (long) sectionProps.get(i).get("id");
                TaskSectionContext section = sections.get(i);
                section.setId(id);
                sectionMap.put(section.getName(), section);
            }
            context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sectionMap);
        }
        if (workOrder != null && preRequestMap != null && !preRequestMap.isEmpty()) {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTaskSectionModule().getTableName())
                    .fields(FieldFactory.getTaskSectionFields());
            int sequence = 1;
            List<TaskSectionContext> sections = new ArrayList<>();
            for (Map.Entry<String, List<V3TaskContext>> entry : preRequestMap.entrySet()) {

                if (!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    TaskSectionContext section = new TaskSectionContext();
                    section.setParentTicketId(workOrder.getId());
                    section.setName(entry.getKey());
                    section.setSequenceNumber(sequence++);
                    section.setPreRequest(Boolean.TRUE);
                    insertBuilder.addRecord(FieldUtil.getAsProperties(section));
                    sections.add(section);
                }
            }
            insertBuilder.save();

            Map<String, TaskSectionContext> sectionMap = new HashMap<>();
            List<Map<String, Object>> sectionProps = insertBuilder.getRecords();
            for (int i = 0; i < sectionProps.size(); i++) {
                long id = (long) sectionProps.get(i).get("id");
                TaskSectionContext section = sections.get(i);
                section.setId(id);
                sectionMap.put(section.getName(), section);
            }
            context.put(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS, sectionMap);
        }
        return false;
    }
}
