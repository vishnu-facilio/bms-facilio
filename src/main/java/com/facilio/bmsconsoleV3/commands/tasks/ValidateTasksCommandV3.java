package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3TaskSectionTemplate;
import com.facilio.bmsconsoleV3.context.V3TaskTemplate;
import com.facilio.bmsconsoleV3.context.V3WorkorderTemplate;
import com.facilio.bmsconsoleV3.util.V3PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidateTasksCommandV3 extends FacilioCommand {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(ValidateTasksCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        int maxUniqueId = 0;
        List<V3TaskContext> tasks = null;
        Map<V3TaskContext, V3TaskTemplate> taskvsTemplateMap= null;

        if(context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES) != null) {
            List<V3TaskSectionTemplate> sectionTemplates =  (List<V3TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES);
            tasks = new ArrayList<>();
            taskvsTemplateMap = new HashMap<>();
            for(V3TaskSectionTemplate sectionTemplate :sectionTemplates) {
                for(V3TaskTemplate taskTemplate:sectionTemplate.getTaskTemplates()) {
                    V3TaskContext task = taskTemplate.getTask();
                    tasks.add(task);
                    taskvsTemplateMap.put(task, taskTemplate);
                }
            }
        }
        else {
            Map<String, List<V3TaskContext>> taskMap = (Map<String, List<V3TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
            if(taskMap == null) {
                tasks = (List<V3TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
                if(tasks == null) {
                    V3TaskContext task = (V3TaskContext) context.get(FacilioConstants.ContextNames.TASK);
                    if(task != null) {
                        tasks = Collections.singletonList(task);
                        maxUniqueId = getMaxUniqueIdFromExistingTasks(task.getParentTicketId());
                        WorkOrderContext wo = WorkOrderAPI.getWorkOrder(task.getParentTicketId(), Collections.singletonList("moduleState"));
                        context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(wo));
                    }
                }
            }
            else {
                tasks = new ArrayList<>();
                for(Map.Entry<String, List<V3TaskContext>> entry : taskMap.entrySet()) {
                    tasks.addAll(entry.getValue());
                }
            }
        }

        if (tasks != null && !tasks.isEmpty()) {
            List<ReadingDataMeta> metaList = new ArrayList<>();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Boolean pmExecution = (Boolean) context.get(FacilioConstants.ContextNames.IS_PM_EXECUTION);
            if (pmExecution == null) {
                pmExecution = false;
            }
            Boolean updatePM = (Boolean) context.get(FacilioConstants.ContextNames.IS_UPDATE_PM);//Temp fix
            if (updatePM == null) {
                updatePM = false;
            }
            else if (context.containsKey(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)){
                PreventiveMaintenance oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
                if (oldPm.getWoTemplate() != null) {
                    maxUniqueId = getMaxUniqueIdFromTemplate(oldPm.getWoTemplate().getId());
                }
            }

            for(V3TaskContext task : tasks) {
                validateSiteSpecificData(task);
                if (task.getUniqueId() == -1) {
                    task.setUniqueId(++maxUniqueId);
                }
                if (task.getInputTypeEnum() == null) {
                    task.setInputType(V3TaskContext.InputType.NONE.getVal());
                }
                else {
                    switch(task.getInputTypeEnum()) {
                        case READING:
//							if ((pm == null || pm.getPmCreationType() != PMCreationType.MULTIPLE.getVal()) && (task.getResource() == null || task.getResource().getId() == -1)) {
//								throw new IllegalArgumentException("Resource cannot be null when reading is enabled for task");
//							}
                            if(task.getReadingFieldId() == -1) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Reading ID cannot be null when reading is enabled for task");
                            }
                            FacilioField readingField = modBean.getField(task.getReadingFieldId());
                            if (task.getResource() != null) {
                                ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);

                                if (meta == null) {
                                    LOGGER.log(Level.SEVERE, "RDM Entry missing for resourceId: " + task.getResource().getId() + " readingFieldId " + task.getReadingFieldId());
                                    CommonCommandUtil.emailAlert("RDM Entry missing"," resourceId: " + task.getResource().getId() + " readingFieldId " + task.getReadingFieldId());
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "RDM Entry missing");
                                }

                                switch (meta.getInputTypeEnum()) {
                                    case CONTROLLER_MAPPED:
                                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Readings that are mapped with controller cannot be used.");
                                    case FORMULA_FIELD:
                                    case HIDDEN_FORMULA_FIELD:
                                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Readings that are mapped with formula field cannot be used.");
                                    case TASK:
                                        if (!pmExecution && !updatePM) {//Temp fix
                                            throw new RESTException(ErrorCode.VALIDATION_ERROR, readingField.getDisplayName() + " cannot be used as it is already used in another task.");
                                        }
                                    default:
                                        metaList.add(meta);
                                        break;
                                }
                            }
                            break;
//						case CHECKBOX:
                        case RADIO:
                            if (task.getReadingFieldId() != -1) {
                                task.setReadingField(modBean.getField(task.getReadingFieldId()));
                                if(task.getReadingField() != null && ((EnumField) task.getReadingField()).getValues().size() < 2) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Minimum two options has to be added for CHECKBOX/ RADIO task");
                                }
                            }
                            else {
                                if(task.getOptions() == null || task.getOptions().size() < 2) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Minimum two options has to be added for CHECKBOX/ RADIO task");
                                }
                            }
                            break;
                        case BOOLEAN:
                            if (task.getReadingFieldId() != -1) {
                                BooleanField field = (BooleanField) modBean.getField(task.getReadingFieldId());
                                task.setReadingField(field);
                                if(field != null &&( field.getTrueVal() == null || field.getFalseVal() == null)) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Both true and false valuse has to be set for BOOLEAN task");
                                }
                            }
                            else {
                                if (task.getOptions() == null || task.getOptions().size() != 2) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Both true and false valuse has to be set for BOOLEAN task");
                                }
                            }
                        default:
                            break;
                    }
                }
            }
            if (!metaList.isEmpty()) {
                context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, metaList);
                context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingDataMeta.ReadingInputType.TASK);
            }
            context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
            V3PreventiveMaintenanceAPI.updateTaskTemplateFromTaskContext(taskvsTemplateMap);
        }
        return false;
    }

    private static int getMaxUniqueIdFromTemplate (long templateId) throws Exception {
        int maxUniqueId = 0;
        V3WorkorderTemplate woTemplate = (V3WorkorderTemplate) TemplateAPI.getTemplate(templateId);
        if (woTemplate.getTaskTemplates() != null) {

            for(V3TaskTemplate template: woTemplate.getTaskTemplates()) {
                V3TaskContext task = template.getTask();
                if (task.getUniqueId() > maxUniqueId) {
                    maxUniqueId = task.getUniqueId();
                }
            }
//			maxUniqueId = woTemplate.getTaskTemplates().stream().map(taskTemplate -> taskTemplate.getTask()).mapToInt(TaskContext::getUniqueId).max().getAsInt();
        }
        return maxUniqueId;
    }

    private int getMaxUniqueIdFromExistingTasks (long parentId) throws Exception {
        int maxId = 0;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTasksModule().getTableName())
                .select(Collections.singletonList(FieldFactory.getField("uniqueId", "MAX(UNIQUE_ID)", FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getCondition("PARENT_TICKET_ID", "parentTicketId", String.valueOf(parentId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.get();
        if (props != null && !props.isEmpty()) {
            maxId = (int) props.get(0).get("uniqueId");
        }
        return maxId;
    }

    private static void validateSiteSpecificData(V3TaskContext task) throws Exception {
        long siteId = task.getSiteId();

        if (siteId == -1) {
            return;
        }

        if (task.getResource() != null && task.getResource().getId() != -1) {
            ResourceContext resource = ResourceAPI.getResource(task.getResource().getId());
            long resourceSiteId = -1;
            if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
                if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                    resourceSiteId = baseSpace.getId();
                } else {
                    resourceSiteId = baseSpace.getSiteId();
                }
            } else {
                AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false); //check deleted ?
                if (asset.getSpaceId() > 0) {
                    BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId());
                    if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                        resourceSiteId = baseSpace.getId();
                    } else {
                        resourceSiteId = baseSpace.getSiteId();
                    }
                }
            }

            if (resourceSiteId > 0) {
                if (resourceSiteId != siteId) {
                    if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Space does not belong in the Workorder request's Site.");
                    } else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Asset does not belong in the Workorder request's Site.");
                    }
                }
            }
        }
    }

}
