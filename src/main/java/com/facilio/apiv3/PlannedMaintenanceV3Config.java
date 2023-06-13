package com.facilio.apiv3;

import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.commands.*;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.DeletePPMPreOpenWorkorders;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.FetchPMDetailsFromPmResourcePlanner;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount50;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

@Config
public class PlannedMaintenanceV3Config {
    @Module("plannedmaintenance")
    public static Supplier<V3Config> getPlannedMaintenance() {
        return () -> new V3Config(PlannedMaintenance.class, new ModuleCustomFieldCount50())
                .update()
                .beforeSave(TransactionChainFactoryV3.PMV2BeforeUpdateChain())
                .afterSave(TransactionChainFactoryV3.PPMAfterPatchChain())
                .create()
                .beforeSave(TransactionChainFactoryV3.PMV2BeforeSaveChain())
                .delete()
                .afterDelete(new DeletePPMPreOpenWorkorders())
                .list()
                .beforeFetch(ReadOnlyChainFactoryV3.getPPMBeforeListFetchChain())
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

//    @Module("pmJobPlan")
//    public static Supplier<V3Config> getPMJobPlan() {
//        return () -> new V3Config(PMJobPlan.class, null)
//                .update()
//                .beforeSave(new PrefillPMJobPlanfields(), new ValidationForJobPlanCategory())
//                .afterSave(TransactionChainFactoryV3.getUpdateJobPlanAfterSaveChain())
//                .create()
//                .beforeSave(new ValidationForJobPlanCategory())
//                .afterSave(TransactionChainFactoryV3.getCreateJobPlanAfterSaveChain())
//                .delete()
//                .list()
//                .beforeFetch(new FetchJobPlanLookupCommand())
//                .summary()
//                .beforeFetch(new FetchJobPlanLookupCommand())
//                .afterFetch(new FillJobPlanDetailsCommand())
//                .build();
//    }

    @Module(FacilioConstants.PM_V2.PM_V2_PLANNER)
    public static Supplier<V3Config> getPMPlanner() {
        return () -> new V3Config(PMPlanner.class, null)
                .update()
                    .beforeSave(TransactionChainFactoryV3.getPMPlannerBeforeUpdateCommand())
                    .afterSave(TransactionChainFactoryV3.PMPlannerAfterUpdateChain())
                .create()
                .afterSave(TransactionChainFactoryV3.getPmV2PlannerAfterCreateChain())
                .delete()
                .afterDelete(TransactionChainFactoryV3.PMPlannerAfterDeleteChain())
                .list()
                .beforeFetch(new PMPlannerSupplementsCommand())
                .summary()
                    .beforeFetch(new PMPlannerSupplementsCommand())
                    .afterFetch(new SetTimelineViewContextCommand())
                .build();
    }

    @Module(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER)
    public static Supplier<V3Config> getPmResourcePlanner() {
        return () -> new V3Config(PMResourcePlanner.class, null)
                .update()
                .beforeSave(new PMResourcePlannerBeforeSaveCommand())
                .create()
                .beforeSave(new PMResourcePlannerBeforeSaveCommand())
                .afterSave(TransactionChainFactoryV3.getPmV2ResourcePlannerAfterCreateChain())
                .delete()
                .list()
                .beforeFetch(new PMResourcePlannerSupplementsCommand())
                .summary()
                .build();
    }
}
