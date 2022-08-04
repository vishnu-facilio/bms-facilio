package com.facilio.apiv3;

import com.facilio.bmsconsole.commands.BeforeSavePMPlannerCommand;
import com.facilio.bmsconsole.commands.PMBeforeCreateCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.commands.PMFetchSupplements;
import com.facilio.bmsconsoleV3.commands.PMPlannerSupplementsCommand;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

@Config
public class PlannedMaintenanceV3Config {
    @Module("plannedmaintenance")
    public static Supplier<V3Config> getPlannedMaintenance() {
        return () -> new V3Config(PlannedMaintenance.class, null)
                .update()
                .beforeSave(new PMBeforeCreateCommand())
                .create()
                .beforeSave(new PMBeforeCreateCommand())
                .delete()
                .list()
                .summary()
                    .beforeFetch(new PMFetchSupplements())
                .build();
    }

    @Module("pmTriggerV2")
    public static Supplier<V3Config> getPMTriggers() {
        return () -> new V3Config(PMTriggerV2.class, null)
                .update()
                .create()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("pmJobPlan")
    public static Supplier<V3Config> getPMJobPlan() {
        return () -> new V3Config(PMJobPlan.class, null)
                .update()
                .create()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("pmPlanner")
    public static Supplier<V3Config> getPMPlanner() {
        return () -> new V3Config(PMPlanner.class, null)
                .update()
                    .beforeSave(new BeforeSavePMPlannerCommand())
                .create()
                .delete()
                .list()
                .summary()
                .build();
    }

    @Module("pmResourcePlanner")
    public static Supplier<V3Config> getPmResourcePlanner() {
        return () -> new V3Config(PMResourcePlanner.class, null)
                .update()
                .create()
                .delete()
                .list()
                .beforeFetch(new PMPlannerSupplementsCommand())
                .summary()
                .build();
    }
}
