package com.facilio.trigger.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleTriggerRecordRelationContext extends V3Context {
    private Long recordId;
    private Long triggerId;
    private Long dateFieldValue;
    private Long executionTime;

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
