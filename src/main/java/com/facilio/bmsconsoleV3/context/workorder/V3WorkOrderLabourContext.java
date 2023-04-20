package com.facilio.bmsconsoleV3.context.workorder;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter@Setter
public class V3WorkOrderLabourContext extends V3Context {

    private double cost = -1;
    private Map<String,Object> totalAmount;
    private Map<String,Object> rate;
    private Long startTime;
    private Long endTime ;
    private Long duration ;
    private Long parentId;
    private LabourContextV3 labour;
    private V3WorkOrderContext parent;
    private CraftContext craft;
    private SkillsContext skill;
    private Integer type;

//    public void calculate() {
//        if (this.duration == -1) {
//            if (this.endTime == -1 || this.startTime == -1) {
//                this.duration = 0;
//            } else {
//                this.duration = this.endTime - this.startTime;
//            }
//        }
//
//        if (this.labour != null) {
//            this.cost = (this.duration / 1000.0 / 60 / 60) * this.labour.getCost();
//        }
//    }

}
