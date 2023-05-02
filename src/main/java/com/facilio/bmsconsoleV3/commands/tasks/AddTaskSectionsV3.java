package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class AddTaskSectionsV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workorder = wos.get(0);

        Map<String, List<V3TaskContext>> taskMap = workorder.getTasksString();
        Map<String, List<TaskContext>> preRequestMap = (Map<String, List<TaskContext>>)
                context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);

        if (workorder != null && taskMap != null && !taskMap.isEmpty()) {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTaskSectionModule().getTableName())
                    .fields(FieldFactory.getTaskSectionFields());
            int sequence = 1;
            List<TaskSectionContext> sections = new ArrayList<>();
            for (Map.Entry<String, List<V3TaskContext>> entry : taskMap.entrySet()) {
                if (!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    TaskSectionContext section = new TaskSectionContext();
                    section.setParentTicketId(workorder.getId());
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
            for (int i = 0; i < sectionProps.size(); i++) {
                long id = (long) sectionProps.get(i).get("id");
                TaskSectionContext section = sections.get(i);
                section.setId(id);
                sectionMap.put(section.getName(), section);
            }
            context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sectionMap);
        }

        if (workorder != null && preRequestMap != null && !preRequestMap.isEmpty()) {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTaskSectionModule().getTableName())
                    .fields(FieldFactory.getTaskSectionFields());
            int sequence = 1;
            List<TaskSectionContext> sections = new ArrayList<>();
            for (Map.Entry<String, List<TaskContext>> entry : preRequestMap.entrySet()) {

                if (!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
                    TaskSectionContext section = new TaskSectionContext();
                    section.setParentTicketId(workorder.getId());
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
