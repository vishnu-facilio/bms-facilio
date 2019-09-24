package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkWorkOrderContext {
    private List<WorkOrderContext> workOrderContexts;
    private List<Map<String, List<TaskContext>>> taskMaps;
    private List<List<AttachmentContext>> attachments;
    private List<Map<String, List<TaskContext>>> preRequestMaps;
    private Map<Long, Map<String, TaskSectionContext>> sectionMap;
    private Map<Long, Map<String, TaskSectionContext>> prerequisiteSectionMap;
    private List<TaskContext> taskContextList;
    private List<TaskContext> prerequisiteContextList;
    private List<Long> nextExecutionTimes;

    public BulkWorkOrderContext(List<BulkWorkOrderContext> bulkWorkOrderContexts) {
        this();
        for (BulkWorkOrderContext bulkWorkOrderContext: bulkWorkOrderContexts) {
            this.workOrderContexts.addAll(bulkWorkOrderContext.workOrderContexts);
            this.taskMaps.addAll(bulkWorkOrderContext.taskMaps);
            this.attachments.addAll(bulkWorkOrderContext.attachments);
            this.preRequestMaps.addAll(bulkWorkOrderContext.preRequestMaps);
        }
    }

    public BulkWorkOrderContext() {
        this.workOrderContexts = new ArrayList<>();
        this.taskMaps = new ArrayList<>();
        this.preRequestMaps = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.sectionMap = new HashMap<>();
        this.prerequisiteSectionMap = new HashMap<>();
        this.taskContextList = new ArrayList<>();
        this.prerequisiteContextList = new ArrayList<>();
    }

    public void addContexts(WorkOrderContext workOrderContexts, Map<String, List<TaskContext>> taskMap, Map<String, List<TaskContext>> preRequestMap, List<AttachmentContext> attachment) {
        this.workOrderContexts.add(workOrderContexts);
        this.taskMaps.add(taskMap);
        this.preRequestMaps.add(preRequestMap);
        this.attachments.add(attachment);
    }

    public List<WorkOrderContext> getWorkOrderContexts() {
        return workOrderContexts;
    }

    public void setWorkOrderContexts(List<WorkOrderContext> workOrderContexts) {
        this.workOrderContexts = workOrderContexts;
    }

    public List<Map<String, List<TaskContext>>> getTaskMaps() {
        return taskMaps;
    }

    public void setTaskMaps(List<Map<String, List<TaskContext>>> taskMaps) {
        this.taskMaps = taskMaps;
    }

    public List<List<AttachmentContext>> getAttachments() {
        return attachments;
    }

    public void setAttachements(List<List<AttachmentContext>> attachments) {
        this.attachments = attachments;
    }

    public List<Map<String, List<TaskContext>>> getPreRequestMaps() {
        return preRequestMaps;
    }

    public void setPreRequestMaps(List<Map<String, List<TaskContext>>> preRequestMaps) {
        this.preRequestMaps = preRequestMaps;
    }



    public void removeElements(List<Integer> indices) {
        Collections.sort(indices);
        for (int i = indices.size() - 1; i >= 0; i--) {
            int index = indices.get(i);
            this.workOrderContexts.remove(index);
            this.taskMaps.remove(index);
            this.preRequestMaps.remove(index);
        }
    }

    public Map<Long, Map<String, TaskSectionContext>> getSectionMap() {
        return sectionMap;
    }

    public void setSectionMap(Map<Long, Map<String, TaskSectionContext>> sectionMap) {
        this.sectionMap = sectionMap;
    }

    public List<TaskContext> getPrerequisiteContextList() {
		return prerequisiteContextList;
	}

	public void setPrerequisiteContextList(List<TaskContext> prerequisiteContextList) {
		this.prerequisiteContextList = prerequisiteContextList;
	}

	public List<TaskContext> getTaskContextList() {
        return taskContextList;
    }

    public void setTaskContextList(List<TaskContext> taskContextList) {
        this.taskContextList = taskContextList;
    }

    public List<Long> getNextExecutionTimes() {
        return nextExecutionTimes;
    }

    public void setNextExecutionTimes(List<Long> nextExecutionTimes) {
        this.nextExecutionTimes = nextExecutionTimes;
    }

	public Map<Long, Map<String, TaskSectionContext>> getPrerequisiteSectionMap() {
		return prerequisiteSectionMap;
	}

	public void setPrerequisiteSectionMap(Map<Long, Map<String, TaskSectionContext>> prerequisiteSectionMap) {
		this.prerequisiteSectionMap = prerequisiteSectionMap;
	}
    
}
