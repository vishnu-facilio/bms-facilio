package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.tasks.SectionInputOptionsContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.protocol.types.Field;

import java.util.*;
import java.util.stream.Collectors;

public class JobPlanAPI {

    public static void deleteJobPlanTasks(Long sectionId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        DeleteRecordBuilder<JobPlanTasksContext> deleteBuilder = new DeleteRecordBuilder<JobPlanTasksContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("TASK_SECTION_ID", "taskSection", String.valueOf(sectionId), NumberOperators.EQUALS));

        deleteBuilder.delete();

    }

    /**
     * Helper function to delete the JobPlan TaskInputOptions
     * -- Not using now, as TaskInputOptions in TASK_INPUT_OPTIONS_NEW table has `ON DELETE CASCADE` constraint added.
     * @param taskId
     * @throws Exception
     */
    public static void deleteJobPlanTaskInputOptions(Long taskId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule taskInputOptionsModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK_INPUT_OPTIONS);
        FacilioField taskIdField = FieldFactory.getJobPlanTaskInputOptionsFields().get("jobPlanTask");

        DeleteRecordBuilder<TaskInputOptionsContext> deleteBuilder = new DeleteRecordBuilder<TaskInputOptionsContext>()
                .module(taskInputOptionsModule)
                .table(taskInputOptionsModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(taskIdField.getColumnName(), taskIdField.getName(), String.valueOf(taskId), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }

    /**
     * Helper function to delete the JobPlan SectionInputOptions
     *
     * @param sectionId
     * @throws Exception
     */
    public static void deleteJobPlanSectionInputOptions(Long sectionId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule sectionInputOptionsModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION_INPUT_OPTIONS);
        FacilioField sectionIdField = FieldFactory.getJobPlanSectionInputOptionsFields().get("jobPlanSection");

        DeleteRecordBuilder<TaskInputOptionsContext> deleteBuilder = new DeleteRecordBuilder<TaskInputOptionsContext>()
                .module(sectionInputOptionsModule)
                .table(sectionInputOptionsModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(sectionIdField.getColumnName(), sectionIdField.getName(), String.valueOf(sectionId), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }

    public static List<JobPlanTaskSectionContext> setJobPlanDetails(long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<JobPlanTaskSectionContext> builder = new SelectRecordsBuilder<JobPlanTaskSectionContext>()
                .module(module)
                .beanClass(JobPlanTaskSectionContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobPlan"), String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER ASC");
        List<JobPlanTaskSectionContext> sections = builder.get();
        if (CollectionUtils.isNotEmpty(sections)) {
            for (JobPlanTaskSectionContext section : sections) {

                if (section.getInputTypeEnum().equals(TaskContext.InputType.RADIO)) {
                    List<Map<String, Object>> options = fetchSectionInputOptions(modBean, section);
                    if (options != null) {
                        section.setInputOptions(options);
                    }
                }

                List<JobPlanTasksContext> splitList = getTasks(section.getId());
                if (CollectionUtils.isNotEmpty(splitList)) {
//                    List<Map<String, Object>> mapList = FieldUtil.getAsMapList(splitList, JobPlanTasksContext.class);
//                    for (Map<String, Object> map : mapList) {
//                        map.values().removeAll(Collections.singleton(null));
//                    }
                    section.setTasks(splitList);
                }
            }
            return sections;
        }
        return null;
    }

    private static List<JobPlanTasksContext> getTasks(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<JobPlanTasksContext> builder = new SelectRecordsBuilder<JobPlanTasksContext>()
                .module(module)
                .beanClass(JobPlanTasksContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("taskSection"), String.valueOf(parentId), NumberOperators.EQUALS));

        List<JobPlanTasksContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {

            for(JobPlanTasksContext jobPlanTask : list) {
                if (jobPlanTask.getInputTypeEnum().equals(V3TaskContext.InputType.RADIO)) {
                    List<Map<String, Object>> optionsMap = fetchTaskInputOptions(modBean, jobPlanTask);
                    List<TaskInputOptionsContext> taskInputOptions = FieldUtil.getAsBeanListFromMapList(optionsMap, TaskInputOptionsContext.class);

                    List<String> options = taskInputOptions.stream().map(TaskInputOptionsContext::getValue).collect(Collectors.toList());
                    if (options != null) {
                        jobPlanTask.setOptions(options);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public static com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext getJobPlan(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        JobPlanContext jp = (JobPlanContext) V3RecordAPI.getRecord(module.getName(), id, JobPlanContext.class);
        List<JobPlanTaskSectionContext> taskSections = setJobPlanDetails(jp.getId());
//        jp.setTaskSectionList(taskSections);
        return jp;
    }

    public static Map<Integer, List<Long>> getResourceIds(long siteId) throws Exception {
        Map<Integer, List<Long>> resourceIdMap = new HashMap<>();
        List<Long> resourceIds = new ArrayList<>();
        List<FloorContext> floorList = SpaceAPI.getAllFloors(siteId);
        List<BuildingContext> buildingList = SpaceAPI.getAllBuildings(siteId);

        List<Long> floorIds = new ArrayList<>();
        for (FloorContext floor : floorList) {
            floorIds.add(floor.getId());
        }

        List<Long> buildingsIds = new ArrayList<>();
        for (BuildingContext building : buildingList) {
            buildingsIds.add(building.getId());
        }

        resourceIdMap.put(1, floorIds);
        resourceIdMap.put(2, buildingsIds);
        return resourceIdMap;

    }

    public static Map<String, List<TaskContext>> getTaskMapFromJobPlan(List<JobPlanTaskSectionContext> sections, Long woResourceId) throws Exception {
        Map<String, List<TaskContext>> taskMap = new LinkedHashMap<>();
        for (JobPlanTaskSectionContext jobPlanSection : sections) {
            List<Long> resourceIds = getMatchingResourceIdsForJobPlan(jobPlanSection.getJobPlanSectionCategory().intValue(), woResourceId, jobPlanSection.getSpaceCategory().getId(), jobPlanSection.getAssetCategory().getId(), -1L);

            Map<String, Integer> dupSectionNameCount = new HashMap<>();
            for (Long resourceId : resourceIds) {
                if (resourceId == null || resourceId < 0) {
                    continue;
                }
                ResourceContext sectionResource = ResourceAPI.getResource(resourceId);

                String sectionName = sectionResource.getName() + " - " + jobPlanSection.getName();

                if (taskMap.containsKey(sectionName)) {
                    Integer count = dupSectionNameCount.get(sectionName);
                    if (count == null) {
                        count = 0;
                    }
                    count = count + 1;
                    dupSectionNameCount.put(sectionName, count);
                    sectionName += sectionName + " - " + count;
                }

                Integer sectionCategory = jobPlanSection.getJobPlanSectionCategory();
                List<JobPlanTasksContext> tasks = jobPlanSection.getTasks();

                List<TaskContext> woTasks = new ArrayList<TaskContext>();
                for (JobPlanTasksContext jobPlanTask : tasks) {
                    if (jobPlanTask.getJobPlanTaskCategory() != null) {
                        List<Long> taskResourceIds = getMatchingResourceIdsForJobPlan(jobPlanTask.getJobPlanTaskCategory().intValue(), sectionResource.getId(), jobPlanTask.getSpaceCategory().getId(), jobPlanTask.getAssetCategory().getId(), -1L);
                        for (Long taskResourceId : taskResourceIds) {
                            if (sectionCategory == PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getIndex() || sectionCategory == PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getIndex()) {
                                if (ObjectUtils.compare(taskResourceId, resourceId) != 0) {
                                    continue;
                                }
                            }
                            ResourceContext taskResource = ResourceAPI.getResource(taskResourceId);

                            jobPlanTask.setResource(taskResource);
                            jobPlanTask.setSiteId(taskResource.getSiteId());
                            TaskContext t = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(jobPlanTask), TaskContext.class);
                            woTasks.add(t);
                        }
                    } else {
                        jobPlanTask.setSiteId(sectionResource.getSiteId());
                        TaskContext t = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(jobPlanTask), TaskContext.class);
                        woTasks.add(t);
                    }


                }
                taskMap.put(sectionName, woTasks);
            }
        }
        return taskMap;
    }

    public static List<Long> getMatchingResourceIdsForJobPlan(Integer category, Long resourceId, Long spaceCategoryID, Long assetCategoryID, Long currentAssetId) throws Exception {

        List<Long> selectedResourceIds = new ArrayList<>();
        switch (category) {
            case 8:
                selectedResourceIds.addAll(Collections.singletonList(resourceId));
                break;
            case 7:
                List<BaseSpaceContext> siteBuildingsWithFloors = SpaceAPI.getSiteBuildingsWithFloors(resourceId);
                for (BaseSpaceContext building : siteBuildingsWithFloors) {
                    selectedResourceIds.add(building.getId());
                }
                break;
            case 1:
                List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(resourceId);
                for (BaseSpaceContext floor : floors) {
                    selectedResourceIds.add(floor.getId());
                }
                break;
            case 3:
                List<SpaceContext> spaces = SpaceAPI.getSpaceListOfCategory(resourceId, spaceCategoryID);
                for (SpaceContext space : spaces) {
                    selectedResourceIds.add(space.getId());
                }
                break;
            case 4:
                List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryID, resourceId);
                for (AssetContext asset : assets) {
                    selectedResourceIds.add(asset.getId());
                }
                break;
            case 5:
                selectedResourceIds.addAll(Collections.singletonList(resourceId));
                break;
            case 6:
                selectedResourceIds.addAll(Collections.singletonList(currentAssetId));
                break;
            default:
                break;
        }
        return selectedResourceIds;
    }

    public static JobPlanContext getJobPlanForPMTrigger(Long pmTriggerId) throws Exception {

        FacilioModule module = ModuleFactory.getPMJobPlanTriggersV3Module();
        List<FacilioField> fields = FieldFactory.getPMJobPlanTriggerV3Fields();
        Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("triggerId"), String.valueOf(pmTriggerId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Long pmJobPlanID = (Long) props.get(0).get("pmjobPlanId");
            PMJobPlanContextV3 pmJobPlan = getPMJobPlan(pmJobPlanID);
            if (pmJobPlan != null) {
                Long jobPlanId = pmJobPlan.getJobPlanId();
                JobPlanContext jobPlan = (JobPlanContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.JOB_PLAN, jobPlanId, JobPlanContext.class);
                if (jobPlan != null) {
                    JobPlanContext fetchedJp = getJobPlan(jobPlan.getId());
                    return fetchedJp;
                }
            }
        }
        return null;
    }

    public static PMJobPlanContextV3 getPMJobPlan(Long id) throws Exception {

        FacilioModule module = ModuleFactory.getPMJobPlanV3Module();
        List<FacilioField> fields = FieldFactory.getPMJobPlanV3Fields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PMJobPlanContextV3.class).get(0);
        }
        return null;
    }

    public static List<PMJobPlanContextV3> getPMForJobPlanId(Long jobPlanId) throws Exception {

        FacilioModule module = ModuleFactory.getPMJobPlanV3Module();
        List<FacilioField> fields = FieldFactory.getPMJobPlanV3Fields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("JOB_PLAN_ID", "jobPlanId", String.valueOf(jobPlanId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, PMJobPlanContextV3.class);
        }
        return null;
    }

    public static JobPlanContext getJobPlanForWorkOrder(WorkOrderContext wo) throws Exception {
        if (wo.getPm() != null && wo.getTrigger() != null) {
            return JobPlanAPI.getJobPlanForPMTrigger(wo.getTrigger().getId());
        } else if (wo.getJobPlan() != null) {
            return wo.getJobPlan();
        }
        return null;
    }

    public static Map<String, List<TaskContext>> getJobPlanTasksForWo(WorkOrderContext workOrder) throws Exception {

        JobPlanContext jobPlan = getJobPlanForWorkOrder(workOrder);
        Map<String, List<TaskContext>> taskMap = new HashMap<>();
        if (jobPlan != null) {
            List<JobPlanTaskSectionContext> taskSections = null;
            // List<JobPlanTaskSectionContext> taskSections = jobPlan.getTaskSectionList();
            if (CollectionUtils.isNotEmpty(taskSections)) {
                Map<String, List<TaskContext>> jobPlanTasks = JobPlanAPI.getTaskMapFromJobPlan(taskSections, workOrder.getResource().getId());
                taskMap.putAll(jobPlanTasks);
                return taskMap;
            }
        }
        return null;
    }

    /**
     * Methods with {@link V3WorkOrderContext}
     * @param workOrderContext 
     */

    public static Map<String, List<V3TaskContext>> getScopedTasksForWo(JobPlanContext jobPlan, Boolean isPreRequest, V3WorkOrderContext workOrderContext) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        LinkedHashMap<String, List<V3TaskContext>> allTasks = new LinkedHashMap<>();

        List<JobPlanTaskSectionContext> initalTaskSections = setJobPlanDetails(jobPlan.getId());
        
        List<JobPlanTaskSectionContext> updatedTaskSections = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        
        
        if (initalTaskSections != null) {
            for (JobPlanTaskSectionContext initalTaskSection : initalTaskSections) {

                List<ResourceContext> sectionResourceList = BulkResourceAllocationUtil.getMultipleResourceToBeAddedFromPM(initalTaskSection.getJobPlanSectionCategoryEnum(), Collections.singletonList(workOrderContext.getResource().getId()), initalTaskSection.getSpaceCategory() == null ? null :  initalTaskSection.getSpaceCategory().getId(), initalTaskSection.getAssetCategory() == null ? null :  initalTaskSection.getAssetCategory().getId(), null, null, false);

                if(CollectionUtils.isNotEmpty(sectionResourceList)) {

                    for(ResourceContext sectionResource : sectionResourceList) {

                        JobPlanTaskSectionContext updatedTaskSection = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(initalTaskSection), JobPlanTaskSectionContext.class);
                        updatedTaskSection.setName(updatedTaskSection.getName() + " - " + sectionResource.getName());
                        updatedTaskSection.setResource(sectionResource);
                        updatedTaskSections.add(updatedTaskSection);
                        sectionNameList.add(updatedTaskSection.getName());  // update sectionName in sectionNameList
                    }
                }
           }

            workOrderContext.setSectionNameList(sectionNameList); // update sectionNameList in workorder

            int taskUniqueId = 1;
            if(CollectionUtils.isNotEmpty(updatedTaskSections)) {

                for (JobPlanTaskSectionContext sectionContext : updatedTaskSections) {

                    if (sectionContext.getTasks() != null) {

                        List<V3TaskContext> tasks = new ArrayList<>();

                        for (JobPlanTasksContext jobPlanTask : sectionContext.getTasks()) {

                            List<ResourceContext> taskResourceList = BulkResourceAllocationUtil.getMultipleResourceToBeAddedFromPM(jobPlanTask.getJobPlanTaskCategoryEnum(), Collections.singletonList(sectionContext.getResource().getId()), jobPlanTask.getSpaceCategory() == null ? null :  jobPlanTask.getSpaceCategory().getId(), jobPlanTask.getAssetCategory() == null ? null :  jobPlanTask.getAssetCategory().getId(), null, null, false);

                            if(CollectionUtils.isNotEmpty(taskResourceList)) {

                                for (ResourceContext taskResource : taskResourceList) {
                                    V3TaskContext task = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(jobPlanTask), V3TaskContext.class);
                                    task.setResource(taskResource);
                                    task.setUniqueId(taskUniqueId++);
                                    tasks.add(task);
                                }
                            }
                        }
                        
                        String sectionName = sectionContext.getName();
                        
                        if(CollectionUtils.isNotEmpty(tasks)) {
                            for(V3TaskContext task : tasks) {
                                if (isPreRequest) {
                                    task.setPreRequest(true);
                                    task.setInputType(V3TaskContext.InputType.BOOLEAN.getVal());
                                }
                            }
                        }

                        if (sectionName != null && CollectionUtils.isNotEmpty(tasks)) {
                            allTasks.put(sectionName, tasks);
                        }
                    }

                }

            }
        }
        return allTasks;
    }

    /**
     * Helper function to fetch the JobPlan TaskInputOptions
     *
     * @param modBean
     * @param task
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> fetchTaskInputOptions(ModuleBean modBean, JobPlanTasksContext task) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK_INPUT_OPTIONS);
        SelectRecordsBuilder<TaskInputOptionsContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanTaskInputOptionsFields().values());
        selectRecordsBuilder
                .module(module)
                .table(module.getTableName())
                .beanClass(TaskInputOptionsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getJobPlanTaskInputOptionsFields().get("jobPlanTask"),
                        String.valueOf(task.getId()), NumberOperators.EQUALS));

        return selectRecordsBuilder.getAsProps();
    }

    /**
     * Helper function to fetch the JobPlan SectionInputOptions
     *
     * @param modBean
     * @param section
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> fetchSectionInputOptions(ModuleBean modBean, JobPlanTaskSectionContext section) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION_INPUT_OPTIONS);
        SelectRecordsBuilder<SectionInputOptionsContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanSectionInputOptionsFields().values());
        selectRecordsBuilder
                .module(module)
                .table(module.getTableName())
                .beanClass(SectionInputOptionsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getJobPlanSectionInputOptionsFields().get("jobPlanSection"),
                        String.valueOf(section.getId()), NumberOperators.EQUALS));

        return selectRecordsBuilder.getAsProps();
    }
    public static List<JobPlanContext> getJobPlanFromGroupId(long groupId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<JobPlanContext> builder = new SelectRecordsBuilder<JobPlanContext>()
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .beanClass(JobPlanContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("group"),String.valueOf(groupId),NumberOperators.EQUALS))
                .orderBy(fieldMap.get("jobPlanVersion").getCompleteColumnName()+ " DESC");
        return builder.get();
    }
    public static Map<String,Object> getJobPlanGroupAndVersion(long jobPlanId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        List<FacilioField> fieldList = new ArrayList<>();

        fieldList.add(fieldMap.get("group"));
        fieldList.add(fieldMap.get("jobPlanVersion"));

        SelectRecordsBuilder<JobPlanContext> builder = new SelectRecordsBuilder<JobPlanContext>()
                .module(module)
                .select(fieldList)
                .beanClass(JobPlanContext.class)
                .andCondition(CriteriaAPI.getIdCondition(jobPlanId,module));
        return builder.getAsProps().get(0);
    }
}
