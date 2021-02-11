package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.modules.FieldUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

public class PMJobPlanContextV3 implements Serializable {

    private static final long serialVersionUID = 1L;
    long id;
    long orgId;
    long pmId;
    long jobPlanId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getPmId() {
        return pmId;
    }

    public void setPmId(long pmId) {
        this.pmId = pmId;
    }

    public long getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(long jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    private JobPlanContext jobPlanContext;

    public JobPlanContext getJobPlanContext() {
        return jobPlanContext;
    }

    public void setJobPlanContext(JobPlanContext jobPlanContext) {
        this.jobPlanContext = jobPlanContext;
    }

    public List<TaskSectionTemplate> prepareAndGetJobPlanSections() throws Exception {

        List<JobPlanTaskSectionContext> sections = jobPlanContext.getTaskSectionList();
        List<TaskSectionTemplate> secTemplateList = new ArrayList<>();

       if(CollectionUtils.isNotEmpty(sections)) {
           Map<String, List<TaskContext>> taskMap = new LinkedHashMap<>();
           for(JobPlanTaskSectionContext ts : sections) {
               TaskSectionTemplate taskSectionTemplate = new TaskSectionTemplate();
               taskSectionTemplate.setAssignmentType(ts.getJobPlanSectionCategory());
               taskSectionTemplate.setAssetCategoryId(ts.getAssetCategoryId());
               taskSectionTemplate.setSpaceCategoryId(ts.getSpaceCategoryId());
               taskSectionTemplate.setSequenceNumber(ts.getSequenceNumber());
               taskSectionTemplate.setName(ts.getName());
               if(CollectionUtils.isNotEmpty(ts.getTasks())) {
                   List<JobPlanTasksContext> jobPlanTasks = FieldUtil.getAsBeanListFromMapList(ts.getTasks(), JobPlanTasksContext.class);
                   for(JobPlanTasksContext jpTask : jobPlanTasks) {
                       TaskTemplate taskTemplate = new TaskTemplate();
                       taskTemplate.setAssignmentType(jpTask.getJobPlanTaskCategory());
                       taskTemplate.setTask(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(jpTask), TaskContext.class));
                       if(CollectionUtils.isNotEmpty(taskSectionTemplate.getTaskTemplates())) {
                          List<TaskTemplate> list = new ArrayList<>();
                          list.addAll(taskSectionTemplate.getTaskTemplates());
                          list.add(taskTemplate);
                           taskSectionTemplate.setTaskTemplates(list);
                       }
                       else {
                           taskSectionTemplate.setTaskTemplates(Collections.singletonList(taskTemplate));
                       }
                   }

               }
               secTemplateList.add(taskSectionTemplate);
           }
       }

        return secTemplateList;
    }

    private List<PMTriggerContext> jpPmTriggers;

    public List<PMTriggerContext> getJpPmTriggers() {
        return jpPmTriggers;
    }

    public void setJpPmTriggers(List<PMTriggerContext> jpPmTriggers) {
        this.jpPmTriggers = jpPmTriggers;
    }
}
