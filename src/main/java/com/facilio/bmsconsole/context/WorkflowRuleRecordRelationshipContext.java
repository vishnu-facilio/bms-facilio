package com.facilio.bmsconsole.context;

import com.facilio.v3.context.V3Context;

public class WorkflowRuleRecordRelationshipContext extends V3Context {

    private Long recordId;
    public Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    private Long ruleId;
    public Long getRuleId() {
        return ruleId;
    }
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    private Long dateFieldValue;
    public Long getDateFieldValue() {
        return dateFieldValue;
    }
    public void setDateFieldValue(Long dateFieldValue) {
        this.dateFieldValue = dateFieldValue;
    }

    private Long executionTime;
    public Long getExecutionTime() {
        return executionTime;
    }
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public enum EventType {

        CREATE(1, "CREATE"),
        PATCH(2, "PATCH"),
        DELETE(3, "DELETE");

        int intVal;
        String name;

        public int getIntVal() {
            return intVal;
        }

        public String getName() {
            return name;
        }

        private EventType(int intVal, String name) {
            this.intVal = intVal;
            this.name = name;
        }
    }

}
