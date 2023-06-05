package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;
import lombok.Getter;
import lombok.Setter;


/**
 * WorkOrderSettings POJO class holds various objects that defines the settings for workorder module.
 * We have the below mentioned WorkOrderFeatureSetting,
 * - executeTask        -> Actions [open/close, add remark, add input, add remarks] in Tasks Tab
 * - manageTask         -> Add Task action in Tasks Tab
 * - inventoryPlaning   -> All actions in Plans Tab
 * - inventoryActuals   -> All actions in Actuals Tab
 */
@Getter
@Setter
public class WorkOrderSettings {
    WorkOrderFeatureSetting executeTask;
    WorkOrderFeatureSetting manageTask;
    WorkOrderFeatureSetting inventoryPlaning;
    WorkOrderFeatureSetting inventoryActuals;



    public WorkOrderSettings() {
        this.executeTask = new WorkOrderFeatureSetting();
        this.manageTask = new WorkOrderFeatureSetting();
        this.inventoryPlaning = new WorkOrderFeatureSetting();
        this.inventoryActuals = new WorkOrderFeatureSetting();
    }
}
