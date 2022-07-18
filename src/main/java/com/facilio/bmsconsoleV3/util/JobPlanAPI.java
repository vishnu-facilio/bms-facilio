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
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public class JobPlanAPI {

    public static void deleteJobPlanTasks(Long sectionId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        DeleteRecordBuilder<JobPlanTasksContext> deleteBuilder = new DeleteRecordBuilder<JobPlanTasksContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("TASK_SECTION_ID", "taskSection", String.valueOf(sectionId), NumberOperators.EQUALS));

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
                .orderBy("ID asc");
        List<JobPlanTaskSectionContext> sections = builder.get();
        if (CollectionUtils.isNotEmpty(sections)) {
            for (JobPlanTaskSectionContext section : sections) {
                List<JobPlanTasksContext> splitList = getTasks(section.getId());
                if (CollectionUtils.isNotEmpty(splitList)) {
                    List<Map<String, Object>> mapList = FieldUtil.getAsMapList(splitList, JobPlanTasksContext.class);
                    for (Map<String, Object> map : mapList) {
                        map.values().removeAll(Collections.singleton(null));
                    }
                    section.setTasks(mapList);
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
            List<Long> resourceIds = getMatchingResourceIdsForJobPlan(jobPlanSection.getJobPlanSectionCategory().intValue(), woResourceId, jobPlanSection.getSpaceCategoryId(), jobPlanSection.getAssetCategoryId(), -1L);

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
                List<JobPlanTasksContext> tasks = FieldUtil.getAsBeanListFromMapList(jobPlanSection.getTasks(), JobPlanTasksContext.class);

                List<TaskContext> woTasks = new ArrayList<TaskContext>();
                for (JobPlanTasksContext jobPlanTask : tasks) {
                    if (jobPlanTask.getJobPlanTaskCategory() != null) {
                        List<Long> taskResourceIds = getMatchingResourceIdsForJobPlan(jobPlanTask.getJobPlanTaskCategory().intValue(), sectionResource.getId(), jobPlanTask.getSpaceCategoryId(), jobPlanTask.getAssetCategoryId(), -1L);
                        for (Long taskResourceId : taskResourceIds) {
                            if (sectionCategory == JobPlanTaskSectionContext.JobPlanSectionCategory.ASSET_CATEGORY.getIndex() || sectionCategory == JobPlanTaskSectionContext.JobPlanSectionCategory.SPACE_CATEGORY.getIndex()) {
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
     */

    public static Map<String, List<V3TaskContext>> getTasksForWo(JobPlanContext jobPlan, Boolean isPreRequest) throws Exception {

        Map<String, List<V3TaskContext>> allTasks = new HashMap<>();

        List<JobPlanTaskSectionContext> taskSections = setJobPlanDetails(jobPlan.getId());
        if (taskSections != null) {
            for (JobPlanTaskSectionContext sectionContext : taskSections) {
                // for each JobPlanTaskSectionContext
                if (sectionContext.getTasks() != null) {

                    // get tasks from JobPlanTaskSectionContext
                    List<Map<String, Object>> tasksMappings = sectionContext.getTasks(); // assuming Map<String, List<V3TaskContext>>
                    List<V3TaskContext> tasks = new ArrayList<>();

                    String sectionName = sectionContext.getName();
                    int taskUniqueId = 1;

                    for (Map<String, Object> taskMap : tasksMappings) {
                        V3TaskContext task = FieldUtil.getAsBeanFromMap(taskMap, V3TaskContext.class);
                        if (task != null) {
                            task.setUniqueId(taskUniqueId++);

                            if (!isPreRequest) {
                                tasks.add(task);  // adding tasks
                            } else {
                                task.setPreRequest(true);
                                task.setInputType(V3TaskContext.InputType.BOOLEAN.getVal());
                                tasks.add(task); // adding prerequisites
                            }
                        }
                    }
                    if (sectionName != null && tasks.size() > 0) {
                        allTasks.put(sectionName, tasks);
                    }
                }
            }
        }
        return allTasks;
    }
}
