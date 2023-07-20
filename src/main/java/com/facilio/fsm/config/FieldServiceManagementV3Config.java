package com.facilio.fsm.config;


import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount15;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount30_BS2;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.*;
import com.facilio.fsm.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;

import java.util.function.Supplier;

@Config
public class FieldServiceManagementV3Config {

    @Module("workType")
    public static Supplier<V3Config> getWorkType() {
        return () -> new V3Config(WorkTypeContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadWorkTypeLookupsCommandV3())
                .summary()
                .delete()
                .build();
    }
    @Module("serviceSkill")
    public static Supplier<V3Config> getServiceSkill() {
        return () -> new V3Config(ServiceSkillsContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("serviceTask")
    public static Supplier<V3Config> getServiceTask() {
        return () -> new V3Config(ServiceTaskContext.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(new FsmTransactionChainFactoryV3().getTaskBeforeSaveChain())
                .afterSave(new FsmTransactionChainFactoryV3().getTaskAfterSaveChain())
                .update()
                .beforeSave(new FsmTransactionChainFactoryV3().getTaskBeforeUpdateChain())
                .afterSave(new FsmTransactionChainFactoryV3().getTaskAfterUpdateChain())
                .list()
                .beforeFetch(new LoadServiceTaskLookupCommandV3())
                .summary()
                .delete()
                .build();
    }
    @Module("serviceOrder")
    public static Supplier<V3Config> getServiceOrder() {
        return () -> new V3Config(ServiceOrderContext.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getServiceOrderBeforeSaveCreateChain())
                .afterSave(new FsmTransactionChainFactoryV3().afterSOCreateChain())
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getSOBeforeUpdateChain())
                .afterSave(new ConstructUpdateCustomActivityCommandV3(),
                        new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY))
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS)
    public static Supplier<V3Config> getServiceOrderPlannedItems() {
        return () -> new V3Config(ServiceOrderPlannedItemsContext.class, null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS)
    public static Supplier<V3Config> getServiceOrderPlannedTools() {
        return () -> new V3Config(ServiceOrderPlannedToolsContext.class, null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES)
    public static Supplier<V3Config> getServiceOrderPlannedServices() {
        return () -> new V3Config(ServiceOrderPlannedServicesContext.class, null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeOff.TIME_OFF)
    public static Supplier<V3Config> getTimeOff(){
        return () -> new V3Config(TimeOffContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .update()
                .list()
                .summary()
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.LocationHistory.LOCATION_HISTORY)
    public static Supplier<V3Config> getLocationHistory(){
        return () -> new V3Config(V3LocationHistoryContext.class,null)
                .create()
                .afterSave(new updatePeopleLocationHistoryCommand())
                .build();
    }
    @Module(FacilioConstants.Territory.TERRITORY)
    public static Supplier<V3Config> getTerritory(){
        return () -> new V3Config(TerritoryContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .update()
                .list()
                .summary()
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)
    public static Supplier<V3Config> getServiceAppointment(){
        return () -> new V3Config(ServiceAppointmentContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .beforeSave(new rollupServiceAppointmentFieldsCommand())
//                .afterSave(new rollUpServiceTaskCommand())
                .update()
                .beforeSave(new rollupServiceAppointmentFieldsCommand())
//                .afterSave(new rollUpServiceTaskCommand())
                .list()
                .beforeFetch(new FetchServiceAppointmentSupplementsCommand())
                .summary()
                .beforeFetch(new FetchServiceAppointmentSupplementsCommand())
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL)
    public static Supplier<V3Config> getPeopleSkillLevel() {
        return () -> new V3Config(PeopleSkillLevelContext.class, null)
                .create()
                .list()
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeSheet.TIME_SHEET)
    public static Supplier<V3Config> getTimeSheet(){
        return () -> new V3Config(TimeSheetContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .update()
                .list()
                .summary()
                .pickList()
                .delete()
                .build();
    }
    @Module(FacilioConstants.Trip.TRIP)
    public static Supplier<V3Config> getTrip(){
        return () -> new V3Config(TripContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }

}
