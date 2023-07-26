package com.facilio.fsm.config;


import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount15;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount30_BS2;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.*;
import com.facilio.fsm.commands.people.FetchLocationHistorySupplements;
import com.facilio.fsm.commands.people.FetchPeopleSkillLevelSupplementsCommand;
import com.facilio.fsm.commands.people.FetchPeopleTerritorySupplementsCommand;
import com.facilio.fsm.commands.people.updatePeopleLocationHistoryCommand;
import com.facilio.fsm.commands.serviceAppointment.FetchServiceAppointmentSupplementsCommand;
import com.facilio.fsm.commands.serviceOrders.SetServiceTaskCommandV3;
import com.facilio.fsm.commands.serviceTasks.LoadTaskPlansCommandV3;
import com.facilio.fsm.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

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
                .afterFetch(new LoadWorkTypeLineItemsCommandV3())
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
                .beforeFetch(new LoadServiceTaskLookupCommandV3())
                .afterFetch(new LoadTaskPlansCommandV3())
                .delete()
                .build();
    }
    @Module("serviceOrder")
    public static Supplier<V3Config> getServiceOrder() {
        return () -> new V3Config(ServiceOrderContext.class, new ModuleCustomFieldCount15())
                .create()
                .beforeSave(FsmTransactionChainFactoryV3.getSOBeforeSaveCreateChain())
                .afterSave(FsmTransactionChainFactoryV3.afterSOCreateChain())
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getSOBeforeUpdateChain())
                .afterSave(FsmTransactionChainFactoryV3.afterSOUpdateChain())
                .list()
                .summary()
                .afterFetch(new SetServiceTaskCommandV3())
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
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY))
                .update()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeOff.TIME_OFF_ACTIVITY))
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
                .list()
                .beforeFetch(new FetchLocationHistorySupplements())
                .build();
    }
    @Module(FacilioConstants.Territory.TERRITORY)
    public static Supplier<V3Config> getTerritory(){
        return () -> new V3Config(TerritoryContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Territory.TERRITORY_ACTIVITY))
                .update()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Territory.TERRITORY_ACTIVITY))
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
                .beforeSave(FsmTransactionChainFactoryV3.getServiceAppointmentBeforeCreateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY))
                .afterSave(FsmTransactionChainFactoryV3.getServiceAppointmentAfterCreateChain())
                .update()
                .beforeSave(FsmTransactionChainFactoryV3.getServiceAppointmentBeforeUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY))
                .afterSave(FsmTransactionChainFactoryV3.getServiceAppointmentAfterUpdateChain())
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
                .beforeFetch(new FetchPeopleSkillLevelSupplementsCommand())
                .summary()
                .beforeFetch(new FetchPeopleSkillLevelSupplementsCommand())
                .delete()
                .build();
    }
    @Module(FacilioConstants.TimeSheet.TIME_SHEET)
    public static Supplier<V3Config> getTimeSheet(){
        return () -> new V3Config(TimeSheetContext.class,new ModuleCustomFieldCount30_BS2())
                .create()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeSheet.TIME_SHEET))
                .update()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.TimeSheet.TIME_SHEET))
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
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Trip.TRIP_ACTIVITY))
                .update()
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.Trip.TRIP_ACTIVITY))
                .list()
                .summary()
                .delete()
                .build();
    }

    @Module(FacilioConstants.Territory.PEOPLE_TERRITORY)
    public static Supplier<V3Config> getPeopleTerritory(){
        return () -> new V3Config(PeopleTerritoryContext.class,null)
                .create()
                .update()
                .list()
                .beforeFetch(new FetchPeopleTerritorySupplementsCommand())
                .summary()
                .beforeFetch(new FetchPeopleTerritorySupplementsCommand())
                .delete()
                .build();
    }

}
