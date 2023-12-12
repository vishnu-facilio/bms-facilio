package com.facilio.bmsconsoleV3.commands;

import com.facilio.agentv2.commands.FetchAgentDetailsCommand;
import com.facilio.agentv2.commands.FetchMessageSourcesCommand;
import com.facilio.backgroundactivity.commands.FillColourCodeForBackgroundActivityCommand;
import com.facilio.bmsconsole.automation.command.ListGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.ListGlobalVariableGroupCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.module.GetSortableFieldsCommand;
import com.facilio.bmsconsoleV3.commands.ODataReadings.GetODataReadingData;
import com.facilio.bmsconsoleV3.commands.ODataReadings.GetODataReadingFieldsCommand;
import com.facilio.bmsconsoleV3.commands.ODataReadings.GetParentIdforCriteria;
import com.facilio.bmsconsoleV3.commands.building.BuildingFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.LoadPeopleAnnouncementLookupCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.UserScopingForAnnouncements;
import com.facilio.bmsconsoleV3.commands.communityFeatures.announcement.addAnnouncementDefaultFields;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.LoadNeighbourHoodFixedFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood.NeighbourhoodFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.DealsAndOffersFillLookupFields;
import com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers.LoadDealsAndOffersFixedFieldsCommand;
import com.facilio.bmsconsoleV3.commands.decommission.FetchDependentResourceDataCommand;
import com.facilio.bmsconsoleV3.commands.decommission.FetchResourceDependentModuleListCommand;
import com.facilio.bmsconsoleV3.commands.employee.LoadEmployeeLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.facility.*;
import com.facilio.bmsconsoleV3.commands.floor.FloorFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.floorplan.*;
import com.facilio.bmsconsole.commands.page.GetSummaryFieldsCommand;
import com.facilio.bmsconsoleV3.commands.formrelation.GetFormRelationListCommand;
import com.facilio.bmsconsoleV3.commands.homepage.getHomePageCommand;
import com.facilio.bmsconsoleV3.commands.homepage.getHomePageWidgetDataCommand;
import com.facilio.bmsconsoleV3.commands.invoice.*;
import com.facilio.bmsconsoleV3.commands.jobplan.AddPlannerIdFilterCriteriaCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FetchExtraFieldsForJobPlanCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.FetchJobPlanLookupCommand;
import com.facilio.bmsconsoleV3.commands.jobplanSection.AddCriteriaForJobPlanSectionBeforeFetchCommand;
import com.facilio.bmsconsoleV3.commands.jobplanSection.AddSupplementForJobPlanSectionCommand;
import com.facilio.bmsconsoleV3.commands.jobplanTask.AddCriteriaForJobPlanTaskBeforeFetchCommand;
import com.facilio.bmsconsoleV3.commands.jobplanTask.AddSupplementForJobPlanTaskCommand;
import com.facilio.bmsconsoleV3.commands.jobplanTask.FillReadingObjForJobPlanTaskCommand;
import com.facilio.bmsconsoleV3.commands.people.FetchLabourAndUserContextForPeople;
import com.facilio.bmsconsoleV3.commands.people.FetchPeopleMeta;
import com.facilio.bmsconsoleV3.commands.people.FetchScopingForPeopleCommandV3;
import com.facilio.bmsconsoleV3.commands.peoplegroup.V3FetchPeopleGroupCommand;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.FetchExtraFieldsForPPMCommand;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchMyReadingImportDataList;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchReadingImportDataById;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchReadingImportDataList;
import com.facilio.bmsconsoleV3.commands.site.SiteFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.tenant.LoadTenantLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.SetTenantSpaceAndContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.userScoping.GetUserScopingConfigCommand;
import com.facilio.bmsconsoleV3.commands.userScoping.GetUserScopingListCommand;
import com.facilio.bmsconsoleV3.commands.userScoping.GetUserScopingModulesCommand;
import com.facilio.bmsconsoleV3.commands.usernotification.AddUserCriteriaMyNotification;
import com.facilio.bmsconsoleV3.commands.usernotification.FetchUnSeenNotificationCommand;
import com.facilio.bmsconsoleV3.commands.vendorcontact.LoadVendorContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.LoadInvitesExtraFieldsCommand;
import com.facilio.bmsconsoleV3.commands.visitorlog.LoadRecordIdForPassCodeCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.SetInviteStatusConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.SetInviteConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.people.FetchPeopleSkillLevelCommand;
import com.facilio.permission.commands.*;
import com.facilio.relation.command.*;
import com.facilio.remotemonitoring.commands.*;

public class ReadOnlyChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getVisitorLoggingBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getInviteVisitorLogBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteStatusConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        c.addCommand(new LoadInvitesExtraFieldsCommand());
        return c;
    }
    
    public static FacilioChain getVisitorLogBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getInviteVisitorBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
 
    public static FacilioChain getBaseVisitBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        c.addCommand(new SetTenantSpaceAndContactsCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadWorkorderLookupsAfterFetchcommandV3());
        c.addCommand(new LoadWorkorderRelatedModulesCommandV3());
        c.addCommand(new GetworkorderHazardsCommandV3());
        //c.addCommand(new GetAvailableStateCommand());
        //c.addCommand(new GetTaskInputDataCommand());
        //c.addCommand(new FetchApprovalRulesCommand());
        c.addCommand(new FetchSourceTypeDetailsForWorkorderCommandV3());
        c.addCommand(new FetchStateflowAndSlaCommandV3());

        return c;
    }

    public static FacilioChain getQuoteBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultCriteriaForQuoteFetchCommandV3());
        c.addCommand(new QuotationFillLookupFields());
        return c;
    }

    public static FacilioChain getInvoiceBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultCriteriaForInvoiceFetchCommand());
        c.addCommand(new FillInvoiceLookupFieldCommand());
        c.addCommand(new AddFixedFieldsForInvoiceCommand());
        return c;
    }


    public static FacilioChain getUserNotificationBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchUnSeenNotificationCommand());
        c.addCommand(new AddUserCriteriaMyNotification());
        return c;
    }
    public static FacilioChain getODataReadingsChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetParentIdforCriteria());
        c.addCommand(new GetODataReadingFieldsCommand());
        c.addCommand(new GetODataReadingData());
        return c;
    }
    public static FacilioChain getFacilityAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFacilityAvailabilityCommandV3());
        return c;
    }

    public static FacilioChain getControlGroupSlotsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchControlGroupSlotCommmand());
        c.addCommand(new ComputeControlGroupSlotCommmand());
        return c;
    }
    
    public static FacilioChain getTenantListToBePublished() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchTenantsToBePublishedCommmand());
        return c;
    }
    
    public static FacilioChain getSummaryFieldsCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(ReadOnlyChainFactory.fetchModuleDataDetailsChain());
        c.addCommand(new GetSummaryFieldsCommand());
        return c;
    }
    
    public static FacilioChain getFloorplanFacilitiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        return c;
    }
    
    public static FacilioChain getFloorplanMapByTypeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchFloorplanMapByTypeCommmand());
        return c;
    }
    
    public static FacilioChain floorplanListSearchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterCommand());
        c.addCommand(new FloorplanDetailsListCommand());
        return c;
    }

    public static FacilioChain getListGlobalVariableGroupChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ListGlobalVariableGroupCommand());
        return chain;
    }

    public static FacilioChain getListGlobalVariableChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ListGlobalVariableCommand());
        return chain;
        
    }
    public static FacilioChain getfloorplanViewerObjectChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFloorPlanCustomizationCommand());
        c.addCommand(new getIndoorFloorPlanViewerCommand());
        c.addCommand(new getFloorplanLayerCommand());
        return c;
    }
    public static FacilioChain getfloorplanBookingObjectChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFloorPlanCustomizationCommand());
        c.addCommand(new getIndoorFloorPlanBookingViewerCommand());
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        c.addCommand(new getIndoorFloorPlanBookingResultCommands());
        c.addCommand(new getFloorplanLayerCommand());
        return c;
    }
    public static FacilioChain getfloorplanNewBookingObjectChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFloorPlanCustomizationCommand());
        c.addCommand(new SetDefaultFloorplanBookingVariableCommand());
        c.addCommand(new getIndoorFloorPlanBookingViewerCommand());
        c.addCommand(new FetchSpaceBookingCommand());
        c.addCommand(new getIndoorFloorPlanBookingNewResultCommands());
        c.addCommand(new SetBookingFormIDCommand());
        c.addCommand(new getFloorplanLayerCommand());
        return c;
    }
    public static FacilioChain getfloorplanPropertiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanPropertiesCommand());
        return c;
    }
    public static FacilioChain getfloorplanBookingPropertiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanPropertiesCommand());
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        c.addCommand(new getFloorplanPropertiesBookingResultCommands());
        
        return c;
    }
    
    public static FacilioChain getSortableFieldsCommand() {
        FacilioChain c = getDefaultChain();
		c.addCommand(FacilioChainFactory.getFieldsByAccessType());
        c.addCommand(new GetSortableFieldsCommand());
        return c;
    }

    public static FacilioChain getRelationDataListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelationParamCommand());
        c.addCommand(new AppendRelationFilterCommand());
        return c;
    }

    public static FacilioChain updateRelationSupplementsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateRelationSupplementsCommand());
        return c;
    }

    public static FacilioChain validateAndGetCustomRelationDataChain() {
        FacilioChain c = getRelationDataListChain();
        c.addCommand(new GetRelatedModuleDataCommand());
        return c;
    }

    public static FacilioChain getCustomRelationDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetRelatedModuleDataCommand());
        return c;
    }

    public static FacilioChain getRelatedDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelatedDataAndSetParamsCommand());
        c.addCommand(new GetRelatedDataListCommand());
        return c;
    }

    public static FacilioChain getRelatedModuleDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelatedModuleDataAndSetParamsCommand());
        c.addCommand(new GetRelatedDataListCommand());
        return c;
    }

    public static FacilioChain getRelationModuleDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelationshipPickListCommand());
        c.addCommand(new GetRelatedDataListCommand());
        return c;
    }

    public static FacilioChain getAssociateDissociateDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelatedDataAndSetParamsCommand());
        c.addCommand(new AssociateOrDissociateDataCommand());
        return c;
    }

    public static FacilioChain getAgentDetailsCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAgentDetailsCommand());
        return c;
    }

    public static FacilioChain getPeopleRoleAndScopingCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchRolesForPeopleCommandV3());
        c.addCommand(new FetchScopingForPeopleCommandV3());
        c.addCommand(new AddSpaceDetailsToTenantContact());
        c.addCommand(new FetchPeopleSkillLevelCommand());
        return c;
    }

    public static FacilioChain getFetchSiteFilterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new SiteFillLookupFieldsCommand());
        c.addCommand(new FetchSpaceExcludingAccessibleSpacesCommandV3());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }

    public static FacilioChain getFetchBuildingFilterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new BuildingFillLookupFieldsCommand());
        c.addCommand(new FetchSpaceExcludingAccessibleSpacesCommandV3());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }

    public static FacilioChain getFetchFloorFilterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FloorFillLookupFieldsCommand());
        c.addCommand(new FetchSpaceExcludingAccessibleSpacesCommandV3());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }


    public static FacilioChain getMessageSourcesCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchMessageSourcesCommand());
        return c;
    }
    public static FacilioChain getReadingImportData() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchReadingImportDataById());
        return c;
    }

    public static FacilioChain getReadingImportDataList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        c.addCommand(new FetchReadingImportDataList());
        return c;
    }
    public static FacilioChain getMyReadingImportDataList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        c.addCommand(new FetchMyReadingImportDataList());

        return c;
    }
    public static FacilioChain getHomepageChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getHomePageCommand());
        return c;
    }

    public static FacilioChain getWidgetDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getHomePageWidgetDataCommand());
        return c;
    }

    public static FacilioChain getTenantContactBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadTenantcontactLookupsCommandV3());
        return chain;
    }
    public static FacilioChain getVendorContactBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadVendorContactLookupCommandV3());
        return chain;
    }
    public static FacilioChain getEmployeeBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadEmployeeLookupCommandV3());
        return chain;
    }

    public static FacilioChain getPeopleAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchLabourAndUserContextForPeople());
        c.addCommand(new FetchScopingForPeopleCommandV3());
        c.addCommand(new FetchPeopleMeta());
        return c;
    }

    public static FacilioChain getPeopleAfterListChain(){
        FacilioChain c =getDefaultChain();
        c.addCommand(new FetchScopingForPeopleCommandV3());
        c.addCommand(new FetchPeopleMeta());
        return c;
    }

    public static FacilioChain getPeopleGroupChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V3FetchPeopleGroupCommand());
        return chain;
    }

    public static FacilioChain getScopeVariable() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchScopeVariableCommand());
        return chain;
    }

    public static FacilioChain getScopeVariableList() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchScopeVariableListCommand());
        return chain;
    }

    public static FacilioChain getGlobalScopeVariableMeta() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetGlobalScopeMetaCommand());
        return chain;
    }

    public static FacilioChain getGlobalScopeVariableFields() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetGlobalScopeVariableFIeldsCommand());
        return chain;
    }

    public static FacilioChain getValueGeneratorsForGlobalScopeVariable() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetValueGeneratorsForGlobalScopeCommand());
        return chain;
    }

    public static FacilioChain getFormRelationList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFormRelationListCommand());
        return c;
    }
    
    public static FacilioChain getJobPlanSectionBeforeListFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSupplementForJobPlanSectionCommand());
        c.addCommand(new AddCriteriaForJobPlanSectionBeforeFetchCommand());
        return c;
    }
    
    public static FacilioChain getJobPlanTaskBeforeListFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSupplementForJobPlanTaskCommand());
        c.addCommand(new AddCriteriaForJobPlanTaskBeforeFetchCommand());
        return c;
    }
    
    public static FacilioChain getJobPlanTaskAfterListFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillReadingObjForJobPlanTaskCommand());
        return c;
    }

    public static FacilioChain getCustomKioskDetailsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchCustomKioskDetailsCommand());
        c.addCommand(new GetCustomkKioskButtonCommand());
        return c;
    }

    public static FacilioChain getFacilityBeforeListFetchChain()
    {
        FacilioChain c= getDefaultChain();
        c.addCommand(new LoadFacilityLookupCommandV3());
        c.addCommand(new LoadFacilityExtraFieldsCommandV3());
        return c;
    }

    public static FacilioChain getFacilityBookingBeforeListFetchChain()
    {
        FacilioChain c= getDefaultChain();
        c.addCommand(new LoadFacilityBookingLookupCommand());
        c.addCommand(new LoadFacilityBookingExtraFieldsCommandV3());
        return c;
    }
    public static FacilioChain getPPMBeforeListFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMFetchSupplements());
        c.addCommand(new FetchExtraFieldsForPPMCommand());
        return c;
    }
    public static FacilioChain getJobPlanBeforeListFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchJobPlanLookupCommand());
        c.addCommand(new FetchExtraFieldsForJobPlanCommand());
        return c;
    }
    public static FacilioChain getDependentResources(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateDecommisionCommand());
        c.addCommand(new FetchDependentResourceDataCommand());
        c.addCommand(new FetchResourceDependentModuleListCommand());
        return c;
    }

    public static FacilioChain getUserScopingModulesListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetUserScopingModulesCommand());
        return c;
    }

    public static FacilioChain getUserScopingListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetUserScopingListCommand());
        return c;
    }

    public  static FacilioChain getUserScopingConfigChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetUserScopingConfigCommand());
        return c;
    }

    public static FacilioChain fetchPermissionSetChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPermissionSetCommand());
        return c;
    }

    public static FacilioChain getPermissionSetGrouping() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPermissionSetGroupingCommand());
        return c;
    }

    public static FacilioChain getPermissionSetTypeGroup() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPermissionSetTypeCommand());
        return c;
    }

    public static FacilioChain getPermissionSetGroupItems() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPermissionSetGroupItemsCommand());
        return c;
    }

    public static FacilioChain getPermissionPermissionItems() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetPermissionSetPermissionItemsCommand());
        return c;
    }

    public static FacilioChain fetchPermissionSetListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPermissionSetsListCommand());
        return c;
    }

    public static FacilioChain backgroundActivityFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillColourCodeForBackgroundActivityCommand());
        return c;
    }

    public static FacilioChain getAnnouncementsBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadPeopleAnnouncementLookupCommand());
        c.addCommand(new UserScopingForAnnouncements());
        c.addCommand(new addAnnouncementDefaultFields());
        return c;
    }
    public static FacilioChain getNeighbourhoodBeforeFetchChain()
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new NeighbourhoodFillLookupFieldsCommand());
        c.addCommand(new LoadNeighbourHoodFixedFieldsCommand());
        return c;
    }
    public static FacilioChain getDealsAndOffersBeforeFetchChain()
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DealsAndOffersFillLookupFields());
        c.addCommand(new LoadDealsAndOffersFixedFieldsCommand());
        return c;
    }

    public static  FacilioChain getQuotationSettingData() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchQuotationSetting());
        return c;
    }
    public static  FacilioChain getInvoiceSettingData() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchInvoiceSetting());
        return c;
    }

    public static  FacilioChain getNumberFormatData() {

        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchNumberFormat());
         return c;
    }
    
    public static FacilioChain getInvoiceAfterFetchSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new HandlePortalInvoiceSettingCommand());
        c.addCommand(new InvoiceFillDetailsCommand());
        c.addCommand(new HandlePortalInvoiceMarkupCommand());
        return c;
    }

    public static FacilioChain getQuoteAfterfetchSummayChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new HandlePortalQuoteSettingCommand());
        c.addCommand(new QuotationFillDetailsCommand());
        c.addCommand(new HandlePortalSummaryMarkupDataCommand());
        c.addCommand(new HandleQuoteDecimalValuesCommand());
        return c;
    }


    public static FacilioChain getAlarmFilterRuleSummaryAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchFilterRuleCriteriaForFilterRuleCommand());
        c.addCommand(new SetSiteAndControllerCriteriaCommand());
        return c;
    }
    public static FacilioChain getFlaggedEventRuleSummaryAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetFlaggedEventRuleRelatedRecordsCommand());
        return c;
    }
    public static FacilioChain getFlaggedEventActionSummaryAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetFlaggedEventBureauActionRelatedRecordsCommand());
        return c;
    }
    public static FacilioChain closeButtonDetails(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CloseButtonDetailsCommand());
        return c;
    }

    public static FacilioChain GetSFG20SettingsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSFG20SettingsCommand());
        return c;
    }

    public static FacilioChain GetSFG20SyncHistoryListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        c.addCommand(new GetSFGSyncHistoryCommand());
        return c;
    }

    public static FacilioChain GetSFG20SyncHistoryDetailsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSFGSyncHistoryDetailsCommand());
        return c;
    }

    public static FacilioChain GetSFG20SyncScheduleDetailsListCountChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSFGSyncScheduleDetailsListCountCommand());
        return c;
    }
    public static FacilioChain getAssetRelationChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAssetRelationsForAssetCategoryCommand());
        return c;
    }

    public static FacilioChain getFetchVirtualRelationDataChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetVirtualRelationDataCommand());
        return chain;
    }

    public static FacilioChain flaggedEventAfterFetchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ComputeTimeRemainingForFlaggedEventCommand());
        c.addCommand(new FillFlaggedEventLogicalControllerCommand());
        c.addCommand(new SetFlaggedAlarmStatusDisplayNameCommand());
        return c;
    }

    public static FacilioChain getGlobalSwitchAccessiblityChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GlobalSwitchAccessiblityCommandV3());
        return c;
    }
}
