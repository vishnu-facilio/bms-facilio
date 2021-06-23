package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;

public class BulkTaskReadingFieldCommand extends FacilioCommand {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(BulkTaskReadingFieldCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenanceAPI.logIf(92L, "Entering BulkTaskReadingFieldCommand");
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

        if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
            return false;
        }

        List<Map<String, List<TaskContext>>> taskMaps = bulkWorkOrderContext.getTaskMaps();
        if (taskMaps == null || taskMaps.isEmpty()) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<ReadingDataMeta> metaList = new ArrayList<>();

        for (int i = 0; i < taskMaps.size(); i++) {
            if (taskMaps.get(i) == null) {
                continue;
            }
            for (List<TaskContext> values: taskMaps.get(i).values()) {
                for (int j = 0; j < values.size(); j++) {
                    TaskContext task = values.get(j);
                    if (task.getInputTypeEnum() == null) {
                        task.setInputType(TaskContext.InputType.NONE);
                    }
                    else {
                        switch(task.getInputTypeEnum()) {
                            case READING:
                                if(task.getReadingFieldId() == -1) {
                                    throw new IllegalArgumentException("Reading ID cannot be null when reading is enabled for task");
                                }
                                FacilioField readingField = modBean.getField(task.getReadingFieldId());
                                if (task.getResource() != null) {
                                    ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
                                    try {
                                        switch (meta.getInputTypeEnum()) {
                                            case CONTROLLER_MAPPED:
                                                throw new IllegalArgumentException("Readings that are mapped with controller cannot be used.");
                                            case FORMULA_FIELD:
                                            case HIDDEN_FORMULA_FIELD:
                                                throw new IllegalArgumentException("Readings that are mapped with formula field cannot be used.");
                                            default:
                                                metaList.add(meta);
                                                break;
                                        }
                                    } catch (NullPointerException e) {
                                        LOGGER.log(Level.SEVERE, "resourceId: " + task.getResource().getId() + " readingFieldId " + task.getReadingFieldId());
                                        throw e;
                                    }
                                }
                                break;
                            case RADIO:
                                if (task.getReadingFieldId() != -1) {
                                    task.setReadingField(modBean.getField(task.getReadingFieldId()));
                                    if(task.getReadingField() != null && ((EnumField) task.getReadingField()).getValues().size() < 2) {
                                        throw new IllegalArgumentException("Minimum two options has to be added for CHECKBOX/ RADIO task");
                                    }
                                }
                                else {
                                    if(task.getOptions() == null || task.getOptions().size() < 2) {
                                        throw new IllegalArgumentException("Minimum two options has to be added for CHECKBOX/ RADIO task");
                                    }
                                }
                                break;
                            case BOOLEAN:
                                if (task.getReadingFieldId() != -1) {
                                    BooleanField field = (BooleanField) modBean.getField(task.getReadingFieldId());
                                    task.setReadingField(field);
                                    if(field != null &&( field.getTrueVal() == null || field.getFalseVal() == null)) {
                                        throw new IllegalArgumentException("Both true and false valuse has to be set for BOOLEAN task");
                                    }
                                }
                                else {
                                    if (task.getOptions() == null || task.getOptions().size() != 2) {
                                        throw new IllegalArgumentException("Both true and false valuse has to be set for BOOLEAN task");
                                    }
                                }
                            default:
                                break;
                        }
                    }
                }
            }
        }

        if (!metaList.isEmpty()) {
            context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, metaList);
            context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingDataMeta.ReadingInputType.TASK);
        }

        PreventiveMaintenanceAPI.logIf(92L, "Done BulkTaskReadingFieldCommand");
        return false;
    }
}
